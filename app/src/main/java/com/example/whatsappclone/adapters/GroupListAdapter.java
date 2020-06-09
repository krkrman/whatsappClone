package com.example.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whatsappclone.ChatActivities.GroupChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.models.GroupModelItem;

import java.util.ArrayList;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {
    ArrayList<GroupModelItem> groupList;
    Context context;

    public GroupListAdapter(ArrayList<GroupModelItem> groupModelItemArrayList , Context context) {
        this.groupList = groupModelItemArrayList;
        // we get the context to apply some functions which we need an activity to be done like startActivity
        this.context = context;
    }


    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GroupViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_view_item , parent , false)); //Here write the item layout

    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, final int position) {
        // here make what you want to do in the item including what happened when clicking on the item
        holder.userNameTxtView.setText(groupList.get(position).getGroupName());
        holder.userLastMessageTxtView.setText(groupList.get(position).getLastMessage());
        holder.userImageView.setImageResource(groupList.get(position).getImage());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupName",groupList.get(position).getGroupName());
                intent.putExtra("groupImage",groupList.get(position).getImage());
                intent.putExtra("participatedPeople",groupList.get(position).getParticipatedPeople());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void setList(ArrayList<GroupModelItem> grouplist){
        this.groupList = grouplist;
        notifyDataSetChanged();
    }



    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        //initialize the view of the item as shown below
        TextView userNameTxtView;
        TextView userLastMessageTxtView;
        ImageView userImageView;
        LinearLayout linearLayout;
        public GroupViewHolder(@NonNull View itemView ) {
            super(itemView);
            userNameTxtView = itemView.findViewById(R.id.name_txt_view);
            userLastMessageTxtView = itemView.findViewById(R.id.last_message_txt_view);
            userImageView = itemView.findViewById(R.id.item_pic);
            linearLayout = itemView.findViewById(R.id.group_item_linear_layout);// this is the whole item which we will click on
        }
    }

}
