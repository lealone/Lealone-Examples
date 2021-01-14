const opsTables = { 
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
				that.lealone.route("ops", "header");
                // location.href = "/";
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "用户名或密码不正确,请重新输入";
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
