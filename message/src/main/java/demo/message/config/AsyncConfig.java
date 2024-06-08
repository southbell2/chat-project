package demo.message.config;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "sendingMessageThreadPoolTaskExecutor")
    public Executor sendingMessageThreadPoolExecutor() {
        TaskQueue taskQueue = new TaskQueue();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(40, 1000,
            60, TimeUnit.SECONDS, taskQueue);
        taskQueue.setParent((ThreadPoolExecutor) executor);
        return executor;
    }
}
