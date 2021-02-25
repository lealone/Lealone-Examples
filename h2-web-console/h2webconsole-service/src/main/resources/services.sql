set @packageName 'org.lealone.examples.h2webconsole.service.generated'; -- 生成的服务接口所在的包名
set @srcPath '../h2webconsole-service/src/main/java'; -- 生成的服务接口对应的源文件所在的根目录

-- 删除服务: admin_service
drop service if exists admin_service;

-- 创建服务: admin_service，会生成一个对应的AdminService接口
create service if not exists admin_service (
  login(password varchar) varchar, 
  save(port varchar, allow_others varchar, ssl varchar) varchar,
  admin() varchar,
  start_translate() varchar,
  shutdown() varchar,
  tools(tool_name varchar, args varchar) varchar
)
package @packageName
implement by 'org.lealone.examples.h2webconsole.service.AdminServiceImpl' -- AdminService接口的默认实现类
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
  logout(jsessionid varchar) varchar,
  test_connection() varchar
)
package @packageName
implement by 'org.lealone.examples.h2webconsole.service.OpsServiceImpl' -- OpsService接口的默认实现类
generate code @srcPath;


-- 删除服务: query_service
drop service if exists query_service;

-- 创建服务: query_service，会生成一个对应的QueryService接口
create service if not exists query_service (
  query(jsessionid varchar, sql varchar) varchar,
  edit_result(jsessionid varchar, row int, op int, value varchar) varchar
)
package @packageName
implement by 'org.lealone.examples.h2webconsole.service.QueryServiceImpl' -- QueryService接口的默认实现类
generate code @srcPath;


drop service if exists database_service;

create service if not exists database_service (
  read_all_database_objects(jsessionid varchar) varchar
)
package @packageName
implement by 'org.lealone.examples.h2webconsole.service.DatabaseServiceImpl'
generate code @srcPath;

