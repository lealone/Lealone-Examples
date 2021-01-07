# Lealone-OpsCenter

基于 Vue + Lealone 开发的 Lealone Ops Center，改编自 [H2 数据库的 Web Console](http://www.h2database.com/html/quickstart.html)



### 构建

build -p



### 运行

cd target\lealone-opscenter-1.0.0\bin

lealone



### 初始化数据

cd target\lealone-opscenter-1.0.0\bin

runSqlScript



### 打开页面

http://localhost:9000/



### 在 IDE 中开发调试


把代码导入到 IDE 后，

先运行 OpsCenterTest 启动应用，

然后运行 OpsCenterSqlScriptTest 初始化数据，

最后就能打开页面查看了。
