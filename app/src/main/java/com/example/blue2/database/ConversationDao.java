package com.example.blue2.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;


/**
 * Conversation data access object interface.
 */
@Dao
public interface ConversationDao {
    /**
     * get all conversations as live data to update automatically
     * LiveData is a data holder class that can be observed within a given lifecycle.
     * @return list of conversations
     */
    @Query("SELECT conversation_id, opponent_name, opponent_address, text_body, sender_address FROM conversation LEFT JOIN message ON conversation_id = parent_conversation_id GROUP BY conversation_id HAVING MAX(message_id)")
    LiveData<List<ConversationResult>> getAll();

    /**
     * get messages inside a specific conversation as live data to update automatically
     * @return list of conversation messages
     */
    @Query("SELECT * FROM message WHERE parent_conversation_id = :conversationId")
    LiveData<List<Message>> getConversationMessages(long conversationId);

    // Get conversation created with specific device
    @Query("SELECT * FROM conversation WHERE opponent_address = :address")
    Conversation getConversation(String address);

    @Insert
    long insert(Conversation conversation);

    @Insert
    long insert(Message message);
}
