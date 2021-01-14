const userAccount = { 
    data() {
        return {
            account: {},
            user: {},
            errorMessage: ""
        }
    },
    methods: {
        getAccountInfo() {
            if(!this.lealone.currentUser) {
                this.lealone.route('user', 'login');
                return;
            }
            var that = this;
            axios.get(PetStore.UserService + '/get_user?user_id=' + this.lealone.currentUser)
            .then(function (response) {
                if(response.data) {
                    if(response.data.account)
                        that.account = response.data.account;
                    if(response.data.user)
                        that.user = response.data.user;
                }
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "用户信息获取失败";
            });
        }
    },
    mounted() {
        this.getAccountInfo();
    }
}
