package com.example.blue2.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Conversation Table
 */
@Entity
public class Conversation {
    /**
     * conversationId has auto generate, "a number that increases every time"
     * and has name conversation_id in DataBase
     */
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "conversation_id")
    public long conversationId;
    @ColumnInfo(name = "opponent_name")
    public String opponentName;
    @ColumnInfo(name = "opponent_address")
    public String opponentAddress;
}
