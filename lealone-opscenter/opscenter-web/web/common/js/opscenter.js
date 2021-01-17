var OpsCenter = {
    Host: '',
}

OpsCenter.OpsService = OpsCenter.Host + '/service/ops_service';
OpsCenter.AdminService = OpsCenter.Host + '/service/admin_service';
OpsCenter.QueryService = OpsCenter.Host + '/service/query_service';

OpsService = Lealone.getService("ops_service");
AdminService = Lealone.getService("admin_service");
QueryService = Lealone.getService("query_service");


OpsCenter.i18n = { 
    data() { return { text: {} } },
    
    // 这个方法会自动调用多次，注册了多少个组件就调用多少次，
    // 如果想避免多次异步请求，虽然弄个标志字段能实现，
    // 但是注册到vue的其他组件拿到的text是空的。
    created() {
    },
    install(app, options) {
        app.mixin(this);
    },
    loadAndMount(app, appName) {
        axios.get(OpsCenter.OpsService + '/read_translations')
        .then(response => {
            var text = response.data.text;
            var newText = OpsCenter.i18n.parse(text);
            app.mixin({
                data() { return { txt: text,  text: newText } },
            });
            Lealone.loadServices(services => {
                // console.log(services);
                app.mount(appName)
            });
        });
    },
    parse(text) {
        var newText = {};
        // 把点号分隔的key变成嵌套对象，然后在膜拜引擎中像这样使用: {{ text.a.b.c }}
        for(var key in text) { 
            var a = key.split('.');
            if(a.length < 2) {
                newText[key] = text[key];
                continue;
            }
            var len = a.length - 1;
            a.reduce((o, k, i) => {
                if(i == len)
                    o[k] = text[key];
                else {
                    if (!o[k]) o[k] = {};
                    else if(o[k] instanceof Object) {
                        return o[k]; 
                    } else {
                        // var oldValue = o[k];
                        // o[k] = {};
                        // o[k]["$text$"] = oldValue;
                    }
                    return o[k];
                }
            }, newText);
        }
        // 支持两种访问方式:
        // {{ txt['toolbar.autoSelect.on'] }}
        // {{ text.toolbar.autoSelect }}
        // 因为vue不能像这样使用{{ text.toolbar.autoSelect.on }}
        return newText;
    }
}
var mount = function(app, appName) {
    // 混入全局的logout方法
    app.mixin({
        methods: {
            logout() {
                AdminService.logout(_=>(location.href = "/admin/index.html"));
            }
        }
    });
    OpsCenter.i18n.loadAndMount(app, appName);
}

