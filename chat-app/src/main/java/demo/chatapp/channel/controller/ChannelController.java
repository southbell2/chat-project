package demo.chatapp.channel.controller;

import demo.chatapp.channel.service.ChannelService;
import demo.chatapp.channel.service.dto.ChannelInfoResponse;
import demo.chatapp.channel.service.dto.JoinChannelResponse;
import demo.chatapp.channel.service.dto.MessageResponse;
import demo.chatapp.security.principal.UserPrincipal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/channel")
    public ResponseEntity<Void> createChannel(@RequestBody String title,
        Authentication authentication) {
        Long masterId = getUserIdFromAuthentication(authentication);
        channelService.createChannel(title, masterId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/channel/{channelId}")
    public ResponseEntity<JoinChannelResponse> joinChannel(@PathVariable Long channelId,
        Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        JoinChannelResponse joinChannelResponse = channelService.joinChannel(channelId, userId);
        return ResponseEntity.ok(joinChannelResponse);
    }

    @GetMapping("/channel/{channelId}")
    public ResponseEntity<List<MessageResponse>> readMoreMessage(@PathVariable Long channelId,
        @RequestParam Long standardMessageId) {
        List<MessageResponse> messageResponses = channelService.getMessages(channelId, standardMessageId);
        return ResponseEntity.ok(messageResponses);
    }

    @DeleteMapping("/channel/{channelId}")
    public ResponseEntity<Void> leaveChannel(@PathVariable Long channelId,
        Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        channelService.leaveChannel(channelId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/channel")
    public ResponseEntity<List<ChannelInfoResponse>> getChannelInfo(@RequestParam(defaultValue = "0") long standardId,
        @RequestParam(defaultValue = "10") int limit) {
        standardId = convertIfDefaultId(standardId);
        List<ChannelInfoResponse> channelInfoResponses = channelService.getChannelInfo(standardId, limit);
        return ResponseEntity.ok(channelInfoResponses);
    }

    @GetMapping("/my-channel")
    public ResponseEntity<List<ChannelInfoResponse>> getMyChannelInfo(
        @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int limit,
        Authentication authentication) {
        Long userId = getUserIdFromAuthentication(authentication);
        List<ChannelInfoResponse> myChannelInfo = channelService.getMyChannelInfo(userId, page,
            limit);
        return ResponseEntity.ok(myChannelInfo);
    }

    private Long getUserIdFromAuthentication(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return userPrincipal.getId();
    }

    private long convertIfDefaultId(long standardId) {
        return (standardId == 0) ? Long.MAX_VALUE : standardId;
    }
}
