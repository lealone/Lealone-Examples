var LealoneRpcClient = (function() {
var L = {};
L.call = function(object, apiName) {
    if(!L.sockjs) {
        L.services = {};
        initSockJS(object.sockjsUrl);
    }
    var serviceName = object.serviceName + "." + apiName;
    // 格式: type;serviceName;[arg1,arg2,...argn]
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
            if(arguments[j].onServiceException) {
                if(!L.services[serviceName]) {
                    L.services[serviceName] = function() {};
                }
                L.services[serviceName]["onServiceException"] = arguments[j].onServiceException;
                L.services[serviceName]["serviceObject"] = arguments[j];
                continue;
            }
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

// TODO 是否要支持手工传递参数
L.old_call4 = function(serviceContext, service, apiName, arguments) {
    if(!L.sockjs) {
        L.services = {};
        initSockJS(service.sockjsUrl);
    }
    var serviceName = service.serviceName + "." + apiName;
    // 格式: type;serviceName;[arg1,arg2,...argn]
    var msg = "1;" + serviceName;
    var length = arguments.length;
    if(typeof arguments[length - 1] == 'function') {
        L.services[serviceName] = function() {};
        L.services[serviceName]["callback"] = arguments[length - 1];
        length--;
    }
    
    if(length == 1 && arguments[0].services) {
        if(!L.services[serviceName]) {
            L.services[serviceName] = function() {};
        }
        L.services[serviceName]["onServiceException"] = arguments[0].onServiceException;
        L.services[serviceName]["serviceObject"] = arguments[0];
        length--;
    }

    if(length > 0) {
        msg += ";[";
        for(var j = 0; j < length; j++) {
            if(arguments[j].onServiceException || arguments[j].services) {
                if(!L.services[serviceName]) {
                    L.services[serviceName] = function() {};
                }
                L.services[serviceName]["onServiceException"] = arguments[j].onServiceException;
                L.services[serviceName]["serviceObject"] = arguments[j];
                continue;
            }
            if(j != 0) {
                msg += ",";
            }
            msg += JSON.stringify(arguments[j]);
        }
        msg += "]";
    } else {
        var names = service.methodInfo[apiName];
        length = names.length;
        if(length > 0) {
            msg += ";[";
            for(var j = 0; j < length; j++) { 
                if(j != 0) {
                    msg += ",";
                }
                msg += JSON.stringify(serviceContext[names[j]]);
            }
            msg += "]";
        }
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

L.call4 = function(serviceContext, service, apiName, arguments) {
    if(!L.sockjs) {
        L.services = {};
        initSockJS(service.sockjsUrl);
    }
    var serviceName = service.serviceName + "." + apiName;
    L.services[serviceName] = function() {};
    L.services[serviceName]["onServiceException"] = serviceContext.onServiceException;
    L.services[serviceName]["serviceObject"] = serviceContext;
    
    // 格式: type;serviceName;[arg1,arg2,...argn]
    var msg = "1;" + serviceName;
    var length = arguments.length;
    if(typeof arguments[length - 1] == 'function') {
        L.services[serviceName]["callback"] = arguments[length - 1];
        length--;
    } 
    var names = service.methodInfo[apiName];
    length = names.length;
    if(length > 0) {
        msg += ";[";
        for(var j = 0; j < length; j++) { 
            if(j != 0) {
                msg += ",";
            }
            msg += JSON.stringify(serviceContext[names[j]]);
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

var services = [];
var serviceNames = [];

L.getService = function(serviceName) {
    if(services[serviceName] != undefined)
        return services[serviceName];
    var object = {
        sockjsUrl: L.sockjsUrl,
        serviceName: serviceName
    }
    serviceNames.push(serviceName);
    services[serviceName] = proxyObject(object, missingMethod);
    return services[serviceName];
};

L.callService = function(serviceContext, serviceName, methodName, methodArgs) {
    var service = services[serviceName];
    L.call4(serviceContext, service, methodName, methodArgs);
};

L.loadServices = function(callback) {
    var object = {
        sockjsUrl: L.sockjsUrl,
        serviceName: "system_service"
    }
    var systemService = proxyObject(object, missingMethod);
    systemService.loadServices(serviceNames.join(","), services => {
        for(var i = 0; i < services.length; i++) {
            var serviceInfo = services[i];
            var service = L.getService(serviceInfo.serviceName);
            var parameterNames = [];
            service.methodInfo = {};
            var funBody = "return {"
            for(var m = 0; m < serviceInfo.serviceMethods.length; m++) {
                var serviceMethodInfo = serviceInfo.serviceMethods[m];
                funBody += serviceMethodInfo.methodName + "(){ Lealone.callService(this, '" 
                         + serviceInfo.serviceName + "', '"+ serviceMethodInfo.methodName + "', arguments)},";
                
                service.methodInfo[serviceMethodInfo.methodName] = serviceMethodInfo.parameterNames;
                for(var p = 0; p < serviceMethodInfo.parameterNames.length; p++) {
                    parameterNames.push(serviceMethodInfo.parameterNames[p]);
                }
            }
            funBody += " }";
            var fun = new Function(funBody);
            service.parameterNames = parameterNames;
            service.methods = fun();
            for(var m in service.methods) {
                service[m] = service.methods[m];
            }
            // console.log(parameterNames);
        }
        if(callback != undefined)
            callback(services);
    });
};

function initSockJS(sockjsUrl) {
    // var sockjs = new SockJS(sockjsUrl, {"transports":"xhr_streaming"});
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
            // 如果有回调就执行它
            if(L.services[serviceName] && L.services[serviceName]["callback"]) {
                try {
                    result = JSON.parse(result); // 尝试转换成json对象
                } catch(e) {
                }
                L.services[serviceName]["callback"](result);
            }
            else if(L.services[serviceName] && L.services[serviceName]["serviceObject"]) {
                try {
                    var serviceObject = L.services[serviceName]["serviceObject"];
                    var m = serviceName.split(".")[1];
                    var route = serviceObject.routes != undefined ? serviceObject.routes[m] : undefined;
                    // var keys = Object.keys(serviceObject);
                    try {
                        result = JSON.parse(result); // 尝试转换成json对象
                    } catch(e) {
                        if(route != undefined && route.redirect != undefined) {
                            location.href = route.redirect;
                        }
                        return; // 不是json对象就不管了
                    }
                    if(route != undefined && route.handler != undefined) {
                        route.handler(result);
                        return;
                    }
                    for(var key in result) {
                        // 找不到hasOwnProperty方法
                        // if(serviceObject.hasOwnProperty(key))
                        // if(keys.indexOf(key) >= 0) //vue3有警告
                        if(serviceObject[key] != undefined)
                            serviceObject[key] = result[key];
                    }
                    if(route != undefined && route.redirect != undefined) {
                        location.href = route.redirect;
                    }
                } catch(e) {
                    console.log(e);
                } 
            }
            break;
        case 3: // error
            var msg = "failed to call service: " + serviceName + ", backend error: " + result;
            if(L.services[serviceName] && L.services[serviceName]["onServiceException"]) 
                L.services[serviceName]["onServiceException"](msg);
            else
                console.log(msg)
            break;
        default:
            var msg = "unknown response type: " + type + ", serviceName: " + serviceName + ", data: " + e.data;
            if(L.services[serviceName] && L.services[serviceName]["onServiceException"]) 
                 L.services[serviceName]["onServiceException"](msg);
            else
                 console.log(msg)
        }
    };
    sockjs.onclose = function() {
        console.log("SockJS close");
    };
}

L.sockjsUrl = "/_lealone_sockjs_";
return {
    setSockjsUrl: function(url) { L.sockjsUrl = url },
    getService: L.getService,
    loadServices: L.loadServices,
    callService: L.callService,
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
        var services = [];
        var len = arguments.length;
        for(var i = 2; i < len; i++){
            if(arguments[i].serviceName == undefined)
                mixins.push(arguments[i]);
            else
                services.push(arguments[i]);
        }
        app.component(name, {
            data() { return { services: services } },
            mixins: mixins, 
            props: {
                // 组件实例的全局唯一ID，默认是组件名
                gid: { type: String, default: name }
            },
            template: document.getElementById(name).innerHTML,
            beforeMount() {
                var len = this.services.length;
                for(var i = 0; i < len; i++){
                    var service = this.services[i];
                    for(var m in service.methods) {
                        if(typeof service[m] == 'function') {
                            let method = service[m];
                            let that = this;
                            var fun = function() {
                                method.apply(that, arguments);
                            }
                            this[m] = fun;
                        }
                    }
                }
            }
        })
    },

    install(app, options) {
        var that = this;
        app.mixin({
            data() { return { lealone: that } },
            mounted() {
                window.lealone = this.lealone; // 这样组件在内部使用this.lealone和lealone都是一样的
            }
        });
    }
}

Lealone.setSockjsUrl = LealoneRpcClient.setSockjsUrl;
Lealone.getService = LealoneRpcClient.getService;
Lealone.loadServices = LealoneRpcClient.loadServices;
Lealone.callService = LealoneRpcClient.callService;
