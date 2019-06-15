new Vue({
    el: '#app',
    data() {
        return {
            dialog: true,
            avatarDialog: false,
            form: {},
            avatarList: [],
        }
    },
    mounted() {
        this.$refs.loader.style.display = 'none';
    },
    methods: {
        handleEditAvatar() {
            this.$http.get('/avatar/avatar.json').then(response => {
                this.avatarList = response.body;
            });
            this.avatarDialog = true;
        },
        changeAvatar(url) {
            this.form.avatar = url;
            this.avatarDialog = false;
        },

        login(form) {
            this.$refs[form].validate(valid => {
                if (valid) {
                    if (this.form.avatar == null || this.form.avatar == '') {
                        this._notify('请选择头像', 'warning')
                        return;
                    }
                    this.form.id = new Date().getTime();
                    this.$http.post('/login', JSON.stringify(this.form)).then(response => {
                        window.location.href = '/' + this.form.id + '/chat'
                        this.dialog = false
                    })
                } else {
                    return false;
                }
            })
        }
    }
})
