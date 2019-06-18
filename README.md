# 基于SpringBoot + WebSocket + Vue实现在线聊天系统

 <p align="center">
  <a href="https://github.com/TyCoding/boot-chat/" target="_blank">
    <img src="https://img.shields.io/badge/BootChat-在线聊天项目-green.svg" alt="Build Status">
  </a>
  <img src="https://img.shields.io/badge/Spring%20Boot-2.1.5.RELEASE-yellowgreen.svg" alt="Downloads">
  <img src="https://img.shields.io/badge/Vue.js-2.6.10-blue.svg" alt="Coverage Status">
  <img src="https://img.shields.io/badge/ElementUI-2.7.0-blue.svg" alt="Coverage Status">
 </p>

 **线上地址**

[Chat](http://39.105.46.235:8087/)

**欢迎star、fork支持**

## 介绍

本仓库中包含了两个子项目：

* [session-chat](https://github.com/TyCoding/boot-chat/tree/master/session-chat)  基于HTTPSession实现会话消息储存，受限于不同浏览器Session不能共享导致的数据丢失（如果使用同一浏览器测试则不会出现问题）

* [redis-chat](https://github.com/TyCoding/boot-chat/tree/master/redis-chat)  基于Redis实现会话消息储存，会话数据不会丢失，并使用定时任务，定时清除Redis中注册时间过长的用户数据以及其会话消息

## 写在前面

本仓库中包含的是两个项目:

*   如果使用 [session-chat]() 项目，则开箱即用
*   如果使用 [redis-chat]() 项目，启动前需要配置好本地Redis环境才可。

**注意** 由于WebSocket限制，HTML与服务端通信，需要保证WebSocket链接的IP和浏览器访问的IP项目，如果是localhost就都是localhost，如果是127.0.0.1就都改为127.0.0.1，否则可能消息推送失败。

## 联系

QQ交流群：671017003

- [Blog@TyCoding's blog](http://www.tycoding.cn)
- [GitHub@TyCoding](https://github.com/TyCoding)
- [ZhiHu@TyCoding](https://www.zhihu.com/people/tomo-83-82/activities)