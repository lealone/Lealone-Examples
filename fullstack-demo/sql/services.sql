-- 删除服务
drop service if exists user_service;

-- 创建服务: user_service，会生成一个对应的UserService接口
create service if not exists user_service (
  add_user(name varchar, age int) long, -- 定义UserService接口方法 add_user
  find_by_name(name varchar) user -- 定义UserService接口方法 find_by_name
)
package 'com.lealone.examples.fullstack.generated.service' -- UserService接口所在的包名
implement by 'com.lealone.examples.fullstack.UserServiceImpl' -- UserService接口的默认实现类
generate code './src/main/java' -- UserService接口源文件的根目录
