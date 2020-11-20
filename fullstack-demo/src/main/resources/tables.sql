-- 删除表
-- drop table if exists user;

-- 创建表: user，会生成一个名为User的模型类
create table if not exists user (
  id long auto_increment primary key,
  name varchar,
  age int
)
package 'org.lealone.examples.fullstack.generated.model' -- User类所在的包名
generate code './src/main/java' -- User类的源文件所在的根目录
