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
        for (int i=0;i<3;i++){//准备给ViewPager 设置要添加的View
            View v= LayoutInflater.from(this).inflate(R.layout.item_pager,null);
            TextView textView=(TextView)v.findViewById(R.id.textView);
            textView.setText("index"+i);
            arr.add(v);
        }
        pager.setList(arr);//设置ViewList
        try {
            pager.setAutoChange(true);//设置自动切换页面
        } catch (Exception e) {
            Toast.makeText(this,""+e.toString(),Toast.LENGTH_LONG).show();
        }



        //LoopView 使用方法---------------------------------------------
        loopView=(LoopView)findViewById(R.id.loopView);
        loopView.setAutoRotationTime(3*1000);//设置自动旋转时间
        loopView.setAutoRotation(true);//启动自动旋转
        loopView.setR(200);//设置半径
    }
}
