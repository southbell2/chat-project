package demo.chatapp.channel.domain;

public class Bucket {

    //2024년 1월 1일 0시 0분 0초 , IdGenerator 클래스와 같다.
    private static final long DEFAULT_CUSTOM_EPOCH = 1704034800000L;
    //1달 단위로 버킷이 만들어진다.
    private static final long UNIT_EPOCH = 2629743000L;

    public static int calculateBucket(long timestamp) {
        timestamp -= DEFAULT_CUSTOM_EPOCH;
        return (int) (timestamp / UNIT_EPOCH);
    }

    private Bucket() {
    }
}
