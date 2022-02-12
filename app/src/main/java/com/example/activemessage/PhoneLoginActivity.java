package com.example.activemessage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;
import java.util.concurrent.TimeUnit;

public class PhoneLoginActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
    final  EditText inputMobile =findViewById(R.id.inputMobile);
    Button buttonGetOTP=findViewById(R.id.buttonGetOTP);
    final ProgressBar progressBar=findViewById(R.id.progressBar);
    final CountryCodePicker  inputCountryCode =findViewById(R.id.inputCountryCode);




    /*kodu yolla butonuna tıklayınca*/
    buttonGetOTP.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (inputMobile.getText().toString().trim().isEmpty())
            {
                Toast.makeText(PhoneLoginActivity.this, "Lütfen Telefonunuzu Giriniz", Toast.LENGTH_SHORT).show();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            buttonGetOTP.setVisibility(View.INVISIBLE);

            String code = inputCountryCode.getSelectedCountryCodeWithPlus();
            String number = inputMobile.getText().toString().trim();
            String phoneNumber = code + number;
            PhoneAuthProvider.getInstance().verifyPhoneNumber(phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    PhoneLoginActivity.this,
                    new PhoneAuthProvider.OnVerificationStateChangedCallbacks()
                    {
                        @Override
                        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential)
                        {
                            progressBar.setVisibility(View.GONE);
                            buttonGetOTP.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onVerificationFailed(@NonNull FirebaseException e)
                        {
                            progressBar.setVisibility(View.GONE);
                            buttonGetOTP.setVisibility(View.VISIBLE);
                            Toast.makeText(PhoneLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCodeSent(@NonNull String verificationID, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken)
                        {
                           progressBar.setVisibility(View.GONE);
                           buttonGetOTP.setVisibility(View.VISIBLE);
                            Intent intent =new Intent(getApplicationContext(),PhoneLoginOTPActivity.class);
                            intent.putExtra("mobile",inputMobile.getText().toString());
                            intent.putExtra("verificationID",verificationID);
                            startActivity(intent);
                        }
                    }
            );

        }
    });

        }}