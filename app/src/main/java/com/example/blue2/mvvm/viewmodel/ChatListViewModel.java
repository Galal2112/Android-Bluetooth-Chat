package com.example.blue2.mvvm.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.blue2.database.AppDatabase;
import com.example.blue2.database.ConversationDao;
import com.example.blue2.database.ConversationResult;

import java.util.List;

public class ChatListViewModel extends AndroidViewModel {
    private LiveData<List<ConversationResult>> mConversations;
    private ConversationDao mDao = AppDatabase.getDatabase(getApplication()).conversationDao();

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
