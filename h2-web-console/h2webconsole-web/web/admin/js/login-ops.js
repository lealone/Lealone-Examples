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
            adminLoginOk: false,
            adminBack: ""
        }
    },
    methods: {
        route(page) {
            if(this.adminLoginOk) {
                lealone.route("admin", page)
            } else {
                this.adminBack = page;
                lealone.route("admin", "login-admin")
            }
        }
    },
    mounted() {
        this.language = this.text.language;  
        this.getLanguages();
        this.getSettings(); 
    }
}
const opsHooks = {
    readTranslations: {
        handle(data) {
            this.i18n = data.text;
            this.text = OpsCenter.i18n.parse(this.i18n);
        }
    },
    login: {
        handle(data) {
            localStorage.currentUser = data; 
            location.href = "/ops/index.html";
        }
    },
    settingSave: { after() { location.href = "/admin/index.html" } },
    settingRemove: { after() { location.href = "/admin/index.html" } }
}

OpsService.hooks = opsHooks;
