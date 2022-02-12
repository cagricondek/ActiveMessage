package com.example.activemessage;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

public class ApplicationSettingsActivity extends AppCompatActivity {
private ImageView hesap,tema,bildirimler,yardım;
private Toolbar application_settings_toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_settings);
        hesap=findViewById(R.id.hesap);
        tema=findViewById(R.id.tema);
        bildirimler=findViewById(R.id.bildirimler);
        yardım=findViewById(R.id.yardım);
        application_settings_toolbar=findViewById(R.id.application_settings_toolbar);
        setSupportActionBar(application_settings_toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setTitle("Ayarlar");



    }
}