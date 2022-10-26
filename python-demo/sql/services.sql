
create service if not exists hello_service (
  hello(name varchar) varchar
)
language 'python' implement by './python/hello_service.py';


create service if not exists user_service (
  add_user(name varchar, age int) long,
  find_by_name(name varchar) user
)
language 'python' implement by './python/user_service.py';

