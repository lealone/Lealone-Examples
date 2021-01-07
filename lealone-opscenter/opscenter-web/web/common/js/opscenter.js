var OpsCenter = {
    Host: '',
};
OpsCenter.OpsService = OpsCenter.Host + '/service/ops_service';
OpsCenter.AdminService = OpsCenter.Host + '/service/admin_service';


const Router = { 
    currentUser: localStorage.currentUser,
    screen: "user" ,
    page: "login", 
    params: [],

    setPage(screen, page) {
        var state = JSON.stringify(this);
        var len = arguments.length;
        for(var i = 2; i < len; i++){
            this.params.push(arguments[i]);
        }

        if(this.screen != screen) {
            this.screen = screen;
            this.page = page;
            sessionStorage.page = page;
            sessionStorage.params = this.params.join();
            location.href = "/" + screen + "/index.html";
            return;
        }
        //加两次，不然popstate有可能返回null，原因不明
        window.history.pushState(state, page, "/" + this.screen + "/index.html");
        this.page = page;
        state = JSON.stringify(this);
        window.history.pushState(state, page, "/" + this.screen + "/index.html");
    }
}


function createVueApp(screen, defaultPage) {
    Router.screen = screen;
    Router.page = sessionStorage.page ? sessionStorage.page : defaultPage;
    Router.params = sessionStorage.params ? sessionStorage.params.split(",") : [];
    sessionStorage.removeItem("page");
    sessionStorage.removeItem("params");
    return Vue.createApp({
        data() { return { router: Router } },
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
                    sessionStorage.params = state.params.join();
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
}


function injectComponent(app, name) {
    var mixins = [];
    var len = arguments.length;
    for(var i=2; i<len; i++){
        mixins.push(arguments[i]);
    }
    app.component(name, {
        mixins: mixins, 
        props: ['router'],
        template: document.getElementById(name).innerHTML
    })
}
