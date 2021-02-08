const viewCart = { 
    data() {
        return {
            cartId: localStorage.cartId ? localStorage.cartId : "test" // TODO
        }
    }
}
const viewCartHooks = {
    removeItem: { after() { this.getItems() } }
}

ViewCartService.hooks = viewCartHooks;
