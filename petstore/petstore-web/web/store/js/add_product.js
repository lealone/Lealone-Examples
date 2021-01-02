 const storeAddProduct = { 
    data() {
        return {
            product: {categoryid: "", productid: "", name: "", descn: ""},
			file: "",
			errorMessage: ""
        }
    },
    methods: {
		getFile(event) {
            this.file = event.target.files[0];
		},
        addProduct() {
			if(!this.product.productid) {
				this.errorMessage = "必须填写产品ID";
				return;
			}
			var formData = new FormData();
			formData.append('product', JSON.stringify(this.product));
            formData.append('logo_file', this.file);
			var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };
			var that = this;
            axios.post(Public.StoreService + '/add_product', formData, config)
			.then(function (response) {
				that.router.setPage("store", "edit_category_list");
			})
			.catch(function (error) {
				console.log(error);
				that.errorMessage = "添加产品失败";
			});
		}
    },
	mounted() {
		var categoryid = this.router.params.pop();
		if(categoryid)
			this.product.categoryid = categoryid;
	}
}
