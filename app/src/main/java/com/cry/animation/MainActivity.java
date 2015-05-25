package com.cry.animation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;


public class MainActivity extends ActionBarActivity {
    LoopView loopView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        loopView=(LoopView)findViewById(R.id.loopview);
        loopView.setAutoRotationTime(3*1000);
        loopView.setAutoRotation(true);
        loopView.setR(200);
        loopView.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void selected(int item) {
                Log.i("ds", "item:" + item);
            }
        });

    }
}
