package com.cry.animation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    LoopView loopView;
    LoopViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager=(LoopViewPager)findViewById(R.id.loopViewPager);
        List<View> arr=new ArrayList<View>();

        for (int i=0;i<3;i++){
            TextView textView=new TextView(this);
            textView.setText("第" + i + "个");
            textView.setBackgroundResource(R.drawable.image_red);
            //textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
            textView.setGravity(TextView.TEXT_ALIGNMENT_CENTER);
            arr.add(textView);
        }
        pager.setList(arr);
        pager.setAutoChange(true);

        loopView=(LoopView)findViewById(R.id.loopView);
        loopView.setAutoRotationTime(3*1000);
        loopView.setAutoRotation(true);
        loopView.setR(200);



    }
}
