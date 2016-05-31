package com.cry.animation.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.cry.animation.R;
import com.cry.loopviews.LoopView;
import com.cry.loopviews.LoopViewPager;
import com.cry.loopviews.listener.OnInvateListener;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {
    LoopView loopView;
    LoopViewPager loopViewPager;

    SeekBar seekBar_x, seekBar_z;

    CheckBox
            checkbox_zd_loopviewpager,
            checkbox_zd_loopview,
            checkbox_use_textview;

    Switch switch_r_animation,
            switch_hx,
            switch_hx_loopview;
    TextView textView_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAllViews();
        initLoopView();
        initListener();
    }

    private void initAllViews() {
        textView_info = (TextView) findViewById(R.id.textView_info);

        switch_hx = (Switch) findViewById(R.id.switch_hx);
        checkbox_zd_loopviewpager = (CheckBox) findViewById(R.id.checkbox_zd_loopviewpager);
        checkbox_zd_loopview = (CheckBox) findViewById(R.id.checkbox_zd_loopview);
        switch_hx_loopview = (Switch) findViewById(R.id.switch_hx_loopview);
        switch_r_animation = (Switch) findViewById(R.id.switch_r_animation);
        checkbox_use_textview = (CheckBox) findViewById(R.id.checkbox_use_textview);


        seekBar_x = (SeekBar) findViewById(R.id.seekBar_x);
        seekBar_z = (SeekBar) findViewById(R.id.seekBar_z);

        seekBar_x.setProgress(seekBar_x.getMax() / 2);
        seekBar_z.setProgress(seekBar_z.getMax() / 2);

        //LoopViewPager 使用方法---------------------------------------------
        loopViewPager = (LoopViewPager) findViewById(R.id.loopViewPager);
        loopViewPager.setAutoChangeTime(1 * 1000);//设置自动切换时间
        loopViewPager.setViewList(getViewList());//设置ViewList

        //LoopView 使用方法---------------------------------------------
        loopView = (LoopView) findViewById(R.id.loopView0);
    }

    private void initListener() {

        loopView.setOnInvateListener(new OnInvateListener() {
            @Override
            public void onInvate(LoopView loopView) {
                try {
                    //打印数据
                    textView_info.setText(
                            "\nRotation:" + (int) loopView.getAngle()
                                    + "\nRotationX:" + (int) loopView.getLoopRotationX()
                                    + "\nRotationZ:" + (int) loopView.getLoopRotationZ()
                                    + "\nR:" + loopView.getR()
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        seekBar_x.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                loopView.setLoopRotationX(progress - seekBar.getMax() / 2);
                loopView.invate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBar_z.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                loopView.setLoopRotationZ(progress - seekBar.getMax() / 2);
                loopView.invate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        switch_hx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopViewPager.setHorizontal(isChecked);//设置LoopViewPager是否横向切换
            }
        });
        switch_hx_loopview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                loopView.setHorizontal(isChecked, true);//设置LoopView是否横向切换
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
        switch_r_animation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                loopView.RAnimation(isChecked);//半径动画
            }
        });
        checkbox_use_textview.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    loopView.removeAllViews();
                    loopView.addView(inflate(R.layout.item_view0));
                    loopView.addView(inflate(R.layout.item_view1));
                    loopView.addView(inflate(R.layout.item_view2));
                    buttonView.setHint("使用layout");
                } else {
                    loopView.removeAllViews();
                    for (int i = 0; i < 6; i++) {
                        loopView.addView(inflate(R.layout.item_textv));
                    }
                    buttonView.setHint("使用TextView");
                }
                try {
                    loopView.setSelectItem(0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //loopView.RAnimation(true);
            }
        });
    }

    private View inflate(int layout) {
        View v = LayoutInflater.from(MainActivity.this).inflate(layout, null);
        LoopView.LayoutParams params = new LoopView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(LoopView.CENTER_IN_PARENT);
        v.setLayoutParams(params);
        return v;
    }


    private void initLoopView() {
        loopView.setAutoRotationTime(1 * 1000)//设置自动旋转时间
                .setR(getResources().getDimension(R.dimen.loopview_width) / 2)//设置半径
                .setLoopRotationX(0)//x轴旋转
                .setLoopRotationZ(0); //z轴旋转
        //.RAnimation(1f,loopView.getR());//半径动画
    }

    /*准备给ViewPager 设置要您的需要添加的View*/
    private List<View> getViewList() {
        List<View> arr = new ArrayList<View>();
        for (int i = 0; i < 3; i++) {
            View v = LayoutInflater.from(this).inflate(R.layout.item_pager, null);
            TextView textView = (TextView) v.findViewById(R.id.textView);
            textView.setText("index" + i);
            arr.add(v);
        }
        return arr;
    }
}
