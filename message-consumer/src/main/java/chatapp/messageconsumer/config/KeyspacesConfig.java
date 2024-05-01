package chatapp.messageconsumer.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("aws")
public class KeyspacesConfig {

    @Value("${cassandra.keyspace}")
    private String keyspace;

    @Bean
    public CqlSession session() {
        return CqlSession.builder()
            .withKeyspace(keyspace)
            .withConfigLoader(DriverConfigLoader.fromClasspath("keyspaces-application.conf"))
            .build();
    }

}
