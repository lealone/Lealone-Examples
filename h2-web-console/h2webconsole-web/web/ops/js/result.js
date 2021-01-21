const opsResult = { 
    data() {
        return {
            result: ""
        }
    },
    mounted() {
        lealone.put(this.gid, this);
        if(lealone.screen == "ops" && lealone.page == this.gid) {
            this.result = lealone.params.result;
        }
    }
}
