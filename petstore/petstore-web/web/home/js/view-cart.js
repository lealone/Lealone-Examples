const viewCart = { 
    data() {
        return {
            items: [],
            errorMessage: "",
            cartId: localStorage.cartId ? localStorage.cartId : "test" // TODO
        }
    },
    mounted() {
        lealone.put(this.gid, this);
        if(lealone.page == "view-cart") {
            if(lealone.params.method == 'addItem') {
                this.addItem(this.cartId, lealone.params.itemId);
            } else {
                this.getItems();
            }
        }
    }
}
const viewCartHooks = {
    update: {
        after() {
            this.getItems();
        }
    },
    removeItem: {
        after() {
            this.getItems();
        }
    },
    addItem: {
        after(data) {
            lealone.route('home', "item-list", {itemAdded:true});
        }
    }
}

ViewCartService.hooks = viewCartHooks;
