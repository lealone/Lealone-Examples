 const userLogin = { 
    data() {
        return {
            user_id: "",
            password: "",
            errorMessage: ""
        }
    },
    methods: {
        login() {
            var that = this;
            axios.post(PetStore.UserService + '/login', this.$data)
            .then(function (response) {
                that.router.currentUser = response.data.USER_ID;
                localStorage.currentUser = response.data.USER_ID;
                location.href = "/";
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "用户名或密码不正确,请重新输入";
            });
        },
        logout() {
            localStorage.removeItem("currentUser");
            axios.get("/user/logout")
            .then(function (response) {
                location.href = "/";
            });
        }
    }
}
