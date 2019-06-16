# 基于WebSocket的在线聊天系统

 <p align="center">
  <a href="https://github.com/TyCoding/boot-chat/" target="_blank">
    <img src="https://img.shields.io/badge/BootChat-在线聊天项目-green.svg" alt="Build Status">
  </a>
  <img src="https://img.shields.io/badge/Spring%20Boot-2.1.5.RELEASE-yellowgreen.svg" alt="Downloads">
  <img src="https://img.shields.io/badge/Vue.js-2.6.10-blue.svg" alt="Coverage Status">
  <img src="https://img.shields.io/badge/ElementUI-2.7.0-blue.svg" alt="Coverage Status">
 </p>
 
**线上地址**

![Chat]()

## 介绍

基于SpringBoot-2.1.5、SpringBoot-Starter-Websocket构建，前端使用Vue.js、ElementUI框架。

实现了：

* 单窗口消息推送
* 群发消息推送
* 上线提醒
* 实时刷新用户列表、消息列表

找 *虫子* 无聊之余，可以来耍一耍！

## 核心依赖

| 依赖 | 版本 |
| --- | --- |
| Spring Boot | 2.1.5.RELEASE |
| spring-boot-starter-websocket | 2.1.5.RELEASE |
| lombok | 1.18.8 |
| spring-boot-starter-thymeleaf | 2.1.5.RELEASE |
| FastJSON | 1.2.58 |
| Vue.js | 2.6.10 |
| Element-UI | 2.7.0 |

## 运行&&部署

```
# 运行

$ git clone https://github.com/TyCoding/boot-chat
-- use idea & eclipse run

# 部署

$ mvn clean package
$ cd taget
$ java -jar boot-chat-0.0.1-SNAPSHOT.jar
```

Access http://localhost:8080 using Chrome

## 请喝果汁

如果此项目对你的学习有些帮助，你或许可以请作者喝一杯果汁以表示鼓励

![](doc/wechat.png)

## 关于我

[传送门](https://www.tycoding.cn/about/)
