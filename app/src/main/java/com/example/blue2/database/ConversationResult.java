package com.example.blue2.database;

import androidx.room.ColumnInfo;

/**
 * projection result for conversation list query.
 */
public class ConversationResult {
    @ColumnInfo(name = "conversation_id")
    public int conversationId;
    @ColumnInfo(name = "opponent_name")
    public String opponentName;
    @ColumnInfo(name = "opponent_address")
    public String opponentAddress;
    @ColumnInfo(name = "text_body")
    public String textBody;
    @ColumnInfo(name = "sender_address")
    public String senderAddress;
}
