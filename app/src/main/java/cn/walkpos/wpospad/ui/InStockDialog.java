package cn.walkpos.wpospad.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xingy.util.ToolUtil;

import cn.walkpos.wpospad.R;

/**
 * Created by xingyao on 2015/8/22.
 */
public class InStockDialog extends Dialog implements View.OnClickListener {

    public InStockDialog(Context context,WithEditNumClickListener listener) {
        super(context, com.xingy.R.style.Dialog);
        mListener = listener;
    }
    public InStockDialog(Context context, int theme) {
        super(context, theme);
    }
    protected InStockDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    protected int           mWinWidth;
    protected TextView      mCaption;
    protected TextView      mMessage;
    protected TextView      mPositive;
    protected TextView      mNegative;

    private String          strCaption;
    private String          strMessage;
    private String          strPositive;
    private String          strNegative;


    private EditText    mNumEt;

    public interface WithEditNumClickListener
    {
        /**
         * onDialogClick
         * @param nButtonId
         */
        public abstract void onDialogClick(int nButtonId, long num);
    }

    private WithEditNumClickListener   mListener;

    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);

        // Load the default configuration.
        setContentView(R.layout.dialog_instock);
        mCaption = (TextView)this.findViewById(R.id.dialog_caption);
        mCaption.setText(strCaption);
        mMessage = (TextView)this.findViewById(R.id.dialog_message);
        mMessage.setText(strMessage);
        mNumEt = (EditText)this.findViewById(R.id.instock_num);
        mPositive = (TextView)this.findViewById(R.id.dialog_btn_positive);
        mNegative = (TextView)this.findViewById(R.id.dialog_btn_negative);

        if(!TextUtils.isEmpty(strPositive))
            mPositive.setText(strPositive);
        if(!TextUtils.isEmpty(strNegative))
            mNegative.setText(strNegative);

        mPositive.setOnClickListener(this);
        mNegative.setOnClickListener(this);

        mWinWidth = this.setAttributes();


    }


    public void setProperty(final String caption, final String info,final String position, final String negative)
    {
        strCaption = caption;
        strMessage = info;
        strPositive = position;
        strNegative = negative;

        if(null!=mCaption)
            mCaption.setText(strCaption);
        if(null!=mMessage)
            mMessage.setText(strMessage);
        if(!TextUtils.isEmpty(strPositive) && null!=mPositive)
            mPositive.setText(strPositive);
        if(!TextUtils.isEmpty(strNegative) && null!=mNegative)
            mNegative.setText(strNegative);

        mNumEt.setText("");

    }



    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event)
    {
        if((null != mListener) && (keyCode == KeyEvent.KEYCODE_BACK))
        {
            mListener.onDialogClick(DialogInterface.BUTTON_NEGATIVE,-1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        if ( null != mListener )
        {
            final String strinput = mNumEt.getText().toString();
            long num = Long.valueOf(strinput);
            if(v== mPositive)
                mListener.onDialogClick(DialogInterface.BUTTON_POSITIVE,num);
            else
                mListener.onDialogClick(DialogInterface.BUTTON_POSITIVE,num);
        }

        // Dismiss the dialog.
        if(this.isShowing())
            dismiss();
    }



    protected int setAttributes()
    {
        Window pWindow = getWindow();
        if ( null == pWindow )
            return 0;

        DisplayMetrics pMetrics = new DisplayMetrics();
        pWindow.getWindowManager().getDefaultDisplay().getMetrics(pMetrics);

        WindowManager.LayoutParams pParams = pWindow.getAttributes();
        pParams.gravity = Gravity.CENTER_HORIZONTAL;
        Context context = getContext();
        if(ToolUtil.isScreenOriatationPortrait(context))
            pParams.width = (int) (pMetrics.widthPixels * 0.9);
        else
            pParams.width = (int) (pMetrics.widthPixels * 0.5);
        pWindow.setAttributes(pParams);

        // Clean up.
        pMetrics = null;
        pWindow = null;

        return pParams.width;
    }
}
