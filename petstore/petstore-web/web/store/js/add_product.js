 const storeAddProduct = { 
    data() {
        return {
            sProduct: {categoryid: "", productid: "", name: "", descn: ""},
			sFile: "",
			sErrorMessage: ""
        }
    },
    methods: {
		getFile(event) {
            this.sFile = event.target.files[0];
		},
        addProduct() {
			if(!this.sProduct.productid) {
				this.sErrorMessage = "必须填写产品ID";
				return;
			}
			var formData = new FormData();
			formData.append('product', JSON.stringify(this.sProduct));
            formData.append('logo_file', this.sFile);
			var config = {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            };
			var that = this;
            axios.post(Public.StoreService + '/add_product', formData, config)
			.then(function (response) {
				that.setPage("store", "edit_category_list");
			})
			.catch(function (error) {
				console.log(error);
				that.sErrorMessage = "添加产品失败";
			});
		},
		openAddProductPage(categoryid) {
			this.pageType = "add_product";
			this.sProduct.categoryid = categoryid;
		}
    }
}
