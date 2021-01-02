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
			var that = this;
            axios.post(Public.UserService + '/register', {user: JSON.stringify(this.user)})
			.then(function (response) {
				if(!that.router.currentUser) {
			        that.router.currentUser = that.user.user_id;
				    localStorage.currentUser = that.user.user_id;
				}
				location.href = "/";
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "登录名\"" + that.user.user_id + "\"已经被人注掉了，请尝试另一个名字";
			});
		}
    }
}
