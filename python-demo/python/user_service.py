
import java

# 使用 java 的类
User = java.type('org.lealone.examples.python.User')

def addUser(name, age):
    # 创建 User 对象
    user = User()

    # 如果 name = 'zhh', age = 18
    # 对应的sql是: insert into user(name, age) values('zhh', 18);
    return user.name.set(name).age.set(age).insert() # 链式调用，insert()返回新增记录的rowId

def findByName(name):
    # 如果 name = 'zhh'
    # 对应的sql是: select * from user where name = 'zhh' limit 1
    return User.dao.where().name.eq(name).findOne()
