 const userAccount = { 
    data() {
        return {
            account: {},
			userInfo: {},
			errorMessage: ""
        }
    },
    methods: {
        getAccountInfo() {
			if(!this.currentUser) {
				this.setPage('user', 'login');
				return;
			}
			var that = this;
            axios.get(Public.UserService + '/get_user?user_id=' + this.currentUser)
			.then(function (response) {
				if(response.data) {
					if(response.data.account)
			            that.account = response.data.account;
					if(response.data.user)
					    that.userInfo = response.data.user;
				}
				that.setPage('user', 'account');
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "用户信息获取失败";
			});
		}
    }
}
