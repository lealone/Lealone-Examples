const opsHeader = { 
    data() {
        return {
            autoCommit: true,
            rowcount: 1000,
            autoComplete: 0,
            autoSelect: 1,
        }
    },
    methods: {
        _query(sql) {
            QueryService.query(lealone.currentUser, sql, data=> {
                if(data.type == "result-table")
                    lealone.route('ops', 'result-table', {result: data});
                else
                    lealone.route('ops', 'result', {result: data});
            })
        },
        setAutoCommit() {
            this._query('@autocommit_' + this.autoCommit + '.');
        },
        setMaxRows() {
            this._query('@maxrows' + this.rowcount + '.');
        },
        refreshTables() {
            lealone.get("tables").readTables();
        },
        commit() {
            this._query('COMMIT');
        },
        rollback() {
            this._query('ROLLBACK');
        },
        run() {
            this._query(lealone.get("query").sql);
        },
        runSelected() {
            lealone.route('ops', 'result', {result: "runSelected sql=" + lealone.get("query").sql});
        },
        cancel() {
            lealone.route('ops', 'result', {result: "sql=@cancel: TODO"});
        },
        history() {
            this._query('@history.'); 
        },
        setAutoComplete() {
            lealone.get('query').autoComplete = this.autoComplete;
        },
        setAutoSelect() {
            lealone.get('query').autoSelect = this.autoSelect;
        }
    },
    mounted() {
        lealone.put(this.gid, this);
    }
}
