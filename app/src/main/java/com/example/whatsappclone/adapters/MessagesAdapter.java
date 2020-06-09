package com.example.whatsappclone.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.models.Message;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {
    ArrayList<Message> messagesList;
    Context context;

    public MessagesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MessagesViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_ticket, parent, false)); //Here write the item layout
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {
        if (messagesList.get(position).isMessageFromMe()) {
            holder.myMessage.setText(messagesList.get(position).getMessageContent());
            holder.myMessageDate.setText(messagesList.get(position).getDate());
            holder.friendMessage.setVisibility(View.GONE);
        } else {
            holder.friendMessage.setText(messagesList.get(position).getMessageContent());
            holder.friendMessageDate.setText(messagesList.get(position).getDate());
            holder.myMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        if (messagesList == null) return 0;
        return messagesList.size();
    }

    public void setList(ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
        notifyDataSetChanged();
    }

    public void setInsertedList(ArrayList<Message> messagesList){
        this.messagesList = messagesList;
        if (!(messagesList.size() < 1))
            notifyItemInserted(messagesList.size()-1);
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        //initialize the view of the item as shown below
        TextView myMessage;
        TextView friendMessage;
        TextView friendMessageDate, myMessageDate;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.my_message);
            friendMessage = itemView.findViewById(R.id.friend_message);
            friendMessageDate = itemView.findViewById(R.id.friend_message_date);
            myMessageDate = itemView.findViewById(R.id.my_message_date);// this is the whole item which we will click on
        }
    }
}
