const loginAdmin = { 
    data() {
        return {
			password: "",
			error: ""
        }
    },
    methods: {
        login() {
            AdminService.login(this.password, data => {
                if(data == "ok") {
                    lealone.get("login-ops").adminLoginOk = true;
                    lealone.route("admin", lealone.get("login-ops").adminBack);
                }
                else {
                    this.error = "密码错误";
                }
            })
        }
    },
    mounted() {
		document.adminLogin.password.focus();
    }
}
