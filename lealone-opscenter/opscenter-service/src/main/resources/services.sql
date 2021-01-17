set @packageName 'org.lealone.opscenter.service.generated'; -- 生成的服务接口所在的包名
set @srcPath '../opscenter-service/src/main/java'; -- 生成的服务接口对应的源文件所在的根目录

-- 删除服务: admin_service
drop service if exists admin_service;

-- 创建服务: admin_service，会生成一个对应的AdminService接口
create service if not exists admin_service (
  login(password varchar) varchar,
  logout() varchar,
  save(port varchar, allow_others varchar, ssl varchar) varchar,
  admin() varchar,
  start_translate() varchar,
  shutdown() varchar,
  tools(tool varchar, args varchar) varchar
)
package @packageName
implement by 'org.lealone.opscenter.service.AdminServiceImpl' -- AdminService接口的默认实现类
generate code @srcPath;


-- 删除服务: ops_service
drop service if exists ops_service;

-- 创建服务: ops_service，会生成一个对应的OpsService接口
create service if not exists ops_service (
  get_languages() varchar,
  get_settings(setting varchar) varchar,
  setting_save(name varchar, driver varchar, url varchar, user varchar) varchar,
  setting_remove(name varchar) varchar,
  read_translations(language varchar) varchar,
  login(url varchar, user varchar, password varchar) varchar,
  test_connection() varchar
)
package @packageName
implement by 'org.lealone.opscenter.service.OpsServiceImpl' -- OpsService接口的默认实现类
generate code @srcPath;


-- 删除服务: query_service
drop service if exists query_service;

-- 创建服务: query_service，会生成一个对应的QueryService接口
create service if not exists query_service (
  query(sql varchar) varchar,
  edit_result(row int, op int, value varchar) varchar
)
package @packageName
implement by 'org.lealone.opscenter.service.QueryServiceImpl' -- QueryService接口的默认实现类
generate code @srcPath;


drop service if exists system_service;

create service if not exists system_service (
  load_services(service_names varchar) varchar
)
package @packageName
implement by 'org.lealone.opscenter.service.SystemServiceImpl'
generate code @srcPath;
