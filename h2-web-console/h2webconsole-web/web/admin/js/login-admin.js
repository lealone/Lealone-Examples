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
                    var ops = lealone.get("login-ops");
                    ops.adminLoginOk = true;
                    lealone.route("admin", ops.adminBack);
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
