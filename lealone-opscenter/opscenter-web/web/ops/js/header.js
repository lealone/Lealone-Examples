const opsHeader = { 
    data() {
        return {
            autoCommit: true
        }
    },
    methods: {
        setAutoCommit() {
            var result = "Auto commit is now " + (this.autoCommit ? "ON" : "OFF");
            this.lealone.route('ops', 'result', {result: result});
        },
        logout() {
            localStorage.removeItem("currentUser");
            axios.get(OpsCenter.OpsService + '/logout')
            .then(function (response) {
                location.href = "/admin/index.html";
            });
        },
        refreshTables() {
            this.lealone.route('ops', 'result', {result: "refreshTables: TODO"});
        },
        commit() {
            this.lealone.route('ops', 'result', {result: "sql=COMMIT: TODO"});
        },
        rollback() {
            this.lealone.route('ops', 'result', {result: "sql=ROLLBACK: TODO"});
        },
        run() {
            this.lealone.route('ops', 'result', {result: "run sql=" + this.lealone.get("query").sql});
        },
        runSelected() {
            this.lealone.route('ops', 'result', {result: "runSelected sql=" + this.lealone.get("query").sql});
        },
        cancel() {
            this.lealone.route('ops', 'result', {result: "sql=@cancel: TODO"});
        },
        history() {
            this.lealone.route('ops', 'result', {result: "sql=@history: TODO"});
        }
    },
    mounted() {
        this.lealone.set(this.gid, this);
    }
}
