const itemlist = { 
    data() { return { itemAdded: false } },
    methods: {
        addCartItem(itemId) {
            var cart = lealone.get('view-cart');
            cart.addItem(cart.cartId, itemId, data=>{
                this.itemAdded = true;
            });
        }
    }
}