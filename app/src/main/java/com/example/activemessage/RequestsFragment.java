package com.example.activemessage;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;


public class RequestsFragment extends Fragment {
private View RequestsFragmentView;
private RecyclerView myRequestsList;
private DatabaseReference ChatRequestsRef,UsersRef,ContactsRef;
private FirebaseAuth mAuth;
private String currentUserID;
    public RequestsFragment()
    {

    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        RequestsFragmentView= inflater.inflate(R.layout.fragment_requests, container, false);
        ChatRequestsRef= FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        mAuth=FirebaseAuth.getInstance();
        currentUserID=mAuth.getCurrentUser().getUid();
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        myRequestsList=(RecyclerView)RequestsFragmentView.findViewById(R.id.chat_requests_list);
        myRequestsList.setLayoutManager(new LinearLayoutManager(getContext()));
        return RequestsFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerOptions<Contacts> options= new FirebaseRecyclerOptions.Builder<Contacts>()
                .setQuery(ChatRequestsRef.child(currentUserID),Contacts.class).build();

        FirebaseRecyclerAdapter<Contacts,RequestsViewHolder>adapter=new FirebaseRecyclerAdapter<Contacts, RequestsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull RequestsViewHolder holder, int position, @NonNull Contacts model)
            {
                holder.itemView.findViewById(R.id.request_accept_btn).setVisibility(View.VISIBLE);
                holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.VISIBLE);

                final String list_user_id=getRef(position).getKey();
                /*databaseden gidip kontrol etme*/
                DatabaseReference getTypeRef=getRef(position).child("request_type").getRef();

                getTypeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.exists())
                        {
                            String type=snapshot.getValue().toString();
                            if (type.equals("received"))
                            {
                            UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if (snapshot.hasChild("profileImage"))
                                    {

                                        final String requestUserProfileImage=snapshot.child("profileImage").getValue().toString();

                                        Picasso.get().load(requestUserProfileImage).into(holder.profileImage);
                                    }
                                    final String requestUserName=snapshot.child("username").getValue().toString();
                                    final String requestUserStatus=snapshot.child("status").getValue().toString();
                                    holder.userName.setText(requestUserName);
                                    holder.userStatus.setText(requestUserStatus);

                                    /*arkadaşlık isteğine tıklayınca*/
                                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v)
                                        {
                                            CharSequence options[]=new CharSequence[]
                                                    {
                                                            "Onayla","İptal"
                                                    };
                                            AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                            builder.setTitle(requestUserName+"Arkadaşlık İsteği");
                                            /*gelen arkadaşlık istegine tıkladıktan sonra dialog arayüzü açma*/
                                            builder.setItems(options, new DialogInterface.OnClickListener()
                                            {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i)
                                                {
                                                if(i==0)
                                                    {
                                                    ContactsRef.child(currentUserID).child(list_user_id).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                        if (task.isSuccessful())
                                                        {
                                                            ContactsRef.child(list_user_id).child(currentUserID).child("Contact").setValue("Saved").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task)
                                                                {
                                                                    if (task.isSuccessful())
                                                                    {
                                                                        ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                                .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                        {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task)
                                                                            {
                                                                                if (task.isSuccessful())
                                                                                {
                                                                                    ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                    {
                                                                                        @Override
                                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                                        {
                                                                                            if (task.isSuccessful())
                                                                                            {
                                                                                                Toast.makeText(getContext(), "İstek Kabul Edildi.", Toast.LENGTH_SHORT).show();
                                                                                            }
                                                                                        }
                                                                                    });
                                                                                }
                                                                            }
                                                                        });
                                                                    }

                                                                }
                                                            });
                                                        }

                                                        }
                                                    });
                                                    }

                                                if(i==1)
                                                    {

                                                                    ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                            .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                    {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task)
                                                                        {
                                                                            if (task.isSuccessful())
                                                                            {
                                                                                ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                                {
                                                                                    @Override
                                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                                    {
                                                                                        if (task.isSuccessful())
                                                                                        {
                                                                                            Toast.makeText(getContext(), "İstek İptal Edildi.", Toast.LENGTH_SHORT).show();
                                                                                        }
                                                                                    }
                                                                                });
                                                                            }
                                                                        }
                                                                    });
                                                    }
                                                }
                                            });
                                            builder.show();
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            }

                            else if(type.equals("sent"))
                                {
                                    Button request_sent_btn=holder.itemView.findViewById(R.id.request_accept_btn);
                                    request_sent_btn.setText("İstek Gönderildi");
                                    holder.itemView.findViewById(R.id.request_cancel_btn).setVisibility(View.INVISIBLE);
                                    UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot)
                                        {
                                            if (snapshot.hasChild("profileImage"))
                                            {

                                                final String requestUserProfileImage=snapshot.child("profileImage").getValue().toString();

                                                Picasso.get().load(requestUserProfileImage).into(holder.profileImage);
                                            }
                                            final String requestUserName=snapshot.child("username").getValue().toString();
                                            final String requestUserStatus=snapshot.child("status").getValue().toString();
                                            holder.userName.setText(requestUserName);
                                            holder.userStatus.setText("Arkadaşlık İsteği: "+requestUserName);

                                            /*arkadaşlık isteğine tıklayınca*/
                                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v)
                                                {
                                                    CharSequence options[]=new CharSequence[]
                                                            {
                                                                    "İsteği İptal Et"
                                                            };
                                                    AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                                                    builder.setTitle("İstek Zaten Gönderildi.");
                                                    /*gelen arkadaşlık istegine tıkladıktan sonra dialog arayüzü açma*/
                                                    builder.setItems(options, new DialogInterface.OnClickListener()
                                                    {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i)
                                                        {


                                                            if(i==0)
                                                            {

                                                                ChatRequestsRef.child(currentUserID).child(list_user_id)
                                                                        .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task)
                                                                    {
                                                                        if (task.isSuccessful())
                                                                        {
                                                                            ChatRequestsRef.child(list_user_id).child(currentUserID)
                                                                                    .removeValue().addOnCompleteListener(new OnCompleteListener<Void>()
                                                                            {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task)
                                                                                {
                                                                                    if (task.isSuccessful())
                                                                                    {
                                                                                        Toast.makeText(getContext(), "İstek İptal Edildi.", Toast.LENGTH_SHORT).show();
                                                                                    }
                                                                                }
                                                                            });
                                                                        }
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    });
                                                    builder.show();
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }

            @NonNull
            @Override
            public RequestsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view=LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.users_display_layout,viewGroup,false);
                RequestsViewHolder holder=new RequestsViewHolder(view);
                return  holder;
            }
        };
        myRequestsList.setAdapter(adapter);
        adapter.startListening();
    }
    public static class RequestsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName,userStatus;
        CircleImageView profileImage;
        Button AcceptButton,CancelButton;
        public RequestsViewHolder(@NonNull View itemView)
        {
            super(itemView);
            userName=itemView.findViewById(R.id.user_profile_name);
            userStatus=itemView.findViewById(R.id.user_status);
            profileImage=itemView.findViewById(R.id.users_profile_image);
            AcceptButton=itemView.findViewById(R.id.request_accept_btn);
            CancelButton=itemView.findViewById(R.id.request_cancel_btn);
        }
    }
}