package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.whatsappclone.ChatActivities.ChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.Settings.ProfileActivity;
import com.example.whatsappclone.models.User;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.chatViewHolder> implements Filterable {
    List<User> chatsList;
    List<User> chatsListFull;
    Context context;

    public ChatListAdapter(Context context) {
        // we get the context to apply some functions which we need an activity to be done like startActivity
        chatsList = new ArrayList<>();
        this.context = context;
    }

    @NonNull
    @Override
    public chatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new chatViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item, parent, false)); //Here write the item layout

    }

    @Override
    public void onBindViewHolder(@NonNull chatViewHolder holder, final int position) {
        // here make what you want to do in the item including what happened when clicking on the item
        holder.friendName.setText(chatsList.get(position).getUsername());
        holder.lastMessage.setText(chatsList.get(position).getAbout());
        if (!chatsList.get(position).getImageUrl().equals(""))
            Picasso.get().load(chatsList.get(position).getImageUrl())
                    .placeholder(R.drawable.default_profile_image).into(holder.friendImage);
        holder.friendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("isItMe", false);
                intent.putExtra("User", chatsList.get(position));
                context.startActivity(intent);
            }
        });

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("User", chatsList.get(position));
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public void setList(List<User> chatsList) {
        this.chatsList = chatsList;
        this.chatsListFull = new ArrayList<>(chatsList); // we do that to take a copy from data if i assign it , it will change with it
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return chatsFilter;
    }

    private Filter  chatsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<User> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList = chatsListFull;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : chatsListFull) {
                    if (item.getUsername().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chatsList.clear();
            chatsList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class chatViewHolder extends RecyclerView.ViewHolder {

        //initialize the view of the item as shown below
        TextView friendName;
        TextView lastMessage;
        ImageView friendImage;
        LinearLayout linearLayout;

        public chatViewHolder(@NonNull View itemView) {
            super(itemView);
            friendName = itemView.findViewById(R.id.name_txt_view);
            lastMessage = itemView.findViewById(R.id.last_message_txt_view);
            friendImage = itemView.findViewById(R.id.item_pic);
            linearLayout = itemView.findViewById(R.id.group_item_linear_layout);// this is the whole item which we will click on
        }
    }
}

