package demo.chatapp.id.manager;

import demo.chatapp.id.generator.IdGenerator;
import demo.chatapp.id.generator.IdGeneratorOriginal;
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
