	const CategoryList = {
	  data() {
		return {
		  categories: []
		}
	  },
	  mounted() {
		axios
		  .get(Public.StoreService + '/get_all_categories')
		  .then(response => (this.categories = response.data))
	  }
	}
	Vue.createApp(CategoryList).mount('#category-list')