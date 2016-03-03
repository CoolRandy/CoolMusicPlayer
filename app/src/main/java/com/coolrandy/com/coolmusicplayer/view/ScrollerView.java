package com.coolrandy.com.coolmusicplayer.view;

import android.content.Context;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

/**
 * Created by admin on 2016/3/3.
 * 采用Scroller实现ViewGroup的功能
 * 有一点需要注意scroll中的坐标系是和原始坐标系相反的
 */
public class ScrollerView extends ViewGroup {

    //Scroller object
    private Scroller scroller;
    private int mTouchSlop;

    //set touch event pos
    private float touchDownX;
    private float touchLastMoveX;
    private float touchMoveX;

    private int leftBorder;
    private int rightBorder;

    public ScrollerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //create object
        scroller = new Scroller(context);
        final ViewConfiguration configuration = ViewConfiguration.get(context);
        //getScaledTouchSlop是一个距离，表示滑动的时候，手的移动要大于这个距离才开始移动控件。如果小于这个距离就不触发移动控件
        mTouchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //循环遍历子布局 测量每一个子控件大小
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++){
            final View childView = getChildAt(i);
            measureChild(childView, widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        if(changed){
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++){
                View childView = getChildAt(i);
                //对每个子布局进行布局，因为要布局为viewpager左右滑动的样式，所以考虑水平布局
                childView.layout(i*childView.getMeasuredWidth(), 0, (i+1)*childView.getMeasuredWidth(), childView.getMeasuredHeight());
            }
            //初始化ScrollerView中子view相对父布局的x坐标，这里要注意是子控件相对父view的坐标系中的坐标值，要区别于getPaddingRight
            leftBorder = getChildAt(0).getLeft();// 0
            rightBorder = getChildAt(childCount-1).getRight();  // 2304

            Log.e("TAG", "leftBorder: " + leftBorder + ", rightBorder: " + rightBorder);
        }
    }

    //start to handle touch event
    //只考虑水平滑动
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:
                touchDownX = ev.getRawX();
                touchLastMoveX = touchDownX;
                break;
            case MotionEvent.ACTION_MOVE:
                touchMoveX = ev.getRawX();
                float offset = Math.abs(touchDownX - touchMoveX);
                if(offset > mTouchSlop){
                    return true;//消费该事件
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                touchMoveX = event.getRawX();
                int scrolledX = (int) (touchLastMoveX - touchMoveX); //该值是手指移动相对上次的距离，这个数值随着手指移动持续更新
                //两种边界情况
                //getScrollX  表示当前子view的左上角相对父布局的左上角的X轴偏移量
                if (getScrollX() + scrolledX < leftBorder) {
                    Log.e("TAG", "左边界情况: " + "getScrollX:" + getScrollX() + ", scrolledX" + scrolledX);
                    scrollTo(leftBorder, 0);
                    return true;
                } else if (getScrollX() + getWidth() + scrolledX > rightBorder) {//右边界 getScrollX = 1536  getScrollX + getWidth == 2034
                    Log.e("TAG", "右边界情况: " + "getScrollX:" + getScrollX() + ", scrolledX" + scrolledX);
                    scrollTo(rightBorder - getWidth(), 0);
                    return true;
                }
                Log.e("TAG", "getWidth: " + getWidth() + ", getScrollX: " + getScrollX()); // getWidth 768  rightBorder 2304  leftBorder 0
                Log.e("TAG", "leftBorder== " + leftBorder + ", rightBorder== " + rightBorder); // rightBorder = 3 * getWidth
                scrollBy(scrolledX, 0);
                touchLastMoveX = touchMoveX;
                break;
            case MotionEvent.ACTION_UP:
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                int targetIndex = (getScrollX() + getWidth() / 2) / getWidth();
                int dx = targetIndex * getWidth() - getScrollX();
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面
                scroller.startScroll(getScrollX(), 0, dx, 0);
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            invalidate();
        }
    }
}
