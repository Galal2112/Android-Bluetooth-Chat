package com.example.blue2.mvvm.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.blue2.R;
import com.example.blue2.database.ConversationResult;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * the data source for the conversations recycler View
 */
public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    // conversations list
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

    // create cell view holder
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    // bind data for the conversations appearing on the screen
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindItem(mConversations.get(position));
    }

    @Override
    public int getItemCount() {
        return mConversations.size();
    }

    /**
     * Conversation ViewHolder.
     */
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
            // display device name
            nameTextView.setText(conversation.opponentName);
            // display first character of device name as image
            if (conversation.opponentName != null) {
                shortcutTextView.setText(String.valueOf(conversation.opponentName.charAt(0)));
            } else {
                shortcutTextView.setText(" ");
            }
            // display last message in the conversation
            messageTextView.setText(conversation.textBody);
        }
    }
}
