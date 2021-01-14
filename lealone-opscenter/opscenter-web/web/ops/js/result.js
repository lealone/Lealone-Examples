const opsResult = { 
    data() {
        return {
            result: ""
        }
    },
    mounted() {
        this.router.set(this.gid, this);
        if(this.router.screen == "ops" && this.router.page == "result") {
            this.result = this.router.params.result;
        }
    }
}
