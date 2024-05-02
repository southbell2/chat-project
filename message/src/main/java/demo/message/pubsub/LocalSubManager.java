package demo.message.pubsub;

import static demo.message.constant.ChannelConstant.REDIS_CHANNEL_PREFIX;

import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile("local")
public class LocalSubManager implements RedisSubManager{

    private final RedisMessageListenerContainer container;
    private final MessageListenerAdapter listenerAdapter;
    private final Map<Long, Integer> hashMap = new HashMap<>();

    @Override
    public synchronized void subIfNecessary(Long channelId) {
        Integer count = hashMap.put(channelId, hashMap.getOrDefault(channelId, 0) + 1);
        if (count == null) {
            String subChannel = REDIS_CHANNEL_PREFIX + channelId;
            container.addMessageListener(listenerAdapter, new ChannelTopic(subChannel));
        }
    }

    @Override
    public synchronized void unSubIfNecessary(Long channelId) {
        Integer count = hashMap.put(channelId, hashMap.get(channelId) - 1);
        if (count == 1) {
            String subChannel = REDIS_CHANNEL_PREFIX + channelId;
            container.removeMessageListener(listenerAdapter, new ChannelTopic(subChannel));
            hashMap.remove(channelId);
        }
    }
}
