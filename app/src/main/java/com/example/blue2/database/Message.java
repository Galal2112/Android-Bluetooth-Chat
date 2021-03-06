package com.example.blue2.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

/**
 * Table Message, it has @ManyToOne relation with conversation
 * @ForeignKey "conversation_id".
 * onDelete CASCADE, if a row of the referenced table is deleted, then all matching rows in the referencing table are deleted.
 */

@Entity(foreignKeys = @ForeignKey(entity = Conversation.class,
        parentColumns = "conversation_id",
        childColumns = "parent_conversation_id",
        onDelete = CASCADE),
        indices = {
                @Index(name = "conversationId_index", value = {"parent_conversation_id"})
        })
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
