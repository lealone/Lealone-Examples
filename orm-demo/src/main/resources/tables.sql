-- 删除表
drop table if exists `order`;
drop table if exists customer;

set @packageName 'com.lealone.examples.orm.generated'; -- 生成的模型类所在的包名
set @srcPath './src/main/java'; -- 生成的模型类对应的源文件所在的根目录

-- 创建customer表，会生成一个名为Customer的模型类
create table if not exists customer (
  id long primary key,
  name char(10),
  notes varchar,
  phone int
) package @packageName generate code @srcPath;

-- 创建order表，会生成一个名为Order的模型类
-- order是关键字，所以要用特殊方式表式
create table if not exists `order` (
  customer_id long,
  order_id int primary key,
  order_date date,
  total double,
  FOREIGN KEY(customer_id) REFERENCES customer(id)
) package @packageName generate code @srcPath;
