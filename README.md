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
- [ ] 发送消息
- [ ] ……

## 使用

开箱即用（Out of the box），代码很简单，没有过度封装。

运行后打开控制台输出的二维码链接，扫码登录即可。

## 提示

可以通过断网的方式保活。

1. 运行程序后登录机器人
2. 手机进入【我】-【设置】-【切换账号】
3. 断网
4. 轻触头像以切换账号
5. 耐心等待【登录中】变为【轻触重试】
6. 轻触头像以切换账号
