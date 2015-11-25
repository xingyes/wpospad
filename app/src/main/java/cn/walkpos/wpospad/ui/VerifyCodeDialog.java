package cn.walkpos.wpospad.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.util.WPosConfig;

/**
 * Created by xingyao on 2015/8/22.
 */
public class VerifyCodeDialog extends Dialog implements View.OnClickListener {

    public VerifyCodeDialog(Context context,VerifyResultListener listener) {
        super(context, com.xingy.R.style.Dialog);
        mListener = listener;
    }

    public VerifyCodeDialog(Context context, int theme) {
        super(context, theme);
    }
    protected VerifyCodeDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public interface VerifyResultListener
    {
        public boolean onVerifyDialogDismiss(final String smsCode);
    }
    private VerifyResultListener mListener;
    protected Ajax          mAjax;
    protected int           mWinWidth;
    protected TextView      mCaption;
    protected TextView      mMessage;
    protected TextView      mPositive;
    protected TextView      mNegative;

    private String          strCaption;
    private String          strMessage;
    private String          strPositive;
    private String          strNegative;
    private String          mPhonestr;

    private EditText    mInputEt;
    private TextView    mRequestVerifyBtn;

    private boolean bSending = false;
    private static final  int COUTING_DOWN_SECOND = 120;
    private int   mCounting;
    private static final int MSG_VERIFY_INTERVAL = 1203;
    private Handler     mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == MSG_VERIFY_INTERVAL)
            {
                mCounting--;
                if(mCounting > 0)
                {
                    mRequestVerifyBtn.setText(getContext().getString(R.string.already_sent_left_second, mCounting));
                    mHandler.sendEmptyMessageDelayed(MSG_VERIFY_INTERVAL,1000);
                }
                else
                {
                    mRequestVerifyBtn.setText(getContext().getString(R.string.resend));
                    mCounting = COUTING_DOWN_SECOND;
                    mRequestVerifyBtn.setEnabled(true);
                    bSending = false;
                }

            }
            else
                super.handleMessage(msg);
        }

    };


    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);

        // Load the default configuration.
        setContentView(R.layout.dialog_verifycode);
        mCaption = (TextView)this.findViewById(R.id.dialog_caption);
        mCaption.setText(strCaption);
        mMessage = (TextView)this.findViewById(R.id.dialog_message);
        mMessage.setText(strMessage);
        mPositive = (TextView)this.findViewById(R.id.dialog_btn_positive);
        mNegative = (TextView)this.findViewById(R.id.dialog_btn_negative);


        mMessage.setVisibility(TextUtils.isEmpty(strMessage) ? View.GONE : View.VISIBLE);
        mInputEt = (EditText)this.findViewById(R.id.input_edit);

        mRequestVerifyBtn = (TextView)this.findViewById(R.id.send_sms_btn);
        if(!TextUtils.isEmpty(strPositive))
            mPositive.setText(strPositive);
        if(!TextUtils.isEmpty(strNegative))
            mNegative.setText(strNegative);

        mPositive.setOnClickListener(this);
        mNegative.setOnClickListener(this);
        mRequestVerifyBtn.setOnClickListener(this);
        mWinWidth = this.setAttributes();



    }

    public void setProperty(final String caption, final String info,final String phone,
                            final String position, final String negative)
    {
        setProperty(caption,info,phone,position,negative,COUTING_DOWN_SECOND);
    }

    public void rewinder()
    {
        mHandler.removeCallbacksAndMessages(null);
        if(null!=mRequestVerifyBtn) {
            mRequestVerifyBtn.setText("发送");
            mRequestVerifyBtn.setEnabled(true);
        }
    }
    public void setProperty(final String caption, final String info,final String phone,
                            final String position, final String negative, final int countdown)

    {
        strCaption = caption;
        strMessage = info;
        mPhonestr = phone;
        strPositive = position;
        strNegative = negative;
        mCounting = countdown;
        if(null!=mCaption)
            mCaption.setText(strCaption);
        if(null!=mMessage) {
            mMessage.setVisibility(TextUtils.isEmpty(strMessage) ? View.GONE : View.VISIBLE);
            mMessage.setText(strMessage);
        }
        if(!TextUtils.isEmpty(strPositive) && null!=mPositive)
            mPositive.setText(strPositive);
        if(!TextUtils.isEmpty(strNegative) && null!=mNegative)
            mNegative.setText(strNegative);

        if(null!=mInputEt)
            mInputEt.setText("");

        rewinder();
    }


    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event)
    {
        if((null != mListener) && (keyCode == KeyEvent.KEYCODE_BACK))
        {
            mListener.onVerifyDialogDismiss("");
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        if(v==mRequestVerifyBtn)
        {
            if (!bSending) {
                if (!resendSms()) {
                    return;
                }

                mRequestVerifyBtn.setEnabled(false);
                mRequestVerifyBtn.setText(getContext().getString(R.string.already_sent_left_second, mCounting));

                bSending = true;
                mHandler.sendEmptyMessageDelayed(MSG_VERIFY_INTERVAL, 1000);
            }
            return;
        }
        else if(v == mPositive)
        {
            verifyCode();
        }
        else if(v == mNegative)
        {
            if(null!=mListener)
                mListener.onVerifyDialogDismiss("");
            if(this.isShowing())
                dismiss();
        }
    }


    private boolean resendSms() {

        if(ToolUtil.isPhoneNum(mPhonestr))
        {
            if(mAjax!=null)
                mAjax.abort();

            mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
            if (null == mAjax)
                return false;


            mAjax.setId(WPosConfig.REQ_SMS);
            mAjax.setData("method", "message.code");
            mAjax.setData("mobile", mPhonestr);

            mAjax.setOnSuccessListener(new OnSuccessListener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject jsonObject, Response response) {

                }
            });

            mAjax.send();
            return true;
        }
        return false;
    }

    private void verifyCode()
    {
        if(null!=mListener)
            mListener.onVerifyDialogDismiss(mInputEt.getText().toString());

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
