package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import java.util.concurrent.TimeUnit;

public class PhoneLoginOTPActivity extends AppCompatActivity {
private EditText inputCode1,inputCode2,inputCode3,inputCode4,inputCode5,inputCode6;
private String verificationID;
FirebaseAuth mAuth;
    private DatabaseReference RootRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login_o_t_p);
        TextView textMobile = findViewById(R.id.textMobile);
        textMobile.setText(String.format("+90-%s", getIntent().getStringExtra("mobile")
        ));
        inputCode1 = findViewById(R.id.inputCode1);
        inputCode2 = findViewById(R.id.inputCode2);
        inputCode3 = findViewById(R.id.inputCode3);
        inputCode4 = findViewById(R.id.inputCode4);
        inputCode5 = findViewById(R.id.inputCode5);
        inputCode6 = findViewById(R.id.inputCode6);
        setupOTPInputs();

        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button buttonVerify = findViewById(R.id.buttonVerify);
        verificationID = getIntent().getStringExtra("verificationID");

        /*onayla butonuna tıklayınca*/
        buttonVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (inputCode1.getText().toString().trim().isEmpty() ||
                        inputCode2.getText().toString().trim().isEmpty() ||
                        inputCode3.getText().toString().trim().isEmpty() ||
                        inputCode4.getText().toString().trim().isEmpty() ||
                        inputCode5.getText().toString().trim().isEmpty() ||
                        inputCode6.getText().toString().trim().isEmpty()) 
                {
                    Toast.makeText(PhoneLoginOTPActivity.this, "Lütfen Doğrulama Kodunu Giriniz", Toast.LENGTH_SHORT).show();
                    return;
                }
                String code=inputCode1.getText().toString()+
                        inputCode2.getText().toString() +
                        inputCode3.getText().toString()+
                        inputCode4.getText().toString()+
                        inputCode5.getText().toString()+
                        inputCode6.getText().toString();
               if (verificationID !=null)
               {
                   progressBar.setVisibility(View.VISIBLE);
                   buttonVerify.setVisibility(View.INVISIBLE);
                   PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(
                           verificationID,
                           code
                   );
                   FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                       @Override
                       public void onComplete(@NonNull Task<AuthResult> task)
                       {
                        progressBar.setVisibility(View.GONE);
                        buttonVerify.setVisibility(View.VISIBLE);
                        if (task.isSuccessful())
                        {
                            Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }
                        else
                            {
                                Toast.makeText(PhoneLoginOTPActivity.this, "Girdiğiniz Doğrulama Kodu Hatalı!", Toast.LENGTH_SHORT).show();
                            }
                       }
                   });
               }
            }
        });
        findViewById(R.id.textResendOTP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+90"+getIntent().getStringExtra("mobile"),
                        60,
                        TimeUnit.SECONDS,
                        PhoneLoginOTPActivity.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                        {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                            {
                                VerifyUserExistance();
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e)
                            {

                                Toast.makeText(PhoneLoginOTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String newverificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                            {
                                verificationID=newverificationID;
                                Toast.makeText(PhoneLoginOTPActivity.this, "Doğrulama Kodu Gönderildi.", Toast.LENGTH_SHORT).show();

                            }
                        }
                );
            }
        });
    }


    /*otp kodlarını birleştirme methodu*/
    private void setupOTPInputs()
    {
        inputCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            if (!s.toString().trim().isEmpty()){
                inputCode2.requestFocus();
            }}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!s.toString().trim().isEmpty()){
                    inputCode3.requestFocus();
                }}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!s.toString().trim().isEmpty()){
                    inputCode4.requestFocus();
                }}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!s.toString().trim().isEmpty()){
                    inputCode5.requestFocus();
                }}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        inputCode5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
                if (!s.toString().trim().isEmpty()){
                    inputCode6.requestFocus();
                }}

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
    private void VerifyUserExistance()
    {
        String currentUserID = mAuth.getCurrentUser().getUid();
        /*userphone eşleşiyor mu diye kontrol ediyoruz*/
        RootRef.child("Users").child(currentUserID).addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if ((snapshot.child("uid").exists()))
                {
                    Toast.makeText(PhoneLoginOTPActivity.this,"Hoşgeldiniz.",Toast.LENGTH_SHORT).show();
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


    }private void SendUserToSettingsActivity() {
        Intent settingsIntent=new Intent(PhoneLoginOTPActivity.this,SettingsActivity.class);
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(settingsIntent);
        finish();
    }
}
