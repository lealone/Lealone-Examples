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
            axios.get(PetStore.CarService + '/get_items')
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
            axios.post(PetStore.CarService + '/add_item?item_id=' + itemId)
            .then(response => {
                this.itemAdded = true;
                lealone.route('store', "item-list");
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "add_item失败";
            });
        },
        removeCartItem(itemId) {
            axios.post(PetStore.CarService + '/remove_item?item_id=' + itemId)
            .then(response => {
                this.itemAdded = false;
                lealone.route('store', "view-cart");
                this.getCartItemList();
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "remove_item失败";
            });
        },
        updateCar() {
            axios.post(PetStore.CarService + '/update')
            .then(response => {
                this.itemAdded = false;
                lealone.route('store', "view-cart");
            })
            .catch(error => {
                console.log(error);
                this.errorMessage = "updateCar失败";
            });
        }
    },
    mounted() {
        if(lealone.page == "view-cart") {
            this.getCartItemList();
        }
    }
}