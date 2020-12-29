const storeLtemlist = { 
    data() {
        return {
            category: {},
            product: {},
            items: {},
			storeLtemlistErrorMessage: ""
        }
    },
    methods: {
        getItemList(productId, pageType) {
			var that = this;
            axios.get(Public.StoreService + '/get_all_product_items?product_id=' + productId)
			.then(function (response) {
				if(response.data) {
					if(response.data.category)
			            that.category = response.data.category;
					if(response.data.product)
					    that.product = response.data.product;
					if(response.data.items)
					    that.items = response.data.items;
				}
				that.setPage('store', pageType);
			})
			.catch(function (error) {
				console.log(error);
				that.storeLtemlistErrorMessage = "ItemList获取失败";
			});
		}
    }
}