 const registerUser = { 
    data() {
        return {
            rUser: {user_id: "", password: "", password2: ""},
			rErrorMessage: ""
        }
    },
    methods: {
        register() {
			if(this.rUser.password != this.rUser.password2) {
				this.errorMessage = "密码验证 必须和 密码 相同";
				return;
			}
			var that = this;
            axios.post(Public.UserService + '/register', {user: JSON.stringify(this.rUser)})
			.then(function (response) {
				if(!that.currentUser) {
			        that.currentUser = that.rUser.user_id;
				    localStorage.currentUser = that.rUser.user_id;
				}
				location.href = "/";
			})
			.catch(function (error) {
				console.log(error);
				that.rErrorMessage = "登录名\"" + that.rUser.user_id + "\"已经被人注掉了，请尝试另一个名字";
			});
		}
    }
}
