create config lealone (
    base_dir: 'target/test-data',
    protocol_server_engine: (
        name: 'tomcat',
        enabled: true,
        port: 8080,
        environment: 'dev',
        web_root: '../petstore-web/web',
        jdbc_url: 'jdbc:lealone:embed:petstore?user=root',
        router: 'com.lealone.examples.petstore.web.PetStoreRouter'
    )
)
