const adminLogin = { 
    data() {
        return {
			password: "",
            error: ""
        }
    },
    methods: {
        login() {
            var that = this;
            axios.post(OpsCenter.AdminService + '/login', { password: this.password })
            .then(function (response) {
                console.log(response.data);
				//that.router.setPage("ops", "header");
				//that.router.setPage("ops", "tables");
                //location.href = "/";
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "密码不正确,请重新输入";
            });
        },
        logout() {
            localStorage.removeItem("currentUser");
            axios.get("/user/logout")
            .then(function (response) {
                location.href = "/";
            });
        }
    },
    mounted() {
		document.adminLogin.password.focus();
    }
}
