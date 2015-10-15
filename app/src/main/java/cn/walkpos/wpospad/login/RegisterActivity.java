package cn.walkpos.wpospad.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.xingy.lib.model.Account;
import com.xingy.lib.ui.CheckBox;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;

import cn.walkpos.wpospad.R;


public class RegisterActivity extends BaseActivity{

    public static final int   TYPE_REGISTER_NEW = 1;
    public static final int   TYPE_RESET_FORGET = 2;
    public static final String  REGISTER_TYPE = "register_type";

    private int   registerType;

    public static final int   ACTIVITY_CODE_LOGIN = 5555;
    public static final int   COUTING_DOWN_SECOND = 120;
    public static final int   MSG_INTERVAL = 0x100;

    private EditText    mPhonev;
    private EditText    mPass1v;
    private EditText    mPass2v;

    private EditText    mInputVerifyCode;

    private TextView    mRequestVerifyBtn;
    private TextView    mSubmitBtn;

    private boolean     bSending;
    private int         mCounting = COUTING_DOWN_SECOND;

    public static final int   REQ_REGISTER = 1;
    public static final int   REQ_SMS = 2;
    public static final int   REQ_CHANGE_PHONE = 3;

    private CheckBox   agreeCheckBtn;

    private Ajax  mAjax;
    private Account act;
    private String  phoneStr;

