package com.cry.animation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cry.animation.LoopView;
import com.cry.animation.LoopViewPager;
import com.cry.animation.R;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    LoopView loopView;
    LoopViewPager pager;
    CheckBox checkBox_hx,checkBox_zd,checkBox_bj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkBox_hx=(CheckBox)findViewById(R.id.checkbox_hx);
        checkBox_zd=(CheckBox)findViewById(R.id.checkbox_zd);
        checkBox_bj=(CheckBox)findViewById(R.id.checkbox_r_animation);

        //LoopViewPager 使用方法---------------------------------------------
        pager=(LoopViewPager)findViewById(R.id.loopViewPager);
        pager.setList(getViewList());//设置ViewList




        //LoopView 使用方法---------------------------------------------
        loopView=(LoopView)findViewById(R.id.loopView);
        loopView.setAutoRotationTime(3 * 1000);//设置自动旋转时间
        loopView.setR(200);//设置半径
        //loopView.RAnimation();//半径动画


        checkBox_hx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pager.setHorizontal(isChecked);//设置是否横向切换
            }
        });
        checkBox_zd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopView.setAutoRotation(isChecked);//启动自动旋转
                pager.setAutoChange(isChecked);
            }
        });
        checkBox_bj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopView.RAnimation(isChecked);//半径动画
            }
        });
    }

    /*准备给ViewPager 设置要添加的View*/
    private List<View> getViewList() {
        List<View> arr=new ArrayList<View>();
        for (int i=0;i<3;i++){
            View v= LayoutInflater.from(this).inflate(R.layout.item_pager,null);
            TextView textView=(TextView)v.findViewById(R.id.textView);
            textView.setText("index"+i);
            arr.add(v);
        }
        return arr;
    }
}
