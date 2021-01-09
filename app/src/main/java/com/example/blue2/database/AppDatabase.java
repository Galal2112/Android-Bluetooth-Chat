package com.example.blue2.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Class responsible for creating the database and controlling its version
 *
 */
@Database(entities = {Conversation.class, Message.class},exportSchema = false, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * Shared instance from AppDatabase used by the app
     */
    private static volatile AppDatabase INSTANCE;

    /**
     * Return or create database instance.
     * @param context Application or Activity context
     * @return shared AppDatabase instance
     */
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

    /**
     * abstract method to get the data access object.
     */
    public abstract ConversationDao conversationDao();

}
