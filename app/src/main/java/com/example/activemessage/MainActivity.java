package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar mToolbar;
    private ViewPager myViewPager;
    private TabLayout myTabLayout;
    private TabAccessorAdapter myTabAccessorAdapter;
    private String profileImageUrlV,usernameV;
    private CircleImageView profileImageHeader;
    private TextView usernameHeader;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference RootRef;
    private String currentUserID;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        currentUserID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();


        mToolbar=(Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
        drawerLayout=findViewById(R.id.drawer_menu);
        navigationView=findViewById(R.id.navView);
        View view=navigationView.inflateHeaderView(R.layout.drawer_header);
        navigationView.setNavigationItemSelectedListener(this);
        profileImageHeader=view.findViewById(R.id.profileImage_header);
        usernameHeader=view.findViewById(R.id.username_header);
        myViewPager=(ViewPager)findViewById(R.id.main_tabs_pager);
        myTabAccessorAdapter=new TabAccessorAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabAccessorAdapter);
        myTabLayout=(TabLayout) findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);


    }
    /*user status*/
    @Override
    protected void onStart()
    {

        super.onStart();
        if (currentUser == null)
        {
            SendUserToLoginActivity();
        }
        else
        {
            updateUserStatus("online");

            VerifyUserExistance();
        }
    }
/*user offline */
    @Override
    protected void onStop() {
        super.onStop();

        if (currentUser!=null)
        {
            updateUserStatus("Çevrimdışı");
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (currentUser!=null)
        {
            updateUserStatus("Çevrimdışı");
        }
    }

    /*after login */
    private void VerifyUserExistance()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        /*firebase userid referance*/
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ((snapshot.child("fullname").exists()))
                {
                    /*drawer menu profil bilgileri*/
                    profileImageUrlV=snapshot.child("profileImage").getValue().toString();
                    usernameV=snapshot.child("fullname").getValue().toString();
                    Picasso.get().load(profileImageUrlV).into(profileImageHeader);
                    usernameHeader.setText(usernameV);

                }
                else
                {
                    SendUserToSettingsActivity();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });


    }



  

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu,menu);
        return true;
    }



/*some intents*/
    private void SendUserToLoginActivity() {
        Intent loginIntent=new Intent(MainActivity.this,LoginActivity.class);
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(loginIntent);
        finish();
    }
    private void SendUserToSettingsActivity() {
        Intent settingsIntent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingsIntent);

    }
    private void SendUserToFindFriendsActivity() {
        Intent findFriendsIntent=new Intent(MainActivity.this,FindFriendsActivity.class);
        startActivity(findFriendsIntent);
    }
    private void SendUserToGroupCreateActivity() {
        Intent settingsIntent=new Intent(MainActivity.this,CreateGroupActivity.class);
        startActivity(settingsIntent);
    }
    private void SendUserToFriendsActivity() {
        Intent friendsIntent=new Intent(MainActivity.this,FriendsActivity.class);
        startActivity(friendsIntent);
    }
    private void SendUserToEditProfileActivity() {
        Intent profileEditIntent=new Intent(MainActivity.this,ProfileViewActivity.class);
        startActivity(profileEditIntent);
    }
    private void SendUserToApplicationSettingsActivity() {
        Intent applicationSettingIntent=new Intent(MainActivity.this,ApplicationSettingsActivity.class);
        startActivity(applicationSettingIntent);
    }

    /*chat fragment user  status*/
    private void updateUserStatus(String state)
    {
        String saveCurrentTime, saveCurrentDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd.MM.yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());
        SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
        saveCurrentTime = currentTime.format(calendar.getTime());
        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        RootRef.child("Users").child(currentUserID).child("userState")
                .updateChildren(onlineStateMap);

    }
    

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        switch (menuitem.getItemId()){
            case R.id.home:
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                break;
            case R.id.profile:
                SendUserToEditProfileActivity();
                break;
            case R.id.friend:
                SendUserToFriendsActivity();
                break;
            case R.id.findFriend:
                startActivity(new Intent(MainActivity.this,FindFriendsActivity.class));
                break;
            case R.id.findGroup:
                SendUserToGroupCreateActivity();
                break;
            case R.id.logout:
                mAuth.signOut();
                SendUserToLoginActivity();
                break;

        }
        return true;
    }
    /*menu options*/
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        super.onOptionsItemSelected(item);


        if (item.getItemId()==R.id.main_logout_option)
        {
            updateUserStatus("Çevrimdışı");
            mAuth.signOut();
            SendUserToLoginActivity();

        }
        if (item.getItemId()==R.id.main_settings_option)
        {
            SendUserToApplicationSettingsActivity();
        }

        if (item.getItemId()==android.R.id.home)
        {
            drawerLayout.openDrawer(GravityCompat.START);
            return  true;
        }
        return true;
}


}
