package com.alirnp.tempviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.alirnp.tempview.TempView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TempView.show();
    }
}
