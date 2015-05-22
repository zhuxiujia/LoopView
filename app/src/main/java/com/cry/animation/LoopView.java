package com.cry.animation;

import android.animation.Animator;
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
import java.util.Timer;
import java.util.TimerTask;

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
    private Timer timer=null;//自动旋转timer
    private boolean autoRotation=false;//自动旋转
    private long autoRotationTime=3000;//旋转时间
    List<View> views=new ArrayList<View>();//子view引用列表

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
            float scale0= (distance -y0)/ (distance+r);
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // initView();
        invate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        initView();
        invate();
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
        AnimRotationTo(finall, null);
    }


    private void AnimRotationTo(float finall, final Runnable complete){
        if(angle==finall){return;}
        valueAnimator= ValueAnimator.ofFloat(angle,finall);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(300);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                angle = (Float) animation.getAnimatedValue();
                invate();
            }
        });
        if(complete!=null){valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                complete.run();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });}
        valueAnimator.start();
    }

    private boolean onTouch(MotionEvent event){
        this.getParent().requestDisallowInterceptTouchEvent(true);//通知父控件勿拦截本控件
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
        try{timer.cancel();
            timer=null;}catch (Exception e){}
        this.autoRotation = autoRotation;
        angle=0;
        invate();
        if(autoRotation){
            timer=  new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(size!=0)AnimRotationTo(angle-360/size,null);
                        }
                    });
                }
            }, 0,autoRotationTime);
        }else{
            try{ timer.cancel();
                timer=null;}catch (Exception e){}
        }
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
