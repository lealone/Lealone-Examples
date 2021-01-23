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
            axios.post(PetStore.UserService + '/login', this.$data)
            .then(response => {
                lealone.currentUser = response.data.USER_ID;
                localStorage.currentUser = response.data.USER_ID;
                location.href = "/";
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "用户名或密码不正确,请重新输入";
            });
        },
        logout() {
            localStorage.removeItem("currentUser");
            axios.get("/user/logout")
            .then(response => {
                location.href = "/";
            });
        }
    }
}
