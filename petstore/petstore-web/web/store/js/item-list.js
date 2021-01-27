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
    methods: {
        addCartItem(itemId) {
            var cart = lealone.get('view-cart');
            cart.addItem(cart.cartId, itemId, data=>{
                this.itemAdded = true;
            });
        }
    },
    mounted() {
        var productId = lealone.params[0];
        if(productId) {
            this.getAllProductItems(productId);
        }
    }
}