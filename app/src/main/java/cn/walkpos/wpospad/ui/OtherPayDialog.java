package cn.walkpos.wpospad.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ToolUtil;

import java.math.BigDecimal;
import java.util.ArrayList;

import cn.walkpos.wpospad.R;

/**
 * Created by xingyao on 2015/8/22.
 */
public class OtherPayDialog extends Dialog implements View.OnClickListener {

    private String  strBill;
    private String  inputStr = "";
    public OtherPayDialog(Context context, String abill) {
        super(context, com.xingy.R.style.Dialog);
        strBill = abill;
        if(TextUtils.isEmpty(strBill))
            strBill = "0.00";
    }
    public OtherPayDialog(Context context, int theme) {
        super(context, theme);
    }
    protected OtherPayDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    protected int           mWinWidth;

    protected TextView  incomeEditv;
    protected TextView         cashChangev;
    protected TextView         billTotalv;

    public interface WithEditNumClickListener
    {
        /**
         * onDialogClick
         * @param nButtonId
         */
        public abstract void onDialogClick(int nButtonId, ArrayList<String> array);
    }



    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);

        // Load the default configuration.
        setContentView(R.layout.dialog_payother);

//        this.findViewById(R.id.btn_10).setOnClickListener(this);
//        this.findViewById(R.id.btn_20).setOnClickListener(this);
//        this.findViewById(R.id.btn_50).setOnClickListener(this);
//        this.findViewById(R.id.btn_100).setOnClickListener(this);
        this.findViewById(R.id.other_pay_ok).setOnClickListener(this);

        billTotalv = (TextView) this.findViewById(R.id.bill_total_tv);
        if(null!=billTotalv)
            billTotalv.setText(this.getContext().getString(R.string.income_x,strBill));
        mWinWidth = this.setAttributes();

    }

    public void setBill(String abill)
    {
        strBill = abill;
        if(TextUtils.isEmpty(strBill))
            strBill = "0.00";
        if(null!=billTotalv)
            billTotalv.setText(this.getContext().getString(R.string.income_x,strBill));
    }



    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event)
    {
//        if((null != mListener) && (keyCode == KeyEvent.KEYCODE_BACK))
//        {
//            mListener.onDialogClick(DialogInterface.BUTTON_NEGATIVE,inputArray);
//            return true;
//        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        int declen = inputStr.indexOf(".");

        switch (v.getId())
        {
//            case R.id.btn_10:
//                inputStr = "10";
//                billTotalv.setText(this.getContext().getString(R.string.income_x,inputStr));
//                break;
//            case R.id.btn_20:
//                inputStr = "20";
//                billTotalv.setText(this.getContext().getString(R.string.income_x,inputStr));
//                break;
//            case R.id.btn_50:
//                inputStr = "50";
//                billTotalv.setText(this.getContext().getString(R.string.income_x,inputStr));
//                break;
//            case R.id.btn_100:
//                inputStr = "100";
//                billTotalv.setText(this.getContext().getString(R.string.income_x,inputStr));
//                break;
            case R.id.other_pay_ok:
                dismiss();
                break;
            default:
                dismiss();
                break;

        }

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
            pParams.width = (int) (pMetrics.widthPixels * 0.75);
        pWindow.setAttributes(pParams);

        // Clean up.
        pMetrics = null;
        pWindow = null;

        return pParams.width;
    }
}
