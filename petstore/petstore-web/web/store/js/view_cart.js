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
			var that = this;
            axios.get(Public.CarService + '/get_items')
			.then(function (response) {
				if(response.data) {
					if(response.data.items)
					    that.items = response.data.items;
				}
				that.router.setPage('store', 'view_cart');
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "ItemList获取失败";
			});
		},
		addCartItem(itemId) {
			var that = this;
            axios.post(Public.CarService + '/add_item?item_id=' + itemId)
			.then(function (response) {
				that.itemAdded = true;
				that.router.setPage('store', "item_list");
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "add_item失败";
			});
		},
		removeCartItem(itemId) {
			var that = this;
            axios.post(Public.CarService + '/remove_item?item_id=' + itemId)
			.then(function (response) {
				that.itemAdded = false;
				that.router.setPage('store', "view_cart");
				that.getCartItemList();
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "remove_item失败";
			});
		},
		updateCar() {
			var that = this;
            axios.post(Public.CarService + '/update')
			.then(function (response) {
				that.itemAdded = false;
				that.router.setPage('store', "view_cart");
				//that.getCartItemList();
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "updateCar失败";
			});
		}
    },
	mounted() {
		 this.getCartItemList();
	}
}