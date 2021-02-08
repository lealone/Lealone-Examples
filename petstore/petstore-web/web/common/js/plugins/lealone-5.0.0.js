const LealoneRpcClient = (function() {
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

L.call4 = function(serviceContext, service, apiName, arguments) {
    if(!L.sockjs) {
        L.services = {};
        initSockJS(service.sockjsUrl);
    }
    if(service.hooks != undefined) { 
        let hook = service.hooks[apiName];
        if(hook != undefined) {
            let beforeHook = hook["before"];
            if(beforeHook != undefined) {
                let ret = beforeHook.apply(serviceContext, arguments);
                if(ret === false)
                    return
            }
        }
    } 
    var serviceName = service.serviceName + "." + apiName;
    L.services[serviceName] = function() {};
    L.services[serviceName]["onServiceException"] = serviceContext.onServiceException;
    L.services[serviceName]["serviceObject"] = serviceContext;
    L.services[serviceName]["hooks"] = service.hooks;
    
    // 格式: type;serviceName;[arg1,arg2,...argn]
    var msg = "1;" + serviceName;
    var argumentCount = arguments.length;
    if(typeof arguments[argumentCount - 1] == 'function') {
        L.services[serviceName]["callback"] = arguments[argumentCount - 1];
        argumentCount--;
    }
    // 过滤掉事件对象
    if(argumentCount > 0 && arguments[argumentCount - 1].defaultPrevented != undefined) {
        argumentCount--;
    }
    var names = service.methodInfo[apiName];
    var columnCount = names.length;
    var length = columnCount > argumentCount ? argumentCount : columnCount;
    var columnIndex = 0;
    msg += ";[";
    if(argumentCount > 0) {
        for(var i = 0; i < length; i++) { 
            if(i != 0) {
                msg += ",";
            }
            msg += JSON.stringify(arguments[i]);
            columnIndex++;
        }
    }
    if(columnIndex < columnCount) { 
        for(var i = columnIndex; i < columnCount; i++) { 
            if(i != 0) {
                msg += ",";
            }
            // 存在相应字段时才加
            if(serviceContext[names[i]] != undefined)
                msg += JSON.stringify(serviceContext[names[i]]);
        }
    }
    msg += "]";
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
        var methodName = serviceName.split(".")[1];
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
                    // var keys = Object.keys(serviceObject);
                    var isJson = true;
                    try {
                        result = JSON.parse(result); // 尝试转换成json对象
                    } catch(e) {
                        isJson = false;
                    }

                    let hooks = L.services[serviceName]["hooks"];
                    if(hooks != undefined) { 
                        let hook = hooks[methodName];
                        if(hook != undefined) {
                            let handleHook = hook["handle"];
                            //手动处理结果
                            if(handleHook != undefined) {
                                handleHook.call(serviceObject, result);
                                return;
                            }
                        }
                    }
                    //自动关联字段
                    if(isJson) {
                        var isInitMethod = serviceObject.services != undefined && serviceObject.services[1] === true;
                        for(var key in result) {
                            // 找不到hasOwnProperty方法
                            // if(serviceObject.hasOwnProperty(key))
                            // if(keys.indexOf(key) >= 0) //vue3有警告
                            if(isInitMethod) {
                                if(serviceObject.$data)
                                    serviceObject.$data[key] = result[key];
                                else
                                    serviceObject[key] = result[key];
                            }
                            else if(serviceObject[key] != undefined) {
                                serviceObject[key] = result[key];
                            }
                        }
                    }
                    if(hooks != undefined) { 
                        let hook = hooks[methodName];
                        if(hook != undefined) {
                            let afterHook = hook["after"];
                            if(afterHook != undefined) {
                                afterHook.call(serviceObject, result);
                            }
                        }
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
            else {
                let hooks = L.services[serviceName]["hooks"];
                if(hooks != undefined) { 
                    let hook = hooks[methodName];
                    if(hook != undefined) {
                        let errorHook = hook["error"];
                        if(errorHook != undefined) {
                            errorHook.call(L.services[serviceName]["serviceObject"], msg);
                            return;
                        }
                    }
                } 
                console.log(msg)
            }
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

    put(key, value) {
    },

    route(screen, page, params, methodName) {
        var state = JSON.stringify(this);
        if(params){
            this.params = params;
        }
        // 当前page没有变化，但是参数可能变了，所以手工调用
        if(this.screen == screen && this.page == page) {
            var instance = this.get(page);
            if(Array.isArray(instance.$options.mounted) && typeof instance.$options.mounted[0] == 'function')
                return instance.$options.mounted[0].apply(instance);
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
        if(this.screen == screen) {
            var instance = this.find(page);
            if(instance) {
                if(methodName)
                    return instance[methodName].apply(instance, this.params);
                else if(Array.isArray(instance.$options.mounted) && typeof instance.$options.mounted[0] == 'function')
                    return instance.$options.mounted[0].apply(instance);
            }
        }
    },

    createVueApp(screen, defaultPage) {
        this.screen = screen;
        this.page = sessionStorage.page ? sessionStorage.page : defaultPage;
        this.params = sessionStorage.params ? JSON.parse(sessionStorage.params) : {};
        sessionStorage.removeItem("page");
        sessionStorage.removeItem("params");
        var options = {
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
        };
        Vue.use(this);
        var app = {
            options: options,
            mount(appName) {
                this.options.el = appName;
                new Vue(this.options);
            },
            component(name, options) {
                Vue.component(name, options);
            },
            mixin(options) {
                Vue.mixin(options);
            }
        }
        return app;
    },

    component(app, name, method) {
        var mixins = [];
        var services = [];
        var methodName = "";
        var init = false;
        var startIndex = 2;
        if(typeof method == 'string') { //单一方法
            methodName = method;
            startIndex = 3;
        }
        else if(method != undefined && method.initMethod != undefined) { //通过配置指定方法名
            methodName = method.initMethod;
            init = true;
            startIndex = 3;
        } else {
            methodName = "*"; //默认是所有方法
        }
        services.push(methodName);
        services.push(init);

        var len = arguments.length;
        for(var i = startIndex; i < len; i++){
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
                if(this._beforeMount_) return;
                var len = this.services.length;
                for(var i = 2; i < len; i++){
                    var service = this.services[i];
                    for(var m in service.methods) {
                        if(typeof service[m] == 'function' && (this.services[0] === "*" || m == this.services[0])) {
                            let method = service[m];
                            let that = this;
                            var fun = function() {
                                method.apply(that, arguments);
                            }
                            this[m] = fun;
                            
                            var methodInfo = service.methodInfo[m];
                            for(var j = 0; j < methodInfo.length; j++){
                                if(this.$data)
                                    this.$data[methodInfo[j]] = "";
                                this[methodInfo[j]] = "";
                            }
                            if(this.$data)
                                this.$data.errorMessage = "";
                            this.errorMessage = "";
                            if(this.services[1]) {
                                this._beforeRender = function(cb) {
                                    method.call(that, cb); 
                                }
                            }
                        }
                    }
                }
                this._beforeMount_ = true;
            }
        })
    },

    install(app, options) {
        var that = this;
        app.mixin({
            data() { return { lealone: that } },
            beforeMount() {
                if(this.getComponentInstance && this.lealone.get == undefined) {
                    let that = this;
                    this.lealone.get = function() {
                       var instance = that.getComponentInstance.apply(that, arguments);
                       if(instance.external) {
                           for(var m in instance.$options.beforeMount) {
                               instance.$options.beforeMount[m].apply(instance);
                           }
                       }
                       return instance;
                    }
                    this.lealone.find = function() {
                        return that.findComponentInstance.apply(that, arguments);
                    }
                }
                window.lealone = this.lealone; // 这样组件在内部使用this.lealone和lealone都是一样的
            }
        });
    }
}

Lealone.setSockjsUrl = LealoneRpcClient.setSockjsUrl;
Lealone.getService = LealoneRpcClient.getService;
Lealone.loadServices = LealoneRpcClient.loadServices;
Lealone.callService = LealoneRpcClient.callService;
