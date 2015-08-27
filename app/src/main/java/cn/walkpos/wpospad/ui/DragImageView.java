package cn.walkpos.wpospad.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xingy.util.DPIUtil;

public class DragImageView extends ImageView{
	private boolean isDrag = false;

    private int mDownX;
    private int mDownY;
    private int moveX;
    private int moveY;
    
    private int screenWidth = DPIUtil.getWidth();;
    private int screenHeight = DPIUtil.getHeight();
    
    private Drawable mDragBitmap;
    
    private RelativeLayout layout;
    private RelativeLayout.LayoutParams params;
    
    private ImageView mDragImageView;
    private Handler mHandler = new Handler();
    private int imageWidth = DPIUtil.getWidth()*90/720;
	public DragImageView(Context context) {
        super(context);
		// TODO Auto-generated constructor stub
	}

    public DragImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mDownX = (int) event.getX();
			mDownY = (int) event.getY();
			mDragBitmap = getDrawable();

			break;
		case MotionEvent.ACTION_MOVE:
			
			break;
		case MotionEvent.ACTION_UP:
			
			break;
		}
		// TODO Auto-generated method stub
		return super.dispatchTouchEvent(event);
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				
				break;
			case MotionEvent.ACTION_MOVE:
                if (!isDrag){
                    isDrag = true;
                    setVisibility(View.INVISIBLE);
                    createDragImage();
                }
				moveX = (int) (getLeft() + event.getX() - mDownX);
				moveY = (int) (getTop() + event.getY() - mDownY);
//                if (isDrag){
                    onDragItem();
//                }

				break;
			case MotionEvent.ACTION_UP:
                if (mDragImageView != null){
                    onStopDrag();
                }else {
                    setVisibility(View.VISIBLE);
                }

                isDrag = false;
				break;
			}
			return true;
		// TODO Auto-generated method stub
	}


	public void createDragImage(){
		layout = (RelativeLayout) getParent();
		params = new RelativeLayout.LayoutParams(imageWidth,imageWidth);
		params.leftMargin = (int) getLeft();
		params.topMargin = (int) getTop();
		mDragImageView = new ImageView(getContext());
        mDragImageView.setScaleType(ScaleType.FIT_XY);
		mDragImageView.setImageDrawable(mDragBitmap);
		layout.addView(mDragImageView,params);
	}
	public void onDragItem(){
		
//		mDragImageView.setX(moveX);
//		mDragImageView.setY(moveY);
		if(moveX>layout.getWidth() - getWidth()){
			moveX = layout.getWidth() - getWidth();
		}
		else if(moveX < 0){
			moveX = 0;
		}
		if(moveY > layout.getHeight() - getHeight()){
			moveY = layout.getHeight() - getHeight();
		}
		else if(moveY<0){
			moveY = 0;
		}
		params.leftMargin = moveX;
    	params.topMargin = moveY;
        if (null != mDragImageView){
            mDragImageView.setLayoutParams(params);
        }
	}
    private void removeDragImage(){
		if(mDragImageView != null){
			layout.removeView(mDragImageView);
			mDragImageView = null;
		}
	}
	public void onStopDrag(){
//        if (!isDrag&&moveX == 0 && moveY == 0){
//            moveX = getLeft();
//            moveY = getTop();
//        }
        if (isDrag){
            if (moveX+mDragImageView.getWidth()/2 >= layout.getWidth()/2){
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageWidth,imageWidth);
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                params.topMargin = moveY;
                setLayoutParams(params);
                TranslateAnimation animation = new TranslateAnimation(0,screenWidth-mDragImageView.getRight(),0,0);
                animation.setDuration(100);
                mDragImageView.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        removeDragImage();
                        setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

            }else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(imageWidth,imageWidth);
                params.leftMargin = 0;
                params.topMargin = moveY;
                setLayoutParams(params);
                TranslateAnimation animation = new TranslateAnimation(0,-mDragImageView.getLeft(),0,0);
                animation.setDuration(100);
                mDragImageView.startAnimation(animation);
                animation.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        removeDragImage();
                        setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });;
            }
        }

	}
}
