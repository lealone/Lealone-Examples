const lealone = { 
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
        var that = this;
        var app = Vue.createApp({
            data() { return { router: that } },
            computed: {
                currentComponent() {
                    return this.router.page;
                }
            },
            mounted() {
                var that = this;
                window.addEventListener('popstate', function(evt){
                    var state = JSON.parse(evt.state);
                    if(!state) return;
                    if(that.router.screen != state.screen) {
                        sessionStorage.page = state.page;
                        sessionStorage.params = JSON.stringify(state.params);
                        location.href = "/" + state.screen + "/index.html";
                        return;
                    }
                    that.router.currentUser = state.currentUser;
                    that.router.params = state.params;
                    that.router.screen = state.screen;
                    that.router.page = state.page;
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
                router: null,
                gid: { type: String, default: name }
            }, 
            template: document.getElementById(name).innerHTML
        })
    },
    install(app, options) {
        var that = this;
        app.mixin({
            data() { return { lealone: that } },
        });
    }
}
