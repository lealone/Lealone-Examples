package org.lealone.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import org.lealone.plugins.vertx.server.HttpServer;

public class DemoApplication {

    public static void main(String[] args) throws Exception {
        // 通过JDBC访问的数据库的URL
        String jdbcUrl = "jdbc:lealone:embed:test";

        // 静态资源文件的根目录，如果有多个可以用逗号分隔
        String webRoot = args.length == 1 ? args[0] : "./web";

        // 启动HttpServer，请在浏览器中打开下面这个URL进行测试:
        // http://localhost:8080/hello.html
        HttpServer server = new HttpServer();
        server.setJdbcUrl(jdbcUrl);
        server.setWebRoot(webRoot);
        server.start();

        createService(jdbcUrl);
    }

    public static void createService(String url) throws Exception {
        // 创建服务: hello_service，会生成一个对应的HelloService接口
        String sql = "create service if not exists hello_service (" //
                + "     say_hello(name varchar) varchar" // HelloService接口方法定义
                + "   )" //
                + "   package 'org.lealone.demo.generated'" // HelloService接口所在的包名
                + "   implement by 'org.lealone.demo.HelloServiceImpl'" // HelloService接口的默认实现类
                + "   generate code './src/main/java'"; // HelloService接口源文件的根目录

        Connection conn = DriverManager.getConnection(url);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        stmt.close();
        conn.close();
    }
}
