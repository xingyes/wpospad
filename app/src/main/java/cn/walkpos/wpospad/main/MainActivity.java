package cn.walkpos.wpospad.main;

import android.os.Bundle;
import android.os.PowerManager;
import android.view.MotionEvent;
import android.view.View;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.store.StoreActivity;
import cn.walkpos.wpospad.ui.NoinputEditText;


public class MainActivity extends BaseActivity {

    private NoinputEditText  accountEt;
    private NoinputEditText passEt;

    private NoinputEditText curEt;
    private PowerManager.WakeLock wakeLock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        PowerManager powerManager = (PowerManager)getSystemService(this.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "My Lock");
        wakeLock.acquire();


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
//        switch (v.getId()) {
//            case R.id.btn_0:
//                curEt.append("0");
//                break;
//            case R.id.btn_1:
//                curEt.append("1");
//                break;
//            case R.id.btn_2:
//                curEt.append("2");
//                break;
//            case R.id.btn_3:
//                curEt.append("3");
//                break;
//            case R.id.btn_4:
//                curEt.append("4");
//                break;
//            case R.id.btn_5:
//                curEt.append("5");
//                break;
//            case R.id.btn_6:
//                curEt.append("6");
//                break;
//            case R.id.btn_7:
//                curEt.append("7");
//                break;
//            case R.id.btn_8:
//                curEt.append("8");
//                break;
//            case R.id.btn_9:
//                curEt.append("9");
//                break;
//
//            case R.id.btn_ok:
//                UiUtils.startActivity(this, StoreActivity.class,true);
//                break;
//            case R.id.verify_login:
//                UiUtils.startActivity(this, StoreActivity.class,true);
//                break;
//            default:
//                super.onClick(v);
//                break;
//        }
    }
}
