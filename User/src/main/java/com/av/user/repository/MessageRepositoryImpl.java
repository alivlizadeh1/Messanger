package com.av.user.repository;

import com.av.user.entity.MessageType;
import com.av.user.exception.User.UserMessageNotFoundException;
import com.av.user.response.MessageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MessageRepositoryImpl implements MessageRepository{
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public MessageRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public MessageResponse insertMessage(Long userId , String messageId , List<MessageType> messageTypes) {
        if(!isUserHaveTheMessage(userId, messageId)){
            jdbcTemplate.update(
                    "insert into user.messages (user_id , message_id) values (?,?)" ,
                    userId , messageId + userId);
        }

        List<String> responses = jdbcTemplate
                .queryForList(
                        "select message_type from user.type_messages where message_id = ?" ,
                        String.class ,
                        messageId + userId
                );

        for (int i = messageTypes.size() - 1 ; i >= 0 ; i--)
            for (String s : responses)
                if (messageTypes.get(i).name().equalsIgnoreCase(s))
                    messageTypes.remove(i);


        for (MessageType messageType : messageTypes)
            jdbcTemplate.update(
                    "insert into user.type_messages values (?,?)" ,
                    messageId + userId , messageType.name()
            );

        List<MessageType> allTypes = new ArrayList<>();
        for (String string : responses)
            allTypes.add(MessageType.valueOf(string));
        allTypes.addAll(messageTypes);

        return new MessageResponse(userId, messageId , allTypes);
    }

    @Override
    public Boolean isUserHaveTheMessage(Long userId, String messageId) {
        List<Long> responses = jdbcTemplate.queryForList(
                "select user_id from user.messages where message_id = ?" ,
                Long.class ,
                messageId + userId
        );
        return responses.size() == 1;
    }

    @Override
    public MessageResponse deleteMessage(Long userId, String messageId , List<MessageType> messageTypes) {
        if (!isUserHaveTheMessage(userId, messageId)){
            throw new UserMessageNotFoundException(
                    "The Message with id '" + messageId + "' for user '" + userId + "' not found."
            );
        }
        jdbcTemplate.update(
                "delete from user.messages where user_id = ? and message_id = ?" ,
                userId ,
                messageId + userId
        );
        messageTypes.forEach(messageType ->
                    jdbcTemplate.update(
                            "delete from user.type_messages where message_id = ? and message_type = ?" ,
                            messageId + userId ,
                            messageType.name()
                    )
                );
        return new MessageResponse(
                userId ,
                messageId ,
                messageTypes
        );
    }

    @Override
    public List<MessageResponse> getMessagesForUser(Long userId) {
        return jdbcTemplate.query(
                "select * from user.messages where user_id = ?" ,
                (rs, rowNum) -> new MessageResponse(
                        rs.getLong("user_id") ,
                        rs.getString("message_id"),
                        new ArrayList<>(
                                jdbcTemplate.query(
                                        "select * from user.type_messages where message_id = ?" ,
                                        (rs1, rowNum1) -> MessageType.valueOf(rs1.getString("message_type").toUpperCase()),
                                        rs.getString("message_id")
                                )
                        )
                ) ,
                userId
        );
    }
}
