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

import com.example.whatsappclone.ChatActivities.GroupChatActivity;
import com.example.whatsappclone.R;
import com.example.whatsappclone.models.GroupModelItem;

import java.util.ArrayList;
import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> implements Filterable {
    List<GroupModelItem> groupList;
    List<GroupModelItem> groupListFull;
    Context context;

    public GroupListAdapter(List<GroupModelItem> groupModelItemArrayList , Context context) {
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
        holder.groupNameTxtView.setText(groupList.get(position).getGroupName());
        holder.groupLastMessageTxtView.setText(groupList.get(position).getLastMessage());
        // holder.groupImageView.setImageResource(groupList.get(position).getImage());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupName",groupList.get(position).getGroupName());
                intent.putExtra("groupImage",groupList.get(position).getImage());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public void setList(List<GroupModelItem> grouplist){
        this.groupList = grouplist;
        this.groupListFull = new ArrayList<>(grouplist); // we do that to take a copy from data if i assign it , it will change with it
        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return groupsFilter;
    }

    private Filter groupsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<GroupModelItem> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList = groupListFull;
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (GroupModelItem item : groupListFull) {
                    if (item.getGroupName().toLowerCase().contains(filterPattern)) {
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
            groupList.clear();
            groupList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class GroupViewHolder extends RecyclerView.ViewHolder {

        //initialize the view of the item as shown below
        TextView groupNameTxtView;
        TextView groupLastMessageTxtView;
        ImageView groupImageView;
        LinearLayout linearLayout;
        public GroupViewHolder(@NonNull View itemView ) {
            super(itemView);
            groupNameTxtView = itemView.findViewById(R.id.name_txt_view);
            groupLastMessageTxtView = itemView.findViewById(R.id.last_message_txt_view);
            groupImageView = itemView.findViewById(R.id.item_pic);
            linearLayout = itemView.findViewById(R.id.group_item_linear_layout);// this is the whole item which we will click on
        }
    }



}
