package cn.walkpos.wpospad.ui;

import android.view.MotionEvent;
import android.view.View;

/**
 * Created by xingyao on 2015/8/22.
 */
public class DragFloatView {

    private static DragFloatView floatViewInstance;

    private DragImageView view;

    private int mDownX;
    private int mDownY;
    private int mUpX;
    private int mUpY;


    public synchronized static DragFloatView getFloatViewInstance()
    {
        if(null == floatViewInstance)
        {
            floatViewInstance = new DragFloatView();
        }
        return floatViewInstance;
    }

    public void init(DragImageView view)
    {
        this.view = view;
        initData();
    }
    public void initData()
    {
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        mDownX = (int) motionEvent.getX();
                        mDownY = (int) motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_UP:

                        mUpX = (int) motionEvent.getX();
                        mUpY = (int) motionEvent.getY();
                        if((mUpX>=mDownX-10&&mUpX<=mDownX+10)&&(mUpY>=mDownY-10&&mUpY<=mDownY+10)){

                        }
                        break;
                }
                return false;
            }
        });

    }
}
