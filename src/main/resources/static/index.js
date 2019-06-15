new Vue({
    el: '#app',
    data() {
        return {
            online: 0,
            date: this.parseTime(new Date().getTime(), ''),
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
    beforeUpdate() {
        this.scroll();
    },
    updated() {
        this.scroll();
    },
    beforeDestroy() {
        if (this.timer) {
            clearInterval(this.timer);
        }
    },
    mounted() {
        this.init();
        let _this = this;
        this.timer = setInterval(() => {
            _this.date = this.parseTime(new Date().getTime(), '');
        }, 1000)
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
            this.$http.get('/chat/' + this.form.id).then(response => {
                this.user = response.body.data
            })

            //加载在线用户列表
            this.$http.get('/chat/online/list').then(response => {
                let data = response.body.data;
                if (data.length > 0) {
                    this.userList = data;
                }
            })
        },

        initWebSocket() {
            let $this = this;
            this.websocket = new WebSocket('ws://localhost:8080/chat/' + this.form.id)
            //链接发送错误时调用
            this.websocket.onerror = function () {
                $this._notify('链接错误', 'WebSocket链接错误', 'error')
            }
            //链接成功时调用
            this.websocket.onopen = function () {
                $this._notify('链接成功', 'WebSocket链接成功', 'success')
            }
            //接收到消息时回调
            this.websocket.onmessage = function (event) {
                $this.form.message = ''
                console.log(event)
                let entity = JSON.parse(event.data);
                if (entity.data == undefined) {
                    $this._notify('消息', entity.msg, 'info')
                    return;
                }
                let data = JSON.parse(event.data).data
                console.log(data)
                if (data.online != undefined) {
                    $this.online = data.online
                }
                $this.messageList.push(data)
            }
            //链接关闭时调用
            this.websocket.onclose = function () {
                $this._notify('链接关闭', 'WebSocket链接关闭', 'info')
            }
        },

        initCommonMessage() {
            this.$http.get('/chat/common').then(response => {
                let data = response.body.data
                if (data.length > 0) {
                    this.messageList = data
                }
            })
        },

        intSelfMessage() {
            this.$http.get('/chat/self/' + this.form.id + '/' + this.current_window_id).then(response => {
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
                this.$http.get('/chat/push/' + this.current_window_id, this.form.message).then(response => {
                    this._notify('推送成功', '消息推送成功', 'success')
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
            this.$http.delete('/chat/' + this.form.id).then(response => {
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
                this.intSelfMessage();
            }
        },

        //窗口滚动
        scroll() {
            let box = this.$refs.box
            box.scrollTop = 10000
        },

        parseTime(time, cFormat) {
            if (arguments.length === 0) {
                return null
            }
            const format = cFormat || '{y}-{m}-{d} {h}:{i}:{s}'
            let date
            if (typeof time === 'object') {
                date = time
            } else {
                if ((typeof time === 'string') && (/^[0-9]+$/.test(time))) {
                    time = parseInt(time)
                }
                if ((typeof time === 'number') && (time.toString().length === 10)) {
                    time = time * 1000
                }
                date = new Date(time)
            }
            const formatObj = {
                y: date.getFullYear(),
                m: date.getMonth() + 1,
                d: date.getDate(),
                h: date.getHours(),
                i: date.getMinutes(),
                s: date.getSeconds(),
                a: date.getDay()
            }
            const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
                let value = formatObj[key]
                // Note: getDay() returns 0 on Sunday
                if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value ] }
                if (result.length > 0 && value < 10) {
                    value = '0' + value
                }
                return value || 0
            })
            return time_str
        }
    }
})
