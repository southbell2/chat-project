package chatapp.messageconsumer.config.threadpool;

import chatapp.messageconsumer.id.ThreadNameQueue;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.threads.StopPooledThreadException;
import org.apache.tomcat.util.threads.TaskThread;

public class CustomThread extends Thread{

    private static final Log log = LogFactory.getLog(TaskThread.class);
    private final long creationTime;

    public CustomThread(ThreadGroup group, Runnable target, String name) {
        super(group, new WrappingRunnable(target), name);
        this.creationTime = System.currentTimeMillis();
    }

    public CustomThread(ThreadGroup group, Runnable target, String name,
        long stackSize) {
        super(group, new WrappingRunnable(target), name, stackSize);
        this.creationTime = System.currentTimeMillis();
    }

    public final long getCreationTime() {
        return creationTime;
    }

    private static class WrappingRunnable implements Runnable {
        private final Runnable wrappedRunnable;
        WrappingRunnable(Runnable wrappedRunnable) {
            this.wrappedRunnable = wrappedRunnable;
        }
        @Override
        public void run() {
            try {
                wrappedRunnable.run();
            } catch(StopPooledThreadException exc) {
                //expected : we just swallow the exception to avoid disturbing
                //debuggers like eclipse's
                log.debug("Thread exiting on purpose", exc);
            } finally {
                Integer name = Integer.parseInt(Thread.currentThread().getName());
                ThreadNameQueue.add(name);
            }
        }

    }

}
