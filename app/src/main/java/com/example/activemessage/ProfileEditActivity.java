package com.example.activemessage;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileEditActivity extends AppCompatActivity {

      private  String receiverUserID,Current_State,senderUserID;
      private  CircleImageView userProfileImage;
      private TextView upMenuProfileFullName,upMenuProfileProfession,upMenuProfileAddress,ProfileUsername,ProfilePhone,ProfileEmail,ProfileAddress;
      private Button SendMessageRequestButton,DeclineMessageRequestButton;
      private DatabaseReference UserRef,ChatRequestRef,ContactsRef,NotificationRef;
      private FirebaseAuth maAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_edit);
        maAuth=FirebaseAuth.getInstance();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef=FirebaseDatabase.getInstance().getReference().child("Chat Requests");
        ContactsRef=FirebaseDatabase.getInstance().getReference().child("Contacts");
        NotificationRef=FirebaseDatabase.getInstance().getReference().child("Notifications");
        receiverUserID=getIntent().getExtras().get("visit_user_id").toString();
        senderUserID=maAuth.getCurrentUser().getUid();
        userProfileImage=(CircleImageView) findViewById(R.id.visit_profile_image);
        upMenuProfileFullName=(TextView)findViewById(R.id.up_menu_name_textView);
        upMenuProfileProfession=(TextView)findViewById(R.id.up_menu_profession_textView);
        upMenuProfileAddress=(TextView)findViewById(R.id.up_menu_address_textView);
        ProfileUsername=(TextView)findViewById(R.id.visit_username);
        ProfilePhone=(TextView)findViewById(R.id.visit_phone);
        ProfileEmail=(TextView)findViewById(R.id.visit_email);
        ProfileAddress=(TextView)findViewById(R.id.visit_address);
        SendMessageRequestButton=(Button)findViewById(R.id.send_message_request_button);
        DeclineMessageRequestButton=(Button)findViewById(R.id.decline_message_request_button);
        Current_State="new";
        RetrieveUserInfo();
    }

    private void RetrieveUserInfo()
    {
        UserRef.child(receiverUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
               if ((snapshot.exists()) &&(snapshot.hasChild("profileImage")))
                {
                    String UserImage=snapshot.child("profileImage").getValue().toString();
                    String UserName=snapshot.child("username").getValue().toString();
                    String UserFullName=snapshot.child("fullname").getValue().toString();
                    String UserAddress=snapshot.child("address").getValue().toString();
                    String UserPhone=snapshot.child("phone").getValue().toString();
                    String UserProfession=snapshot.child("profession").getValue().toString();
                    String UserEmail=snapshot.child("email").getValue().toString();
                    String UserStatus=snapshot.child("status").getValue().toString();
                    Picasso.get().load(UserImage).placeholder(R.drawable.profile).into(userProfileImage);
                    ProfileUsername.setText(UserName);
                    upMenuProfileFullName.setText(UserFullName);
                    upMenuProfileAddress.setText(UserAddress);
                    ProfileAddress.setText(UserAddress);
                    ProfilePhone.setText(UserPhone);
                    upMenuProfileProfession.setText(UserProfession);
                    ProfileEmail.setText(UserEmail);
                    ManageChatRequest();
                }
               else
               {
                   String UserName=snapshot.child("username").getValue().toString();
                   String UserFullName=snapshot.child("fullname").getValue().toString();
                   String UserAddress=snapshot.child("address").getValue().toString();
                   String UserPhone=snapshot.child("phone").getValue().toString();
                   String UserProfession=snapshot.child("profession").getValue().toString();
                   String UserEmail=snapshot.child("email").getValue().toString();
                   String UserStatus=snapshot.child("status").getValue().toString();
                   ProfileUsername.setText(UserName);
                   upMenuProfileFullName.setText(UserFullName);
                   upMenuProfileAddress.setText(UserAddress);
                   ProfileAddress.setText(UserAddress);
                   ProfilePhone.setText(UserPhone);
                   upMenuProfileProfession.setText(UserProfession);
                   ProfileEmail.setText(UserEmail);

                   ManageChatRequest();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void ManageChatRequest()
    {    /*mesaj isteği butonu geri çıkınca eski haline dönmesin diye*/
        ChatRequestRef.child(senderUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot)
                    {
                        if (snapshot.hasChild(receiverUserID))
                        {
                            String request_type = snapshot.child(receiverUserID).child("request_type").getValue().toString();

                            if (request_type.equals("sent"))
                            {
                                Current_State="request_sent";
                                SendMessageRequestButton.setText("İptal Et");
                            }
                            else if(request_type.equals("received"))
                            {
                                Current_State="request_received";
                                SendMessageRequestButton.setText("Onayla");
                                DeclineMessageRequestButton.setVisibility(View.VISIBLE);
                                DeclineMessageRequestButton.setEnabled(true);
                                DeclineMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v)
                                    {
                                        CancelChatRequest();
                                    }
                                });
                            }
                        }
                        else
                            {
                            ContactsRef.child(senderUserID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot)
                                {
                                    if (snapshot.hasChild(receiverUserID))
                                    {
                                        Current_State="friends";
                                        SendMessageRequestButton.setText("Arkadaşı Sil");
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        if (!senderUserID.equals(receiverUserID))
        {
                SendMessageRequestButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        SendMessageRequestButton.setEnabled(false);
                        if (Current_State.equals("new"))
                        {
                               SendChatRequest(); 
                            
                        }
                        if (Current_State.equals("request_sent"))
                        {
                            CancelChatRequest();
                        }
                        if (Current_State.equals("request_received"))
                          {
                            AcceptChatRequest();
                          }
                        if (Current_State.equals("friends"))
                        {
                            RemoveSpecificContact();
                        }
                        }
                });
        }
        else
            {
                SendMessageRequestButton.setVisibility(View.INVISIBLE);
        }
    }

    private void RemoveSpecificContact()
    {ContactsRef.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task)
        {
            if (task.isSuccessful())
            {
                ContactsRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            SendMessageRequestButton.setEnabled(true);
                            Current_State="new";
                            SendMessageRequestButton.setText("Arkadaş Ekle");
                            DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                            DeclineMessageRequestButton.setEnabled(false);
                        }
                    }
                });
            }
        }
    });
    }

    private void AcceptChatRequest()
    {
        ContactsRef.child(senderUserID).child(receiverUserID).child("Contacts").setValue("Saved")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task)
                    {
                        if (task.isSuccessful())
                        {
                            ContactsRef.child(receiverUserID).child(senderUserID).child("Contacts").setValue("Saved")
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                            ChatRequestRef.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task)
                                                {
                                                if (task.isSuccessful())
                                                {
                                                    ChatRequestRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task)
                                                        {
                                                            SendMessageRequestButton.setEnabled(true);
                                                            Current_State="friends";
                                                            SendMessageRequestButton.setText("Arkadaşı Sil");
                                                            DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                                            DeclineMessageRequestButton.setEnabled(false);
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

    private void CancelChatRequest()
    {
        ChatRequestRef.child(senderUserID).child(receiverUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    ChatRequestRef.child(receiverUserID).child(senderUserID).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            if (task.isSuccessful())
                            {
                                SendMessageRequestButton.setEnabled(true);
                                Current_State="new";
                                SendMessageRequestButton.setText("Arkadaş Ekle");
                                DeclineMessageRequestButton.setVisibility(View.INVISIBLE);
                                DeclineMessageRequestButton.setEnabled(false);
                            }
                        }
                    });
                }
            }
        });
    }

    private void SendChatRequest()
    {
        ChatRequestRef.child(senderUserID).child(receiverUserID).child("request_type").setValue("sent").
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                if (task.isSuccessful())
                {
                    ChatRequestRef.child(receiverUserID).child(senderUserID).child("request_type").setValue("received").
                            addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        HashMap<String,String>chatNotificationMap=new HashMap<>();
                                        chatNotificationMap.put("from",senderUserID);
                                        chatNotificationMap.put("type","request");
                                        NotificationRef.child(receiverUserID).push().setValue(chatNotificationMap)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task)
                                                    {
                                                    if (task.isSuccessful())
                                                        {
                                                            SendMessageRequestButton.setEnabled(true);
                                                            Current_State="request_sent";
                                                            SendMessageRequestButton.setText("İptal Et");
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



