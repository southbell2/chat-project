package chatapp.messageconsumer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("aws")
public class AWSKey {

    public AWSKey(@Value("${aws.accessKey}")String accessKey, @Value("${aws.secretKey}")String secretKey) {
        System.setProperty("aws.accessKeyId", accessKey);
        System.setProperty("aws.secretKey", secretKey);
    }
}
