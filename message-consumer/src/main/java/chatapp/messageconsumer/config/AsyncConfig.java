package chatapp.messageconsumer.config;

import java.util.concurrent.Executor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${server.tomcat.threads.max:200}")
    private int maxThreadPoolSize;
    @Value("${server.tomcat.threads.min-spare:10}")
    private int threadPoolCoreSize;

    @Bean(name = "consumerThreadPoolTaskExecutor")
    public Executor consumerThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolCoreSize);
        executor.setMaxPoolSize(maxThreadPoolSize);
        executor.setQueueCapacity(20);
        executor.setThreadNamePrefix("Consumer Task-");
        return executor;
    }

}
