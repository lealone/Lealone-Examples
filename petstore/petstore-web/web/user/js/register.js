const userRegister = { 
    data() {
        return {
            user: {user_id: "", password: "", password2: ""},
            errorMessage: ""
        }
    },
    methods: {
        register() {
            if(this.user.password != this.user.password2) {
                this.errorMessage = "密码验证 必须和 密码 相同";
                return;
            }
            axios.post(PetStore.UserService + '/register', {user: JSON.stringify(this.user)})
            .then(response => {
                if(!lealone.currentUser) {
                    lealone.currentUser = this.user.user_id;
                    localStorage.currentUser = this.user.user_id;
                }
                location.href = "/";
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "登录名\"" + this.user.user_id + "\"已经被人注掉了，请尝试另一个名字";
            });
        }
    }
}
