package com.example.remindme;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AboutScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_screen);
        getSupportActionBar().setTitle(R.string.titleAbout);
    }
}