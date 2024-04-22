package chatapp.messageconsumer.id;

import chatapp.messageconsumer.id.generator.IdGeneratorNoSync;
import java.util.HashMap;
import java.util.Map;

public class IdGeneratorMap {

    public static final Map<Integer, IdGeneratorNoSync> idGeneratorMap = new HashMap<>();

    private IdGeneratorMap() {
    }

    public static void initMap(int maxThreadName) {
        for (int n = 0; n < maxThreadName; n++) {
            idGeneratorMap.put(n, new IdGeneratorNoSync(n));
        }
    }

}
