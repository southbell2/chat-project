package chatapp.messageconsumer.id.manager;

import chatapp.messageconsumer.id.IdGeneratorMap;
import chatapp.messageconsumer.id.ThreadNameQueue;
import chatapp.messageconsumer.id.generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("nosync")
@Profile("id-nosync")
@Slf4j
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
