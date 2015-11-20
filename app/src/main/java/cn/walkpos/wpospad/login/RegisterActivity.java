package cn.walkpos.wpospad.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.xingy.lib.AppStorage;
import com.xingy.lib.ui.CheckBox;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.util.WPosConfig;


public class RegisterActivity extends BaseActivity implements OnSuccessListener<JSONObject>{

    public static final int   TYPE_REGISTER_NEW = 1;
    public static final int   TYPE_RESET_FORGET = 2;
    public static final String  REGISTER_TYPE = "register_type";

    private int   registerType;
    private String accountArrayStr;
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

    public static final int   REQ_SMS = 2;
    public static final int   REQ_CHANGE_PHONE = 3;

    private CheckBox   agreeCheckBtn;

    private Ajax  mAjax;
    private String  mPhoneStr;

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

    @Override
    public void onResume()
    {
        super.onResume();
        mPhonev.setText("");
        mPass1v.setText("");
        mPass2v.setText("");
        mPhonev.append("18521359287");
        mPass1v.append("654321mm");
        mPass2v.append("654321mm");

    }

    private void initViews() {
        loadNavBar(R.id.register_nav);
        mNavBar.setText(registerType== TYPE_REGISTER_NEW ? R.string.register_now : R.string.reset_password);

        mPhonev = (EditText) this.findViewById(R.id.phone_num);
        mPhonev.setInputType(InputType.TYPE_CLASS_NUMBER);
        mPhonev.requestFocus();

        mPass1v = (EditText) this.findViewById(R.id.new_pass1);
        mPass2v = (EditText) this.findViewById(R.id.new_pass2);

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
                    registerAccount();
                }
                else
                {
                    changePasswd();
                }
                break;
            default:
                super.onClick(v);
                break;

        }
    }


    /**
     *  new account
     */
    private void registerAccount() {
        if(!agreeCheckBtn.isChecked())
        {
            UiUtils.makeToast(this, "请阅读并同意服务协议");
            return;
        }
        mPhoneStr = mPhonev.getText().toString();
        if(!ToolUtil.isPhoneNum(mPhoneStr))
        {
            UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
            return;
        }
        String passwd = mPass1v.getText().toString();
        if(ToolUtil.isEmpty(passwd))
        {
            UiUtils.makeToast(this, "密码不能为空");
            return;
        }
        String passwd2 = mPass2v.getText().toString();
        if(ToolUtil.isEmpty(passwd2) || !passwd.equals(passwd2))
        {
            UiUtils.makeToast(this, "两次密码输入不一致");
            return;
        }
        String verifycode = mInputVerifyCode.getText().toString();
        if(ToolUtil.isEmpty(verifycode))
        {
            UiUtils.makeToast(this, R.string.please_input_verifycode);
            return;
        }

        if(null!=mAjax)
            mAjax.abort();
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;


        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_REGISTER);
        mAjax.setData("method", "passport.signup");
        mAjax.setData("mobile", mPhoneStr);
        mAjax.setData("password",passwd);
        mAjax.setData("psw_confirm",passwd2);
        mAjax.setData("vcode", verifycode);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void changePasswd() {
        mPhoneStr = mPhonev.getText().toString();
        if(!ToolUtil.isPhoneNum(mPhoneStr))
        {
            UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
            return;
        }
        String passwd = mPass1v.getText().toString();
        if(ToolUtil.isEmpty(passwd))
        {
            UiUtils.makeToast(this, "密码不能为空");
            return;
        }
        String passwd2 = mPass2v.getText().toString();
        if(ToolUtil.isEmpty(passwd2) || !passwd.equals(passwd2))
        {
            UiUtils.makeToast(this, "两次密码输入不一致");
            return;
        }
        String verifycode = mInputVerifyCode.getText().toString();
        if(ToolUtil.isEmpty(verifycode))
        {
            UiUtils.makeToast(this, R.string.please_input_verifycode);
            return;
        }

        if(null!=mAjax)
            mAjax.abort();
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;


        showLoadingLayer();

//        mobile=18521359287&newpassword=654321mm&psw_confirm=654321mm
        mAjax.setId(WPosConfig.REQ_RESET_PASSWD);
        mAjax.setData("method", "passport.reset");
        mAjax.setData("mobile", mPhoneStr);
        mAjax.setData("newpassword",passwd);
        mAjax.setData("psw_confirm",passwd2);
        mAjax.setData("msgcode", verifycode);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }

    private boolean requestVerifyCode() {
        String phoneNum = mPhonev.getText().toString();
        if(ToolUtil.isPhoneNum(phoneNum))
        {
            if(mAjax!=null)
                mAjax.abort();

            mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
            if (null == mAjax)
                return false;


            showLoadingLayer();

            mAjax.setId(WPosConfig.REQ_SMS);
            mAjax.setData("method", "message.code");
            mAjax.setData("mobile", mPhoneStr);

            mAjax.setOnSuccessListener(this);
            mAjax.setOnErrorListener(this);

            mAjax.send();
            return true;
        }
        else
        {
            UiUtils.makeToast(this, R.string.please_input_correct_phone_num);
        }
        return false;
    }




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

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {

        closeLoadingLayer();

        int errno = jsonObject.optInt("response_code",-1);
        if(errno!=0)
        {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this,msg);
            return;
        }


        if(response.getId() == WPosConfig.REQ_REGISTER)
        {
            UiUtils.makeToast(this, jsonObject.optString("res", "注册成功"));
            WPosApplication.account = new WposAccount();
            WPosApplication.account.mobile  = mPhoneStr;
            JSONObject data = jsonObject.optJSONObject("data");
            WPosApplication.account.token = data.optString("token");

            ArrayList<String> accountArray = new ArrayList<String>();
            accountArrayStr = AppStorage.getData(LoginActivity.STORAGE_ACCOUNT_ARRAY);
            if(!TextUtils.isEmpty(accountArrayStr))
            {
                String items[] = accountArrayStr.split(",");
                if(null!=items && items.length>0)
                {
                    for(String name: items)
                        accountArray.add(name);
                }
            }

            if(accountArray.size()<=0) {
                accountArrayStr = mPhoneStr;
                AppStorage.setData(LoginActivity.STORAGE_ACCOUNT_ARRAY, accountArrayStr, true);
            }
            else if(!accountArray.contains(mPhoneStr))
            {
                accountArrayStr +="," + mPhoneStr;
                AppStorage.setData(LoginActivity.STORAGE_ACCOUNT_ARRAY, accountArrayStr, true);
            }

            UiUtils.startActivity(RegisterActivity.this, VerifyMidActivity.class, true);
            finish();
        }
        else if(WPosConfig.REQ_RESET_PASSWD == response.getId())
        {
            JSONObject data = jsonObject.optJSONObject("data");
            if(null!=data && data.optBoolean("status"))
            {
                UiUtils.makeToast(this,jsonObject.optString("res","密码更新成功"));
                finish();
            }
            else
            {
                UiUtils.makeToast(this,jsonObject.optString("res","密码更新失败请重试"));
            }
        }else if(response.getId() == WPosConfig.REQ_SMS)
        {
            UiUtils.makeToast(this,jsonObject.optString("res","短信发送成功"));
        }

    }


    @Override
    public void onBackPressed()
    {
        UiUtils.startActivity(this,LoginActivity.class,true);
        super.onBackPressed();
    }
}
