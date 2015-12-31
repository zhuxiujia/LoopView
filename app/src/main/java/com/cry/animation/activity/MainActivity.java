package com.cry.animation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.cry.animation.R;
import com.cry.loopviews.LoopView;
import com.cry.loopviews.LoopViewPager;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    LoopView loopView;
    LoopViewPager loopViewPager;
    CheckBox checkBox_hx,checkbox_zd_loopviewpager,checkBox_bj,checkbox_zd_loopview,checkbox_hx_loopview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        checkBox_hx=(CheckBox)findViewById(R.id.checkbox_hx);
        checkbox_zd_loopviewpager=(CheckBox)findViewById(R.id.checkbox_zd_loopviewpager);
        checkbox_zd_loopview=(CheckBox)findViewById(R.id.checkbox_zd_loopview);
        checkbox_hx_loopview=(CheckBox)findViewById(R.id.checkbox_hx_loopview);
        checkBox_bj=(CheckBox)findViewById(R.id.checkbox_r_animation);

        //LoopViewPager 使用方法---------------------------------------------
        loopViewPager =(LoopViewPager)findViewById(R.id.loopViewPager);
        loopViewPager.setAutoChangeTime(1*1000);//设置自动切换时间
        loopViewPager.setList(getViewList());//设置ViewList




        //LoopView 使用方法---------------------------------------------
        loopView=(LoopView)findViewById(R.id.loopView);
        loopView.setAutoRotationTime(1 * 1000);//设置自动旋转时间
        loopView.setR(getResources().getDimension(R.dimen.loopview_width)/2);//设置半径
        //loopView.RAnimation();//半径动画


        checkBox_hx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopViewPager.setHorizontal(isChecked);//设置LoopViewPager是否横向切换
            }
        });
        checkbox_hx_loopview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopView.setHorizontal(isChecked);//设置LoopView是否横向切换
            }
        });
        checkbox_zd_loopviewpager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopViewPager.setAutoChange(isChecked);//启动LoopViewPager自动切换
            }
        });
        checkbox_zd_loopview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopView.setAutoRotation(isChecked);//启动LoopView自动切换
            }
        });
        checkBox_bj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopView.RAnimation(isChecked);//半径动画
            }
        });
    }

    /*准备给ViewPager 设置要您的需要添加的View*/
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
