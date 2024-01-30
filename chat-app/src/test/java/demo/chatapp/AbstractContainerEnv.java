package demo.chatapp;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.CassandraContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public abstract class AbstractContainerEnv {

    public static final String KEYSPACE_NAME = "test";
    public static CassandraContainer<?> cassandra = new CassandraContainer<>("cassandra:4.1.3")
        .withExposedPorts(9042);

    static {
        cassandra.start();
        createKeyspace(cassandra.getCluster());
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

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("cassandra.contact-point", cassandra::getHost);
        registry.add("cassandra.local-datacenter", () -> cassandra.getLocalDatacenter());
        registry.add("cassandra.port", () -> cassandra.getMappedPort(9042));
        registry.add("cassandra.keyspace", () -> KEYSPACE_NAME);
    }


}
