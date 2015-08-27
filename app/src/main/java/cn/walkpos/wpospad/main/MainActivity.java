package cn.walkpos.wpospad.main;

import android.os.Bundle;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accountEt = (NoinputEditText)this.findViewById(R.id.account);
        passEt = (NoinputEditText)this.findViewById(R.id.password);

        findViewById(R.id.btn_0).setOnClickListener(this);
        findViewById(R.id.btn_1).setOnClickListener(this);
        findViewById(R.id.btn_2).setOnClickListener(this);
        findViewById(R.id.btn_3).setOnClickListener(this);
        findViewById(R.id.btn_4).setOnClickListener(this);
        findViewById(R.id.btn_5).setOnClickListener(this);
        findViewById(R.id.btn_6).setOnClickListener(this);
        findViewById(R.id.btn_7).setOnClickListener(this);
        findViewById(R.id.btn_8).setOnClickListener(this);
        findViewById(R.id.btn_9).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_ok).setOnClickListener(this);
        findViewById(R.id.verify_login).setOnClickListener(this);

        passEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    passEt.setCursorVisible(true);
                    curEt = passEt;
                    return false;
                }
                return false;
            }
        });
        accountEt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    accountEt.setCursorVisible(true);
                    curEt = accountEt;
                    return false;
                }
                return false;
            }
        });
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.btn_0:
                curEt.append("0");
                break;
            case R.id.btn_1:
                curEt.append("1");
                break;
            case R.id.btn_2:
                curEt.append("2");
                break;
            case R.id.btn_3:
                curEt.append("3");
                break;
            case R.id.btn_4:
                curEt.append("4");
                break;
            case R.id.btn_5:
                curEt.append("5");
                break;
            case R.id.btn_6:
                curEt.append("6");
                break;
            case R.id.btn_7:
                curEt.append("7");
                break;
            case R.id.btn_8:
                curEt.append("8");
                break;
            case R.id.btn_9:
                curEt.append("9");
                break;

            case R.id.btn_ok:
                UiUtils.startActivity(this, StoreActivity.class,true);
                break;
            case R.id.verify_login:
                UiUtils.startActivity(this, StoreActivity.class,true);
                break;
            default:
                super.onClick(v);
                break;
        }
    }
}
