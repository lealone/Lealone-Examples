const opsResult = { 
    data() {
        return {
            result: ""
        }
    },
    mounted() {
        this.router.components["result"] = this;
        if(this.router.screen == "ops" && this.router.page == "result") {
            this.result = this.router.params.pop();
        }
    }
}
