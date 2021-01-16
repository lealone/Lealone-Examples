var LealoneRpcClient = (function() {
var L = {};
L.call = function(object, apiName) {
    if(!L.sockjs) {
        L.services = {};
        initSockJS(object.sockjsUrl);
    }
    var serviceName = object.serviceName + "." + apiName;
    //格式: type;serviceName;[arg1,arg2,...argn]
    var msg = "1;" + serviceName;
    var length = arguments.length;
    if(typeof arguments[length - 1] == 'function') {
        L.services[serviceName] = function() {};
        L.services[serviceName]["callback"] = arguments[length - 1];
        length--;
    }
    if(length > 2) {
        msg += ";[";
        for(var j = 2; j < length; j++) {
            if(j != 2) {
                msg += ",";
            }
            msg += JSON.stringify(arguments[j]);
        }
        msg += "]";
    }
    if(L.sockjsReady)
        L.sockjs.send(msg);
    else {
        if(!L.penddingMsgs) {
            L.penddingMsgs = [];
        } 
        L.penddingMsgs.push(msg);
    }
};

var proxyObject = function (object, missingMethod) {
      const proxyObject = new Proxy(object, {
        get(object, property) {
          if (Reflect.has(object, property)) {
            return Reflect.get(object, property);
          } else {
            return (...args) => Reflect.apply(missingMethod, proxyObject, [object, property, ...args]);
          }
        }
      });
      return proxyObject;
};

var missingMethod  = function(object, method, ...args) {
    L.call(object, method, ...args);
};

L.getService = function(serviceName) {
    var object = {
        sockjsUrl: L.sockjsUrl,
        serviceName: serviceName
    }
    return proxyObject(object, missingMethod);
};

function initSockJS(sockjsUrl) {
    //var sockjs = new SockJS(sockjsUrl, {"transports":"xhr_streaming"});
    var sockjs = new SockJS(sockjsUrl);
    L.sockjs = sockjs;
    sockjs.onopen = function() {
        L.sockjsReady = true; 
        if(L.penddingMsgs) {
            for(var i = 0; i < L.penddingMsgs.length; i++) {
                sockjs.send(L.penddingMsgs[i]);
            }
            L.penddingMsgs = [];
        }
    };
    sockjs.onmessage = function(e) {
        var a = JSON.parse(e.data);
        var type = a[0];
        var serviceName = a[1]; 
        var result = a[2];
        switch(type) {
        case 2: // 正常返回
            //如果有回调就执行它
            if(L.services[serviceName] && L.services[serviceName]["callback"]) {
                try {
                    result = JSON.parse(result); //尝试转换成json对象
                } catch(e) {
                }
                L.services[serviceName]["callback"](result);
            }
            break;
        case 3: // error info
            console.log("failed to call service: " + serviceName + ", backend error: " + result)
            break;
        default:
            console.log("unknown response type: " + type + ", serviceName: " + serviceName + ", data: " + e.data)
        }
    };
    sockjs.onclose = function() {
        console.log("SockJS close");
    };
}

L.sockjsUrl = "/_lealone_sockjs_";
return {
    setSockjsUrl: function(url) { L.sockjsUrl = url },
    getService: L.getService
};
})();


const Lealone = { 
    currentUser: localStorage.currentUser,
    screen: "" ,
    page: "", 
    params: {},
    components: [],

    set(key, value) {
        this.components[key] = value;
    },

    get(key) {
        return this.components[key];
    },

    route(screen, page, params) {
        var state = JSON.stringify(this);
        if(params){
            this.params = params;
        }
        // 当前page没有变化，但是参数可能变了，所以手工调用
        if(this.screen == screen && this.page == page) {
            if(this.components[page])
                this.components[page].$options.mounted.call(this.components[page]);
            return;
        }
        if(this.screen != screen) {
            this.screen = screen;
            this.page = page;
            sessionStorage.page = page;
            sessionStorage.params = JSON.stringify(this.params);
            location.href = "/" + screen + "/index.html";
            return;
        }
        // 加两次，不然popstate有可能返回null，原因不明
        // window.history.pushState(state, page, "/" + this.screen + "/" + page);
        window.history.pushState(state, page, null);
        this.page = page;
        state = JSON.stringify(this);
        // window.history.pushState(state, page, "/" + this.screen + "/" + page);
        window.history.pushState(state, page, null);
    },

    createVueApp(screen, defaultPage) {
        this.screen = screen;
        this.page = sessionStorage.page ? sessionStorage.page : defaultPage;
        this.params = sessionStorage.params ? JSON.parse(sessionStorage.params) : {};
        sessionStorage.removeItem("page");
        sessionStorage.removeItem("params");
        var app = Vue.createApp({
            data() { return { } },
            computed: {
                currentComponent() {
                    return this.lealone.page;
                }
            },
            mounted() {
                var that = this;
                window.addEventListener('popstate', function(evt){
                    var state = JSON.parse(evt.state);
                    if(!state) return;
                    if(that.lealone.screen != state.screen) {
                        sessionStorage.page = state.page;
                        sessionStorage.params = JSON.stringify(state.params);
                        location.href = "/" + state.screen + "/index.html";
                        return;
                    }
                    that.lealone.currentUser = state.currentUser;
                    that.lealone.params = state.params;
                    that.lealone.screen = state.screen;
                    that.lealone.page = state.page;
                }, false);
            }
        });
        app.use(this);
        return app;
    },

    component(app, name) {
        var mixins = [];
        var len = arguments.length;
        for(var i = 2; i < len; i++){
            mixins.push(arguments[i]);
        }
        app.component(name, {
            mixins: mixins, 
            props: {
                // 组件实例的全局唯一ID，默认是组件名
                gid: { type: String, default: name }
            }, 
            template: document.getElementById(name).innerHTML
        })
    },

    install(app, options) {
        var that = this;
        app.mixin({
            data() { return { lealone: that } },
            mounted() {
                window.lealone = this.lealone; //这样组件在内部使用this.lealone和lealone都是一样的
            }
        });
    }
}

Lealone.setSockjsUrl = LealoneRpcClient.setSockjsUrl;
Lealone.getService = LealoneRpcClient.getService;
