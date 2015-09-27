package cn.walkpos.wpospad.main;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.store.StoreActivity;
import cn.walkpos.wpospad.ui.NoinputEditText;


public class MainActivity extends BaseActivity {

    private PowerManager.WakeLock wakeLock;

    private TextView     verifyHintV;
    private TextView     verifyCheckV;
    private TextView     storeNameV;

    private TextView     checkoutBtn;
    private TextView     proBtn;
    private TextView     staffBtn;
    private TextView     statisticsBtn;
    private TextView     moneyBtn;
    private TextView     settingBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PowerManager powerManager = (PowerManager)getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        wakeLock.acquire();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        verifyHintV = (TextView)this.findViewById(R.id.verify_statinfo);
        verifyCheckV = (TextView)this.findViewById(R.id.verify_check);
        storeNameV = (TextView)this.findViewById(R.id.store_name);

        checkoutBtn = (TextView)this.findViewById(R.id.checkout_btn);
        proBtn = (TextView)this.findViewById(R.id.pro_btn);
        staffBtn = (TextView)this.findViewById(R.id.staff_btn);
        statisticsBtn = (TextView)this.findViewById(R.id.statistics_btn);
        moneyBtn = (TextView)this.findViewById(R.id.money_btn);
        settingBtn = (TextView)this.findViewById(R.id.setting_btn);

        verifyCheckV.setOnClickListener(this);
        checkoutBtn.setOnClickListener(this);
        proBtn.setOnClickListener(this);
        staffBtn.setOnClickListener(this);
        statisticsBtn.setOnClickListener(this);
        moneyBtn.setOnClickListener(this);
        settingBtn.setOnClickListener(this);





    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != wakeLock)
            wakeLock.release();
        wakeLock = null;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.verify_check:
                UiUtils.makeToast(this,"去查看进度");
                break;
            case R.id.checkout_btn:
                UiUtils.makeToast(this,"去结账页面");
                break;
            case R.id.pro_btn:
                UiUtils.makeToast(this,"去管理商品界面");
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
