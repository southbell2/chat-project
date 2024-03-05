package demo.message;


import static demo.message.constant.ChannelConstant.SUB_CHANNEL_URL;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import demo.message.message.ChatMessage;
import demo.message.message.MessageType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
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
    static long channelId = 123L;
    static String nickname1 = "test1";
    static String nickname2 = "test2";
    static String nickname3 = "test3";


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
        StompSession stompSession1 = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
        }).get(2, TimeUnit.SECONDS);
        StompSession stompSession2 = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
        }).get(2, TimeUnit.SECONDS);

        List<ChatMessage> list1 = new ArrayList<>();
        subscribe(stompSession1, list1);

        List<ChatMessage> list2 = new ArrayList<>();
        subscribe(stompSession2, list2);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setNickname(nickname1);
        chatMessage.setChannelId(channelId);

        //when
        stompSession1.send("/pub/join", chatMessage);
        Thread.sleep(1000);
        ChatMessage retMessage = list2.get(0);

        //then
        assertThat(retMessage.getMessageType()).isEqualTo(MessageType.JOIN);
        assertThat(retMessage.getChannelId()).isEqualTo(channelId);
        assertThat(retMessage.getNickname()).isEqualTo(nickname1);
        assertThat(retMessage.getContent()).isEqualTo(retMessage.getNickname() + "님이 입장하셨습니다.");
    }

    @Test
    void 채널_퇴장하기() throws ExecutionException, InterruptedException, TimeoutException {
        //given
        //3개의 웹소켓 연결
        StompSession stompSession1 = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
        }).get(2, TimeUnit.SECONDS);
        StompSession stompSession2 = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
        }).get(2, TimeUnit.SECONDS);
        StompSession stompSession3 = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
        }).get(2, TimeUnit.SECONDS);
        List<ChatMessage> list1 = new ArrayList<>();
        List<ChatMessage> list2 = new ArrayList<>();
        List<ChatMessage> list3 = new ArrayList<>();

        subscribe(stompSession1, list1);
        subscribe(stompSession2, list2);
        subscribe(stompSession3, list3);

        ChatMessage chatMessage1 = new ChatMessage();
        chatMessage1.setNickname(nickname1);
        chatMessage1.setChannelId(channelId);
        ChatMessage chatMessage2 = new ChatMessage();
        chatMessage2.setNickname(nickname2);
        chatMessage2.setChannelId(channelId);
        ChatMessage chatMessage3 = new ChatMessage();
        chatMessage3.setNickname(nickname3);
        chatMessage3.setChannelId(channelId);

        //채널 입장
        stompSession1.send("/pub/join", chatMessage1);
        Thread.sleep(1000);
        stompSession2.send("/pub/join", chatMessage2);
        Thread.sleep(1000);
        stompSession3.send("/pub/join", chatMessage3);
        Thread.sleep(1000);

        //when && then
        stompSession3.send("/pub/leave", chatMessage3);
        Thread.sleep(1000);
        ChatMessage ret2 = list2.get(3);
        ChatMessage ret1 = list1.get(3);

        assertThat(ret2.getMessageType()).isEqualTo(MessageType.LEAVE);
        assertThat(ret2.getChannelId()).isEqualTo(channelId);
        assertThat(ret2.getNickname()).isEqualTo(nickname3);
        assertThat(ret2.getContent()).isEqualTo(nickname3 + "님이 퇴장하셨습니다.");
        assertThat(ret1.getMessageType()).isEqualTo(MessageType.LEAVE);
        assertThat(ret1.getChannelId()).isEqualTo(channelId);
        assertThat(ret1.getNickname()).isEqualTo(nickname3);
        assertThat(ret1.getContent()).isEqualTo(nickname3 + "님이 퇴장하셨습니다.");

        stompSession2.send("/pub/leave", chatMessage2);
        Thread.sleep(1000);
        ret1 = list1.get(4);
        assertThat(ret1.getMessageType()).isEqualTo(MessageType.LEAVE);
        assertThat(ret1.getChannelId()).isEqualTo(channelId);
        assertThat(ret1.getNickname()).isEqualTo(nickname2);
        assertThat(ret1.getContent()).isEqualTo(nickname2 + "님이 퇴장하셨습니다.");
    }

    private void subscribe(StompSession stompSession,
        List<ChatMessage> list) {
        stompSession.subscribe(SUB_CHANNEL_URL + channelId, new StompSessionHandlerAdapter() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return ChatMessage.class;
            }

            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                list.add((ChatMessage) payload);
            }
        });
    }
}
