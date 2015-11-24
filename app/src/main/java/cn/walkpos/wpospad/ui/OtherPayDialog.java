package cn.walkpos.wpospad.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnErrorListener;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.util.WPosConfig;
import cn.walkpos.wpospad.zxing.android.CaptureActivity;
import cn.walkpos.wpospad.zxing.android.encode.QRCodeEncoder;

/**
 * Created by xingyao on 2015/8/22.
 */
public class OtherPayDialog extends Dialog implements OnSuccessListener<JSONObject>,
        OnErrorListener,View.OnClickListener {

    public  int scanType;
    public static final int WX_BARCODE = 1;
    public static final int ALIPAY_BARCODE = 2;
    public static final int WX_SCAN = 3;
    public static final int ALIPAY_SCAN = 3;

    private BaseActivity mActivity;
    private Ajax         mAjax;
    private String  strOrderId;
    private String  strBill;
    private String  inputStr = "";
    private Bitmap  barCodeBm;
    private ImageView  barCodev;
    private View    scanOptLayout;
    public OtherPayDialog(Context context, final String orderid, final String abill) {
        super(context, com.xingy.R.style.Dialog);
        mActivity = (BaseActivity)context;
        strOrderId = orderid;
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

    protected TextView         billTotalv;

    @Override
    public void onError(Ajax ajax, Response response) {
        UiUtils.makeToast(mActivity,"扫码失败，请稍后重试...");
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        mActivity.closeLoadingLayer();

        int errno = jsonObject.optInt("response_code", -1);
        if (errno != 0) {
            String msg = jsonObject.optString("res", mActivity.getString(R.string.network_error));
            UiUtils.makeToast(mActivity, msg);
            return;
        }

        if (response.getId() == WPosConfig.REQ_WX_BARCODE) {
            JSONObject data = jsonObject.optJSONObject("data");

            barCodeBm = QRCodeEncoder.toBitmap(data.optString("payurl"), DPIUtil.dip2px(280),DPIUtil.dip2px(280), BarcodeFormat.QR_CODE);
            barCodev.setImageBitmap(barCodeBm);
            barCodev.setVisibility(View.VISIBLE);
            scanOptLayout.setVisibility(View.GONE);
        }
    }

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

        scanOptLayout = this.findViewById(R.id.scan_opt_layout);
        barCodev = (ImageView)this.findViewById(R.id.barcode_img);
        barCodev.setVisibility(View.GONE);
        this.findViewById(R.id.scan_ali_btn).setOnClickListener(this);
        this.findViewById(R.id.scan_wx_btn).setOnClickListener(this);
        this.findViewById(R.id.barcode_ali_btn).setOnClickListener(this);
        this.findViewById(R.id.barcode_wx_btn).setOnClickListener(this);
//        this.findViewById(R.id.other_pay_ok).setOnClickListener(this);

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
        if(barCodev.getVisibility() == View.VISIBLE)
        {
            barCodev.setVisibility(View.GONE);
            scanOptLayout.setVisibility(View.VISIBLE);
            return true;
        }
        else
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
            case R.id.scan_wx_btn:
                scanType = WX_SCAN;
                UiUtils.makeToast(getContext(),"唤起微信扫码，扫码枪准备...");
                Intent ait = new Intent(mActivity, CaptureActivity.class);
                mActivity.startActivityForResult(ait,CaptureActivity.REQ_SCAN_CODE);

                break;
            case R.id.barcode_wx_btn:
                scanType = WX_BARCODE;
                UiUtils.makeToast(getContext(),"微信扫码 生成二维码被扫");
                geneScanBarcode();
                break;
            case R.id.scan_ali_btn:
                scanType = ALIPAY_SCAN;
                UiUtils.makeToast(getContext(),"唤起支付宝扫码");
                break;
            case R.id.barcode_ali_btn:
                scanType = ALIPAY_BARCODE;
                UiUtils.makeToast(getContext(),"唤起支付宝 生成二维码被扫");
                break;
//            case R.id.other_pay_ok:
//                dismiss();
//                break;
            default:
                dismiss();
                break;

        }

    }


    private void geneScanBarcode()
    {
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        mActivity.showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_WX_BARCODE);
        mAjax.setData("method", "weixinpay.scancode");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("order_id", strOrderId);
        mAjax.setOnErrorListener(this);
        mAjax.setOnSuccessListener(this);
        mAjax.send();


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
