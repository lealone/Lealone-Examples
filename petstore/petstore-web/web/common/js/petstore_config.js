var Public = {
    Host: '',
};
Public.UserService = Public.Host + '/service/user_service';
Public.StoreService = Public.Host + '/service/store_service';
Public.CarService = Public.Host + '/service/car_service';


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

const router = { 
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
