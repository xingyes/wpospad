package cn.walkpos.wpospad.ui;

import android.app.Dialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

/**
 * Created by xingyao on 2015/8/22.
 */
public class FlingDown2GoneLayout extends RelativeLayout {

    private static final int FLING_DOWN_DIS = 20;
    public FlingDown2GoneLayout(Context context) {
        super(context);
    }
    public FlingDown2GoneLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public FlingDown2GoneLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private float lastY,lastX;
    private float yDistance,xDistance;
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                lastX = ev.getX();
                lastY = ev.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();
                xDistance += Math.abs(curX - lastX);
                yDistance += Math.abs(curY - lastY);
                android.util.Log.e("FLING GONE", "delt Y:" + (curY - lastY));
                if(curY - lastY > FLING_DOWN_DIS)
                    setVisibility(GONE);

                lastX = curX;
                lastY = curY;
                //�????移�?��??�?>�???��??离�??�?�????�????
                if(xDistance > yDistance)
                    return false;
                break;
        }

        boolean returnVal = false;
        try {
            returnVal = super.onInterceptTouchEvent(ev);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnVal;
    }
}
