package com.example.blue2.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
<<<<<<< Updated upstream
=======
import androidx.room.Index;
>>>>>>> Stashed changes
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(foreignKeys = @ForeignKey(entity = Conversation.class,
        parentColumns = "conversation_id",
        childColumns = "parent_conversation_id",
<<<<<<< Updated upstream
        onDelete = CASCADE))
=======
        onDelete = CASCADE),
        indices = {
                @Index(name = "conversationId_index", value = {"parent_conversation_id"})
        })
>>>>>>> Stashed changes
public class Message {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "message_id")
    public long messageId;
    @ColumnInfo(name = "parent_conversation_id")
    public long parentConversationId;
    @ColumnInfo(name = "text_body")
    public String textBody;
    @ColumnInfo(name = "sender_address")
    public String senderAddress;
}
