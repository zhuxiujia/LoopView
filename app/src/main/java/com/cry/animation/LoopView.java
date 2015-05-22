package com.cry.animation;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zxj on 15/5/20.
 * 水平旋转轮播控件
 */
public class LoopView extends FrameLayout{
    Context con;
    ValueAnimator valueAnimator=null;//动画类
    GestureDetector mGestureDetector=null;//手势类
    private int   size=0;//个数
    private float r=200;//半径
    private float distance =4*r;//camera和观察的旋转物体距离， 距离越长,最大物体和最小物体比例越不明显
    private float angle=0;//角度
    private boolean autoRotation=false;//自动旋转
    private long autoRotationTime=3000;//旋转时间
    List<View> views=new ArrayList<>();//子view引用列表

    Handler handler=new Handler();
    public LoopView(Context context) {
        super(context);
        init(context);
    }

    public LoopView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.con=context;
        mGestureDetector= new GestureDetector(context, getGeomeryController());
        //restPosition();
    }
    private void sortList(List<View> list){
        Comparator comp=new SortComparator();
        Collections.sort(list, comp);
        for (int i=0;i<list.size();i++){
           list.get(i).bringToFront();
            list.get(i).setEnabled(i==(list.size()-1)&&angle%(360/size)==0 ? true:false);
        }

    }
    private class SortComparator implements Comparator<View> {
        @Override
        public int compare(View lhs, View rhs) {
            int result=0;
            try {
                result=(int)(100*lhs.getScaleX() -100*rhs.getScaleX());
            }catch (Exception e){}
            return result;
        }
    }
    private GestureDetector.SimpleOnGestureListener getGeomeryController(){
        return new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //Log.i("ds","distance:"+distanceX);
                angle+=distanceX/6;
                invate();
                return true;
            }
        };
    }

    public void invate() {
        int width= getWidth();
        for (int i=0;i<views.size();i++){
            float x0 = (float) Math.sin(Math.toRadians(angle+180- i * 360 / size))*r;
            float y0=(float)Math.cos(Math.toRadians(angle+180 - i * 360 / size))*r;
            float scale0= (distance -y0)/ (distance+views.get(i).getWidth()/2);
            views.get(i).setScaleX(scale0);
            views.get(i).setScaleY(scale0);
            views.get(i).setX(width / 2 + x0 - views.get(i).getWidth() / 2);
        }
       List<View> arr=new ArrayList<View>();
       for (int i=0;i<views.size();i++){
           arr.add(views.get(i));
       }
        sortList(arr);
        postInvalidate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        initView();
        invate();
        super.onLayout(changed, l, t, r, b);
    }

    private void initView() {
        for (int i=0;i<views.size();i++){
            views.remove(i);
        }
        final int count=getChildCount();
        size=count;
        for (int i=0;i<count;i++){
            views.add(getChildAt(i));
        }
    }

    private void restPosition() {
        float finall=0;
        float part =360/size;//一份的角度
        if(angle<0){
            part =-part;}
        float minvalue= (int)(angle/ part)* part;//最小角度
        float maxvalue=(int)(angle/ part)* part + part;//最大角度
        if(angle>=0) {//分为是否小于0的情况
            if (angle > (minvalue + part / 2)) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        }else{
            if (angle < (minvalue + part / 2)) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        }
        AnimRotationTo(finall);
    }

    private void AnimRotationTo(float finall){
        if(angle==finall){return;}
        valueAnimator= ValueAnimator.ofFloat(angle,finall);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(300);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle= (float)animation.getAnimatedValue();
                invate();
            }
        });
        valueAnimator.start();
    }

    private boolean onTouch(MotionEvent event){
        if (event.getAction()==MotionEvent.ACTION_UP){
            restPosition();
            return true;
        }
        mGestureDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        onTouch(event);
        return true;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouch(ev);
        return super.dispatchTouchEvent(ev);
    }



    public float getAngle() {
        return angle;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public float getR() {
        return r;
    }

    public void setR(float r) {
        this.r = r;
        this.distance=4*r;
    }

    public void setAutoRotation(boolean autoRotation) {
        this.autoRotation = autoRotation;
        angle=0;
        sendDelayed();
    }
    private void sendDelayed(){
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimRotationTo(angle-360/size);
                if(autoRotation)sendDelayed();
            }
        }, autoRotationTime);
    }

    public long getAutoRotationTime() {
        return autoRotationTime;
    }

    public void setAutoRotationTime(long autoRotationTime) {
        this.autoRotationTime = autoRotationTime;
    }

    public boolean isAutoRotation() {
        return autoRotation;
    }
}
