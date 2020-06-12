package com.example.whatsappclone.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.R;
import com.example.whatsappclone.generalClasses.SharedPreference;
import com.example.whatsappclone.models.Message;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class GroupMessagesAdapter extends RecyclerView.Adapter<GroupMessagesAdapter.MessagesViewHolder> {

    public static final int RECEIVED_MESSAGE = 100;
    public static final int SEND_MESSAGE = 200;
    SharedPreference sharedPreference;
    ArrayList<Message> messagesList;
    Context context;

    public GroupMessagesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public MessagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == SEND_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.messages_sent_ticket, parent, false);
        } else if (viewType == RECEIVED_MESSAGE) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_messages_recieved, parent, false);
        }
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MessagesViewHolder holder, final int position) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        holder.messageContent.setText(messagesList.get(position).getMessageContent());
        holder.messageDate.setText(messagesList.get(position).getDate());
        if (getItemViewType(position) == RECEIVED_MESSAGE) {
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference();//myRef is the head of
            myRef.child("Users").child(firebaseUser.getUid()).child("imageUrl").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String imageUrl = dataSnapshot.getValue(String.class);
                    if (!imageUrl.equals("")) {
                        Picasso.get().load(imageUrl).fit().centerCrop()
                                .placeholder(R.drawable.default_profile_image)
                                .error(R.drawable.default_profile_image)
                                .into(holder.imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("image put", "happened");
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
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

    /*public void setInsertedList(ArrayList<Message> messagesList){
        this.messagesList = messagesList;
        if (!(messagesList.size() < 1))
            notifyItemInserted(messagesList.size()-1);
    }*/

    @Override
    public int getItemViewType(int position) {
        sharedPreference = new SharedPreference(context);
        Message message = messagesList.get(position);
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (message.getSenderID().equals(firebaseUser.getUid()))
            return SEND_MESSAGE;
        else
            return RECEIVED_MESSAGE;
    }

    public static class MessagesViewHolder extends RecyclerView.ViewHolder {
        //initialize the view of the item as shown below
        TextView messageContent, messageDate;
        ImageView imageView;

        public MessagesViewHolder(@NonNull View itemView) {
            super(itemView);
            messageContent = itemView.findViewById(R.id.message_content);
            messageDate = itemView.findViewById(R.id.message_date);// this is the whole item which we will click on
            imageView = itemView.findViewById(R.id.friend_image);
        }
    }
}
