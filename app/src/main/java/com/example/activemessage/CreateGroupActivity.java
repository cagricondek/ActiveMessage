package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

public class CreateGroupActivity extends AppCompatActivity {
    private static final int CAMERA_REQUEST_CODE=100;
    private static final int STORAGE_REQUEST_CODE=200;
    private static final int IMAGE_PICK_CAMERA_CODE=300;
    private static final int IMAGE_PICK_GALLERY_CODE=400;
    private String[] cameraPermissions;
    private String[] storagePermissions;
    private Uri image_uri=null;
    private androidx.appcompat.widget.Toolbar CreateGroupToolbar;
private ActionBar actionBar;
private FirebaseAuth firebaseAuth;
private ImageView groupIconIv;
private EditText groupTitleEt,groupDescriptionEt;
private FloatingActionButton createGroupBtn;
private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        groupIconIv=findViewById(R.id.groupIconIv);
        groupTitleEt=findViewById(R.id.groupTitleEt);
        groupDescriptionEt=findViewById(R.id.groupDescriptionEt);
        createGroupBtn=findViewById(R.id.createGroupBtn);
        cameraPermissions= new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermissions=  new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        firebaseAuth = FirebaseAuth.getInstance();
        CreateGroupToolbar=(Toolbar) findViewById(R.id.create_group_toolbar);
        setSupportActionBar( CreateGroupToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setTitle("Grup Oluştur");





        groupIconIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                showImagePickDialog();
            }
        });

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                startCreatingGroup();
            }
        });

    }

    private void startCreatingGroup() {
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Grup Oluştur");
        String groupTitle=groupTitleEt.getText().toString().trim();
        String groupDescription=groupDescriptionEt.getText().toString().trim();
        if (TextUtils.isEmpty(groupTitle))
        {
            Toast.makeText(this, "Grup ismi boş bırakılamaz.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.show();
        String g_timestamp=""+System.currentTimeMillis();
             if (image_uri==null)
             {
                 createGroup(""+g_timestamp,""+groupTitle,
                         ""+groupDescription,"");
             }
              else
             {
                String fileNameAndPath="Group_Imgs"+"image"+g_timestamp;
                 StorageReference storageReference= FirebaseStorage.getInstance().getReference(fileNameAndPath);
                 storageReference.putFile(image_uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri>p_uriTask=taskSnapshot.getStorage().getDownloadUrl();
                        while (!p_uriTask.isSuccessful());
                        Uri p_downloadUri=p_uriTask.getResult();
                        if (p_uriTask.isSuccessful())
                        {
                            createGroup(""+g_timestamp,""+groupTitle,
                                    ""+groupDescription,""+p_downloadUri);
                        }
                     }
                 }).addOnFailureListener(new OnFailureListener() {
                     @Override
                     public void onFailure(@NonNull Exception e) {
                         progressDialog.dismiss();
                         Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                     }
                 });
             }

    }

    private void createGroup(String g_timestamp,String groupTitle,String groupDescription,String groupIcon)
    {
        HashMap<String ,String> hashMap=new HashMap<>();
        hashMap.put("groupId",""+g_timestamp);
        hashMap.put("groupTitle",""+groupTitle);
        hashMap.put("groupDescription",""+groupDescription);
        hashMap.put("groupIcon",""+groupIcon);
        hashMap.put("timestamp",""+g_timestamp);
        hashMap.put("createdBy",""+firebaseAuth.getUid());

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("Groups");
        ref.child(g_timestamp).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid)
            {
                HashMap<String ,String>hashMap1=new HashMap<>();
                hashMap1.put("uid",firebaseAuth.getUid());
                hashMap1.put("role","creator");
                hashMap1.put("timestamp",g_timestamp);
                DatabaseReference ref1=FirebaseDatabase.getInstance().getReference("Groups");
                ref1.child(g_timestamp).child("Participants").child(firebaseAuth.getUid()).
                        setValue(hashMap1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateGroupActivity.this, "Grup Oluşturuldu.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });





            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(CreateGroupActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showImagePickDialog() {
        String[] options={"Camera","Gallery"};
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Resim Seçin:").setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                if (which==0)
                {
                            if (!checkCameraPermission())
                             {
                              requestCameraPermission();
                             }
                          else
                             {
                                 pickFromCamera();
                             }

                }
                else
                {
                         if (!checkStoragePermission())
                         {
                             requestStoragePermission();
                         }
                         else
                        {
                            pickFromGallery();
                        }

                }
            }
        }).show();
    }


    private void pickFromGallery(){
        Intent intent=new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent,IMAGE_PICK_GALLERY_CODE);
    }


    private void pickFromCamera(){
        ContentValues cv=new ContentValues();
        cv.put(MediaStore.Images.Media.TITLE,"Group Image Icon Title");
        cv.put(MediaStore.Images.Media.DESCRIPTION,"Group Image Icon Description");
        image_uri=getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,cv);
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(intent,IMAGE_PICK_CAMERA_CODE);
    }


    private boolean checkStoragePermission(){
        boolean result= ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return result;
    }


    private void requestStoragePermission(){
        ActivityCompat.requestPermissions(this,storagePermissions,STORAGE_REQUEST_CODE);
    }

    private boolean checkCameraPermission() {

        boolean result=ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)==(PackageManager.PERMISSION_GRANTED);
        boolean result1=ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)==(PackageManager.PERMISSION_GRANTED);
        return result&&result1;
    }

    private  void requestCameraPermission(){
        ActivityCompat.requestPermissions(this,cameraPermissions,CAMERA_REQUEST_CODE);
    }





    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case CAMERA_REQUEST_CODE:
                {
                    if (grantResults.length>0)
                    {
                        boolean cameraAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                        boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                        if (cameraAccepted&&storageAccepted)
                        {
                            pickFromCamera();
                        }
                        else
                            {
                                Toast.makeText(this, "Kamera ve Depolama Erişimi Gerekli", Toast.LENGTH_SHORT).show();

                            }
                    }
                }
                break;
            case STORAGE_REQUEST_CODE:{

                        if (grantResults.length>0)
                        {
                            boolean storageAccepted=grantResults[0]==PackageManager.PERMISSION_GRANTED;
                            if (storageAccepted)
                            {
                                pickFromGallery();
                            }
                            else
                                {
                                    Toast.makeText(this, "Depolama Erişimi Gerekli", Toast.LENGTH_SHORT).show();
                                }
                             }
                    }
            }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode==RESULT_OK)
        {
            if (requestCode==IMAGE_PICK_GALLERY_CODE)
            {
                image_uri=data.getData();
                groupIconIv.setImageURI(image_uri);
            }
            else if (requestCode==IMAGE_PICK_CAMERA_CODE)
            {
                groupIconIv.setImageURI(image_uri);
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}