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
        testConnection() {
        },
        changeLanguage() {
            var that = this;
            axios.get(OpsCenter.OpsService + '/read_translations?language=' + this.language)
            .then(response => {
                that.txt = response.data.text;
                that.text = OpsCenter.i18n.parse(that.txt);
            });
        },
        settingSave() {
            var that = this;
            axios.post(OpsCenter.OpsService + '/setting_save', 
                  { name: this.name, driver: this.driver, url: this.url, user: this.user })
            .then(function (response) { 
                location.href = "/admin/index.html";
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "配置保存失败";
            });
        },
        settingRemove() {
            var that = this;
            axios.post(OpsCenter.OpsService + '/setting_remove', { name: this.name })
            .then(function (response) { 
                location.href = "/admin/index.html";
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "配置删除失败";
            });
        },
        getSettings() {
            var that = this;
            axios.get(OpsCenter.OpsService + '/get_settings?setting=' + this.setting)
            .then(response => {
                that.settings = response.data.settings;
                that.setting = response.data.setting;
                that.name = response.data.name;
                that.driver = response.data.driver;
                that.url = response.data.url;
                that.user = response.data.user;
            });
        }
    },
    mounted() {
    	this.language = this.text.language;
        var that = this;
        axios.get(OpsCenter.OpsService + '/get_languages')
        .then(response => {
            that.languages = response.data;
        });
        this.getSettings(); 
    }
}
