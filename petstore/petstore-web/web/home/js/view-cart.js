const viewCart = { 
    data() {
        return {
            items: [],
            errorMessage: "",
            cartId: localStorage.cartId ? localStorage.cartId : "test" // TODO
        }
    },
    mounted() {
        this.getItems();
    }
}
const viewCartHooks = {
    removeItem: {
        after() {
            this.getItems();
        }
    }
}

ViewCartService.hooks = viewCartHooks;
