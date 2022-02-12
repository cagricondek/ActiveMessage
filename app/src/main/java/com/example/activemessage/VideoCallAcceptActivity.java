package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.google.firebase.messaging.FcmBroadcastProcessor;
import com.squareup.picasso.Picasso;

import org.jitsi.meet.sdk.JitsiMeet;
import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;

import java.net.MalformedURLException;
import java.net.URL;

public class VideoCallAcceptActivity extends AppCompatActivity {
    private ImageView videoCallAcceptImage;
    private TextView videoCallAcceptName,videoCallAcceptProfession;
    private FloatingActionButton declineVideoCallButton;
    private String receiver_url,receive_prof,receiver_name,receiver_uid;
    private DatabaseReference reference;
    private FirebaseDatabase database=FirebaseDatabase.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call_accept);
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        videoCallAcceptImage=findViewById(R.id.videoCallAcceptImage);
        videoCallAcceptName=findViewById(R.id.videoCallAcceptName);
        videoCallAcceptProfession=findViewById(R.id.videoCallAcceptProfession);
        declineVideoCallButton=findViewById(R.id.declineVideoCallButton);
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                URL server;
                try {
                    server=new URL("https://meet.jit.si");
                    JitsiMeetConferenceOptions jitsiMeetConferenceOptions=
                            new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(server)
                                    .setWelcomePageEnabled(false)
                                    .build();
                    JitsiMeet.setDefaultConferenceOptions(jitsiMeetConferenceOptions);


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                String roomCode="123activemessage";
                JitsiMeetConferenceOptions jitsiMeetConferenceOptions=
                        new JitsiMeetConferenceOptions.Builder()
                                .setRoom(roomCode)
                                .setWelcomePageEnabled(false).build();
                JitsiMeetActivity.launch(VideoCallAcceptActivity.this,jitsiMeetConferenceOptions);
            }
        }, 5000);


        declineVideoCallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(VideoCallAcceptActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });


        Bundle bundle=getIntent().getExtras();
        if(bundle!=null){

            receiver_uid=bundle.getString("uid");

        }
        else{
            Toast.makeText(this, "Hata Oluştu.", Toast.LENGTH_SHORT).show();
        }




        reference=database.getReference("Users").child(receiver_uid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                receiver_name=snapshot.child("fullname").getValue().toString();
                receive_prof=snapshot.child("profession").getValue().toString();
                receiver_url=snapshot.child("profileImage").getValue().toString();
                videoCallAcceptName.setText(receiver_name);
                Picasso.get().load(receiver_url).into(videoCallAcceptImage);
                videoCallAcceptProfession.setText(receive_prof);

                }
                else
                {
                    Toast.makeText(VideoCallAcceptActivity.this, "Hata Oluştu!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

}