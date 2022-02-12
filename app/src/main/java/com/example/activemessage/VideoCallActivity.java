package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;

public class VideoCallActivity extends AppCompatActivity {

    private ImageView videoCallImage;
    private TextView videoCallName,videoCallProfession;
    private FloatingActionButton acceptVideoCallButton,cancelVideoCallButton;
    private String sender_url,sender_prof,sender_name,receiver_uid;
    private DatabaseReference referenceCaller;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);

        acceptVideoCallButton = findViewById(R.id.acceptVideoCallButton);
        cancelVideoCallButton = findViewById(R.id.cancelVideoCallButton);
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        receiver_uid=user.getUid();
        videoCallImage = findViewById(R.id.videoCallImage);
        videoCallName = findViewById(R.id.videoCallName);
        videoCallProfession = findViewById(R.id.videoCallProfession);
        URL serverURL;


        referenceCaller=database.getReference("Users").child(receiver_uid);
        referenceCaller.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    sender_name=snapshot.child("fullname").getValue().toString();
                    sender_prof=snapshot.child("profession").getValue().toString();
                    sender_url=snapshot.child("profileImage").getValue().toString();

                    videoCallName.setText(sender_name);
                    Picasso.get().load(sender_url).into(videoCallImage);
                    videoCallProfession.setText(sender_prof);

                }
                else
                {
                    Toast.makeText(VideoCallActivity.this, "Hata Oluştu!", Toast.LENGTH_SHORT).show();
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });








        acceptVideoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }

        });

        cancelVideoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });


    }

}
