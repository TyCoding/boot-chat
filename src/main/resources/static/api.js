let api = {

    /**
     * 头像列表 -- 本地json文件加载
     */
    getAvatarList() {
        return '/avatar/avatar.json'
    },

    /**
     * 登录接口
     */
    login() {
        return '/login'
    },

    /**
     * 跳转至主页面
     */
    redirect(id) {
        return '/' + id + '/chat'
    },

    /**
     * 根据ID获取用户信息
     */
    getUser(id) {
        return '/chat/' + id
    },

    /**
     * 获取在线用户列表
     */
    getOnline() {
        return '/chat/online/list'
    },

    /**
     * WebSocket服务器链接接口
     */
    websocket(id) {
        return 'ws://127.0.0.1:8080/chat/' + id
    },

    /**
     * 获取群发消息列表
     */
    getCommon() {
        return '/chat/common'
    },

    /**
     * 获取与指定窗口的消息列表
     */
    getSelf(fromId, toId) {
        return '/chat/self/' + fromId + '/' + toId
    },

    /**
     * 向指定窗口推送消息
     */
    pushId(toId) {
        return '/chat/push/' + toId
    },

    /**
     * 注销用户
     */
    logout(id) {
        return '/chat/' + id
    }
}
