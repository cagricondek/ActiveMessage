package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

public class RegisterActivity extends AppCompatActivity {


private Button CreateAccountButton;
private EditText UserName,UserEmail,UserPassword,UserConformPassword;
private TextView AlreadyHaveAccountLink;
private FirebaseAuth mAuth;
private DatabaseReference RootRef;
private ProgressDialog loadingBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth= FirebaseAuth.getInstance();
        RootRef= FirebaseDatabase.getInstance().getReference();
        InitializeFields();


/*register ve login menu */
AlreadyHaveAccountLink.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v)
        {
        SendUserToLoginActivity();

         }
    });
    /*kayit ol butonu*/
    CreateAccountButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) 
        {
        CreateNewAccount();
        }
    });

    }
/*new account create*/
    private void CreateNewAccount()
    {   String username=UserName.getText().toString();
        String email=UserEmail.getText().toString();
        String password=UserPassword.getText().toString();
        String conform_password=UserConformPassword.getText().toString();
        if(TextUtils.isEmpty(email))
        {
            Toast.makeText(this,"LÃ¼tfen geÃ§erli bir email giriniz!",Toast.LENGTH_SHORT).show();
        }
        if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"LÃ¼tfen geÃ§erli bir ÅŸifre giriniz!",Toast.LENGTH_SHORT).show();
        }
        else
            {   loadingBar.setTitle("KayÄ±t Ol");
                loadingBar.setMessage("KayÄ±t OluÅŸturuluyor,LÃ¼tfen Bekleyiniz...");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();
                /*firebase register with email pass*/
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                        if (task.isSuccessful())
                        {
                            FirebaseMessaging.getInstance().getToken()
                                    .addOnCompleteListener(new OnCompleteListener<String>() {
                                        @Override
                                        public void onComplete(@NonNull Task<String> task) {
                                            if (!task.isSuccessful()) {
                                            }
                                            String deviceToken = task.getResult();
                            String currentUserID=mAuth.getCurrentUser().getUid();
                            RootRef.child("Users").child(currentUserID).setValue("");
                            RootRef.child("Users").child(currentUserID).child("device_token").setValue(deviceToken);
                                        }
                                    });

                            SendUserToMainActivity();
                            Toast.makeText(RegisterActivity.this,"BaÅŸarÄ±yla KayÄ±t OluÅŸturdunuz...",Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                        else
                            {
                                String message=task.getException().toString();
                                Toast.makeText(RegisterActivity.this,"Hata!"+message,Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                            }
                    }
                });
            }
    }


    /*data init*/
    private void InitializeFields() {
        CreateAccountButton=(Button) findViewById(R.id.register_button);
        UserName=(EditText) findViewById(R.id.register_username);
        UserEmail=(EditText) findViewById(R.id.register_email);
        UserPassword=(EditText) findViewById(R.id.register_password);
        UserConformPassword=(EditText) findViewById(R.id.register_conform_password);
        AlreadyHaveAccountLink=(TextView) findViewById(R.id.alreadyHaveAccount);
        loadingBar=new ProgressDialog(this);
    }
    /*menu gecis*/
    private void SendUserToLoginActivity()
    {
        Intent loginIntent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(loginIntent);
    }

    /*main back*/
    private void SendUserToMainActivity() {
        Intent mainIntent=new Intent(RegisterActivity.this,MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

}
