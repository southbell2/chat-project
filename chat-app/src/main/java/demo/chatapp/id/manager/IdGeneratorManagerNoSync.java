package demo.chatapp.id.manager;

import demo.chatapp.id.IdGeneratorMap;
import demo.chatapp.id.ThreadNameQueue;
import demo.chatapp.id.generator.IdGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("id-nosync")
public class IdGeneratorManagerNoSync implements IdGeneratorManager{

    @Value("${server.tomcat.threads.max:200}")
    private int maxThreadPoolSize;
    @Value("${server.tomcat.threads.min-spare:10}")
    private int threadPoolCoreSize;

    public IdGeneratorManagerNoSync() {
        int maxThreadName = ThreadNameQueue.initQueue(maxThreadPoolSize);
        IdGeneratorMap.initMap(maxThreadName);
    }

    @Override
    public IdGenerator getIdGenerator() {
        int threadName = Integer.parseInt(Thread.currentThread().getName());
        return IdGeneratorMap.idGeneratorMap.get(threadName);
    }


}
