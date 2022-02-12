package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileViewActivity extends AppCompatActivity {

    private  String currentUserID;
    private CircleImageView userProfileImage;
    private TextView upMenuProfileFullName,upMenuProfileProfession,upMenuProfileAddress,ProfileUsername,ProfilePhone,ProfileEmail,ProfileAddress;
    private Button EditProfileBtn;
    private DatabaseReference UserRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_view);
        mAuth = FirebaseAuth.getInstance();
        UserRef=FirebaseDatabase.getInstance().getReference().child("Users");
        currentUserID=mAuth.getCurrentUser().getUid();
        EditProfileBtn=(Button) findViewById(R.id.edit_profile_btn);
        userProfileImage=(CircleImageView) findViewById(R.id.visit_profile_image);
        upMenuProfileFullName=(TextView)findViewById(R.id.up_menu_name_textView);
        upMenuProfileProfession=(TextView)findViewById(R.id.up_menu_profession_textView);
        upMenuProfileAddress=(TextView)findViewById(R.id.up_menu_address_textView);
        ProfileUsername=(TextView)findViewById(R.id.visit_username);
        ProfilePhone=(TextView)findViewById(R.id.visit_phone);
        ProfileEmail=(TextView)findViewById(R.id.visit_email);
        ProfileAddress=(TextView)findViewById(R.id.visit_address);

        RetrieveUserInfo();



        EditProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SendUserToSettingsActivity();
            }
        });
    }



    private void RetrieveUserInfo()
    {
        UserRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ((snapshot.exists()))
                {
                    String UserImage=snapshot.child("profileImage").getValue().toString();
                    String UserName=snapshot.child("username").getValue().toString();
                    String UserFullName=snapshot.child("fullname").getValue().toString();
                    String UserAddress=snapshot.child("address").getValue().toString();
                    String UserPhone=snapshot.child("phone").getValue().toString();
                    String UserProfession=snapshot.child("profession").getValue().toString();
                    String UserEmail=snapshot.child("email").getValue().toString();
                    Picasso.get().load(UserImage).placeholder(R.drawable.profile).into(userProfileImage);
                    ProfileUsername.setText(UserName);
                    upMenuProfileFullName.setText(UserFullName);
                    upMenuProfileAddress.setText(UserAddress);
                    ProfileAddress.setText(UserAddress);
                    ProfilePhone.setText(UserPhone);
                    upMenuProfileProfession.setText(UserProfession);
                    ProfileEmail.setText(UserEmail);

                }
                else
                {
                    SendUserToSettingsActivity();


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void SendUserToSettingsActivity() {
        Intent settingsIntent=new Intent(ProfileViewActivity.this,SettingsActivity.class);
        startActivity(settingsIntent);
    }
}
