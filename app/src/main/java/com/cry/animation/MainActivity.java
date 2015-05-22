package com.cry.animation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;


public class MainActivity extends ActionBarActivity {
    LoopView loopView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        loopView=(LoopView)findViewById(R.id.loopview);
        loopView.setAutoRotation(true);
        loopView.setR(200);

    }
}
