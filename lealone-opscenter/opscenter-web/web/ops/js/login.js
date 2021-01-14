const opsLogin = { 
    data() {
        return {
			url: "jdbc:lealone:tcp://localhost:9210/lealone",
		    user: "root",
			password: "",
            languages: []
        }
    },
    methods: {
        login() {
            var that = this;
            axios.post(OpsCenter.OpsService + '/login', { url: this.url, user: this.user, password: this.password })
            .then(function (response) {
                console.log(response.data);
				// that.router.route("ops", "header");
				that.router.route("ops", "tables");
                // location.href = "/";
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
    },
    mounted() {
        axios.get(OpsCenter.OpsService + '/get_language_combo')
        .then(response => {
			this.languages = response.data;
		});
    }
}
