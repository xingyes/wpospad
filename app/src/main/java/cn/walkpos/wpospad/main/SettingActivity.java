package cn.walkpos.wpospad.main;

import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingy.lib.IVersion;
import com.xingy.lib.ui.CheckBox;
import com.xingy.lib.ui.UiUtils;
import com.xingy.lib.ui.VerticalRangeSeekBar;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import cn.walkpos.wpospad.R;


public class SettingActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {


    private RadioGroup  setRg;
    private RelativeLayout container;
    private RelativeLayout.LayoutParams rl;

    private class StoreSetHolder{
        public View  rootv;
        public EditText   namev;
        public EditText   typev;
        public EditText   phonev;
        public EditText   urlv;
        public EditText   slognv;

        public CheckBox   phoneCheck;
        public CheckBox   urlCheck;
        public CheckBox   slognCheck;
    };
    private StoreSetHolder   storeHolder;

    private class DevSetHolder{
        public View  rootv;

    };
    private DevSetHolder   devHolder;

    private class PasswdSetHolder{
        public View  rootv;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        loadNavBar(R.id.setting_nav);
        container = (RelativeLayout)this.findViewById(R.id.container_layout);
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
        storeHolder = new StoreSetHolder();
        storeHolder.rootv = getLayoutInflater().inflate(R.layout.set_basic_pg,null);

        devHolder = new DevSetHolder();
        devHolder.rootv = getLayoutInflater().inflate(R.layout.set_dev_pg,null);

        passwdHolder = new PasswdSetHolder();
        passwdHolder.rootv = getLayoutInflater().inflate(R.layout.set_passwd_pg,null);

        initOtherPg();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
//            case R.id.verify_check:
//                UiUtils.makeToast(this,"去查看进度");
//                break;
//            case R.id.checkout_btn:
//                UiUtils.makeToast(this,"去结账页面");
//                break;
//            case R.id.pro_btn:
//                UiUtils.makeToast(this,"去管理商品界面");
//                break;
//            case R.id.staff_btn:
//                UiUtils.makeToast(this,"管理员工界面");
//                break;
//            case R.id.statistics_btn:
//                UiUtils.makeToast(this,"统计报表界面");
//                break;
//            case R.id.money_btn:
//                UiUtils.makeToast(this,"金额提现管理界面");
//                break;
//            case R.id.setting_btn:
//                UiUtils.makeToast(this,"基本设置界面");
//                break;
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
        otherHolder.feedbackV.findViewById(R.id.submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.makeToast(SettingActivity.this,"提交反馈：" +otherHolder.fbcontEt.getText().toString());
            }
        });

        otherHolder.updateV = getLayoutInflater().inflate(R.layout.info_update_pg,null);
        otherHolder.upVersionV = (TextView)otherHolder.updateV.findViewById(R.id.version_info);
        otherHolder.upTimeV = (TextView)otherHolder.updateV.findViewById(R.id.update_time);
        otherHolder.upVersionV.setText(getString(R.string.version_info_x, IVersion.getVersionName()));
        otherHolder.upTimeV.setText(getString(R.string.update_time_x, ToolUtil.toDateSecond(System.currentTimeMillis())));
        otherHolder.updateBtn = (TextView)otherHolder.updateV.findViewById(R.id.update_btn);
        otherHolder.updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UiUtils.makeToast(SettingActivity.this,"检查更新");
                otherHolder.updateBtn.setEnabled(false);
                otherHolder.upTimeV.setText(getString(R.string.update_time_x, ToolUtil.toDateSecond(System.currentTimeMillis())));

            }
        });
        otherHolder.shareV = getLayoutInflater().inflate(R.layout.info_share_pg,null);

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
}
