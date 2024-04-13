package demo.chatapp.id;

import java.util.HashMap;
import java.util.Map;

public class IdGeneratorMap {

    public static final Map<Integer, IdGenerator> idGeneratorMap = new HashMap<>();

    private IdGeneratorMap() {
    }

    public static void initMap(int maxThreadName) {
        for (int n = 0; n < maxThreadName; n++) {
            idGeneratorMap.put(n, new IdGenerator(n));
        }
    }

}
