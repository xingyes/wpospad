package cn.walkpos.wpospad.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import cn.walkpos.wpospad.BlueBle.DeviceListActivity;
import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.main.WPosApplication;

/**
 * Created by xingyao on 2015/8/22.
 */
public class CardPayDialog extends Dialog implements View.OnClickListener {

    private String  strOrderId;
    private String  strBill;
    private String  inputStr = "";
    public interface OnQueryListener{
        public void onQuery(final String orderid);
        public void onCancel(final String orderid);
    }
    private OnQueryListener  queryListner;
    public CardPayDialog(Context context, OnQueryListener listner,final String orderid,final String abill) {
        super(context, com.xingy.R.style.Dialog);
        strOrderId = orderid;
        queryListner = listner;
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
    private static final int SHOW_READ = 10222;
    private Handler blueHandler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case SHOW_READ:
                    String info = (String) msg.obj;
                    opTv.setText(info);
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    };

    private Thread   swapThread = new Thread(new SwapThread());
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


    private TextView   opTv;
    @Override
    protected void onCreate(Bundle aSavedInstanceState) {
        super.onCreate(aSavedInstanceState);

        // Load the default configuration.
        setContentView(R.layout.dialog_paycard);

        this.findViewById(R.id.card_pay_ok).setOnClickListener(this);
        opTv = (TextView)this.findViewById(R.id.mpos_operation);
        billTotalv = (TextView) this.findViewById(R.id.bill_total_tv);
        if(null!=billTotalv)
            billTotalv.setText(this.getContext().getString(R.string.income_x,strBill));
        mWinWidth = this.setAttributes();



    }

    public void startThread()
    {
        swapThread.start();
    }

    public void stopThread()
    {
        swapThread.interrupt();
        blueHandler.removeCallbacksAndMessages(null);
    }

    public void setPayInfo(final String orderid,final String abill)
    {
        strOrderId = orderid;
        strBill = abill;
        if(TextUtils.isEmpty(strBill))
            strBill = "0.00";
        if(null!=billTotalv)
            billTotalv.setText(this.getContext().getString(R.string.income_x,strBill));

        if(null!=opTv)
            opTv.setText("请操作MPOS 进行刷卡");
    }



    @Override
    public boolean onKeyDown (int keyCode, KeyEvent event)
    {
        if(queryListner!=null)
            queryListner.onCancel(strOrderId);
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
                if(queryListner!=null)
                {
                    queryListner.onQuery(strOrderId);
                }
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


    private int timeout = 0;

    public class SwapThread implements Runnable {

        public void setTimeout(int t)
        {
            timeout = t;
        }

        @Override
        public void run() {

            opTv.setText("请在MPOS上进行刷卡操作:");

            swapCard();

            inputKey();
        }
    }

    private void swapCard()
    {
        byte[] in = new byte[1024];
        int ret = 0;
        String cmd = Thread.currentThread().getName();

        if(timeout == 0)
        {
            timeout = 1000;
        }

        ret = WPosApplication.GposService.getCommandStart();

        if(ret >= 0)
        {
            byte[] byteDisp = new byte[512];
            String str = "SWIPE CARD>>>";

            byteDisp = str.getBytes();

            ret = WPosApplication.GposService.getCommandDisp((byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00, byteDisp);
        }


        if(ret >= 0)
        {
            byte[] byteMag = new byte[1024];

            ret = WPosApplication.GposService.getCommandMag(byteMag, 500);


            String readMessage = DeviceListActivity.bytes2Ascii(byteMag, 0, ret);

            blueHandler.obtainMessage(SHOW_READ, ret, -1,readMessage + "\n请在MPOS上输入密码：")
                    .sendToTarget();

        }

        //if(ret >= 0)
        {
            ret = WPosApplication.GposService.getCommandEnd();

            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if(ret < 0)
        {
            return;
        }
    }


    private void inputKey()
    {
        byte[] byteKey = new byte[20];

        int ret = WPosApplication.GposService.getCommandStart();

        if(ret >= 0)
        {
            byte[] byteDisp = new byte[50];

            String str = "Input Key:";

            byteDisp = str.getBytes();

            ret = WPosApplication.GposService.getCommandDisp((byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, byteDisp);
        }

        if(ret>= 0) {
            ret = WPosApplication.GposService.getCommandKeyInput((byte) 0x02, (byte) 0x00, (byte) 'P', (byte) 0x06, (byte) 0x06, byteKey, (byte) 0x1D);

            if (ret <= 0) {
                UiUtils.makeToast(getContext(), "get Key timeout");
            } else {
                String info = DeviceListActivity.bytes2Ascii(byteKey,0, ret);
                blueHandler.obtainMessage(SHOW_READ, ret, -1,"获得密码:" + info).sendToTarget();
            }
        }

        ret = WPosApplication.GposService.getCommandEnd();
    }

    @Override
    public void dismiss()
    {
        stopThread();
        super.dismiss();
    }
}
