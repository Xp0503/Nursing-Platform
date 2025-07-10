// MessageController.java
package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.Message;
import org.csu.medicine.nursingplatform.entity.MessageConversation;
import org.csu.medicine.nursingplatform.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messages")
@CrossOrigin
public class MessageController {

    @Autowired
    private MessageService messageService;

    // 获取用户所有会话
    @GetMapping("/conversations")
    public ResponseEntity<List<MessageConversation>> getConversations(
            @RequestParam Long userId) {
        return ResponseEntity.ok(messageService.getUserConversations(userId));
    }

    // 获取会话消息记录
    @GetMapping("/{conversationId}")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable Long conversationId) {
        return ResponseEntity.ok(messageService.getConversationMessages(conversationId));
    }

    // 发送消息
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody Message message) {
        messageService.sendMessage(message);
        return ResponseEntity.ok().build();
    }
}
