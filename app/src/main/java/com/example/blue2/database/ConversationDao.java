package com.example.blue2.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ConversationDao {
    @Query("SELECT conversation_id, opponent_name, opponent_address, text_body, sender_address FROM conversation LEFT JOIN message ON conversation_id = parent_conversation_id GROUP BY conversation_id HAVING MAX(message_id)")
    List<ConversationResult> getAll();

    @Query("SELECT * FROM message WHERE parent_conversation_id = :conversationId")
    List<Message> getConversationMessages(int conversationId);

    @Insert
    long insert(Conversation conversation);

    @Insert
    long insert(Message message);
}
