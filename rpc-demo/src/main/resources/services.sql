-- 删除服务
drop service if exists hello_service;

-- 创建服务: hello_service，会生成一个对应的HelloService接口
create service if not exists hello_service (
  say_hello(name varchar) varchar -- HelloService接口方法定义
)
package 'org.lealone.examples.rpc.generated' -- HelloService接口所在的包名
implement by 'org.lealone.examples.rpc.HelloServiceImpl' -- HelloService接口的默认实现类
generate code './src/main/java' -- HelloService接口源文件的根目录
