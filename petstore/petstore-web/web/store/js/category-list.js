const storeCategoryList = {
    data() {
        return {
            categories: []
        }
    },
    mounted() {
        this.getAllCategories();
    }
}
