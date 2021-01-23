var PetStore = {
    Host: '',
};
PetStore.UserService = PetStore.Host + '/service/user_service';
PetStore.StoreService = PetStore.Host + '/service/store_service';
PetStore.CarService = PetStore.Host + '/service/car_service';

UserService = Lealone.getService("user_service");


var mount = function(app, appName) {
    // 混入全局的logout方法
    app.mixin({
        methods: {
            logout() {
                localStorage.removeItem("currentUser");
                axios.get("/user/logout")
                .then(response => {
                    location.href = "/";
                });
            }
        }
    });
    Lealone.loadServices(services => {
        // console.log(services);
        app.mount(appName)
    });
}