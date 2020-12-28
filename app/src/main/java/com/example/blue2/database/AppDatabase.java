package com.example.blue2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

<<<<<<< Updated upstream
@Database(entities = {Conversation.class, Message.class }, version = 1)
=======
@Database(entities = {Conversation.class, Message.class},exportSchema = false, version = 1)
>>>>>>> Stashed changes
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "database-blue-chat")
                            .allowMainThreadQueries().build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract ConversationDao conversationDao();

}
