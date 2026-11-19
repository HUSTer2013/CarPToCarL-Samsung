package io.github.shaonialife.carptocar.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * [CriticalEvents] 的取舍规则。
 *
 * 这个环存在的唯一意义就是「日志滚掉之后还能看到会话开头的结论行」，
 * 所以两条断言最关键：**关键行必须留下、趋势行必须不留**。
 */
class CriticalEventsTest {
    @Before
    fun setUp() {
        CriticalEvents.clear()
    }

    @After
    fun tearDown() {
        CriticalEvents.clear()
    }

    @Test
    fun `协商结果与自检留下`() {
        CriticalEvents.note("[10:00:00.000] AIRPLAY: ✓ 音频流协商成功（type=102 media format=0x800000 48000Hz）")
        CriticalEvents.note("[10:00:00.100] AUDIO: 自检 = 首帧 548B 本机解码 1024 个样本，峰值 9123")
        assertEquals(2, CriticalEvents.snapshot().size)
    }

    @Test
    fun `首轮申请和实际视频尺寸随日志导出`() {
        val startupGeometry =
            listOf(
                "[10:00:00.000] CARLIFE: 引擎首轮画布 1280×720（默认）",
                "[10:00:00.100] CARLIFE: 主动推送首轮 98311 视频编码初始化 1280x720@60",
                "[10:00:01.000] DISPLAY: 车机 2296×1080（/info 通告）",
                "[10:00:02.000] VIDEO: 尺寸 coded=1280x720 visible=1280x720 declared=1280x720",
                "[10:00:02.001] VIDEO: 已向车机推送提示帧（已连接，但没有 CarLife 画面）",
                "[10:00:11.000] CARLIFE: 首帧超时，编码申请 2296x1080 → 1280x720",
                "[10:00:11.300] CARLIFE: 已下发 98311 1280x720@30",
                "[10:00:11.400] CARLIFE: 手机确认编码参数 1280x720@30（申请 1280x720@30），启动推流",
                "[10:00:12.000] CARLIFE: 已收到引擎首帧（4096B）",
                "[10:00:12.001] CARLIFE: 码流真实分辨率 1280x720（原 0 x 0，谈好档位 1280x720）",
            )
        startupGeometry.forEach(CriticalEvents::note)
        repeat(500) { CriticalEvents.note("[10:00:05.000] VIDEO: 帧 #$it 已发送") }
        assertEquals(startupGeometry, CriticalEvents.snapshot())
    }

    @Test
    fun `每 30 秒的趋势行不进环`() {
        CriticalEvents.note("[10:00:30.000] AUDIO: 链路体检 t102/AAC_LC —— 累计 1406 包；锚点 30/30 被拒")
        CriticalEvents.note("[10:00:30.100] CARLIFE: 音频直通 216 包 / 864KB / 1413kbps · 电平 100%")
        CriticalEvents.note("[10:00:30.200] VIDEO: 帧 #41641 61916B (key) 已发送")
        assertTrue(CriticalEvents.snapshot().isEmpty())
    }

    @Test
    fun `超过上限时丢最旧的`() {
        repeat(250) { CriticalEvents.note("[10:00:00.000] AUDIO: 自检 #$it") }
        val lines = CriticalEvents.snapshot()
        assertEquals(200, lines.size)
        assertTrue(lines.first().endsWith("#50"))
        assertTrue(lines.last().endsWith("#249"))
    }

    @Test
    fun `顺序与时间戳原样保留`() {
        CriticalEvents.note("[10:00:00.000] SESSION 开始")
        CriticalEvents.note("[10:00:05.000] STREAM: 顺序 = 音频优先")
        assertEquals(
            listOf("[10:00:00.000] SESSION 开始", "[10:00:05.000] STREAM: 顺序 = 音频优先"),
            CriticalEvents.snapshot(),
        )
    }
}
