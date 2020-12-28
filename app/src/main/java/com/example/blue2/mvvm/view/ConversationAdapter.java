package com.example.blue2.mvvm.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.blue2.R;
import com.example.blue2.database.ConversationResult;

import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    private List<ConversationResult> mConversations;

    public ConversationAdapter(List<ConversationResult> conversations) {
        this.mConversations = conversations;
    }

    public void setConversations(List<ConversationResult> conversations) {
        this.mConversations = conversations;
    }

    public List<ConversationResult> getConversations() {
        return mConversations;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mConversations.get(position));
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView shortcutTextView;
        private final TextView nameTextView;
        private final TextView messageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.tv_name);
            shortcutTextView = itemView.findViewById(R.id.tv_shortcut);
            messageTextView = itemView.findViewById(R.id.tv_last_message);
        }

        void bindItem(ConversationResult conversation) {
            nameTextView.setText(conversation.opponentName);
            if (conversation.opponentName != null) {
                shortcutTextView.setText(String.valueOf(conversation.opponentName.charAt(0)));
            } else {
                shortcutTextView.setText(" ");
            }
            messageTextView.setText(conversation.textBody);
        }
    }
}
