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

import com.cry.loopviews.listener.OnInvateListener;
import com.cry.loopviews.listener.OnItemSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by zxj on 15/5/20.
 * 水平旋转轮播控件
 */
public class LoopView extends RelativeLayout {
    final static int LoopR = 200;
    final String TAG = getClass().getSimpleName();
    ValueAnimator restAnimator = null;//回位动画
    ValueAnimator rAnimation = null;//半径动画
    ValueAnimator zAnimation = null;
    ValueAnimator xAnimation = null;
    GestureDetector mGestureDetector = null;//手势类
    Comparator comp = new SortComparator();
    List<View> viewSortArray = new ArrayList<>();
    List<View> views = new ArrayList<>();//子view引用列表
    private int selectItem = 0;//当前选择项
    private int size = 0;//个数
    private float r = LoopR;//半径
    private float distance = 3 * r;//camera和观察的旋转物体距离， 距离越长,最大物体和最小物体比例越不明显
    private float angle = 0;//角度
    private float last_angle = 0;
    private boolean autoRotation = false;//自动旋转
    private boolean touching = false;//正在触摸
    private int loopRotationX = 0, loopRotationZ = 0;//x轴旋转和轴旋转，y轴无效果
    private OnInvateListener onInvateListener = null;//刷新侦听
    private OnItemSelectedListener onItemSelectedListener = null;//选择事件接口
    LoopHandler loopHandler = new LoopHandler(3000) {//自动切换角度用handler
        @Override
        public void du() {
            try {
                AnimRotationTo(angle - 360 / size, null);
            } catch (Exception e) {
            }
        }
    };

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

    /*初始化view attached 后，算子view 数目*/
    private void init(final Context context) {
        mGestureDetector = new GestureDetector(context, getGeomeryController());
        setOnHierarchyChangeListener(new OnHierarchyChangeListener() {
            @Override
            public void onChildViewAdded(View parent, View child) {
                views.add(child);
                size = getChildCount();
            }

            @Override
            public void onChildViewRemoved(View parent, View child) {
                views.remove(child);
                size = getChildCount();
            }
        });
    }

    /*排序以整理重叠顺序*/
    private void sortList(List<View> list) {
        Collections.sort(list, comp);
        for (int i = 0; i < list.size(); i++) {
            list.get(i).bringToFront();
            list.get(i).setEnabled(i == (list.size() - 1) && angle % (360 / size) == 0 ? true : false);
        }
    }

