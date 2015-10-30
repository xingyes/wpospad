package cn.walkpos.wpospad.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
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

import java.util.ArrayList;

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
    protected TextView      mInputPre;
    protected TextView      mInputPre2;

    private String          strCaption;
    private String          strMessage;
    private String          strPositive;
    private String          strNegative;


    private EditText    mInputEt;
    private String          strInputPre;
    private View        editLayout2;
    private EditText    mInputEt2;
    private String          strInputPre2;

    private ArrayList<String> inputArray;

    public interface WithEditNumClickListener
    {
        /**
         * onDialogClick
         * @param nButtonId
         */
        public abstract void onDialogClick(int nButtonId, ArrayList<String> array);
    }

    private WithEditNumClickListener   mListener;
    private int    mInputType;
    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);

        inputArray = new ArrayList<String>();
        // Load the default configuration.
        setContentView(R.layout.dialog_instock);
        mCaption = (TextView)this.findViewById(R.id.dialog_caption);
        mCaption.setText(strCaption);
        mMessage = (TextView)this.findViewById(R.id.dialog_message);
        mMessage.setText(strMessage);
        mInputEt = (EditText)this.findViewById(R.id.input_edit);
        editLayout2 = this.findViewById(R.id.edit_layout2);
        mInputEt2 = (EditText)this.findViewById(R.id.input_edit2);
        mPositive = (TextView)this.findViewById(R.id.dialog_btn_positive);
        mNegative = (TextView)this.findViewById(R.id.dialog_btn_negative);
        mInputPre = (TextView)this.findViewById(R.id.input_pre);
        mInputPre.setText(strInputPre);
        mInputPre2 = (TextView)this.findViewById(R.id.input_pre2);

        editLayout2.setVisibility( TextUtils.isEmpty(strInputPre2) ? View.GONE: View.VISIBLE);
        mInputPre2.setText(strInputPre2);

        mMessage.setVisibility(TextUtils.isEmpty(strMessage) ? View.GONE : View.VISIBLE);
        mInputEt.setInputType(mInputType);
        if(!TextUtils.isEmpty(strPositive))
            mPositive.setText(strPositive);
        if(!TextUtils.isEmpty(strNegative))
            mNegative.setText(strNegative);

        mPositive.setOnClickListener(this);
        mNegative.setOnClickListener(this);

        mWinWidth = this.setAttributes();


    }


    public void setProperty(final String caption, final String info,final String editpre,final String editpre2,
                            final String position, final String negative,
                            int aInputType)
    {
        strCaption = caption;
        strMessage = info;
        strPositive = position;
        strNegative = negative;
        strInputPre = editpre;
        strInputPre2 = editpre2;
        mInputType = aInputType;

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

        //edit layout1
        if(null!=mInputPre)
            mInputPre.setText(strInputPre);
        if(null!=mInputEt)
        {
            mInputEt.setInputType(mInputType);
            mInputEt.setText("");
        }

        if(null!=editLayout2)
            editLayout2.setVisibility( TextUtils.isEmpty(strInputPre2) ? View.GONE: View.VISIBLE);

        if(null!=mInputPre2)
            mInputPre2.setText(strInputPre2);
        if(null!=mInputEt2)
            mInputEt2.setText("");
    }


    public void setProperty(final String caption, final String info,final String editpre,final String position, final String negative)
    {
        setProperty(caption,info,editpre,"",position,negative,InputType.TYPE_CLASS_TEXT);
    }

    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event)
    {
        if((null != mListener) && (keyCode == KeyEvent.KEYCODE_BACK))
        {
            mListener.onDialogClick(DialogInterface.BUTTON_NEGATIVE,inputArray);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        if ( null != mListener )
        {
            String strinput = mInputEt.getText().toString();
            inputArray.add(strinput);
            if(mInputEt2.getVisibility()==View.VISIBLE)
                inputArray.add(mInputEt2.getText().toString());

            if(v== mPositive)
                mListener.onDialogClick(DialogInterface.BUTTON_POSITIVE,inputArray);
            else
                mListener.onDialogClick(DialogInterface.BUTTON_POSITIVE,inputArray);
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
