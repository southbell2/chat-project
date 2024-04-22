package chatapp.messageconsumer.id.manager;

import chatapp.messageconsumer.id.generator.IdGenerator;
import chatapp.messageconsumer.id.generator.IdGeneratorOriginal;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("id-original")
public class IdGeneratorManagerOriginal implements IdGeneratorManager{

    private final IdGeneratorOriginal idGenerator = new IdGeneratorOriginal();

    @Override
    public IdGenerator getIdGenerator() {
        return idGenerator;
    }
}
