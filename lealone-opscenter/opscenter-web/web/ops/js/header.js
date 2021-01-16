const opsHeader = { 
    data() {
        return {
            autoCommit: true
        }
    },
    methods: {
        setAutoCommit() {
            var result = "Auto commit is now " + (this.autoCommit ? "ON" : "OFF");
            var that = this;
            var sql = "";
            axios.post(OpsCenter.QueryService + '/query', { sql: sql })
            .then(function (response) {
            	lealone.route('ops', 'result', {result: result});
            })
        },
        refreshTables() {
            lealone.route('ops', 'result', {result: "refreshTables: TODO"});
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
