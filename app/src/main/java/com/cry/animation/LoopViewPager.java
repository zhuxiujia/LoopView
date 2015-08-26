package com.cry.animation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
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

/**
 * Created by zxj on 15/6/6.
 */
public class LoopViewPager extends FrameLayout{
    private Context con=null;
    int pagerWidth =0;
    int pagerHeight=0;

    private BaseAdapter adapter=null;//Adapter
    private List<View> list=new ArrayList<View>();//view数组
    private GestureDetector mGestureDetector;//手势
    private int item=0;//滑动项目
    private int distence=0;//距离
    private ValueAnimator valueAnimator=null;//切换动画
    private boolean autoChange=false;
    private long autoChangeTime=4000;//旋转时间
    private ViewGroup.LayoutParams layoutParams=new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
    private OnItemSelectedListener onItemSelectedListener=null;

    private boolean horizontal=true;//滑动方向
    boolean touching=false;//is touching
    int last_touch_point=0;//上次点击
    int  position=0;//now view x position

    LoopHandler loopHandler=new LoopHandler(2000) {
        @Override
        public void du() {
            try{if(horizontal){AnimationTo(getScrollX(), (getScrollX()/pagerWidth+1) * pagerWidth);}else {
               AnimationTo(getScrollY(), (getScrollY()/pagerHeight+1) * pagerHeight);
            }}catch (Exception e){}
        }
    };

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
                if(horizontal) {
                    if(pagerWidth!=0){
                    distence = getScrollX() + (int) distanceX;
                    if (Math.abs(distence / pagerWidth + 1) == list.size() + 1) {
                        distence = 0;
                    }}
                }else {
                    if(pagerHeight!=0){
                    distence = getScrollY() + (int) distanceY;
                    if (Math.abs(distence / pagerHeight + 1) == list.size() + 1) {
                        distence = 0;
                    }}
                }

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
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            pagerWidth = r - l;
            pagerHeight =b-t;
        }
    }


    public void setHorizontal(boolean horizontal) {
        this.item=0;
        this.horizontal = horizontal;
        this.requestLayout();
        try {
            distence=0;
            invate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAdapter(BaseAdapter adapter){
        this.adapter=adapter;
    }



    public void invate() throws Exception{
        //Log.i("ds", "distence:" + distence);
        recycleAndAddView();
        if(horizontal){LoopViewPager.this.scrollTo(distence, 0);}else{
            LoopViewPager.this.scrollTo(0,distence);
        }
    }
    /*回收view*/
    private void recycleAndAddView() {
        int next_position=0;
        int nextItem =0;//下一个
        int thisItem =0;

        int pagerLength=0;
        if(horizontal){pagerLength=pagerWidth;}else{pagerLength=pagerHeight;}
        if(distence>0){
             next_position=distence/ pagerLength +1;
             nextItem =Math.abs(next_position%list.size());

            thisItem = nextItem -1;
            if(thisItem <0){
                thisItem =list.size()-1;}
            if(thisItem >(list.size()-1)){
                thisItem =0;}
            try{this.addView(list.get(thisItem),layoutParams);}catch (Exception e){}
            list.get(thisItem).bringToFront();
            if(horizontal){list.get(thisItem).setX((next_position - 1) * pagerLength);
                          list.get(thisItem).setY(0);
            }else {
                list.get(thisItem).setY((next_position - 1) * pagerLength);
                list.get(thisItem).setX(0);
            }


            try{this.addView(list.get(nextItem),layoutParams);}catch (Exception e){}
            list.get(nextItem).bringToFront();
            if(horizontal){list.get(nextItem).setX(next_position * pagerLength);
            list.get(nextItem).setY(0);
            }else {
                list.get(nextItem).setY(next_position * pagerLength);
                list.get(nextItem).setX(0);
            }
        }else if(distence<0){
             next_position=distence/ pagerLength -1;
             nextItem =Math.abs(next_position%list.size());
            thisItem = nextItem -1;
            if(thisItem <0){
                thisItem =list.size()-1;}
            if(thisItem >(list.size()-1)){
                thisItem =0;}
            try{this.addView(list.get(thisItem),layoutParams);}catch (Exception e){}
            list.get(thisItem).bringToFront();
            if(horizontal){list.get(thisItem).setX((next_position + 1) * pagerLength);
            list.get(thisItem).setY(0);
            }else {
                list.get(thisItem).setY((next_position + 1) * pagerLength);
                list.get(thisItem).setX(0);
            }

            try{this.addView(list.get(nextItem),layoutParams);}catch (Exception e){}
            list.get(nextItem).bringToFront();
            if(horizontal){list.get(nextItem).setX(next_position * pagerLength);
            list.get(nextItem).setY(0);
            }else {
                list.get(nextItem).setY(next_position * pagerLength);
                list.get(nextItem).setX(0);
            }
        }else{
            next_position=0;
            nextItem =0;
            thisItem =0;
            try{this.addView(list.get(0),layoutParams);}catch (Exception e){}
            list.get(nextItem).bringToFront();
            list.get(0).setX(0);
            list.get(0).setY(0);
        }

        if(distence>=0){
            position=distence/ pagerLength;
        }else{
            position=distence/ pagerLength -1;
        }
        item=thisItem;
        /*回收*/
        for (int i=0;i<list.size();i++){
            if(i!= nextItem &&i!= thisItem){try{this.removeView(list.get(i));}catch (Exception e){}}
        }

    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        try {
            if (list.size() <= 1) {
                return super.dispatchTouchEvent(ev);
            }
        } catch (Exception e) {
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            touching = true;
            if (horizontal) {
                last_touch_point = (int) ev.getX();
            } else {
                last_touch_point = (int) ev.getY();
            }

            try{setAuto(false);}catch (Exception e){}
        }
        mGestureDetector.onTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL) {
            touching = false;
            if (horizontal) {
                if (ev.getX() - last_touch_point < 0) {//left
                    AnimationTo(getScrollX(), (position + 1) * pagerWidth);
                } else {//right
                    AnimationTo(getScrollX(), (position) * pagerWidth);
                }
            } else {
                if (ev.getY() - last_touch_point < 0) {//
                    AnimationTo(getScrollY(), (position + 1) * pagerHeight);
                } else {//right
                    AnimationTo(getScrollY(), (position) * pagerHeight);
                }
            }
        }
        if (Math.abs(ev.getX() - last_touch_point) > 20&&horizontal==true) {
            return true;
        } else if(Math.abs(ev.getY() - last_touch_point) > 20&&horizontal==false){
           return true;
        }else {
            return super.dispatchTouchEvent(ev);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }


    private void AnimationTo(int from,int to){
        if(from==to){return;}
        try {
            valueAnimator = ValueAnimator.ofInt(from, to);
            valueAnimator.setDuration(500);
            valueAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (onItemSelectedListener != null) {
                        onItemSelectedListener.selected(item, list.get(item));
                    }
                    if(autoChange){try{setAuto(autoChange);}catch (Exception e){}}
                    Log.i("ds","item:"+item);
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
                    try {
                        if(touching==false){
                        distence = (Integer) animation.getAnimatedValue();
                        invate();}
                    } catch (Exception e) {
                    }
                }
            });
            valueAnimator.start();
        }catch (Exception e){
        }
    }



    public void  setCurrentItem(int item){
        this.item=item;
        try{invate();}catch (Exception e){}
    }

    public int getItem() {
        return item;
    }

    public void setAutoChange(boolean change) {
        if(list.size()<=1){
            try{setAuto(false);}catch (Exception e){}
            }else {
            autoChange = change;
            try {
                setAuto(change);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setAuto(boolean auto) throws Exception{
         loopHandler.setLoop(auto);
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
