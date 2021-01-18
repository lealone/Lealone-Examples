set @packageName '${packageName}.service.generated'; -- 生成的服务接口所在的包名
set @srcPath '../${appName}-service/src/main/java'; -- 生成的服务接口对应的源文件所在的根目录

-- 删除服务: ${appName}_service
drop service if exists ${appName}_service;

-- 创建服务: ${appName}_service，会生成一个对应的 ${appNameCamel}Service 接口
create service if not exists ${appName}_service (
  hello(name varchar) varchar,
  say_bye() varchar
)
package @packageName
implement by '${packageName}.service.${appNameCamel}ServiceImpl' -- ${appNameCamel}Service 接口的默认实现类
generate code @srcPath;
