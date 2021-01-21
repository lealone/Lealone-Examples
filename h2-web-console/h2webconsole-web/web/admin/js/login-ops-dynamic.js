﻿const loginOps = { 
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

            routes: {
                readTranslations: { handler: data=>{
                    this.i18n = data.text;
                    this.text = OpsCenter.i18n.parse(this.i18n);
                }},
                login: {  handler: data=>{
                    localStorage.currentUser = data; 
                    location.href = "/ops/index.html";
                }},
                settingSave: { redirect: "/admin/index.html" },
                settingRemove: { redirect: "/admin/index.html" }
            },
        }
    },

    mounted() {
        this.language = this.text.language;  
        this.getLanguages();
        this.getSettings(); 
    }
}