    private Handler     mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == MSG_INTERVAL)
            {
                mCounting--;
                if(mCounting > 0)
                {
                    mRequestVerifyBtn.setText(getString(R.string.left_second,mCounting));
                    mHandler.sendEmptyMessageDelayed(MSG_INTERVAL,1000);
                }
                else
                {
                    mRequestVerifyBtn.setText(getString(R.string.send));
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent ait = getIntent();
        if(null==ait)
        {
            finish();
            return;
        }

        registerType = ait.getIntExtra(REGISTER_TYPE,TYPE_REGISTER_NEW);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        // 初始化布局元素
        initViews();
    }

    private void initViews() {
        loadNavBar(R.id.register_nav);
        mNavBar.setText(registerType== TYPE_REGISTER_NEW ? R.string.register_now : R.string.reset_password);

        mPhonev = (EditText) this.findViewById(R.id.phone_num);
        mPhonev.setOnClickListener(this);
        mPhonev.requestFocus();

        mPass1v = (EditText) this.findViewById(R.id.new_pass1);
        mPass1v.setOnClickListener(this);

        mPass2v = (EditText) this.findViewById(R.id.new_pass2);
        mPass2v.setOnClickListener(this);


        mInputVerifyCode = (EditText) this.findViewById(R.id.input_verify);
        mRequestVerifyBtn = (TextView) this.findViewById(R.id.request_verify_code);
        mRequestVerifyBtn.setOnClickListener(this);

        mSubmitBtn = (TextView)findViewById(R.id.passwd_submit_btn);
        mSubmitBtn.setOnClickListener(this);

        findViewById(R.id.agree_layout).setVisibility(View.GONE);

        if(registerType == TYPE_REGISTER_NEW ) {
            agreeCheckBtn = (CheckBox) findViewById(R.id.agree_btn);
            findViewById(R.id.agree_layout).setVisibility(View.VISIBLE);
            findViewById(R.id.agreement_info).setOnClickListener(this);
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.request_verify_code:
                if (!bSending) {
                    if (!requestVerifyCode()) {
                        return;
                    }

                    mRequestVerifyBtn.setEnabled(false);
                    mRequestVerifyBtn.setText(getString(R.string.left_second, mCounting));

                    bSending = true;

                    mHandler.sendEmptyMessageDelayed(MSG_INTERVAL, 1000);

                }
                break;
            case R.id.agreement_info:
                UiUtils.makeToast(this,"Show agreement");
                break;
            case R.id.passwd_submit_btn:
                if(registerType == TYPE_REGISTER_NEW) {
                    UiUtils.makeToast(this, "Register succ");
                    UiUtils.startActivity(RegisterActivity.this, VerifyMidActivity.class, true);
                }
                else
                {
                    UiUtils.makeToast(this, "Reset password succ");
                    UiUtils.startActivity(RegisterActivity.this, LoginActivity.class, true);
                }
                break;
            default:
                super.onClick(v);
                break;

        }
    }


    private void verifyAndLogin() {

//        String phoneNum = mPhonev.getText().toString();
//        if(!ToolUtil.isPhoneNum(phoneNum))
//        {
//            UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
//            return;
//        }
//        String verifycode = mInputVerifyCode.getText().toString();
//        if(ToolUtil.isEmpty(verifycode))
//        {
//            UiUtils.makeToast(this, R.string.please_input_verifycode);
//            return;
//        }
//
//        if(null!=mAjax)
//            mAjax.abort();
//        mAjax = ServiceConfig.getAjax(braConfig.URL_VERIFY_LOGIN);
//        if (null == mAjax)
//            return;
//        showLoadingLayer();
//        mAjax.setId(REQ_REGISTER);
//        mAjax.setData("type",3);
//        mAjax.setData("mobile", phoneNum);
//        mAjax.setData("vcode", verifycode);
//
//        mAjax.setOnSuccessListener(this);
//        mAjax.setOnErrorListener(this);
//        mAjax.send();
    }

    private boolean requestVerifyCode() {
        String phoneNum = mPhonev.getText().toString();
        if(ToolUtil.isPhoneNum(phoneNum))
        {
//            if(mAjax!=null)
//                mAjax.abort();
//            mAjax = ServiceConfig.getAjax(braConfig.URL_HOME_FLOOR);//URL_VERIFYCODE_SMS
//            if (null == mAjax)
//                return false;
//
//            showLoadingLayer();
//            mAjax.setId(REQ_SMS);
//            mAjax.setData("phone_number", phoneNum);
//
//            mAjax.setOnSuccessListener(this);
//            mAjax.setOnErrorListener(this);
//            mAjax.send();
            return true;
        }
        else
        {
            UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
        }
        return false;
    }

//    @Override
//    public void onSuccess(JSONObject v, Response response) {
//        closeLoadingLayer();
//        final int ret = v.optInt("err");
//        if(ret != 0 )
//        {
//            String msg =  v.optString("data");
//            UiUtils.makeToast(this, ToolUtil.isEmpty(msg) ? this.getString(R.string.parser_error_msg): msg);
//            return;
//        }
//
//        if(response.getId() == REQ_CHANGE_PHONE)
//        {
//            UiUtils.makeToast(this,R.string.submit_succ);
//            act.phone = phoneStr;
//            ILogin.setActiveAccount(act);
//            ILogin.saveIdentity(act);
//            setResult(RESULT_OK);
//            finish();
//        }
//        else if(response.getId() == REQ_REGISTER)
//        {
//            JSONObject data = v.optJSONObject("dt");
//            if(null == data)
//            {
//                UiUtils.makeToast(this, this.getString(R.string.parser_error_msg));
//                return;
//            }
//            UiUtils.makeToast(VerifyLoginActivity.this,R.string.login_succ);
//            Account account = new Account();
//            account.uid = data.optString("uid");
//            account.nickName = data.optString("nickname");
//            account.token = data.optString("token");
//            account.iconUrl = data.optString("himg");
//            account.phone = data.optString("phone");
//            account.rowCreateTime = new Date().getTime();
//
//            ILogin.setActiveAccount(account);
//            ILogin.saveIdentity(account);
//            finish();
//        }
//    }

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK)
//		{
//			finish();
//			return true;
//		}
//		else
//			return super.onKeyDown(keyCode, event);
//	}




    @Override
    protected void onDestroy()
    {
        if(null!=mAjax)
        {
            mAjax.abort();
            mAjax = null;
        }

        super.onDestroy();
    }
}
