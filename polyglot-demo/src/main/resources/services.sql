-- 删除服务
drop service if exists hello_service;

-- 创建 hello_service 服务，用 js 实现
create service if not exists hello_service (
  hello(name varchar) varchar
)
language 'js' implement by './js/hello_service.js';


drop service if exists time_service;

-- 创建 time_service 服务，默认用 java 语言实现
create service if not exists time_service (
  get_current_time() varchar
)
implement by 'org.lealone.examples.polyglot.TimeService';

