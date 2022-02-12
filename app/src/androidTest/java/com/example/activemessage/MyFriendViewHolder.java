package com.example.activemessage;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFriendViewHolder extends RecyclerView.ViewHolder {
CircleImageView profileImageUrl;
TextView UserName,UserStatus;


    public MyFriendViewHolder(@NonNull View itemView) {
        super(itemView);
        profileImageUrl=itemView.findViewById(R.id.users_profile_image);
        UserName=itemView.findViewById(R.id.user_profile_name);
        UserStatus=itemView.findViewById(R.id.user_status);
    }
}
