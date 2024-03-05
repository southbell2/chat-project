package demo.chatapp;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class AbstractContainerEnv {

    private static final String KEYSPACE_NAME = "test";
    public static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1.3")
        .withExposedPorts(9042);
    public static GenericContainer<?> redis = new GenericContainer<>(
        DockerImageName.parse("redis:6.0.20")).withExposedPorts(6379);

    static {
        cassandra.start();
        createKeyspace(cassandra.getCluster());
        redis.start();
        
        System.setProperty("spring.data.redis.host", redis.getHost());
        System.setProperty("spring.data.redis.port", String.valueOf(redis.getMappedPort(6379)));

        System.setProperty("cassandra.contact-point", cassandra.getHost());
        System.setProperty("cassandra.local-datacenter", cassandra.getLocalDatacenter());
        System.setProperty("cassandra.port", String.valueOf(cassandra.getMappedPort(9042)));
        System.setProperty("cassandra.keyspace", KEYSPACE_NAME);
    }

    private static void createKeyspace(Cluster cluster) {
        try (Session session = cluster.connect()) {
            session.execute("CREATE KEYSPACE IF NOT EXISTS " + KEYSPACE_NAME +
                " WITH replication = \n" +
                "{'class':'SimpleStrategy','replication_factor':'1'};");
            session.execute(
                """
                    CREATE TABLE IF NOT EXISTS test.messages (
                    channel_id bigint,\s
                    bucket int,\s
                    message_id bigint,\s
                    nickname text,\s
                    content text,\s
                    created_at timestamp,\s
                    PRIMARY KEY ((channel_id, bucket), message_id)) WITH CLUSTERING ORDER BY (message_id DESC)"""
            );

        }
    }

//    @DynamicPropertySource
//    static void setProperties(DynamicPropertyRegistry registry) {
//        registry.add("cassandra.contact-point", cassandra::getHost);
//        registry.add("cassandra.local-datacenter", cassandra::getLocalDatacenter);
//        registry.add("cassandra.port", () -> cassandra.getMappedPort(9042));
//        registry.add("cassandra.keyspace", () -> KEYSPACE_NAME);
//    }


}
