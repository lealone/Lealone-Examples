const opsHeader = { 
    data() {
        return {
            autoCommit: true,
            rowcount: 1000
        }
    },
    methods: {
        _query(sql) {
            axios.post(OpsCenter.QueryService + '/query', { sql: sql, jsessionid: lealone.currentUser })
            .then(function (response) {
                if(response.data.type == "result-table")
                    lealone.route('ops', 'result-table', {result: response.data});
                else
                    lealone.route('ops', 'result', {result: response.data});
            })
        },
        setAutoCommit() {
            this._query('@autocommit_' + this.autoCommit + '.');
        },
        setMaxRows() {
            this._query('@maxrows' + this.rowcount + '.');
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
            this._query(this.lealone.get("query").sql);
            // this.lealone.route('ops', 'result', {result: "run sql=" + this.lealone.get("query").sql});
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
