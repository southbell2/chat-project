package demo.chatapp.channel.controller;

import demo.chatapp.channel.service.ChannelService;
import demo.chatapp.security.principal.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@RequiredArgsConstructor
public class ChannelController {

    private final ChannelService channelService;

    @PostMapping("/channel")
    public ResponseEntity<Void> createChannel(@RequestBody String title,
        Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Long masterId = userPrincipal.getId();
        channelService.createChannel(title, masterId);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}