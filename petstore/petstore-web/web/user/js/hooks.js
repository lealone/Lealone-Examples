const userHooks = {
    getUser: {
        before() {
            if(!lealone.currentUser) {
                lealone.route('user', 'login');
                return false;
            }
            this.user_id = lealone.currentUser;
        }
    },
    register: {
        before() {
            if(this.password != this.password2) {
                this.errorMessage = "密码验证 必须和 密码 相同";
                return false;
            }
        },
        after(response) {
            if(!lealone.currentUser) {
                lealone.currentUser = this.user.user_id;
                localStorage.currentUser = this.user.user_id;
            }
            location.href = "/";
        }
    },
    update: {
        before() {
            this.account.user_id = lealone.currentUser;
        },
        after() {
            lealone.route('user', 'account');
        }
    },
    login: {
        after(data) {
            lealone.currentUser = data.USER_ID;
            localStorage.currentUser = data.USER_ID;
            location.href = "/";
        },
        error(msg) {
            this.errorMessage = "用户名或密码不正确,请重新输入";
        }
    }
}
