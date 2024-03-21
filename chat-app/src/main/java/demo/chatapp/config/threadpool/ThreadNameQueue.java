package demo.chatapp.config.threadpool;

import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ThreadNameQueue {

    private static final Queue<Integer> queue = new ConcurrentLinkedQueue<>();;
    private static int maxQueueNumber = 210;

    private ThreadNameQueue() {
    }

    public static Integer poll() {
        return queue.poll();
    }

    public static boolean add(Integer number) {
        Objects.requireNonNull(number);
        if (number > maxQueueNumber) {
            throw new RuntimeException("큐에 넣을 수 있는 최대 값보다 큰 값입니다.");
        }
        return queue.add(number);
    }

    static int initQueue(int maxThreadPoolSize) {
        maxQueueNumber = maxThreadPoolSize + 10;
        for (int n = 0; n < maxQueueNumber; n++) {
            queue.add(n);
        }
        return maxQueueNumber;
    }

}
