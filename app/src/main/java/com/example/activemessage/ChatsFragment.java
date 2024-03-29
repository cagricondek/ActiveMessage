package com.example.activemessage;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsFragment extends Fragment {

    private View PrivateChatsView;
    private RecyclerView chatsList;
    private DatabaseReference ChatsRef,UsersRef;
    private FirebaseAuth mAuth;
    private String currentUserID;

    public ChatsFragment()
    {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


      PrivateChatsView= inflater.inflate(R.layout.fragment_chats, container, false);
      mAuth=FirebaseAuth.getInstance();
      currentUserID=mAuth.getCurrentUser().getUid();
      ChatsRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
      UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
      chatsList=(RecyclerView) PrivateChatsView.findViewById(R.id.chats_list);

      chatsList.setLayoutManager(new LinearLayoutManager(getContext()));
      return PrivateChatsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options= new FirebaseRecyclerOptions.Builder<Contacts>().
                setQuery(ChatsRef,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,ChatsViewHolder> adapter= new FirebaseRecyclerAdapter<Contacts, ChatsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ChatsViewHolder holder, int position, @NonNull Contacts model)
            {
            final String usersIDs=getRef(position).getKey();
                final String[] retImage = {"default_image"};
            UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists())
                    {
                        if (snapshot.hasChild("profileImage"))
                        {
                            retImage[0] = snapshot.child("profileImage").getValue().toString();
                            Picasso.get().load(retImage[0]).into(holder.profileImage);
                        }



                        final String retName = snapshot.child("fullname").getValue().toString();
                        final  String retStatus=snapshot.child("status").getValue().toString();

                        holder.userName.setText(retName);
                        holder.userStatus.setText("Son Görülme:" + "\n" + "Tarih" + "Saat");


                            /*kullanıcı son görülme ve çevrimiçi durumu*/
                        if (snapshot.child("userState").hasChild("state"))
                        {
                            String state=snapshot.child("userState").child("state").getValue().toString();
                            String date=snapshot.child("userState").child("date").getValue().toString();
                            String time=snapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("online"))
                            {
                                holder.userStatus.setText("Çevrimiçi");
                                holder.user_online_status.setVisibility(View.VISIBLE);
                            }
                            else if (state.equals("Çevrimdışı"))
                            {
                                holder.userStatus.setText("Son Görülme:"  + date + " "+time);
                                holder.user_online_status.setVisibility(View.INVISIBLE);
                            }
                        }
                        else
                        {
                            holder.userStatus.setText("Çevrimdışı");
                        }



                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("visit_user_id", usersIDs);
                                chatIntent.putExtra("visit_user_name", retName);
                                if(retName.length()>10){

                                    retName.substring(0,10);
                                }
                                chatIntent.putExtra("visit_image", retImage[0]);
                                startActivity(chatIntent);
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
            }

            @NonNull
            @Override
            public ChatsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
               View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                return new ChatsViewHolder(view);
            }
        };
        chatsList.setAdapter(adapter);
        adapter.startListening();
    }


    public static  class  ChatsViewHolder extends  RecyclerView.ViewHolder
    {
        CircleImageView profileImage;
        TextView userStatus,userName;
        ImageView user_online_status;
        public ChatsViewHolder(@NonNull View itemView) {
            super(itemView);


            profileImage=itemView.findViewById(R.id.users_profile_image);
            userStatus=itemView.findViewById(R.id.user_status);
            userName=itemView.findViewById(R.id.user_profile_name);
            user_online_status=itemView.findViewById(R.id.user_online_status);

        }
    }
}