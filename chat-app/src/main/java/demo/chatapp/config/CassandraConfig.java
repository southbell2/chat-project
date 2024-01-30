package demo.chatapp.config;

import com.datastax.oss.driver.api.core.CqlSession;
import java.net.InetSocketAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "demo.chatapp.channel.repository")
public class CassandraConfig {

    @Value("${cassandra.contact-point}")
    private String contactPoint;
    @Value("${cassandra.port}")
    private int port;
    @Value("${cassandra.local-datacenter}")
    private String localDataCenter;
    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Bean
    public CqlSession session() {
        return CqlSession.builder()
            .withKeyspace(keyspace)
            .addContactPoint(new InetSocketAddress(contactPoint, port))
            .withLocalDatacenter(localDataCenter)
            .build();
    }

}
