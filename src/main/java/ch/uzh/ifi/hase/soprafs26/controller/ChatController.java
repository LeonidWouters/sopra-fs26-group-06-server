package ch.uzh.ifi.hase.soprafs26.controller;

import ch.uzh.ifi.hase.soprafs26.entity.ChatMessage;
import ch.uzh.ifi.hase.soprafs26.rest.dto.ChatMessageGetDTO;
import ch.uzh.ifi.hase.soprafs26.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs26.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/users/{userId}/chat/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ChatMessageGetDTO> getChatHistory(@PathVariable Long userId, @PathVariable Long friendId, @RequestHeader("token") String token) {

        List<ChatMessage> history = chatService.getChatHistory(userId, friendId);

        List<ChatMessageGetDTO> dtos = new ArrayList<>();
        for (ChatMessage message : history) {
            dtos.add(DTOMapper.INSTANCE.convertEntityToChatMessageGetDTO(message));
        }

        return dtos;
    }
}