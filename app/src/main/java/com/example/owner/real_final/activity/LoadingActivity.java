package com.example.owner.real_final.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.owner.real_final.R;


public class LoadingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(R.layout.loading_activity);

        startLoading();
    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(LoadingActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 1000);
        */
        try {
            Thread.sleep(1000);
            Intent loadIntent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(loadIntent);
            finish();
        }catch(InterruptedException e){
            e.printStackTrace();
        }

    }
}