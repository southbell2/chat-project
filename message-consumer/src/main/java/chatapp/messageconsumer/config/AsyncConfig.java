package chatapp.messageconsumer.config;

import chatapp.messageconsumer.config.threadpool.CustomThreadFactory;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    public static final int CORE_POOL_SIZE = 20;
    public static final int MAXIMUM_POOL_SIZE = 200;

    @Bean(name = "consumerThreadPoolTaskExecutor")
    @DependsOn("nosync")
    @Profile("id-nosync")
    public Executor idNoSyncThreadPoolExecutor() {
        TaskQueue taskQueue = new TaskQueue();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE,
            60, TimeUnit.SECONDS, taskQueue, new CustomThreadFactory(true, Thread.NORM_PRIORITY));
        taskQueue.setParent((ThreadPoolExecutor) executor);
        return executor;
    }

    @Bean(name = "consumerThreadPoolTaskExecutor")
    @Profile("id-original")
    public Executor idOriginalThreadPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(CORE_POOL_SIZE);
        executor.setMaxPoolSize(MAXIMUM_POOL_SIZE);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("Consumer Task - ");
        executor.setDaemon(true);
        return executor;
    }

}
