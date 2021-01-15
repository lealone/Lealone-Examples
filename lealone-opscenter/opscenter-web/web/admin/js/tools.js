var current = '';
function go(name) {
    if (name == current) {
        return;
    }
    var tools = document.getElementsByTagName('div');
    for (i = 0; i < tools.length; i++) {
        var div = tools[i];
        if (div.id.substring(0, 4) == 'tool') {
            div.style.display = (div.id == 'tool' + name) ? '' : 'none';
        }
    }
    document.getElementById('commandLine').style.display='';
    document.getElementById('toolName').innerHTML = name;
    document.getElementById('tool').value = name;
    document.getElementById('result').style.display = 'none';
    current = name;
    update();
}
function quote(x) {
    var q = '';
    for (var i=0; i<x.length; i++) {
        var c = x.charAt(i);
        if (c == '"' || c == '\\' || c == ',') {
            q += '\\';
        }
        q += c;
    }
    return q;
}
function update() {
    var line = '', args = '';
    for (var i = 0; i < 9; i++) {
        var f = document.getElementById('option' + current + '.' + i);
        if (f != null && f.value.length > 0) {
            var x = quote(f.value);
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
            args += '-' + f.name + ',' + quote(f.value);
        }
    }
    document.getElementById('toolOptions').innerHTML = line;
    document.getElementById('args').value = args;
}

const tools = { 
    data() {
        return {
			tool: "",
		    toolResult: ""
        }
    },
    methods: {
    },
    mounted() {
        var t = this.tool;
        if (t != '') {
            go(t);
            document.getElementById('result').style.display = '';
        }
    }
}
