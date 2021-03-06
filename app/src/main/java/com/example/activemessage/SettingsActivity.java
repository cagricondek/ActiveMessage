package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.HashMap;
import de.hdodenhof.circleimageview.CircleImageView;

public class  SettingsActivity extends AppCompatActivity {
    private CircleImageView profileImageView;
    private EditText userName,fullName,Phone,Email,Address,Profession;
    private ProgressBar profileSaveProgressBar;
    private TextView editProfileTextView;
    private Button editProfileSaveButton;
    private androidx.appcompat.widget.Toolbar SettingsToolbar;
    private static final int REQUEST_CODE = 101;
    Uri imageUri;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    DatabaseReference mRef,usernameRef;
    StorageReference storageReference;
    ProgressBar editProfileProgressbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profileImageView=findViewById(R.id.profileImageView);
        userName=findViewById(R.id.edit_profile_username);
        fullName=findViewById(R.id.edit_profile_fullname);
        Phone=findViewById(R.id.edit_profile_phone);
        Email=findViewById(R.id.edit_profile_email);
        Address=findViewById(R.id.edit_profile_address);
        Profession=findViewById(R.id.edit_profile_profession);
        editProfileSaveButton=findViewById(R.id.edit_profile_save_button);
        editProfileTextView=findViewById(R.id.edit_profile_progressbar_textview);
        editProfileProgressbar=new ProgressBar(this);
        profileSaveProgressBar=findViewById(R.id.edit_profile_progressbar);
        SettingsToolbar=(Toolbar) findViewById(R.id.settings_toolbar);
        setSupportActionBar(SettingsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Profil Ayarlar??");
        mAuth=FirebaseAuth.getInstance();
        mUser=mAuth.getCurrentUser();
        mRef=FirebaseDatabase.getInstance().getReference().child("Users");
        usernameRef=FirebaseDatabase.getInstance().getReference().child("Users").child("username");
        storageReference=FirebaseStorage.getInstance().getReference().child("profileImage");
        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,REQUEST_CODE);

            }
        });


        /*kaydet butonuna bas??nca*/
        editProfileSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveData();
            }
        });
    }
/*edittext datas??n?? alma*/
    private void SaveData()
    {
        String username=userName.getText().toString();
        String fullname=fullName.getText().toString();
        String phone=Phone.getText().toString();
        String email=Email.getText().toString();
        String address=Address.getText().toString();
        String profession=Profession.getText().toString();
        if (username.isEmpty()||username.length()<3)
        {
            showError(userName,"Kullan??c?? ad?? en az 3 karakterden olu??mal??d??r!");
        }

        else if (fullname.isEmpty())
        {
            showError(fullName,"L??tfen Ad??n??z?? Giriniz!");
        }
        else if (phone.isEmpty())
        {
            showError(Phone,"L??tfen Telefon Numaran??z?? Giriniz!");
        }
        else if (phone.length()>11)
        {
            showError(Phone,"Telefon Numaras?? 11 Rakamdan Olu??mal??d??r!");
        }
        else if (email.isEmpty())
        {
            showError(Email,"L??tfen Emailinizi Giriniz!");
        }
        else if (address.isEmpty())
        {
            showError(Address,"L??tfen Ge??erli Bir Adres Giriniz!");
        }
        else if (profession.isEmpty())
        {
            showError(Profession,"L??tfen Mesle??inizi Giriniz!");
        }
        else if (imageUri==null)
        {
            Toast.makeText(this, "L??tfen Profil Foto??raf?? Se??iniz.", Toast.LENGTH_SHORT).show();
        }
        else
        {


            editProfileProgressbar.setVisibility(View.VISIBLE);
            profileSaveProgressBar.setVisibility(View.VISIBLE);
            editProfileTextView.setVisibility(View.VISIBLE);
            storageReference.child(mUser.getUid()).putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    if (task.isSuccessful())
                    {
                        storageReference.child(mUser.getUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri)
                            {
                                        HashMap hashMap=new HashMap();
                                        hashMap.put("username",username);
                                        hashMap.put("fullname",fullname);
                                         hashMap.put("phone",phone);
                                         hashMap.put("email",email);
                                          hashMap.put("address",address);
                                         hashMap.put("profession",profession);
                                         hashMap.put("profileImage",uri.toString());
                                         hashMap.put("status","offline");


                                         mRef.child(mUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                                             @Override
                                             public void onSuccess(Object o)
                                             {
                                                 Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
                                                 startActivity(intent);
                                                 editProfileProgressbar.setVisibility(View.INVISIBLE);
                                                 editProfileTextView.setVisibility(View.INVISIBLE);
                                                 Toast.makeText(SettingsActivity.this, "Profil Bilgileri Kaydedildi.", Toast.LENGTH_SHORT).show();

                                             }
                                         }).addOnFailureListener(new OnFailureListener() {
                                             @Override
                                             public void onFailure(@NonNull Exception e) {
                                                 Toast.makeText(SettingsActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                             }
                                         });
                            }
                        });
                    }
                }
            });
        }

    }
/*ekrana error bast??rma methodu*/
    private void showError(EditText input, String s) {
        input.setError(s);
        input.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REQUEST_CODE&&resultCode==RESULT_OK&&data!=null)
        {
        imageUri=data.getData();
        profileImageView.setImageURI(imageUri);

            }
        }

    }
