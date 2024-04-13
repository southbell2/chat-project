package demo.chatapp.config.threadpool;

import demo.chatapp.id.IdGeneratorMap;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Objects;
import java.util.concurrent.ThreadFactory;
import org.apache.tomcat.util.security.PrivilegedSetAccessControlContext;
import org.apache.tomcat.util.security.PrivilegedSetTccl;
import org.apache.tomcat.util.threads.Constants;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomThreadFactory implements ThreadFactory {

    private final ThreadGroup group;
    private final boolean daemon;
    private final int threadPriority;

    @Autowired
    public CustomThreadFactory(boolean daemon, int priority) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        this.daemon = daemon;
        this.threadPriority = priority;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread t = makeCustomThread(r);

        t.setDaemon(daemon);
        t.setPriority(threadPriority);

        if (Constants.IS_SECURITY_ENABLED) {
            // Set the context class loader of newly created threads to be the
            // class loader that loaded this factory. This avoids retaining
            // references to web application class loaders and similar.
            PrivilegedAction<Void> pa = new PrivilegedSetTccl(
                t, getClass().getClassLoader());
            AccessController.doPrivileged(pa);

            // This method may be triggered from an InnocuousThread. Ensure that
            // the thread inherits an appropriate AccessControlContext
            pa = new PrivilegedSetAccessControlContext(t);
            AccessController.doPrivileged(pa);
        } else {
            t.setContextClassLoader(getClass().getClassLoader());
        }

        return t;
    }

    private CustomThread makeCustomThread(Runnable r) {
        Integer name = ThreadNameQueue.poll();
        Objects.requireNonNull(name, "쓰레드 이름은 null이 될 수 없습니다.");
        return new CustomThread(group, r, name.toString());
    }
}
