const tools = { 
    data() {
        return {
            toolName: "",
            args: "",
            toolResult: "",
        }
    },
    methods: {
        show(toolName) {
            return this.toolName === toolName;
        },
        go(toolName) {
            this.toolName = toolName;
            this.update();
        },
        quote(x) {
            var q = '';
            for (var i=0; i<x.length; i++) {
                var c = x.charAt(i);
                if (c == '"' || c == '\\' || c == ',') {
                    q += '\\';
                }
                q += c;
            }
            return q;
        },
        update() {
            var line = '', args = '';
            for (var i = 0; i < 9; i++) {
                var f = document.getElementById('option' + this.toolName + '.' + i);
                if (f != null && f.value.length > 0) {
                    var x = this.quote(f.value);
                    if (f.type == 'password') {
                        x = '';
                        for (var j = 0; j < f.value.length; j++) {
                            x += '*';
                        }
                    }
                    line += ' -' + f.name + ' "' + x + '"';
                    if (args.length > 0) {
                        args += ',';
                    }
                    args += '-' + f.name + ',' + this.quote(f.value);
                }
            }
            this.args = args;
        }
    }
}

