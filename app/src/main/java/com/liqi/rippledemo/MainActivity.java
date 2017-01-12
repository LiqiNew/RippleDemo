package com.liqi.rippledemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liqi.rippledemo.diy.view.RippleView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private RippleView mRippleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRippleView = (RippleView) findViewById(R.id.RippleView);
        mRippleView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.RippleView:
                if (mRippleView.isStarTag())
                    mRippleView.stop();
                else
                    mRippleView.star();
                break;
        }
    }
}
