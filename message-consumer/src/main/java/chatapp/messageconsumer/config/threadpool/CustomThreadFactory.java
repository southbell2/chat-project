package chatapp.messageconsumer.config.threadpool;

import chatapp.messageconsumer.id.ThreadNameQueue;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final boolean daemon;
    private final int threadPriority;

    @Autowired
    public CustomThreadFactory(boolean daemon, int priority) {
        group = Thread.currentThread().getThreadGroup();
        this.daemon = daemon;
        this.threadPriority = priority;
    }

    @Override
    public Thread newThread(@NotNull Runnable r) {
        Thread t = makeCustomThread(r);
        t.setDaemon(daemon);
        t.setPriority(threadPriority);
        return t;
    }

    private CustomThread makeCustomThread(Runnable r) {
        Integer name = ThreadNameQueue.poll();
        Objects.requireNonNull(name, "쓰레드 이름은 null이 될 수 없습니다.");
        return new CustomThread(group, r, name.toString());
    }
}
