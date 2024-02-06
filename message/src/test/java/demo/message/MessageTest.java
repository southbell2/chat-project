package demo.message;


import static demo.message.constant.ChannelConstant.SUB_CHANNEL_URL;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import demo.message.message.ChatMessage;
import demo.message.message.MessageType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
public class MessageTest extends AbstractContainerEnv {

    static WebSocketStompClient stompClient =
        new WebSocketStompClient(
            new SockJsClient(List.of(
                new WebSocketTransport(
                    new StandardWebSocketClient()
                )
            )
            )
        );

    static String url;
    static StompSession stompSession1;
    static StompSession stompSession2;
    static long channelId = 123L;
    static String nickname1 = "test1";


    @BeforeAll
    static void beforeTest() throws ExecutionException, InterruptedException, TimeoutException {
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        ObjectMapper objectMapper = messageConverter.getObjectMapper();
        objectMapper.registerModules(new JavaTimeModule(), new ParameterNamesModule());
        stompClient.setMessageConverter(messageConverter);
        url = "ws://localhost:8080/ws-stomp";

    }


    @Test
    void 채널_입장하기() throws ExecutionException, InterruptedException, TimeoutException {
        //given
        //웹소켓 연결
        stompSession1 = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
        }).get(2, TimeUnit.SECONDS);

        CompletableFuture<ChatMessage> subFuture = new CompletableFuture<>();
        stompSession1.subscribe(SUB_CHANNEL_URL + channelId, new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                subFuture.complete((ChatMessage) payload);
            }
        });
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setNickname(nickname1);
        chatMessage.setChannelId(channelId);

        //when
        stompSession1.send("/pub/join", chatMessage);
        ChatMessage retMessage = subFuture.get(3, TimeUnit.SECONDS);

        //then
        assertThat(retMessage.getMessageType()).isEqualTo(MessageType.JOIN);
        assertThat(retMessage.getChannelId()).isEqualTo(channelId);
        assertThat(retMessage.getNickname()).isEqualTo(nickname1);
        assertThat(retMessage.getContent()).isEqualTo(retMessage.getNickname() + "님이 입장하셨습니다.");
    }
}
