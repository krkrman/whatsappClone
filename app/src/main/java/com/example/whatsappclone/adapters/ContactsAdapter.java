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
import com.example.whatsappclone.models.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.UserViewHolder> implements Filterable {
    List<User> usersList;
    List<User> usersListFull;
    Context context;

    public ContactsAdapter( Context context) {
        usersList = new ArrayList<>();
        usersListFull = new ArrayList<>();
        // we get the context to apply some functions which we need an activity to be done like startActivity
        this.context = context;
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UserViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item , parent , false)); //Here write the item layout

    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, final int position) {
        // here make what you want to do in the item including what happened when clicking on the item
        holder.userNameTxtView.setText(usersList.get(position).getUsername());
        holder.userLastMessageTxtView.setText(usersList.get(position).getAbout());
        if (!usersList.get(position).getImageUrl().equals(""))
            Picasso.get().load(usersList.get(position).getImageUrl())
                    .placeholder(R.drawable.default_profile_image).into(holder.userImageView);
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChatActivity.class);
                intent.putExtra("User",usersList.get(position));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public void setList(List<User> usersList){
        this.usersList = usersList;
        this.usersListFull = new ArrayList<>(usersList); // we do that to take a copy from data if i assign it , it will change with it

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
                filteredList = usersListFull;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (User item : usersListFull) {
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
            usersList.clear();
            usersList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        //initialize the view of the item as shown below
        TextView userNameTxtView;
        TextView userLastMessageTxtView;
        ImageView userImageView;
        LinearLayout linearLayout;
        public UserViewHolder(@NonNull View itemView ) {
            super(itemView);
            userNameTxtView = itemView.findViewById(R.id.name_txt_view);
            userLastMessageTxtView = itemView.findViewById(R.id.last_message_txt_view);
            userImageView = itemView.findViewById(R.id.item_pic);
            linearLayout = itemView.findViewById(R.id.group_item_linear_layout);// this is the whole item which we will click on
        }
    }

}
