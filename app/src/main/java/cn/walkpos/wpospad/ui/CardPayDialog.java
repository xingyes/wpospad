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

import java.util.ArrayList;

import cn.walkpos.wpospad.R;

/**
 * Created by xingyao on 2015/8/22.
 */
public class CardPayDialog extends Dialog implements View.OnClickListener {

    private String  strOrderId;
    private String  strBill;
    private String  inputStr = "";
    public CardPayDialog(Context context, final String orderid,final String abill) {
        super(context, com.xingy.R.style.Dialog);
        strOrderId = orderid;
        strBill = abill;
        if(TextUtils.isEmpty(strBill))
            strBill = "0.00";
    }
    public CardPayDialog(Context context, int theme) {
        super(context, theme);
    }
    protected CardPayDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    protected int           mWinWidth;

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
        setContentView(R.layout.dialog_paycard);

        this.findViewById(R.id.card_pay_ok).setOnClickListener(this);

        billTotalv = (TextView) this.findViewById(R.id.bill_total_tv);
        if(null!=billTotalv)
            billTotalv.setText(this.getContext().getString(R.string.income_x,strBill));
        mWinWidth = this.setAttributes();

    }


    public void setPayInfo(final String orderid,final String abill)
    {
        strOrderId = orderid;
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
        switch (v.getId())
        {

            case R.id.card_pay_ok:
                UiUtils.makeToast(getContext(),"正在连接设备...");
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
