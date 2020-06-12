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
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesViewHolder> {

    public static final int RECEIVED_MESSAGE = 100;
    public static final int SEND_MESSAGE = 200;

    ArrayList<Message> messagesList;
    Context context;

    public MessagesAdapter(ArrayList<Message> messagesList) {
        this.messagesList = messagesList;
    }

    public MessagesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == SEND_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_sent_ticket, parent, false);
        }else if (viewType == RECEIVED_MESSAGE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_recieved_ticket, parent, false);
        }
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {
        holder.messageContent.setText(messagesList.get(position).getMessageContent());
        holder.messageDate.setText(messagesList.get(position).getDate());
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

    @Override
    public int getItemViewType(int position) {
        Message message = messagesList.get(position);
        if (message.isMessageFromMe())
            return SEND_MESSAGE;
        else
            return RECEIVED_MESSAGE;
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        //initialize the view of the item as shown below
        TextView messageContent , messageDate;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            messageDate = itemView.findViewById(R.id.message_date);// this is the whole item which we will click on
        }
    }
}
