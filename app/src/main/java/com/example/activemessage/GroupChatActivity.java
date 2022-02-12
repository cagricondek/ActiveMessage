package com.example.activemessage;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

private FirebaseAuth firebaseAuth;
private String groupId,myGroupRole="";
private ImageView groupIconIv;
private TextView groupTitleTv;
private ImageButton SendMessageButton,SendFileButton;
private EditText GroupMessageText;
private RecyclerView chatRv;
private ArrayList<ModelGroupChat> groupChatArrayList;
private AdapterGroupChat adapterGroupChat;
private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
         toolbar=findViewById(R.id.toolbar);
        groupIconIv=findViewById(R.id.groupIconIv);
        groupTitleTv=findViewById(R.id.groupTitleTv);
        SendMessageButton=findViewById(R.id.send_group_message_btn);
        SendFileButton=findViewById(R.id.send_group_message_file_btn);
        GroupMessageText=findViewById(R.id.input_group_message);
        chatRv=findViewById(R.id.chatRv);
        setSupportActionBar(toolbar);
        Intent intent=getIntent();
        groupId=intent.getStringExtra("groupId");

        firebaseAuth= FirebaseAuth.getInstance();
        loadGroupInfo();
        loadGroupMessages();
        loadMyGroupRole();

        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            String message=GroupMessageText.getText().toString().trim();
            if (TextUtils.isEmpty(message))
            {
                Toast.makeText(GroupChatActivity.this, "Bir Mesaj Yazınız", Toast.LENGTH_SHORT).show();
            }
            else
                {
                    sendMessage(message);
                }
            }
        });
    }

    private void loadMyGroupRole()
    {
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Groups");
        reference.child(groupId).child("Participants").orderByChild("uid")
                .equalTo(firebaseAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    myGroupRole=""+ds.child("role").getValue();
                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void loadGroupMessages() {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(groupId).child("Messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                groupChatArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren())
                {
                    ModelGroupChat model=ds.getValue(ModelGroupChat.class);
                    groupChatArrayList.add(model);
                }

                adapterGroupChat=new AdapterGroupChat(GroupChatActivity.this,groupChatArrayList);
                chatRv.setAdapter(adapterGroupChat);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void sendMessage(String message) {
String timestamp=""+System.currentTimeMillis();
HashMap<String,Object>hashMap=new HashMap<>();
hashMap.put("sender","" + firebaseAuth.getUid());
hashMap.put("message",""+message);
hashMap.put("timestamp",""+timestamp);
hashMap.put("type",""+"text");
DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
ref.child(groupId).child("Messages").child(timestamp).setValue(hashMap)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                GroupMessageText.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
             @Override
            public void onFailure(@NonNull Exception e) {
                 Toast.makeText(GroupChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


            }



    private void loadGroupInfo() {
        groupChatArrayList=new ArrayList<>();

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Groups");
        ref.orderByChild("groupId").equalTo(groupId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds:snapshot.getChildren())
                {
                    String groupTitle=""+ds.child("groupTitle").getValue();
                    String groupDescription=""+ds.child("groupDescription").getValue();
                    String groupIcon=""+ds.child("groupIcon").getValue();
                    String timestamp=""+ds.child("timestamp").getValue();
                    String createdBy=""+ds.child("createdBy").getValue();

                    groupTitleTv.setText(groupTitle);
                    try
                    {
                        Picasso.get().load(groupIcon).placeholder(R.drawable.ic_profile).into(groupIconIv);
                    }
                    catch (Exception e)
                    {
                        groupIconIv.setImageResource(R.drawable.ic_profile);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu,menu);
        menu.findItem(R.id.main_settings_option).setVisible(false);
        menu.findItem(R.id.main_logout_option).setVisible(false);
        menu.findItem(R.id.action_add_participant).setVisible(false);
        if (myGroupRole.equals("creator")||myGroupRole.equals("admin")){
            menu.findItem(R.id.action_add_participant).setVisible(true);
        }
        else
        {
            menu.findItem(R.id.action_add_participant).setVisible(false);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id=item.getItemId();
        if (id==R.id.action_add_participant){
        Intent intent=new Intent(this,GroupParticipantAddActivity.class);
        intent.putExtra("groupId",groupId);
        startActivity(intent);
        }


        return super.onOptionsItemSelected(item);
    }
}

