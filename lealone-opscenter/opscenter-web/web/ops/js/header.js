const opsHeader = { 
    data() {
        return {
            autoCommit: true
        }
    },
    methods: {
        setAutoCommit() {
            var result = "Auto commit is now " + (this.autoCommit ? "ON" : "OFF");
            // ql=@autocommit_' + (document.header.autoCommit.checked ? 'true' : 'false') + '.'
            this.router.setPage('ops', 'result', result);
        },
        login() {
            var that = this;
            axios.post(OpsCenter.OpsService + '/login', { url: this.url, user: this.user, password: this.password })
            .then(function (response) {
                console.log(response.data);
                // that.router.setPage("ops", "header");
                that.router.setPage("ops", "tables");
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
        this.router.components["ops-header"] = this;
        axios.get(OpsCenter.OpsService + '/get_language_combo')
        .then(response => {
			this.languages = response.data;
		});
    }
}
