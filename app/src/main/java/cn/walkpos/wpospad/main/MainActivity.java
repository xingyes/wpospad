package cn.walkpos.wpospad.main;

import android.os.Bundle;
import android.os.PowerManager;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.xingy.lib.ILogin;
import com.xingy.lib.model.Account;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.CircleImageView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.login.LoginActivity;
import cn.walkpos.wpospad.store.StoreManageActivity;


public class MainActivity extends BaseActivity {

    private PowerManager.WakeLock wakeLock;
    public static final String imgtesturl = "http://g.hiphotos.baidu.com/baike/w%3D268/sign=ed2f3c98b1119313c743f8b65d390c10/4ec2d5628535e5dd597d578575c6a7efce1b6213.jpg";

    private View         userLayout;
    private CircleImageView   userIconV;
    private TextView     userInfoV;   //name\nphone

    private TextView     verifyHintV;

    private TextView     storeNameV;

    private ImageView     checkoutBtn;
    private ImageView     proBtn;
    private ImageView     staffBtn;
    private ImageView     statisticsBtn;
    private ImageView     moneyBtn;
    private ImageView     settingBtn;

    private ImageLoader   mImgLoader;
    private AppDialog     userShiftDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PowerManager powerManager = (PowerManager)getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        wakeLock.acquire();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, WPosApplication.globalMDCache);

        verifyHintV = (TextView)this.findViewById(R.id.verify_statinfo);
        verifyHintV.setOnClickListener(this);

        userLayout = this.findViewById(R.id.user_layout);
        userLayout.setVisibility(View.GONE);
        userIconV = (CircleImageView)this.findViewById(R.id.user_icon);
        userInfoV = (TextView)this.findViewById(R.id.user_info);


        storeNameV = (TextView)this.findViewById(R.id.store_name);
        storeNameV.setOnClickListener(this);

        checkoutBtn = (ImageView)this.findViewById(R.id.checkout_btn);
        proBtn = (ImageView)this.findViewById(R.id.pro_btn);
        staffBtn = (ImageView)this.findViewById(R.id.staff_btn);
        staffBtn.setEnabled(false);
        statisticsBtn = (ImageView)this.findViewById(R.id.statistics_btn);
        statisticsBtn.setEnabled(false);

        moneyBtn = (ImageView)this.findViewById(R.id.money_btn);
        moneyBtn.setEnabled(false);
        settingBtn = (ImageView)this.findViewById(R.id.setting_btn);

        verifyHintV.setOnClickListener(this);
        userLayout.setOnClickListener(this);
        checkoutBtn.setOnClickListener(this);
        proBtn.setOnClickListener(this);
        staffBtn.setOnClickListener(this);
        statisticsBtn.setOnClickListener(this);
        moneyBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);

        initUserInfo();


    }

    private void initUserInfo()
    {

        userIconV.setUseShader(true);
        userIconV.setImageUrl(imgtesturl,mImgLoader);

        userInfoV.setText("小二\n18516162727");

        Account act = ILogin.getActiveAccount();
        if(act!=null)
        {
            userLayout.setVisibility(View.VISIBLE);
            verifyHintV.setVisibility(View.GONE);
        }
        else
        {


            userLayout.setVisibility(View.GONE);
            verifyHintV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != wakeLock)
            wakeLock.release();
        wakeLock = null;

        if(null!=userShiftDialog && userShiftDialog.isShowing())
            userShiftDialog.dismiss();
        userShiftDialog = null;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.store_name:
                if(verifyHintV.getVisibility() == View.VISIBLE) {
                    verifyHintV.setVisibility(View.GONE);
                    userLayout.setVisibility(View.VISIBLE);
                    staffBtn.setEnabled(true);
                    statisticsBtn.setEnabled(true);
                    moneyBtn.setEnabled(true);
                }
                break;
            case R.id.verify_statinfo:
                UiUtils.makeToast(this, "去查看进度");
                break;
            case R.id.user_layout:
                if(null==userShiftDialog)
                {
                    userShiftDialog = UiUtils.showDialog(MainActivity.this,R.string.caption_hint,
                            R.string.r_u_change_account,R.string.btn_ok,R.string.btn_cancel,new AppDialog.OnClickListener() {
                                @Override
                                public void onDialogClick(int nButtonId) {
                                    if(nButtonId == AppDialog.BUTTON_POSITIVE)
                                    {
                                        UiUtils.startActivity(MainActivity.this, LoginActivity.class,true);
                                        finish();
                                        return;
                                    }
                                }
                            });
                }
                else
                    userShiftDialog.show();
                break;
            case R.id.checkout_btn:
                UiUtils.makeToast(this,"去结账页面");
                break;
            case R.id.pro_btn:
                UiUtils.startActivity(this,StoreManageActivity.class,true);
                break;
            case R.id.staff_btn:
                UiUtils.startActivity(this,StaffManageActivity.class,true);
                break;
            case R.id.statistics_btn:
                UiUtils.makeToast(this,"统计报表界面");
                break;
            case R.id.money_btn:
                UiUtils.makeToast(this,"金额提现管理界面");
                break;
            case R.id.setting_btn:
                UiUtils.startActivity(this,SettingActivity.class,true);
                break;
            default:
                super.onClick(v);
                break;
        }

    }
}
