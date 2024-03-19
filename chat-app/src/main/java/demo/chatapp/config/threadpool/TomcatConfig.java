package demo.chatapp.config.threadpool;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.apache.tomcat.util.threads.TaskQueue;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {

    @Value("${server.tomcat.threads.max:200}")
    private int maxThreadPoolSize;
    @Value("${server.tomcat.threads.min-spare:10}")
    private int threadPoolCoreSize;

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> webServerFactoryCustomizer() {
        //ThreadNameQueue 초기화
        ThreadNameQueue.initQueue(maxThreadPoolSize);
        //ThreadPoolExecutor 만들기
        Executor executor = createThreadPoolExecutor();
        return factory -> factory.addConnectorCustomizers(connector -> {
            connector.getProtocolHandler().setExecutor(executor);
        });
    }

    private Executor createThreadPoolExecutor() {
        TaskQueue taskQueue = new TaskQueue();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadPoolCoreSize, maxThreadPoolSize,
            60, TimeUnit.SECONDS, taskQueue, new CustomThreadFactory(true, Thread.NORM_PRIORITY));
        taskQueue.setParent((ThreadPoolExecutor) executor);
        return executor;
    }

}
