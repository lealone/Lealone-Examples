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
            if(!lealone.currentUser) {
                lealone.route('user', 'login');
                return;
            }
            axios.get(PetStore.UserService + '/get_user?user_id=' + lealone.currentUser)
            .then(response => {
                if(response.data) {
                    if(response.data.account)
                        this.account = response.data.account;
                    if(response.data.user)
                        this.user = response.data.user;
                }
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "用户信息获取失败";
            });
        }
    },
    mounted() {
        this.getAccountInfo();
    }
}
