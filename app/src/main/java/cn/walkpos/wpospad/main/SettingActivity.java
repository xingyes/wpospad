package cn.walkpos.wpospad.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xingy.lib.AppStorage;
import com.xingy.lib.IVersion;
import com.xingy.lib.ui.CheckBox;
import com.xingy.lib.ui.UiUtils;
import com.xingy.preference.Preference;
import com.xingy.share.ShareInfo;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.login.RegisterActivity;
import cn.walkpos.wpospad.module.BranchInfoModule;
import cn.walkpos.wpospad.util.ShareUtil;
import cn.walkpos.wpospad.util.WPosConfig;


public class SettingActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener,
        ShareUtil.CallbackListener,OnSuccessListener<JSONObject>{

    private Ajax         mAjax;
    private BranchInfoModule   mBranchInfo;
    private RadioGroup  setRg;
    private RelativeLayout container;
    private RelativeLayout.LayoutParams rl;
    private ImageLoader    mImageLoader;

    @Override
    public void onComplete(Object obj) {
    }

    @Override
    public void onError(String msg) {
    }

    @Override
    public void onCancel() {
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

        if(response.getId() == WPosConfig.REQ_MODIFY_BRANCH_INFO)
        {
            String msg = jsonObject.optString("res", "修改成功");
            UiUtils.makeToast(this,msg);
            AppStorage.setData(MainActivity.BRANCH_INFO_MODIFIED,"true",true);
        }
    }

    private class StoreSetHolder{
        public View  rootv;
        public EditText   namev;
        public EditText   typev;
        public EditText   phonev;
        public CheckBox   phonePrintCheck;
        public EditText   urlv;
        public CheckBox   urlPrintCheck;
        public EditText   slognv;
        public CheckBox   slognPrintCheck;
    };
    private StoreSetHolder   storeHolder;

    private class DevSetHolder{
        public View  rootv;
        public android.widget.CheckBox  printInvoiceCheck;
        public android.widget.CheckBox  scanGunCheck;
        public android.widget.CheckBox  moneyboxCheck;
        public android.widget.CheckBox  clientScreenCheck;
        public CheckBox       mposCheck;
        public CheckBox       swipCheck;
        public TextView       mposStatV;

    };
    private DevSetHolder   devHolder;

    private class PasswdSetHolder{
        public View  rootv;
        public EditText  phoneEt;
        public EditText  newPass1;
        public EditText  newPass2;
        public EditText  verifycodeEt;
        public TextView  reqverifyBtn;
        public boolean  bSending = false;
        public int mCounting = RegisterActivity.COUTING_DOWN_SECOND;


    };
    private PasswdSetHolder   passwdHolder;

    private class OtherInfoHolder{
        public View  rootv;
        public RadioGroup  otherRg;
        public int   rgIdx;


        public View    qaV;
        public View    feedbackV;
        public EditText fbcontEt;

        public View    shareV;
        public View    updateV;
        public TextView upVersionV;
        public TextView upTimeV;
        public TextView updateBtn;


        public InfoVpAdapter vpAdapter;
        public ViewPager    infoVp;

    };

    public int otherTabIDs [] = {R.id.qa_rb,R.id.feedback_rb,R.id.share_rb,R.id.update_rb};
    private OtherInfoHolder   otherHolder;

    public static final int   MSG_INTERVAL = 0x200;

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg)
        {
            if(passwdHolder==null)
                return;
            if(msg.what == MSG_INTERVAL)
            {
                passwdHolder.mCounting--;
                if(passwdHolder.mCounting > 0)
                {
                    passwdHolder.reqverifyBtn.setText(getString(R.string.left_second,passwdHolder.mCounting));
                    mHandler.sendEmptyMessageDelayed(MSG_INTERVAL,1000);
                }
                else
                {
                    passwdHolder.reqverifyBtn.setText(getString(R.string.send));
                    passwdHolder.mCounting = RegisterActivity.COUTING_DOWN_SECOND;
                    passwdHolder.reqverifyBtn.setEnabled(true);
                    passwdHolder.bSending = false;
                }

            }
            else
                super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent ait = getIntent();
        if(null==ait)
        {
            finish();
            return;
        }

        mBranchInfo = (BranchInfoModule)ait.getSerializableExtra(MainActivity.BRANCH_INFO);
        if(null == mBranchInfo) {
            mBranchInfo = new BranchInfoModule();
            mBranchInfo.store_bn = Preference.getInstance().getBranchNum();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        loadNavBar(R.id.setting_nav);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mQueue, WPosApplication.globalMDCache);

        container = (RelativeLayout)this.findViewById(R.id.container_layout);
        this.findViewById(R.id.manage_staff_btn).setOnClickListener(this);
        rl = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rl.addRule(RelativeLayout.CENTER_HORIZONTAL);
        setRg = (RadioGroup)this.findViewById(R.id.opt_radio_group);
        initPageViews();

        setRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            private int idx = -1;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(idx==checkedId)
                    return;
                idx = checkedId;
                switch (checkedId)
                {
                    case R.id.device_rb:
                        container.removeAllViews();
                        container.addView(devHolder.rootv,rl);
                        break;
                    case R.id.passwd_rb:
                        container.removeAllViews();
                        container.addView(passwdHolder.rootv,rl);
                        break;
                    case R.id.other_rb:
                        container.removeAllViews();
                        container.addView(otherHolder.rootv,rl);
                        break;
                    case R.id.store_rb:
                    default:
                        container.removeAllViews();
                        container.addView(storeHolder.rootv,rl);
                        break;

                }
            }
        });

        setRg.check(R.id.store_rb);

    }

    private void initPageViews()
    {
        initBasicPg();
        initDevPg();
        initPasswdPg();
        initOtherPg();
    }

    /**
     *  店铺设置
     */
    private void initBasicPg()
    {
        storeHolder = new StoreSetHolder();
        storeHolder.rootv = getLayoutInflater().inflate(R.layout.set_basic_pg,null);
        storeHolder.namev =(EditText)storeHolder.rootv.findViewById(R.id.store_name);
        storeHolder.namev.append(mBranchInfo.store_name);

        storeHolder.typev =(EditText)storeHolder.rootv.findViewById(R.id.store_type);
        storeHolder.rootv.findViewById(R.id.type_select_layout).setOnClickListener(this);

        storeHolder.phonev =(EditText)storeHolder.rootv.findViewById(R.id.store_phone);
        storeHolder.phonev.append(mBranchInfo.tel);
        storeHolder.phonePrintCheck = (CheckBox)storeHolder.rootv.findViewById(R.id.phone_on_invoice);

        storeHolder.urlv =(EditText)storeHolder.rootv.findViewById(R.id.store_web);
        storeHolder.urlv.append(mBranchInfo.web_url);
        storeHolder.urlPrintCheck = (CheckBox)storeHolder.rootv.findViewById(R.id.web_on_invoice);

        storeHolder.slognv =(EditText)storeHolder.rootv.findViewById(R.id.store_slogn);
        storeHolder.slognv.append(mBranchInfo.brief);

        storeHolder.slognPrintCheck = (CheckBox)storeHolder.rootv.findViewById(R.id.slogn_on_invoice);

        storeHolder.rootv.findViewById(R.id.store_set_submit).setOnClickListener(this);
    }

    private void submitStoreModify()
    {
        String namestr = storeHolder.namev.getText().toString();
        if(TextUtils.isEmpty(namestr))
        {
            UiUtils.makeToast(this,"店铺名为空");
            return;
        }
        String typestr = storeHolder.typev.getText().toString();
        if(TextUtils.isEmpty(typestr))
        {
            UiUtils.makeToast(this,"店铺类型为空");
            return;
        }
        String phonestr = storeHolder.phonev.getText().toString();
        if(TextUtils.isEmpty(phonestr))
        {
            UiUtils.makeToast(this,"店铺电话为空");
            return;
        }
        String webstr = storeHolder.urlv.getText().toString();
        String slognstr = storeHolder.slognv.getText().toString();
        if(TextUtils.isEmpty(webstr))
            webstr = "";
        if(TextUtils.isEmpty(slognstr))
            slognstr = "";

        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;


        showLoadingLayer();
        mAjax.setId(WPosConfig.REQ_MODIFY_BRANCH_INFO);
        mAjax.setData("method", "store.edit");
        mAjax.setData("store_bn", WPosApplication.StockBn);
        mAjax.setData("store_name", namestr);
        mAjax.setData("logo", "");
        mAjax.setData("addr", "");
        mAjax.setData("tel",phonestr);
        mAjax.setData("web_url",webstr);
        mAjax.setData("brief",slognstr);
        mAjax.setData("print","true");


        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

        AppStorage.setData(MainActivity.BRANCH_INFO_MODIFIED, "true", true);

    }
 //end of 店铺


    /**
     *  设备设置
     */
    private void initDevPg()
    {
        devHolder = new DevSetHolder();
        devHolder.rootv = getLayoutInflater().inflate(R.layout.set_dev_pg,null);
        devHolder.printInvoiceCheck = (android.widget.CheckBox)devHolder.rootv.findViewById(R.id.print_invoice_rb);
        devHolder.scanGunCheck = (android.widget.CheckBox)devHolder.rootv.findViewById(R.id.scangun_rb);
        devHolder.moneyboxCheck = (android.widget.CheckBox)devHolder.rootv.findViewById(R.id.moneybox_rb);
        devHolder.clientScreenCheck = (android.widget.CheckBox)devHolder.rootv.findViewById(R.id.client_screen_rb);

        devHolder.mposCheck = (CheckBox)devHolder.rootv.findViewById(R.id.mpos_ck);
        devHolder.swipCheck = (CheckBox)devHolder.rootv.findViewById(R.id.swip_machine_ck);

        devHolder.mposStatV =(TextView)devHolder.rootv.findViewById(R.id.mpos_stat);
        devHolder.rootv.findViewById(R.id.check_mposs_btn).setOnClickListener(this);
        devHolder.rootv.findViewById(R.id.mpos_bind_btn).setOnClickListener(this);
    }


    /**
     * 修改密码pg
     */
    private void initPasswdPg()
    {
        passwdHolder = new PasswdSetHolder();
        passwdHolder.rootv = getLayoutInflater().inflate(R.layout.set_passwd_pg,null);

        passwdHolder.phoneEt = (EditText)passwdHolder.rootv.findViewById(R.id.phone_num);
        passwdHolder.newPass1 = (EditText)passwdHolder.rootv.findViewById(R.id.new_pass1);
        passwdHolder.newPass2 = (EditText)passwdHolder.rootv.findViewById(R.id.new_pass2);
        passwdHolder.verifycodeEt = (EditText)passwdHolder.rootv.findViewById(R.id.input_verify);

        passwdHolder.reqverifyBtn = (TextView)passwdHolder.rootv.findViewById(R.id.request_verify_code);
        passwdHolder.reqverifyBtn.setOnClickListener(this);
        passwdHolder.rootv.findViewById(R.id.passwd_submit_btn).setOnClickListener(this);
        passwdHolder.bSending = false;
    }

    private boolean requestVerifyCode()
    {
        String phoneNum = passwdHolder.phoneEt.getText().toString();
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

    private void submitPasswd()
    {
        String phonestr = passwdHolder.phoneEt.getText().toString();
        if(TextUtils.isEmpty(phonestr))
        {
            UiUtils.makeToast(this,"手机号为空");
            return;
        }
        String pass1str = passwdHolder.newPass1.getText().toString();
        if(TextUtils.isEmpty(pass1str))
        {
            UiUtils.makeToast(this,"密码不能为空");
            return;
        }
        String pass2str = passwdHolder.newPass2.getText().toString();
        if(TextUtils.isEmpty(pass2str))
        {
            UiUtils.makeToast(this,"密码不能为空");
            return;
        }
        else if(!pass1str.equals(pass2str))
        {
            UiUtils.makeToast(this,"两次密码输入不一致");
            return;
        }
        String verifycodestr = passwdHolder.verifycodeEt.getText().toString();
        if(TextUtils.isEmpty(verifycodestr))
        {
            UiUtils.makeToast(this,"验证码为空");
            return;
        }

        UiUtils.makeToast(this,"提交：" + phonestr + "," + pass1str + "=" + pass2str + "," + verifycodestr);
    }
    //end of 密码pg


    /**
     *  其他 页面
     */
    private void initOtherPg()
    {
        if(null!=otherHolder)
            return;
        otherHolder = new OtherInfoHolder();

        otherHolder.rootv = getLayoutInflater().inflate(R.layout.set_otherinfo_pg,null);
        otherHolder.otherRg =  (RadioGroup)otherHolder.rootv.findViewById(R.id.item_radiogroup);
        otherHolder.otherRg.setOnCheckedChangeListener(this);

        otherHolder.qaV = getLayoutInflater().inflate(R.layout.info_qa_pg,null);
        otherHolder.feedbackV = getLayoutInflater().inflate(R.layout.info_fb_pg,null);
        otherHolder.fbcontEt = (EditText)otherHolder.feedbackV.findViewById(R.id.fb_content);
        otherHolder.feedbackV.findViewById(R.id.feedback_submit_btn).setOnClickListener(this);

        otherHolder.updateV = getLayoutInflater().inflate(R.layout.info_update_pg,null);
        otherHolder.upVersionV = (TextView)otherHolder.updateV.findViewById(R.id.version_info);
        otherHolder.upTimeV = (TextView)otherHolder.updateV.findViewById(R.id.update_time);
        otherHolder.upVersionV.setText(getString(R.string.version_info_x, IVersion.getVersionName()));
        otherHolder.upTimeV.setText(getString(R.string.update_time_x, ToolUtil.toDateSecond(System.currentTimeMillis())));
        otherHolder.updateBtn = (TextView)otherHolder.updateV.findViewById(R.id.update_btn);
        otherHolder.updateBtn.setOnClickListener(this);

        otherHolder.shareV = getLayoutInflater().inflate(R.layout.info_share_pg,null);
        otherHolder.shareV.findViewById(R.id.share_weixin).setOnClickListener(this);
        otherHolder.shareV.findViewById(R.id.share_weibo).setOnClickListener(this);
        otherHolder.shareV.findViewById(R.id.share_timeline).setOnClickListener(this);
        otherHolder.shareV.findViewById(R.id.share_qzone).setOnClickListener(this);

        otherHolder.vpAdapter = new InfoVpAdapter();
        otherHolder.vpAdapter.addPageViews(otherHolder.qaV);
        otherHolder.vpAdapter.addPageViews(otherHolder.feedbackV);
        otherHolder.vpAdapter.addPageViews(otherHolder.shareV);
        otherHolder.vpAdapter.addPageViews(otherHolder.updateV);

        otherHolder.infoVp = (ViewPager)otherHolder.rootv.findViewById(R.id.otherinfo_vp);
        otherHolder.infoVp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                otherHolder.otherRg.check(otherTabIDs[i]);
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });
        otherHolder.infoVp.setAdapter(otherHolder.vpAdapter);
        otherHolder.otherRg.check(otherTabIDs[0]);
    }

    @Override
    public void onClick(View v)
    {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.title = "WPOS";
        shareInfo.summary = "云支点wpos支付平台";
        shareInfo.wxcontent = "云支点wpos支付平台";
        shareInfo.wxMomentsContent = "云支点wpos支付平台";
        shareInfo.iconUrl = MainActivity.imgtesturl;
        shareInfo.url = "http://www.baidu.com";
        switch (v.getId()) {
            case R.id.manage_staff_btn:
                UiUtils.startActivity(this,StaffManageActivity.class,true);
                break;
//            店铺pg
            case R.id.type_select_layout:
                UiUtils.makeToast(this,"选择店铺类型");
                break;
            case R.id.store_set_submit:
                submitStoreModify();
                break;
//            设备pg
            case R.id.check_mposs_btn:
                UiUtils.makeToast(this,"去查看其他mpos");
                break;
            case R.id.mpos_bind_btn:
                UiUtils.makeToast(this,"去绑定");
                break;
//            密码pg
            case R.id.request_verify_code:
                if (!passwdHolder.bSending) {
                    if (!requestVerifyCode()) {
                        return;
                    }
                    passwdHolder.reqverifyBtn.setEnabled(false);
                    passwdHolder.reqverifyBtn.setText(getString(R.string.left_second, passwdHolder.mCounting));
                    passwdHolder.bSending = true;
                    mHandler.sendEmptyMessageDelayed(MSG_INTERVAL, 1000);
                }
                break;
            case R.id.passwd_submit_btn:
                submitPasswd();
                break;
            //其他页面
            case R.id.feedback_submit_btn:
                UiUtils.makeToast(SettingActivity.this,"提交反馈：" +otherHolder.fbcontEt.getText().toString());
                break;
            case R.id.update_btn:
                UiUtils.makeToast(SettingActivity.this,"检查更新");
                otherHolder.updateBtn.setEnabled(false);
                otherHolder.upTimeV.setText(getString(R.string.update_time_x, ToolUtil.toDateSecond(System.currentTimeMillis())));
                break;
            //分享子页面
            case R.id.share_weixin:
                ShareUtil.sendShare(this,shareInfo,ShareUtil.F_WEIXIN,this,mImageLoader);
                UiUtils.makeToast(this,"Share to Weixin");
                break;
            case R.id.share_weibo:
                ShareUtil.sendShare(this,shareInfo,ShareUtil.F_WEIBO,this,mImageLoader);
                UiUtils.makeToast(this,"Share to weibo");
                break;
            case R.id.share_timeline:
                ShareUtil.sendShare(this,shareInfo,ShareUtil.F_TIMELINE,this,mImageLoader);
                UiUtils.makeToast(this,"Share to timeline");
                break;
            case R.id.share_qzone:
                ShareUtil.sendShare(this,shareInfo,ShareUtil.F_QZONE,this,mImageLoader);
                UiUtils.makeToast(this,"Share to qzone");
                break;
            default:
                super.onClick(v);
                break;
        }

    }


    public class InfoVpAdapter extends PagerAdapter{

        private  ArrayList<View> viewArray;

        public void addPageViews(View v)
        {
            if(null == viewArray)
                viewArray = new ArrayList<View>();
            viewArray.add(v);
        }
        public void clear()
        {
            if(null!=viewArray)
                viewArray.clear();
        }

        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return (view == arg1);
        }

        @Override
        public int getCount() {
            return (null==viewArray? 0 : viewArray.size());
        }

        @Override
        public Object instantiateItem(View view, int position) {

            View pagerView = viewArray.get(position);
            ((ViewPager) view).addView(pagerView);

            return pagerView;
        }

        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ((ViewPager) view).removeView((View) arg2);
            System.gc();

        }
    };






    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if(group == otherHolder.otherRg)
        {
            if(otherHolder.rgIdx == checkedId)
                return;

            otherHolder.rgIdx = checkedId;
            switch (checkedId)
            {
                case R.id.feedback_rb:
                    otherHolder.infoVp.setCurrentItem(1);
                    break;
                case R.id.share_rb:
                    otherHolder.infoVp.setCurrentItem(2);
                    break;
                case R.id.update_rb:
                    otherHolder.infoVp.setCurrentItem(3);
                    break;
                case R.id.qa_rb:
                default:
                    otherHolder.infoVp.setCurrentItem(0);
                    break;

            }
        }
    }


}
