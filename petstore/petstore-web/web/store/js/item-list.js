const storeItemlist = { 
    data() {
        return {
            category: {},
            product: {},
            items: {},
            itemAdded: false,
            errorMessage: ""
        }
    },
    mounted() {
        lealone.put(this.gid, this);
        if(lealone.page == "item-list") {
            this.itemAdded = lealone.params.itemAdded;
            var productId = lealone.params.productId;
            if(productId) {
                this.getAllProductItems(productId);
            }
        }
    }
}