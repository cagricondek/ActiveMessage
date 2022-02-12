package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    private static final int camera_pick_req=1;
    private static final int camera=2;
private  String messageReceiverID,messageReceiverName,messageReceiverImage,messageSenderID;
private TextView userName,userLastSeen;
private CircleImageView userImage;
private Toolbar ChatToolBar;
private ImageButton SendMessageButton,SendFilesButton,phoneSoundCall,phoneVideoCall,chatOptionsMenu;
private EditText MessageInputText;
private FirebaseAuth mAuth;
private DatabaseReference RootRef;
private final List<Messages> messagesList= new ArrayList<>();
private LinearLayoutManager linearLayoutManager;
private MessageAdapter messageAdapter;
private RecyclerView userMessagesList;
private String saveCurrentTime,saveCurrentDate;
private String checker="",myUrl="";
private String phoneNumberHolder;
private StorageTask uploadTask;
private Uri fileUri;
private ProgressDialog loadingBar;
private FrameLayout file_frame_layout;
private ImageButton send_pdf_btn,send_picture_btn,send_camera_file_btn,send_voice_file_btn,send_document_file_btn;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mAuth = FirebaseAuth.getInstance();
        messageSenderID = mAuth.getCurrentUser().getUid();
        RootRef = FirebaseDatabase.getInstance().getReference();
        messageReceiverID = getIntent().getExtras().get("visit_user_id").toString();
        messageReceiverName = getIntent().getExtras().get("visit_user_name").toString();
        messageReceiverImage = getIntent().getExtras().get("visit_image").toString();
        IntializeControllers();
        userName.setText(messageReceiverName);
        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile).into(userImage);




        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SendMessage();
            }
        });
        DisplayLastSeen();






        phoneSoundCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhoneCalls();
            }
        });
        phoneVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


        Intent intent=new Intent(ChatActivity.this,VideoCallAcceptActivity.class);
        intent.putExtra("uid",messageReceiverID);
        startActivity(intent);

            }
        });
        chatOptionsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this, "Henüz Aktif Değil.", Toast.LENGTH_SHORT).show();
            }
        });

                SendFilesButton.setOnClickListener(new View.OnClickListener() {
                    boolean isPressed = false;

                    @Override
                    public void onClick(View v) {
                        if (isPressed == false) {
                            file_frame_layout.setVisibility(View.VISIBLE);
                            isPressed = true;
                        } else {
                            file_frame_layout.setVisibility(View.INVISIBLE);
                            isPressed = false;
                        }
                    }
                });

                    send_picture_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            checker="image";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("image/*");
                            startActivityForResult(intent.createChooser(intent,"Bir Resim Seçiniz"),438);
                            file_frame_layout.setVisibility(View.INVISIBLE);
                             }
                            });



                    send_pdf_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checker="pdf";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf");
                            startActivityForResult(intent.createChooser(intent,"Bir PDF Seçiniz"),438);
                            file_frame_layout.setVisibility(View.INVISIBLE);
                          }
                          });


                    send_document_file_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            checker="docx";
                            Intent intent=new Intent();
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/docx");
                            startActivityForResult(intent.createChooser(intent,"Bir Word Belgesi Seçiniz"),438);
                            file_frame_layout.setVisibility(View.INVISIBLE);
                        }
                          });

                    send_camera_file_btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        Intent intent   =new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                        startActivity(intent);


                        }
                    });
                    send_voice_file_btn.setOnClickListener(new View.OnClickListener() {
                         @Override
                      public void onClick(View v) {




                            Toast.makeText(ChatActivity.this, "ses dosyasi", Toast.LENGTH_SHORT).show();
                     }
                         });
    }

    private void PhoneCalls() {

        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {



            String phoneNumberHolder = snapshot.child("phone").getValue().toString();
            try {
                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse("tel:" + phoneNumberHolder));
                startActivity(call);
            }
            catch (Exception e)
            {
                Toast.makeText(ChatActivity.this, "Arama işlemi başarısız!", Toast.LENGTH_SHORT).show();
            }



        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {

             }
        });
    }



    private void IntializeControllers()
    {
    file_frame_layout=(FrameLayout)findViewById(R.id.file_frame_layout);
    send_pdf_btn=(ImageButton)findViewById(R.id.send_pdf_btn);
    send_picture_btn=(ImageButton)findViewById(R.id.send_picture_btn);
    send_camera_file_btn=(ImageButton)findViewById(R.id.send_camera_file_btn);
    send_voice_file_btn=(ImageButton)findViewById(R.id.send_voice_file_btn);
    send_document_file_btn=(ImageButton)findViewById(R.id.send_document_file_btn);
    ChatToolBar=(Toolbar)findViewById(R.id.chat_toolbar);
    setSupportActionBar(ChatToolBar);
    ActionBar actionBar=getSupportActionBar();
    actionBar.setDisplayHomeAsUpEnabled(true);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setTitle("");
    LayoutInflater layoutInflater=(LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View actionBarView =layoutInflater.inflate(R.layout.custom_chat_bar,null);
    actionBar.setCustomView(actionBarView);
    userImage=(CircleImageView)findViewById(R.id.custom_profile_IMAGE);
    userName=(TextView)findViewById(R.id.custom_profile_name);
    userLastSeen=(TextView)findViewById(R.id.custom_user_last_seen);
    SendMessageButton= (ImageButton)findViewById(R.id.send_message_btn);
    SendFilesButton= (ImageButton)findViewById(R.id.send_files_btn);
    MessageInputText=(EditText) findViewById(R.id.input_message);
    messageAdapter= new MessageAdapter(messagesList);
    userMessagesList= (RecyclerView) findViewById(R.id.private_messages_list_of_users);
    loadingBar=new ProgressDialog(this);
    linearLayoutManager=new LinearLayoutManager(this);
    userMessagesList.setLayoutManager(linearLayoutManager);
    userMessagesList.setAdapter(messageAdapter);
    Calendar calendar=Calendar.getInstance();
    SimpleDateFormat currentDate=new SimpleDateFormat("dd.MM.yyyy");
    saveCurrentDate=currentDate.format(calendar.getTime());
    SimpleDateFormat currentTime=new SimpleDateFormat("HH:mm");
    saveCurrentTime=currentTime.format(calendar.getTime());
        phoneSoundCall=(ImageButton)findViewById(R.id.phoneSoundCall);
        phoneVideoCall=(ImageButton)findViewById(R.id.phoneVideoCall);
        chatOptionsMenu=(ImageButton)findViewById(R.id.chatOptionsMenu);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==438 && resultCode==RESULT_OK && data!=null && data.getData()!=null)
        {
            loadingBar.setTitle("Gönderiliyor");
            loadingBar.setMessage("Lütfen bekleyiniz...");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();
            fileUri=data.getData();
            if (!checker.equals("image"))
            {
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Document Files");
                String messageSenderRef="Messages/" + messageSenderID + "/" + messageReceiverID;
                String messageReceiverRef="Messages/" + messageReceiverID + "/" + messageSenderID;
                DatabaseReference userMessageKeyRef=RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();
                final String messagePushID=userMessageKeyRef.getKey();
                StorageReference filePath= storageReference.child(messagePushID + "." + checker);
                filePath.putFile(fileUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful())
                    {
                        Map messageTextBody= new HashMap();
                        messageTextBody.put("message",task.getResult().getStorage().toString());
                        messageTextBody.put("name",fileUri.getLastPathSegment());
                        messageTextBody.put("type",checker);
                        messageTextBody.put("from",messageSenderID);
                        messageTextBody.put("to",messageReceiverID);
                        messageTextBody.put("messageID",messagePushID);
                        messageTextBody.put("time",saveCurrentTime);
                        messageTextBody.put("date",saveCurrentDate);
                        Map messageBodyDetails=new HashMap();
                        messageBodyDetails.put(messageSenderRef + "/" + messagePushID,messageTextBody);
                        messageBodyDetails.put(messageReceiverRef + "/" + messagePushID,messageTextBody);
                        RootRef.updateChildren(messageBodyDetails);
                        loadingBar.dismiss();
                    }
                }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                    loadingBar.dismiss();
                        Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot)
                    {
                       double p=(100.0*snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                       loadingBar.setMessage((int) p + " % Yükleniyor...");
                    }
                });
            }
            else if (checker.equals("image"))
            {
                StorageReference storageReference= FirebaseStorage.getInstance().getReference().child("Image Files");
                String messageSenderRef="Messages/" + messageSenderID + "/" + messageReceiverID;
                String messageReceiverRef="Messages/" + messageReceiverID + "/" + messageSenderID;
                DatabaseReference userMessageKeyRef=RootRef.child("Messages")
                        .child(messageSenderID).child(messageReceiverID).push();
               final String messagePushID=userMessageKeyRef.getKey();
                StorageReference filePath= storageReference.child(messagePushID + "." + "jpg");
                uploadTask=filePath.putFile(fileUri);
                uploadTask.continueWithTask(new Continuation() {
                    @Override
                    public Object then(@NonNull Task task) throws Exception
                    {
                        if (!task.isSuccessful())
                        {
                            throw task.getException();
                        }

                        return filePath.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful())
                        {
                        Uri downloadUrl=task.getResult();
                        myUrl=downloadUrl.toString();

                            Map messageTextBody= new HashMap();
                            messageTextBody.put("message",myUrl);
                            messageTextBody.put("name",fileUri.getLastPathSegment());
                            messageTextBody.put("type",checker);
                            messageTextBody.put("from",messageSenderID);
                            messageTextBody.put("to",messageReceiverID);
                            messageTextBody.put("messageID",messagePushID);
                            messageTextBody.put("time",saveCurrentTime);
                            messageTextBody.put("date",saveCurrentDate);

                            Map messageBodyDetails=new HashMap();
                            messageBodyDetails.put(messageSenderRef + "/" + messagePushID,messageTextBody);
                            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID,messageTextBody);

                            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener()
                            {
                                @Override
                                public void onComplete(@NonNull Task task)
                                {
                                    if (task.isSuccessful())
                                    {
                                        loadingBar.dismiss();

                                    }
                                    else
                                    {
                                        loadingBar.dismiss();
                                        Toast.makeText(ChatActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                                    }
                                    MessageInputText.setText("");
                                }
                            });

                        }
                    }
                });
            }
            else
            {   loadingBar.dismiss();
                Toast.makeText(this, "Bir Dosya Seçiniz!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void DisplayLastSeen()
    {
        RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if (snapshot.child("userState").hasChild("state"))
                {
                    String state=snapshot.child("userState").child("state").getValue().toString();
                    String date=snapshot.child("userState").child("date").getValue().toString();
                    String time=snapshot.child("userState").child("time").getValue().toString();

                    if (state.equals("online"))
                    {
                        userLastSeen.setText("Çevrimiçi");


                    }
                    else if (state.equals("Çevrimdışı"))
                    {
                        userLastSeen.setText(date +" "+time);
                    }
                }
                else
                {
                    userLastSeen.setText("Çevrimdışı");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error)
            {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        RootRef.child("Messages").child(messageSenderID).child(messageReceiverID)
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                        Messages messages=snapshot.getValue(Messages.class);

                        messagesList.add(messages);
                        messageAdapter.notifyDataSetChanged();
                        userMessagesList.smoothScrollToPosition(userMessagesList.getAdapter().getItemCount());

                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private  void  SendMessage()
    {
        String messageText=MessageInputText.getText().toString();
        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "Bir Mesaj Yazınız", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String messageSenderRef="Messages/" + messageSenderID + "/" + messageReceiverID;
            String messageReceiverRef="Messages/" + messageReceiverID + "/" + messageSenderID;
            /*her mesaj için özel bir key oluştur*/
            DatabaseReference userMessageKeyRef=RootRef.child("Messages")
                    .child(messageSenderID).child(messageReceiverID).push();
            String messagePushID=userMessageKeyRef.getKey();

            Map messageTextBody= new HashMap();
            messageTextBody.put("message",messageText);
            messageTextBody.put("type","text");
            messageTextBody.put("from",messageSenderID);
            messageTextBody.put("to",messageReceiverID);
            messageTextBody.put("messageID",messagePushID);
            messageTextBody.put("time",saveCurrentTime);
            messageTextBody.put("date",saveCurrentDate);

            Map messageBodyDetails=new HashMap();
            messageBodyDetails.put(messageSenderRef + "/" + messagePushID,messageTextBody);
            messageBodyDetails.put(messageReceiverRef + "/" + messagePushID,messageTextBody);

            RootRef.updateChildren(messageBodyDetails).addOnCompleteListener(new OnCompleteListener()
            {
                @Override
                public void onComplete(@NonNull Task task)
                {
                    if (task.isSuccessful())
                    {

                    }
                    else
                    {
                        Toast.makeText(ChatActivity.this, "Hata", Toast.LENGTH_SHORT).show();
                    }
                    MessageInputText.setText("");
                }
            });
        }
    }


}