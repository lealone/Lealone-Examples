var OpsCenter = {
    Host: '',
}

OpsCenter.OpsService = OpsCenter.Host + '/service/ops_service';
OpsCenter.AdminService = OpsCenter.Host + '/service/admin_service';


const i18n = { 
    data() {
        return {
			text: { a: {} }
        }
    },
    mounted() {
        var that = this;
        axios.get(OpsCenter.OpsService + '/read_translations')
        .then(response => {
			that.text = response.data.text;
		});
    }
}

