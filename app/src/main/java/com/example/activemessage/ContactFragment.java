package com.example.activemessage;

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


public class ContactFragment extends Fragment {
private View ContactsView;
private RecyclerView myContactsList;
private DatabaseReference ContactsRef,UsersRef;
private FirebaseAuth mAuth;
private String currentUserID;

    public ContactFragment() {

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ContactsView= inflater.inflate(R.layout.fragment_contact, container, false);
        myContactsList=(RecyclerView) ContactsView.findViewById(R.id.contacts_list);
        myContactsList.setLayoutManager(new LinearLayoutManager(getContext()));
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        ContactsRef= FirebaseDatabase.getInstance().getReference().child("Contacts").child(currentUserID);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        return ContactsView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions options=new FirebaseRecyclerOptions.Builder<Contacts>().setQuery(ContactsRef,Contacts.class).build();
        FirebaseRecyclerAdapter<Contacts,ContactsViewHolder> adapter=new FirebaseRecyclerAdapter<Contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactsViewHolder holder, int position, @NonNull Contacts model)
            {
                String usersIDs=getRef(position).getKey();
                UsersRef.child(usersIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                    if (snapshot.exists())
                    {


                        if (snapshot.child("userState").hasChild("state"))
                        {
                            String state=snapshot.child("userState").child("state").getValue().toString();
                            String date=snapshot.child("userState").child("date").getValue().toString();
                            String time=snapshot.child("userState").child("time").getValue().toString();

                            if (state.equals("Çevrimiçi"))
                            {
                                holder.onlineIcon.setVisibility(View.VISIBLE);
                            }
                            else if (state.equals("Çevrimdışı"))
                            {
                                holder.onlineIcon.setVisibility(View.INVISIBLE);
                            }
                        }
                        else
                        {
                            holder.onlineIcon.setVisibility(View.INVISIBLE);
                        }




                        if (snapshot.hasChild("profileImage"))
                        {
                            String UserImage=snapshot.child("profileImage").getValue().toString();
                            String ProfileStatus=snapshot.child("status").getValue().toString();
                            String ProfileName=snapshot.child("username").getValue().toString();

                            holder.userName.setText(ProfileName);
                            holder.userStatus.setText(ProfileStatus);
                            Picasso.get().load(UserImage).placeholder(R.drawable.profile).into(holder.profileImage);
                        }
                        else
                        {
                            String ProfileStatus=snapshot.child("status").getValue().toString();
                            String ProfileName=snapshot.child("username").getValue().toString();

                            holder.userName.setText(ProfileName);
                            holder.userStatus.setText(ProfileStatus);

                        }
                    }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error)
                    {

                    }
                });
            }

            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                ContactsViewHolder viewHolder= new ContactsViewHolder(view);
                return viewHolder;
            }
        };

        myContactsList.setAdapter(adapter);
        adapter.startListening();
    }
public  static class ContactsViewHolder extends  RecyclerView.ViewHolder
{
    TextView userName,userStatus;
    CircleImageView profileImage;
    ImageView onlineIcon;
    public ContactsViewHolder(@NonNull View itemView) {
        super(itemView);
        userName=itemView.findViewById(R.id.user_profile_name);
        userStatus=itemView.findViewById(R.id.user_status);
        profileImage=itemView.findViewById(R.id.users_profile_image);
        onlineIcon=(ImageView) itemView.findViewById(R.id.user_online_status);
         }
    }
}