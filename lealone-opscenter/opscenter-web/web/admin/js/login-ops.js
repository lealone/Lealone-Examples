const loginOps = { 
    data() {
        return {
            name: "",
            driver: "",
			url: "",
		    user: "",
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
                location.href = "/ops/index.html";
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
    	this.language = this.text.language;
        var that = this;
        axios.get(OpsCenter.OpsService + '/get_languages')
        .then(response => {
            that.languages = response.data;
        });
        axios.get(OpsCenter.OpsService + '/get_settings')
        .then(response => {
            that.settings = response.data.settings;
            that.setting = response.data.setting;
            that.name = response.data.name;
            that.driver = response.data.driver;
            that.url = response.data.url;
            that.user = response.data.user;
        });
    }
}
