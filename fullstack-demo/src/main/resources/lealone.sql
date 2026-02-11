create config lealone (
    base_dir: 'target/test-data',
    protocol_server_engine: (
        name: 'tomcat',
        enabled: true,
        port: 8080,
        web_root: './web',
        jdbc_url: 'jdbc:lealone:embed:test?user=root'
    )
)
