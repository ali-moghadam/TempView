package com.alirnp.tempviewdemo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.alirnp.tempview.TempView;

public class MainActivity extends AppCompatActivity {

    private TempView mTempView1;
    private TempView mTempView2;
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTempView1 = findViewById(R.id.tempView1);
        mTempView2 = findViewById(R.id.tempView2);
        mTextView = findViewById(R.id.statusTempView1);


        // setIndicatorModeValues(mTempView1);


        mTempView1.setOnSeekCirclesListener(new TempView.OnSeekChangeListener() {
            @Override
            public void onSeekChange(int value) {
                mTextView.setText(String.format("onSeekChange : value = %s", value));
            }

            @Override
            public void onSeekComplete(int value) {
                mTextView.setText(String.format("onSeekComplete : value = %s", value));
                mTempView2.setTemp(value);
            }
        });

    }

    private void setIndicatorModeValues(TempView mTempView) {
        mTempView.setMinValue(10);
        mTempView.setMaxValue(20);
        mTempView.setTemp(15);
    }
}

