package com.example.blue2.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ConversationDao {
    @Query("SELECT conversation_id, opponent_name, opponent_address, text_body, sender_address FROM conversation LEFT JOIN message ON conversation_id = parent_conversation_id GROUP BY conversation_id HAVING MAX(message_id)")
    LiveData<List<ConversationResult>> getAll();

    @Query("SELECT * FROM message WHERE parent_conversation_id = :conversationId")
    LiveData<List<Message>> getConversationMessages(long conversationId);

    @Query("SELECT * FROM conversation WHERE opponent_address = :address")
    Conversation getConversation(String address);

    @Insert
    long insert(Conversation conversation);

    @Insert
    long insert(Message message);
}
