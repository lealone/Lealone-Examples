const admin = { 
    data() {
        return {
        	allowOthers: 'true',
        	ssl: 'false',
        	port: "",
        	sessions: [],
        }
    },
    mounted() {
        this.admin();
    }
}
