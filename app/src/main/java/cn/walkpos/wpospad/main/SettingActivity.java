package cn.walkpos.wpospad.main;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import cn.walkpos.wpospad.R;


public class SettingActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



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
}
