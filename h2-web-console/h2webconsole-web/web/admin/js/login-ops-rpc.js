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
            OpsService.login(this.url, this.user, this.password, this, data => {
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
            OpsService.settingSave(this.name, this.driver, this.url, this.user, this, data => { 
                location.href = "/admin/index.html";
            });
        },
        settingRemove() {
            OpsService.settingRemove(this.name, this, data => { 
                location.href = "/admin/index.html";
            });
        },
        getSettings() {
            OpsService.getSettings(this.setting, this);
        }
    },
    mounted() {
        this.language = this.text.language;  
        OpsService.getLanguages(this);
        this.getSettings(); 
    }
}
