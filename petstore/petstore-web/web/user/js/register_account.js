 const userRegisterAccount = { 
    data() {
        return {
            account: {},
			errorMessage: ""
        }
    },
    methods: {
		update() {
			var that = this;
			this.account.user_id = this.router.currentUser;
            axios.post(PetStore.UserService + '/update', {account: JSON.stringify(this.account)})
			.then(function (response) {
				that.router.setPage('user', 'account');
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "用户信息修改失败，请重试";
			});
		}
    }
}
