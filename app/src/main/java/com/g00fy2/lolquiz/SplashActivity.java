package com.g00fy2.lolquiz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        //beendet die activity (kein zurück mehr auf diese seite)
        finish();
    }

}