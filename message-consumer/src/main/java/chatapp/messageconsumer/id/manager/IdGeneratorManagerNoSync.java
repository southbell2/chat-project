package chatapp.messageconsumer.id.manager;

import static chatapp.messageconsumer.config.AsyncConfig.MAXIMUM_POOL_SIZE;

import chatapp.messageconsumer.id.IdGeneratorMap;
import chatapp.messageconsumer.id.ThreadNameQueue;
import chatapp.messageconsumer.id.generator.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component("nosync")
@Profile("id-nosync")
@Slf4j
public class IdGeneratorManagerNoSync implements IdGeneratorManager{

    public IdGeneratorManagerNoSync() {
        int maxThreadName = ThreadNameQueue.initQueue(MAXIMUM_POOL_SIZE);
        IdGeneratorMap.initMap(maxThreadName);
    }

    @Override
    public IdGenerator getIdGenerator() {
        int threadName = Integer.parseInt(Thread.currentThread().getName());
        return IdGeneratorMap.idGeneratorMap.get(threadName);
    }


}
