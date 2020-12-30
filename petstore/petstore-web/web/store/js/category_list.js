const storeCategoryList = {
    data() {
        return {
            categories: []
        }
    },
    mounted() {
        axios.get(Public.StoreService + '/get_all_categories')
        .then(response => (this.categories = response.data))
    }
}
