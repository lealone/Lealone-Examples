
create service if not exists hello_service (
  hello(name varchar) varchar
)
language 'js' implement by './js/hello_service.js';


create service if not exists user_service (
  add_user(name varchar, age int) long,
  find_by_name(name varchar) user
)
language 'js' implement by './js/user_service.js';

