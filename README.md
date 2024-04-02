# YAWB（Yet Another WeChat Bot）

## 功能

- [x] Login 登录
    - [x] JS Login
    - [x] QR Code
    - [x] Login
- [x] Init 初始化
- [x] Contact 获取通讯录
- [x] Sync 同步消息
    - [x] Sync Check
    - [x] Sync
- [x] Send Msg 发送消息
    - [x] TEXT 文本消息
    - [ ] ……
- [x] Interceptor 拦截器【插件】
- [ ] ……

## 使用

### 运行

JDK 17 环境开箱即用（Out of the box），代码很简单，没有过度封装。

运行后打开控制台输出的二维码链接，扫码登录即可。

> [!WARNING]
> 不建议在其它终端上使用已登录本程序的账号发送消息。

> [!NOTE]
> 进行语音视频通信时，由于微信服务器没返回正确的 statusLine 会抛出异常。

### 拦截器【插件】

参考 `org.eu.cciradih.yawb.interceptor.impl` 下面的实现。

> [!CAUTION]
> 做了拦截器排序 `BotInterceptorSort.value()` 校验。

## 提示

可以通过断网的方式保活。

1. 运行程序后登录机器人
2. 手机进入【我】-【设置】-【切换账号】
3. 断网
4. 轻触头像以切换账号
5. 耐心等待【登录中】变为【轻触重试】
6. 轻触头像以切换账号
