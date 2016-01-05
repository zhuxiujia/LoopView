package com.cry.loopviews;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zxj on 15/5/20.
 * 水平旋转轮播控件
 */
public class LoopView extends RelativeLayout{
    final String TAG=getClass().getSimpleName();
    final static int LoopR=200;
    ValueAnimator restAnimator =null;//回位动画
    ValueAnimator rAnimation =null;//半径动画
    GestureDetector mGestureDetector=null;//手势类
    Comparator comp=new SortComparator();
    List<View> viewSortArray =new ArrayList<>();
    private int selectItem=0;//当前选择项
    private int   size=0;//个数
    private float r=LoopR;//半径
    private float distance =3*r;//camera和观察的旋转物体距离， 距离越长,最大物体和最小物体比例越不明显
    private float angle=0;//角度
    private float last_angle=0;
    private boolean autoRotation=false;//自动旋转
    private boolean touching=false;//正在触摸
    private boolean horizontal=true;//是否横向滑动，否则竖直方向滑动
    private int loopRotationX =0, loopRotationZ =0;//x轴旋转和轴旋转，y轴无效果
    List<View> views=new ArrayList<>();//子view引用列表


    LoopHandler loopHandler=new LoopHandler(3000) {
        @Override
        public void du() {
            try{if(size!=0)AnimRotationTo(angle-360/size,null);}catch (Exception e){}
        }
    };
    private OnItemSelectedListener onItemSelectedListener=null;//选择事件接口

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

