// MessageService.java
package org.csu.medicine.nursingplatform.service;

import org.csu.medicine.nursingplatform.entity.Message;
import org.csu.medicine.nursingplatform.entity.MessageConversation;
import org.csu.medicine.nursingplatform.mapper.MessageContactMapper;
import org.csu.medicine.nursingplatform.mapper.MessageConversationMapper;
import org.csu.medicine.nursingplatform.mapper.MessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageConversationMapper conversationMapper;

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private MessageContactMapper contactMapper;

    // 获取用户所有会话
    public List<MessageConversation> getUserConversations(Long userId) {
        return conversationMapper.selectByUserId(userId);
    }

    // 获取会话所有消息
    public List<Message> getConversationMessages(Long conversationId) {
        return messageMapper.selectByConversationId(conversationId);
    }

    // 发送消息
    @Transactional
    public void sendMessage(Message message) {
        // 插入消息
        message.setCreateTime(LocalDateTime.now());
        messageMapper.insert(message);

        // 更新会话最后消息
        MessageConversation conversation = conversationMapper.selectById(message.getConversationId());
        if (conversation != null) {
            conversation.setLastMessage(message.getContent());
            conversation.setLastMessageTime(LocalDateTime.now());
            conversationMapper.updateById(conversation);
        }
    }
}