    /*触摸算角度，然后刷新*/
    private GestureDetector.SimpleOnGestureListener getGeomeryController() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                angle += Math.cos(Math.toRadians(loopRotationZ)) * (distanceX / 4)
                        + Math.sin(Math.toRadians(loopRotationZ)) * (distanceY / 4);
                invate();
                return true;
            }
        };
    }

    /*刷新数据变化到界面*/
    public void invate() {
        for (int i = 0; i < views.size(); i++) {
            /*计算角度平均分后各个顶点位置*/
            double radians = angle + 180 - i * 360 / size;
            float x0 = (float) Math.sin(Math.toRadians(radians)) * r;
            float y0 = (float) Math.cos(Math.toRadians(radians)) * r;
            float scale0 = (distance - y0) / (distance + r);
            views.get(i).setScaleX(scale0);
            views.get(i).setScaleY(scale0);
            float rotationX_y = (float) Math.sin(Math.toRadians(loopRotationX * Math.cos(Math.toRadians(radians)))) * r;
            float rotationZ_y = -(float) Math.sin(Math.toRadians(-loopRotationZ)) * x0;
            float rotationZ_x = (((float) Math.cos(Math.toRadians(-loopRotationZ)) * x0) - x0);

            views.get(i).setTranslationX(x0 + rotationZ_x);
            views.get(i).setTranslationY(rotationX_y + rotationZ_y);
        }
        redoSortArray();
        sortList(viewSortArray);
        invalidate();
        if (this.onInvateListener != null) {
            loopHandler.post(new Runnable() {
                @Override
                public void run() {
                    onInvateListener.onInvate(LoopView.this);
                }
            });
        }
    }

    /*角度刷新侦听*/
    public void setOnInvateListener(OnInvateListener onInvateListener) {
        this.onInvateListener = onInvateListener;
    }

    /*重新排序*/
    private void redoSortArray() {
        viewSortArray.clear();
        for (int i = 0; i < views.size(); i++) {
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
        if (changed) {
            setSelectItem(0);
            RAnimation();
        }
    }

    /*创建x轴动画*/
    public void createXAnimation(int from, int to, boolean start) {
        if (xAnimation != null) if (xAnimation.isRunning() == true) xAnimation.cancel();
        xAnimation = ValueAnimator.ofInt(from, to);
        xAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                loopRotationX = (Integer) animation.getAnimatedValue();
                invate();
            }
        });
        xAnimation.setInterpolator(new DecelerateInterpolator());
        xAnimation.setDuration(2000);
        if (start) xAnimation.start();
    }

    /*z轴动画*/
    public ValueAnimator createZAnimation(int from, int to, boolean start) {
        if (zAnimation != null) if (zAnimation.isRunning() == true) zAnimation.cancel();
        zAnimation = ValueAnimator.ofInt(from, to);
        zAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                loopRotationZ = (Integer) animation.getAnimatedValue();
                invate();
            }
        });
        zAnimation.setInterpolator(new DecelerateInterpolator());
        zAnimation.setDuration(2000);
        if (start) zAnimation.start();
        return zAnimation;
    }

    /*半径动画*/
    public void RAnimation() {
        RAnimation(1f, r);
    }

    /*半径动画*/
    public void RAnimation(boolean fromZeroToLoopR) {
        if (fromZeroToLoopR) {
            RAnimation(1f, LoopR);
        } else {
            RAnimation(LoopR, 1f);
        }
    }

    /*半径动画*/
    public void RAnimation(float from, float to) {
        if (rAnimation != null) if (rAnimation.isRunning() == true) rAnimation.cancel();
        rAnimation = ValueAnimator.ofFloat(from, to);
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

    /*重置位置*/
    private void restPosition() {
        if (size == 0) {
            return;
        }
        float finall = 0;
        float part = 360 / size;//一份的角度
        if (angle < 0) {
            part = -part;
        }
        float minvalue = (int) (angle / part) * part;//最小角度
        float maxvalue = (int) (angle / part) * part + part;//最大角度
        if (angle >= 0) {//分为是否小于0的情况
            if (angle - last_angle > 0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        } else {
            if (angle - last_angle < 0) {
                finall = maxvalue;
            } else {
                finall = minvalue;
            }
        }
        AnimRotationTo(finall, null);
    }

    /*播放旋转动画*/
    private void AnimRotationTo(float finall, final Runnable complete) {
        if (angle == finall) {
            return;
        }
        restAnimator = ValueAnimator.ofFloat(angle, finall);
        restAnimator.setInterpolator(new DecelerateInterpolator());
        restAnimator.setDuration(300);
        restAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (touching == false) {
                    angle = (Float) animation.getAnimatedValue();
                    invate();
                }
            }
        });
        restAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (touching == false) {
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

        if (complete != null) {
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
            });
        }
        restAnimator.start();
    }

    /*计算item*/
    private int calculateItem() {
        return (int) (angle / (360 / size)) % size;
    }

    private boolean onTouch(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            last_angle = angle;
            touching = true;
        }
        boolean sc = mGestureDetector.onTouchEvent(event);
        if (sc) {
            this.getParent().requestDisallowInterceptTouchEvent(true);//通知父控件勿拦截本控件
        }
        if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            touching = false;
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

    public LoopView setR(float r) {
        this.r = r;
        distance = 3 * r;
        return this;
    }

    public int getSelectItem() {
        return selectItem;
    }

    /*selecItem must > 0*/
    public LoopView setSelectItem(int selectItem) {
        if (size > 0) {
            if (selectItem > 0 && selectItem < size) {
                this.selectItem = selectItem;
                AnimRotationTo(selectItem * (360 / size), null);
            } else {
                Log.e(TAG, "must selectItem >0 or selectItem<size");
            }
        } else {
            Log.e(TAG, "size is zero");
        }
        return this;
    }

    public LoopView setOnItemSelectedListener(OnItemSelectedListener onItemSelectedListener) {
        this.onItemSelectedListener = onItemSelectedListener;
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

    public LoopView setAutoRotation(boolean autoRotation) {
        loopHandler.setLoop(autoRotation);
        return this;
    }

    public LoopView setHorizontal(boolean horizontal, boolean anim) {
        if (anim) {
            if (horizontal) {
                createZAnimation(getLoopRotationZ(), 0, true);
            } else {
                createZAnimation(getLoopRotationZ(), 90, true);
            }
        } else {
            if (horizontal) {
                setLoopRotationZ(0);
            } else {
                setLoopRotationZ(90);
            }
            invate();
        }
        return this;
    }

    public int getLoopRotationX() {
        return loopRotationX;
    }

    public LoopView setLoopRotationX(int loopRotationX) {
        this.loopRotationX = loopRotationX;
        return this;
    }

    public int getLoopRotationZ() {
        return loopRotationZ;
    }

    public LoopView setLoopRotationZ(int loopRotationZ) {
        this.loopRotationZ = loopRotationZ;
        return this;
    }

    public ValueAnimator getRestAnimator() {
        return restAnimator;
    }

    public ValueAnimator getrAnimation() {
        return rAnimation;
    }

    public ValueAnimator getzAnimation() {
        return zAnimation;
    }

    public void setzAnimation(ValueAnimator zAnimation) {
        this.zAnimation = zAnimation;
    }

    public ValueAnimator getxAnimation() {
        return xAnimation;
    }

    public void setxAnimation(ValueAnimator xAnimation) {
        this.xAnimation = xAnimation;
    }

    private class SortComparator implements Comparator<View> {
        @Override
        public int compare(View lhs, View rhs) {
            int result = 0;
            try {
                result = (int) (1000 * lhs.getScaleX() - 1000 * rhs.getScaleX());
            } catch (Exception e) {
            }
            return result;
        }
    }
}
