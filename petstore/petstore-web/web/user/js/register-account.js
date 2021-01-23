const userRegisterAccount = { 
    data() {
        return {
            account: {},
            errorMessage: ""
        }
    },
    methods: {
        update() {
            this.account.user_id = lealone.currentUser;
            axios.post(PetStore.UserService + '/update', {account: JSON.stringify(this.account)})
            .then(response => {
                lealone.route('user', 'account');
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "用户信息修改失败，请重试";
            });
        }
    }
}
