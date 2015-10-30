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
public class CashPayDialog extends Dialog implements View.OnClickListener {

    private String  strBill;
    private String  inputStr = "";
    private double   mIncome;
    private boolean inDecimals = false;
    public CashPayDialog(Context context, String abill) {
        super(context, com.xingy.R.style.Dialog);
        strBill = abill;
        if(TextUtils.isEmpty(strBill))
            strBill = "0.00";
    }
    public CashPayDialog(Context context, int theme) {
        super(context, theme);
    }
    protected CashPayDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
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
        setContentView(R.layout.dialog_paycash);
        this.findViewById(R.id.btn_0).setOnClickListener(this);
        this.findViewById(R.id.btn_1).setOnClickListener(this);
        this.findViewById(R.id.btn_2).setOnClickListener(this);
        this.findViewById(R.id.btn_3).setOnClickListener(this);
        this.findViewById(R.id.btn_4).setOnClickListener(this);
        this.findViewById(R.id.btn_5).setOnClickListener(this);
        this.findViewById(R.id.btn_6).setOnClickListener(this);
        this.findViewById(R.id.btn_7).setOnClickListener(this);
        this.findViewById(R.id.btn_8).setOnClickListener(this);
        this.findViewById(R.id.btn_9).setOnClickListener(this);
        this.findViewById(R.id.btn_10).setOnClickListener(this);
        this.findViewById(R.id.btn_20).setOnClickListener(this);
        this.findViewById(R.id.btn_50).setOnClickListener(this);
        this.findViewById(R.id.btn_100).setOnClickListener(this);
        this.findViewById(R.id.btn_point).setOnClickListener(this);
        this.findViewById(R.id.btn_del).setOnClickListener(this);
        this.findViewById(R.id.cash_pay_ok).setOnClickListener(this);

        incomeEditv = (TextView) this.findViewById(R.id.cash_income_ev);
        cashChangev = (TextView) this.findViewById(R.id.cash_change_tv);
        billTotalv = (TextView) this.findViewById(R.id.bill_total_tv);
        if(null!=billTotalv)
            billTotalv.setText(strBill);
        mWinWidth = this.setAttributes();

    }

    public void setBill(String abill)
    {
        strBill = abill;
        if(TextUtils.isEmpty(strBill))
            strBill = "0.00";
        if(null!=billTotalv)
            billTotalv.setText(strBill);
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
            case R.id.btn_0:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr += "0";
                break;
            case R.id.btn_1:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "1";
                break;
            case R.id.btn_2:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "2";
                break;
            case R.id.btn_3:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "3";
                break;
            case R.id.btn_4:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "4";
                break;
            case R.id.btn_5:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "5";
                break;
            case R.id.btn_6:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "6";
                break;
            case R.id.btn_7:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "7";
                break;
            case R.id.btn_8:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "8";
                break;
            case R.id.btn_9:
                if(inDecimals && declen>0 &&  inputStr.length() - declen>=3)
                    UiUtils.makeToast(this.getContext(),"只精确到0.01");
                else
                    inputStr+= "9";
                break;
            case R.id.btn_10:
                inputStr = "10";
                inDecimals = false;
                break;
            case R.id.btn_20:
                inputStr = "20";
                inDecimals = false;
                break;
            case R.id.btn_50:
                inputStr = "50";
                inDecimals = false;
                break;
            case R.id.btn_100:
                inputStr = "100";
                inDecimals = false;
                break;
            case R.id.btn_point:
                if(!inDecimals)
                    inputStr += ".";
                inDecimals = true;
                break;
            case R.id.btn_del:
                if(inputStr.length()>0) {
                    if(inDecimals && declen>=inputStr.length()-1)
                        inDecimals = false;
                    inputStr = inputStr.substring(0, inputStr.length() - 1);
                }
                break;
            case R.id.cash_pay_ok:
                dismiss();
                break;
            default:
                dismiss();
                break;

        }

        incomeEditv.setText(inputStr);
        BigDecimal bill = new BigDecimal(strBill);
        BigDecimal income = new BigDecimal(inputStr);
        BigDecimal change = income.subtract(bill);
        cashChangev.setText(change.toString());
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
