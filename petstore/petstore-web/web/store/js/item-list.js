const storeItemlist = { 
    data() {
        return {
            category: {},
            product: {},
            items: {},
            errorMessage: ""
        }
    },
    methods: {
        getItemList(productId) {
            axios.get(PetStore.StoreService + '/get_all_product_items?product_id=' + productId)
            .then(response => {
                if(response.data) {
                    if(response.data.category)
                        this.category = response.data.category;
                    if(response.data.product)
                        this.product = response.data.product;
                    if(response.data.items)
                        this.items = response.data.items;
                }
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "ItemList获取失败";
            });
        }
    },
    mounted() {
        if(lealone.page == "item-list" || lealone.page == "edit-item-list") {
            var productId = lealone.params.productId;
            if(productId)
                this.getItemList(productId);
        }
    }
}