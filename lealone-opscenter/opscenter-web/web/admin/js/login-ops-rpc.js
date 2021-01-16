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
        onServiceException(errorMessage) {
            this.errorMessage = errorMessage;
        },
        login() {
            OpsService.login(this.url, this.user, this.password, data => {
                console.log(data);
                location.href = "/ops/index.html";
            });
        },
        testConnection() {
        },
        changeLanguage() {
            OpsService.readTranslations(this.language, data => { 
                this.txt = data.text;
                this.text = OpsCenter.i18n.parse(this.txt);
            });
        },
        settingSave() {
            OpsService.settingSave(this.name, this.driver, this.url, this.user, data => { 
                location.href = "/admin/index.html";
            });
        },
        settingRemove() {
            OpsService.settingRemove(this.name, data => { 
                location.href = "/admin/index.html";
            });
        },
        getSettings() {
            OpsService.getSettings(this.setting, data => { 
                this.settings = data.settings;
                this.setting = data.setting;
                this.name = data.name;
                this.driver = data.driver;
                this.url = data.url;
                this.user = data.user;
            });
        }
    },
    mounted() {
        this.language = this.text.language;  
        OpsService.getLanguages(this.setting, data => { 
            this.languages = data;
        });
        this.getSettings(); 
    }
}
