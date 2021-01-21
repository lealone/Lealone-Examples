﻿const opsResult = { 
    data() {
        return {
            result: ""
        }
    },
    mounted() {
        var lealone = this.lealone;
        lealone.put(this.gid, this);
        if(lealone.screen == "ops" && lealone.page == this.gid) {
            this.result = lealone.params.result;
        }
    }
}
