const storeCategoryList = {
    data() {
        return {
            categories: []
        }
    },
    mounted() {
        axios.get(PetStore.StoreService + '/get_all_categories')
        .then(response => (this.categories = response.data))
    }
}
