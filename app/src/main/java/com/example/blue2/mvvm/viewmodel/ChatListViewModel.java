package com.example.blue2.mvvm.viewmodel;

import android.app.Application;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.ConversationResult;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * this class is to access data from the database
 */
public class ChatListViewModel extends AndroidViewModel {
    private LiveData<List<ConversationResult>> mConversations;

    private ConversationDao mDao = AppDatabase.getDatabase(getApplication()).conversationDao();


    /**
     * ChatListViewModel constructor. and call the super constructor.
     * @param application the application context.
     */
    public ChatListViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<ConversationResult>> getConversations() {
        if (mConversations == null) {
            mConversations = mDao.getAll();
        }
        return mConversations;
    }
}
