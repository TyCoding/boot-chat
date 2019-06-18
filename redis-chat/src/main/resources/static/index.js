new Vue({
    el: '#app',
    data() {
        return {
            online: 0,
            websocket: undefined,
            user: {
                id: '',
                avatar: '',
                name: ''
            },

            form: {message: ''},

            //当前激活窗口ID
            current_window_id: 0,

            //在线用户列表
            userList: [],
            //推送消息列表
            messageList  : [],

        }
    },
    updated() {
        this.scroll()
    },
    mounted() {
        this.init();
        this.$refs.loader.style.display = 'none';
    },
    created() {
        this.form.id = this.subURL();
    },
    methods: {
        _message(message, type) {
            this.$message({
                message: message,
                type: type
            })
        },
        _notify(title, message, type) {
            this.$notify({
                title: title,
                message: message,
                type: type
            });
        },

        /**
         * Get Rest URL params
         * for example: http://localhost:8080/1560490905528/chat
         *
         * return 1560490905528
         */
        subURL() {
            return window.location.pathname.substring(window.location.pathname.indexOf('/') + 1, window.location.pathname.lastIndexOf('/'))
        },

        init() {
            console.log(this.messageList)
            /**
             * 加载用户信息
             */
            this.initUser();

            /**
             * 加载公共消息列表 -- 群组
             */
            this.initCommonMessage();

            /**
             * 每次刷新页面，主动链接WebSocket
             */
            this.initWebSocket();
        },

        initUser() {
            //加载当前用户信息
            this.$http.get(api.getUser(this.form.id)).then(response => {
                this.user = response.body.data
            })

            //加载在线用户列表
            this.$http.get(api.getOnline()).then(response => {
                let data = response.body.data;
                if (data.length > 0) {
                    this.userList = data;
                    this.online = this.userList.length
                }
            })
        },

        initWebSocket() {
            let $this = this;
            this.websocket = new WebSocket(api.websocket(this.form.id))
            //链接发送错误时调用
            this.websocket.onerror = function () {
                $this._notify('提醒', 'WebSocket链接错误', 'error')
            }
            //链接成功时调用
            this.websocket.onopen = function () {
                $this._notify('提醒', 'WebSocket链接成功', 'success')
            }
            //接收到消息时回调
            this.websocket.onmessage = function (event) {
                $this.clean()
                let entity = JSON.parse(event.data);

                //上线提醒
                if (entity.data == undefined) {
                    $this.initUser()
                    $this._notify('消息', entity.msg, 'info')
                    $this.scroll()
                    return;
                }

                //消息接收
                let data = JSON.parse(event.data).data
                if (data.to != undefined) {
                    //单个窗口发送，仅推送到指定的窗口
                    if (data.from.id == $this.current_window_id) {
                        $this.messageList.push(data)
                    }
                } else {
                    //群发，推送到官方群组窗口
                    $this.messageList.push(data)
                }
                $this.scroll()
            }
            //链接关闭时调用
            this.websocket.onclose = function () {
                $this._notify('提醒', 'WebSocket链接关闭', 'info')
            }
        },

        initCommonMessage() {
            this.$http.get(api.getCommon()).then(response => {
                let data = response.body.data
                if (data.length > 0) {
                    this.messageList = data
                }
            })
        },

        initSelfMessage() {
            this.$http.get(api.getSelf(this.form.id, this.current_window_id)).then(response => {
                let data = response.body.data
                this.messageList = data
            })
        },

        //推送消息
        send() {
            if (this.form.message == null || this.form.message.trim() == '') {
                this._message('请输入消息内容', 'warning')
                return;
            }
            if (!this.current_window_id) {
                this.websocket.send(this.form.message.replace(/[\r\n]/g,""))
                this.initCommonMessage()
            } else {
                let data = {
                    message: this.form.message,
                    from: this.user
                }
                this.$http.post(api.pushId(this.current_window_id), JSON.stringify(data)).then(response => {
                    if (response.body.code == 200) {
                        this.initSelfMessage()
                        this.clean()
                        this._notify('提醒', '消息推送成功', 'success')
                    } else {
                        this._notify('提醒', response.body.msg, 'error')
                    }
                })
            }
            this.scroll()
        },

        //清空消息
        clean() {
            this.form.message = ''
        },

        //注销
        logout() {
            this.$http.delete(api.logout(this.form.id)).then(response => {
                this.websocket.close()
                window.location.href = "/"
            })
        },

        //切换选择窗口
        selectWindow(id) {
            this.current_window_id = id;
            if (!this.current_window_id) {
                this.initCommonMessage();
            } else {
                this.initSelfMessage();
            }
        },

        //窗口滚动
        scroll() {
            let box = this.$refs.box
            box.scrollTop = 10000000
        },
    }
})
