package com.example.activemessage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class FriendsActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private RecyclerView FriendsRecycleList;
    private DatabaseReference usersRef;


    /*initialized data*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        FriendsRecycleList = (RecyclerView) findViewById(R.id.friends_recycler_list);
        FriendsRecycleList.setLayoutManager(new LinearLayoutManager(this));
        mToolbar = (Toolbar) findViewById(R.id.friends_toolbar);
        setSupportActionBar(mToolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Arkadaşlar");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options=new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(usersRef,Contacts.class)
                .build();
        FirebaseRecyclerAdapter<Contacts,FriendViewHolder> adapter= new FirebaseRecyclerAdapter<Contacts, FriendViewHolder>(options) {
            @Override
            /*firebaseden kullanıcı datası çekme*/
            protected void onBindViewHolder(@NonNull FriendViewHolder holder, int position, @NonNull Contacts model)
            {
                holder.username.setText(model.getUsername());
                holder.userStatus.setText(model.getStatus());
                Picasso.get().load(model.getProfileImage()).placeholder(R.drawable.profile).into(holder.profileImage);
                /*kullanıcı bilgilerini görmek için profile tıklama*/
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        String visit_user_id=getRef(position).getKey();
                        Intent profileIntent = new Intent(FriendsActivity.this,ProfileEditActivity.class);
                        profileIntent.putExtra("visit_user_id",visit_user_id);
                        startActivity(profileIntent);
                    }
                });
            }

            @NonNull
            @Override
            public FriendViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                FriendViewHolder viewHolder=new FriendViewHolder(view);
                return  viewHolder;
            }
        };
        FriendsRecycleList.setAdapter(adapter);

        adapter.startListening();
    }
    public static  class  FriendViewHolder extends  RecyclerView.ViewHolder
    {
        TextView username,userStatus;
        CircleImageView profileImage;
        public FriendViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
        }
    }
}