set @packageName 'org.lealone.examples.petstore.service.generated'; -- 生成的服务接口所在的包名
set @srcPath '../petstore-service/src/main/java'; -- 生成的服务接口对应的源文件所在的根目录

-- 删除服务: user_service
drop service if exists user_service;

-- 创建服务: user_service，会生成一个对应的UserService接口
create service if not exists user_service (
  login(user_id varchar, password varchar) user,
  register(user_id varchar, password varchar, password2 varchar) void,
  update(account Account) void,
  get_user(user_id varchar) varchar
)
package @packageName
implement by 'org.lealone.examples.petstore.service.UserServiceImpl' -- UserService接口的默认实现类
generate code @srcPath;


-- 删除服务: store_service
drop service if exists store_service;

-- 创建服务: store_service，会生成一个对应的StoreService接口
create service if not exists store_service (
  add_product(product Product, logo varchar) varchar,
  get_all_categories() varchar,
  get_all_product_items(product_id varchar) varchar
)
package @packageName
implement by 'org.lealone.examples.petstore.service.StoreServiceImpl' -- StoreService接口的默认实现类
generate code @srcPath;


-- 删除服务: car_service
drop service if exists car_service;

-- 创建服务: car_service，会生成一个对应的CarService接口
create service if not exists car_service (
  add_item(car_id varchar, item_id varchar) void,
  remove_item(car_id varchar, item_id varchar) void,
  update(car_id varchar, item_id varchar, quantity int) void,
  get_items(car_id varchar) varchar
)
package @packageName
implement by 'org.lealone.examples.petstore.service.CarServiceImpl' -- CarService接口的默认实现类
generate code @srcPath;

drop service if exists system_service;

create service if not exists system_service (
  load_services(service_names varchar) varchar
)
package @packageName
implement by 'org.lealone.examples.petstore.service.SystemServiceImpl'
generate code @srcPath;
