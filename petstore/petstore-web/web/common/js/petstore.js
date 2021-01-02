var PetStore = {
    Host: '',
};
PetStore.UserService = PetStore.Host + '/service/user_service';
PetStore.StoreService = PetStore.Host + '/service/store_service';
PetStore.CarService = PetStore.Host + '/service/car_service';


const Router = { 
    currentUser: localStorage.currentUser,
    screen: "user" ,
    page: "login", 
	params: [],

    setPage(screen, page) {
		var len = arguments.length;
        for(var i=2; i<len; i++){
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
		this.page = page;
    },

    display(page) {
        return this.page == page;
    } 
}


function createVueApp(screen, defaultPage) {
    Router.screen = screen;
    Router.page = sessionStorage.page ? sessionStorage.page : defaultPage; 
    Router.params = sessionStorage.params ? sessionStorage.params.split(",") : [];
    return Vue.createApp({ data() { return { router: Router } } }); 
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

