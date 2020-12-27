var Public = {
   // Host: 'http://222.84.166.34:9999', //支持CORS
    Host: '',
};
Public.AdminService = Public.Host + '/service/admin_service';
Public.CrudService = Public.Host + '/service/crud_service';
Public.WebService = Public.Host + '/service/web_service';

Public.GetTableData = Public.CrudService + '/get_table_data';
Public.AddTableData = Public.CrudService + '/add_table_data';
Public.DelTableData = Public.CrudService + '/del_table_data';
Public.EditTableData = Public.CrudService + '/edit_table_data';

Public.ImportData = Public.WebService + '/import_data';
Public.ExportData = Public.WebService + '/export_data';

Public.UserService = Public.Host + '/service/user_service';
Public.StoreService = Public.Host + '/service/store_service';
