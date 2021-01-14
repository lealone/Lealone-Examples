const opsLogin = { 
    data() {
        return {
            name: "",
            driver: "",
			url: "jdbc:lealone:tcp://localhost:9210/lealone",
		    user: "root",
			password: "",
			language: "zh_CN",
            languages: [],
            setting: "",
            settings: [],
            errorMessage: "",
        }
    },
    methods: {
        login() {
            var that = this;
            axios.post(OpsCenter.OpsService + '/login', { url: this.url, user: this.user, password: this.password })
            .then(function (response) {
                console.log(response.data);
                location.href = "/ops/ops.html";
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "用户名或密码不正确,请重新输入";
            });
        },
        changeLanguage() {
            var that = this;
            axios.get(OpsCenter.OpsService + '/read_translations?language=' + this.language)
            .then(response => {
                that.txt = response.data.text;
                that.text = OpsCenter.i18n.parse(that.txt);
            });
        },
        testConnection() {
        },
        settingSave() {
        },
        settingRemove() {
        }
    },
    mounted() {
        var that = this;
        axios.get(OpsCenter.OpsService + '/get_language_combo')
        .then(response => {
            that.languages = response.data;
        });
    }
}
