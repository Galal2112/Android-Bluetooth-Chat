package com.example.blue2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.blue2.database.Message;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder>{

    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECIEVED = 2;
    private List<Message> mMessages;

    public MessagesAdapter(List<Message> messages){
        this.mMessages = messages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        if (viewType == VIEW_TYPE_RECIEVED){
            layout = R.layout.item_received_message;
        } else {
            layout = R.layout.item_sent_message;
        }
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(mMessages.get(position));
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = mMessages.get(position);
        if (message.senderAddress == null){
            return VIEW_TYPE_SENT;
        }
        return VIEW_TYPE_RECIEVED;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mMessageTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.mMessageTextView = itemView.findViewById(R.id.tv_message);
        }

        void bind (Message message){
            mMessageTextView.setText(message.textBody);
        }
    }

}
