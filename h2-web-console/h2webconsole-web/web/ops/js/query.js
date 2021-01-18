const opsQuery = { 
    data() {
        return {
			sql: "",
        }
    },
    methods: {
        run() {
            this.lealone.route('ops', 'result', {result: "run sql=" + this.sql});
        },
        runSelected() {
            this.lealone.route('ops', 'result', {result: "runSelected sql=" + this.sql});
        },
        manualAutoComplete() {
            this.lealone.route('ops', 'result', {result: "manualAutoComplete sql=" + this.sql});
        },
    },
    mounted() {
        this.lealone.set(this.gid, this);
    }
}