    private void init(final Context context) {
        mGestureDetector= new GestureDetector(context, getGeomeryController());
        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                views.add(child);
                size=getChildCount();
            }
            @Override
            public void onChildViewRemoved(View parent, View child) {
                views.remove(child);
                size=getChildCount();
            }
        });
    }
    private void sortList(List<View> list){
        Collections.sort(list, comp);
        for (int i=0;i<list.size();i++){
            list.get(i).bringToFront();
            list.get(i).setEnabled(i == (list.size() - 1) && angle % (360 / size) == 0 ? true : false);
        }
    }
    private class SortComparator implements Comparator<View> {
        @Override
        public int compare(View lhs, View rhs) {
            int result=0;
            try {
                result=(int)(1000*lhs.getScaleX() -1000*rhs.getScaleX());
            }catch (Exception e){}
            return result;
        }
    }
    private GestureDetector.SimpleOnGestureListener getGeomeryController(){
        return new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(horizontal){angle+=distanceX/4;}
                else {angle+=distanceY/4;}
                invate();
                return true;
            }
        };
    }
    public void invate() {
        for (int i=0;i<views.size();i++){
            double radians=angle+180- i * 360 / size;
            float x0 = (float) Math.sin(Math.toRadians(radians))*r;
            float y0=(float)Math.cos(Math.toRadians(radians))*r;
            float scale0= (distance -y0)/ (distance+r);
            views.get(i).setScaleX(scale0);
            views.get(i).setScaleY(scale0);
            float rotationX_y=(float)Math.sin(Math.toRadians(loopRotationX *Math.cos(Math.toRadians(radians))))*r;
            float rotationZ_y=(float)Math.sin(Math.toRadians(loopRotationZ *Math.sin(Math.toRadians(radians))))*r;
            if(horizontal){
                views.get(i).setTranslationX( x0 );
                views.get(i).setTranslationY(rotationX_y+rotationZ_y);
            }
            else {
                views.get(i).setTranslationY( x0 );
                views.get(i).setTranslationX(rotationX_y+rotationZ_y);
            }
        }
        redoSortArray();
        sortList(viewSortArray);
        invalidate();
    }
    private void redoSortArray() {
        viewSortArray.clear();
        for (int i=0;i<views.size();i++){
            viewSortArray.add(views.get(i));
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        invate();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
       if(changed){
           setSelectItem(0);
           RAnimation();
       }
    }


    public void RAnimation() {
       RAnimation(1f,r);
    }
    public void RAnimation(boolean fromZeroToLoopR) {
        if(fromZeroToLoopR){RAnimation(1f,LoopR);}else {
            RAnimation(LoopR,1f);
        }
    }
    public void RAnimation(float from,float to) {
        if(rAnimation!=null)if(rAnimation.isRunning()==true)rAnimation.cancel();
        rAnimation=ValueAnimator.ofFloat(from,to);
        rAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                r = (Float) valueAnimator.getAnimatedValue();
                invate();
            }
        });
        rAnimation.setInterpolator(new DecelerateInterpolator());
        rAnimation.setDuration(2000);
        rAnimation.start();
    }
    private void restPosition() {
        if(size==0){return;}
        float finall=0;
        float part =360/size;//一份的角度
        if(angle<0){part =-part;}
        float minvalue= (int)(angle/ part)* part;//最小角度
        float maxvalue=(int)(angle/ part)* part + part;//最大角度
        if(angle>=0) {//分为是否小于0的情况
            if (angle-last_angle>0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        }else{
            if (angle-last_angle<0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        }
        AnimRotationTo(finall, null);
    }
    private void AnimRotationTo(float finall, final Runnable complete){
        if(angle==finall){return;}
        restAnimator = ValueAnimator.ofFloat(angle,finall);
        restAnimator.setInterpolator(new DecelerateInterpolator());
        restAnimator.setDuration(300);
        restAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if(touching==false){angle = (Float) animation.getAnimatedValue();
                invate();}
            }
        });
        restAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(touching==false) {
                    selectItem = calculateItem();
                    if (selectItem < 0) {
                        selectItem = size + selectItem;
                    }
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.selected(selectItem, views.get(selectItem));
                    }
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        if(complete!=null){
            restAnimator.addListener(new Animator.AnimatorListener() {
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
        restAnimator.start();
    }

    private int calculateItem() {
        return (int) (angle / (360 / size)) % size;
    }

    private boolean onTouch(MotionEvent event){
        if(event.getAction()==MotionEvent.ACTION_DOWN){last_angle=angle;touching=true;}
        boolean sc=mGestureDetector.onTouchEvent(event);
        if(sc){
            this.getParent().requestDisallowInterceptTouchEvent(true);//通知父控件勿拦截本控件
        }
        if (event.getAction()==MotionEvent.ACTION_UP||event.getAction()==MotionEvent.ACTION_CANCEL){
            touching=false;
            restPosition();
            return true;
        }
        return true;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        onTouch(ev);
        return super.dispatchTouchEvent(ev);
    }

    public List<View> getViews() {
        return views;
    }

    public float getAngle() {
        return angle;
    }

    public LoopView setAngle(float angle) {
        this.angle = angle;
        return this;
    }

    public float getDistance() {
        return distance;
    }

    public LoopView setDistance(float distance) {
        this.distance = distance;
        return this;
    }

    public float getR() {
        return r;
    }

    public int getSelectItem() {
        return selectItem;
    }
     /*selecItem must > 0*/
    public LoopView setSelectItem(int selectItem){
        if(size>0) {
            if (selectItem > 0 && selectItem < size) {
                this.selectItem = selectItem;
                AnimRotationTo(selectItem * (360 / size), null);
            }else {
                Log.e(TAG,"must selectItem >0 or selectItem<size");
            }
        }else {
            Log.e(TAG,"size is zero");
        }
        return this;
    }

    public LoopView setR(float r) {
        this.r = r;
        distance=3*r;
        return this;
    }

    public LoopView setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
        return this;
    }

    public LoopView setAutoRotation(boolean autoRotation) {
        loopHandler.setLoop(autoRotation);
        return this;
    }

    public long getAutoRotationTime() {
        return loopHandler.loopTime;
    }

    public LoopView setAutoRotationTime(long autoRotationTime) {
        loopHandler.setLoopTime(autoRotationTime);
        return this;
    }

    public boolean isAutoRotation() {
        return autoRotation;
    }

    public LoopView setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
        invate();
        return  this;
    }

    public LoopView setLoopRotationX(int loopRotationX) {
        this.loopRotationX = loopRotationX;
        return this;
    }

    public LoopView setLoopRotationZ(int loopRotationZ) {
        this.loopRotationZ = loopRotationZ;
        return this;
    }

    public int getLoopRotationX() {
        return loopRotationX;
    }

    public int getLoopRotationZ() {
        return loopRotationZ;
    }

    public ValueAnimator getRestAnimator() {
        return restAnimator;
    }

    public ValueAnimator getrAnimation() {
        return rAnimation;
    }
}
