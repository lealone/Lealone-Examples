const admin = { 
    data() {
        return {
        	allowOthers: 'true',
        	ssl: 'false',
        	port: "",
        	sessions: [],
        }
    },
    methods: {
    	save() {
            var that = this;
            axios.post(OpsCenter.AdminService + '/save', 
            		  { port: this.port, ssl: this.ssl, allow_others: this.allowOthers })
            .then(function (response) { 
                //location.href = "/admin/index.html";
            })
            .catch(function (error) {
                console.log("配置保存失败: " + error);
            });
        },
        startTranslate() {
        	axios.get(OpsCenter.AdminService + '/start_translate');
        },
        shutdown() {
        	axios.get(OpsCenter.AdminService + '/shutdown');
        }
    },
    mounted() {
        var that = this;
        axios.get(OpsCenter.AdminService + '/admin')
        .then(response => {
            that.allowOthers = response.data.allowOthers;
            that.ssl = response.data.ssl;
            that.port = response.data.port;
            that.sessions = response.data.sessions;
        });
    }
}
