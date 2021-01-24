const storeViewCart = { 
    data() {
        return {
            items: [],
            itemAdded: false,
            errorMessage: ""
        }
    },
    methods: {
        getCartItemList() {
            axios.get(PetStore.ViewCartService + '/get_items')
            .then(response => {
                if(response.data) {
                    if(response.data.items)
                        this.items = response.data.items;
                }
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "ItemList获取失败";
            });
        },
        addCartItem(itemId) {
            axios.post(PetStore.ViewCartService + '/add_item?item_id=' + itemId)
            .then(response => {
                this.itemAdded = true;
                lealone.route('home', "item-list");
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "add_item失败";
            });
        },
        removeCartItem(itemId) {
            axios.post(PetStore.ViewCartService + '/remove_item?item_id=' + itemId)
            .then(response => {
                this.itemAdded = false;
                lealone.route('home', "view-cart");
                this.getCartItemList();
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "remove_item失败";
            });
        },
        updateCart() {
            axios.post(PetStore.ViewCartService + '/update')
            .then(response => {
                this.itemAdded = false;
                lealone.route('home', "view-cart");
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "updateCart失败";
            });
        }
    },
    mounted() {
        if(lealone.page == "view-cart") {
            this.getCartItemList();
        }
    }
}