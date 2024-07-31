package chatapp.messageconsumer.config.retry;

import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.listener.RetryListenerSupport;

@Slf4j
public class CassandraRetryListener extends RetryListenerSupport {

    @Override
    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback,
        Throwable throwable) {
        log.error("[ERROR]카산드라에 메세지 저장 중 에러 발생, 재시도 횟수 = {}, msg = {}, ex = {}",
            context.getRetryCount(), context.getAttribute("message"), throwable);
    }
}
