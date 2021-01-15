set @packageName 'org.lealone.opscenter.service.generated'; -- 生成的服务接口所在的包名
set @srcPath '../opscenter-service/src/main/java'; -- 生成的服务接口对应的源文件所在的根目录

-- 删除服务: admin_service
drop service if exists admin_service;

-- 创建服务: admin_service，会生成一个对应的AdminService接口
create service if not exists admin_service (
  login(password varchar) varchar,
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

-- 创建服务: web_service，会生成一个对应的WOpsService接口
create service if not exists ops_service (
  get_languages() varchar,
  get_settings(setting varchar) varchar,
  setting_save(name varchar, driver varchar, url varchar, user varchar) varchar,
  setting_remove(name varchar) varchar,
  read_translations(language varchar) varchar,
  login(url varchar, user varchar, password varchar) varchar
)
package @packageName
implement by 'org.lealone.opscenter.service.OpsServiceImpl' -- OpsService接口的默认实现类
generate code @srcPath;
