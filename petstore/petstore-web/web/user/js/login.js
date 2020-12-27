 const userLogin = { 
    data() {
        return {
            user: {user_id: "", password: ""}
        }
    },
    methods: {
        login() {
            axios.post(Public.UserService + '/login', this.user)
			.then(function (response) {
				location.href = "/";
			})
			.catch(function (error) {
				console.log(error);
			});
		}
    }
}
