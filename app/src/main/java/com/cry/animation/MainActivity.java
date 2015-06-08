package com.cry.animation;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {
    LoopView loopView;
    LoopViewPager pager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //LoopViewPager 使用方法---------------------------------------------
        pager=(LoopViewPager)findViewById(R.id.loopViewPager);
        List<View> arr=new ArrayList<View>();
        for (int i=0;i<3;i++){
            View v= LayoutInflater.from(this).inflate(R.layout.item_pager,null);
            TextView textView=(TextView)v.findViewById(R.id.textView);
            textView.setText("index"+i);
            arr.add(v);
        }
        pager.setList(arr);
        try {
            pager.setAutoChange(true);
        } catch (Exception e) {
            Toast.makeText(this,""+e.toString(),Toast.LENGTH_LONG).show();
        }



        //LoopView 使用方法---------------------------------------------
        loopView=(LoopView)findViewById(R.id.loopView);
        loopView.setAutoRotationTime(3*1000);
        loopView.setAutoRotation(true);
        loopView.setR(200);
    }
}
