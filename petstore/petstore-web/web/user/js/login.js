	const userLogin = {
	  data() {
		return {
		  name: "",
		  password: ""
		}
	  },
	  methods: {
		login() {
		  axios
		  .post(Public.UserService + '/login?password=' + this.password + '&user_id=' + this.name, {
		  name: this.name,
		  password: this.password
		}) //this.$data
		  .then(response => (this.categories = response.data))
		}
	  }
	}
