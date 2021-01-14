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
            var that = this;
            axios.get(PetStore.StoreService + '/get_all_product_items?product_id=' + productId)
            .then(function (response) {
                if(response.data) {
                    if(response.data.category)
                        that.category = response.data.category;
                    if(response.data.product)
                        that.product = response.data.product;
                    if(response.data.items)
                        that.items = response.data.items;
                }
            })
            .catch(function (error) {
                console.log(error);
                that.errorMessage = "ItemList获取失败";
            });
        }
    },
    mounted() {
        if(this.lealone.page == "item-list" || this.lealone.page == "edit-item-list") {
            var productId = this.lealone.params.productId;
            if(productId)
                this.getItemList(productId);
        }
    }
}