package demo.message.pubsub;

//레디스의 subscription 관리
public interface RedisSubManager {

    void subIfNecessary(Long channelId);

    void unSubIfNecessary(Long channelId);
}
