package com.cry.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zxj on 15/6/6.
 */
public class LoopViewPager extends FrameLayout{
    private Context con=null;
    float pagerwidth=0;//View宽度
    private BaseAdapter adapter=null;//Adapter
    private List<View> list=new ArrayList<View>();//view数组
    private GestureDetector mGestureDetector;//手势
    private int item=0;//滑动项目
    private float distence=0;//距离
    float last_distence=0;//记录上次按下的距离
    private ValueAnimator valueAnimator=null;//切换动画
    private Handler hand=new Handler();
    private Timer timer=null;
    private TimerTask task=null;
    private boolean autoChange=false;
    private long autoChangeTime=4000;//旋转时间
    private ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    private OnItemSelectedListener onItemSelectedListener=null;

    public LoopViewPager(Context context) {
        super(context);
        init(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoopViewPager(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }


    private void init(Context context) {
        this.con=context;
        mGestureDetector = new GestureDetector(context, getOnGestureListener());
    }

    private GestureDetector.SimpleOnGestureListener getOnGestureListener(){
        return new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if(distence>-pagerwidth&&distence<pagerwidth&&list.size()>1) {  distence+=-distanceX;}
                try {
                    invate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return true;
            }
        };
    }

    public void setList(List<View> list) {
        try{setAutoChange(false);}catch (Exception e){}
        try{removeAllViews();}catch (Exception e){}
        this.list = list;
        try {
            invate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed,l,t,r,b);
       if(changed) {pagerwidth=r-l;}
        if(changed) {
            try {
                 invate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }



    public void setAdapter(BaseAdapter adapter){
        this.adapter=adapter;
    }



    public void invate() throws Exception{
        //Log.i("ds","distence:"+distence);
        if(distence==pagerwidth&&pagerwidth!=0){
            item=getLastItem(item);
            distence=0;
        }else if(distence==-pagerwidth&&pagerwidth!=0){
            item=getFirstItem(item);
            distence=0;
        }
        list.get(item).setX(distence);
             if(distence<0){
                 addFirstView().setX(pagerwidth+distence);
             }else{
                 addLastView().setX(distence-pagerwidth);
             }
        recycleView();
        if(distence==0){
            list.get(item).setX(0);
        }
    }
    /*回收view*/
    private void recycleView() {
         for(int i=0;i<list.size();i++){
             if(distence==0){
                 if(i!=item){try{this.removeView(list.get(i));}catch (Exception e){}}
             }else{
                 //if(distence<0)if(i!=getFirstItem(item))try{this.removeView(list.get(item)); }catch (Exception e){}
                 //if(distence>0)if((i!=getLastItem(item)))try {this.removeView(list.get(item));}catch (Exception e){}
                 if(i!=getFirstItem(item)&&i!=getLastItem(item)){
                     try{this.removeView(list.get(i));}catch (Exception e){}
                 }
             }
         }
        if(distence!=0){
            if(distence<0)try{this.addView(list.get(getFirstItem(item)),layoutParams);}catch (Exception e){}
            if(distence>0)try{this.addView(list.get(getLastItem(item)),layoutParams);}catch (Exception e){}
        }
        try{this.addView(list.get(item),layoutParams);}catch (Exception e){}
    }
    private View addFirstView(){
       return list.get(getFirstItem(item));
    }
    private View addLastView(){
        return list.get(getLastItem(item));
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            last_distence=distence;
        }
        mGestureDetector.onTouchEvent(ev);
        if(ev.getAction()==MotionEvent.ACTION_UP||ev.getAction()==MotionEvent.ACTION_CANCEL){
            if(last_distence<distence){
                AnimationTo(distence,pagerwidth);
            }else if(last_distence>distence){
                AnimationTo(distence,-pagerwidth);
            }else{
                AnimationTo(distence,0);
            }
            if(autoChange){
                try{setAuto(true);}catch (Exception e){}
            }
            Log.i("ds","motion: up");
        }else{
                try{setAuto(false);}catch (Exception e){}
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }



    private void AnimationTo(float from,float to){
        if(from==to){return;}
        valueAnimator=ValueAnimator.ofFloat(from, to);
        valueAnimator.setDuration(500);
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if(onItemSelectedListener!=null){onItemSelectedListener.selected(item, list.get(item));}
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                distence = (Float) animation.getAnimatedValue();
                try {
                    invate();
                } catch (Exception e) {
                }
            }
        });
        valueAnimator.start();
    }

    private int getFirstItem(int _item){
        int it=_item;
        it++;
        if(it>list.size()-1){it=0;}
        return it;
    }
    private int getLastItem(int _item){
        int it=_item;
        it--;
        if(it<0){it=list.size()-1;}
        return it;
    }

    public void  setCurrentItem(int item){
        this.item=item;
        try{invate();}catch (Exception e){}
    }

    public int getItem() {
        return item;
    }

    public void setAutoChange(boolean change) {
        autoChange=change;
        try{ setAuto(autoChange);}catch (Exception e){}
    }

    private void setAuto(boolean auto) throws Exception{
        if(auto) {
            if(timer==null) {
                if(list.size()<=1){
                    throw new Exception("this size must more than 1");
                }
                timer = new Timer();
                task = new TimerTask() {
                    @Override
                    public void run() {
                        //Log.i("ds", "first:" + item + ":child:" + getChildCount());
                        try {
                            hand.post(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                            AnimationTo(distence, -pagerwidth);
                                    } catch (Exception e) {
                                    }
                                }
                            });
                        }catch (Exception e){
                            try{task.cancel();timer=null;}catch (Exception e2){}
                        }
                    }
                };
                timer.schedule(task, autoChangeTime, autoChangeTime);
            }
        }else{
            try{
                if(timer!=null){
                timer.cancel();
                timer=null;}
            }catch (Exception e){}
        }
    }

    public void setAutoChangeTime(long autoChangeTime) {
        this.autoChangeTime = autoChangeTime;
    }

    public long getAutoChangeTime() {
        return autoChangeTime;
    }

    public boolean isAutoChange() {
        return autoChange;
    }

    public void setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
    }
}
