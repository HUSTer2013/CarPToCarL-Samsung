# CarPToCarL-Samsung

面向国行三星 Galaxy 的 [CarPToCarL](https://github.com/jjuugk/CarPToCarL) 适配分支。手机通过系统「三星车联」里的百度 CarLife+ 提供车机界面，本应用将 H.264 码流直通到无线 CarPlay 接收端，并将接收端触控回传给 CarLife+。

这是独立的兼容性实验项目。请在停车时配置和测试。许可证、版权声明和使用边界见 [LICENSE](LICENSE)、[NOTICE](NOTICE) 和 [DISCLAIMER.md](DISCLAIMER.md)。

## 已验证的平台

| 手机与接收端 | 实测结果 |
| --- | --- |
| 国行 Galaxy S24 → DiPlay | `1.2.70-samsung-test3` 实测出画和触控正常。CarLife+ 实际码流与 DiPlay 触控视图均为 **2296×1080**，该轮未触发 720p 回退。 |
| Galaxy S24 → Porsche Macan EV | 实车连接、画面和触控待验证。 |

DiPlay 成功的一轮从接收端连接到真实画面约 22 秒，其中本机 CarLife+ 引擎接通后约 2.3 秒收到首帧。具体时间以当次设备和连接状态为准。

## 三星适配改动

- **三星车联入口**。在「连接准备」中加入「打开三星车联设置」。按钮只在系统能解析到已启用、可公开启动的三星车联设置页时显示。进入设置后，由用户继续进入百度 CarLife+ 的无线连接流程。
- **按接收端尺寸初始化**。每次创建本机 CarLife+ 引擎会话时读取最近一次接收端通告的画布尺寸，包括同一次应用运行中的重连。没有记录时使用 1280×720。
- **首帧超时回退**。三星设备已下发编码初始化、所申请的宽或高超过 1280×720、且约 10 秒仍未收到引擎帧时，本次引擎会话尝试一次 1280×720。触控继续使用接收端视图坐标。
- **连接日志**。「复制日志」和导出日志可保留较早的关键事件，包括接收端通告尺寸、编码申请、引擎确认、引擎首帧、SPS 实际尺寸和提示帧。
- **独立测试包**。Debug 版为 `1.2.70-samsung-test3`，包名 `io.github.shaonialife.carptocar.samsungtest3`，可与上游原版并存。

## 使用方法

1. 在 Galaxy 上保留系统「三星车联」(`com.samsung.android.carlink`) 和系统百度 CarLife+ 组件 (`com.baidu.carlife.samsung`)。
2. 在手机中打开 **三星车联 → 百度 CarLife+ → 无线连接**。如果应用中显示「打开三星车联设置」，也可从该入口进入。
3. 让 DiPlay 进入无线接收模式，用本应用启动连接。如果接收端先显示「已连接，但没有 CarLife 画面」，进入上述无线连接页并等待本机引擎出帧。
4. 若仍停在提示页，通过应用的「复制日志」或「导出日志」查看本轮编码请求、确认回包和首帧记录。对外分享日志前请移除设备标识、蓝牙地址和网络信息。

## 构建 Debug APK

需要 JDK 17 和 Android SDK Platform 34。手机安装要求 Android 12 或以上。

```bash
./gradlew :app:assembleDebug
```

生成位置为 `app/build/outputs/apk/debug/app-debug.apk`。

## 上游

基于 [jjuugk/CarPToCarL 的 `95f8b6b` 提交](https://github.com/jjuugk/CarPToCarL/tree/95f8b6b9a6b3f30e1b33aa03c63a2c215ae4a5da)（应用版本 1.2.70）。上游的 [README](https://github.com/jjuugk/CarPToCarL/blob/95f8b6b9a6b3f30e1b33aa03c63a2c215ae4a5da/README.md) 包含通用使用说明、协议和历史版本。感谢上游作者以及 [CarDeck](https://github.com/shaonialife/cardeck) 项目。
