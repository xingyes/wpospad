package cn.walkpos.wpospad.login;

import android.os.Bundle;
import android.view.View;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.main.MainActivity;


public class VerifyPicActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_pic);

        findViewById(R.id.use_wx).setOnClickListener(this);
        findViewById(R.id.submit_btn).setOnClickListener(this);

    }


    @Override
    public void onResume()
    {
        super.onResume();
    }



    @Override
    public void onClick(View v)
    {
//        Bundle  bundle = null;
        switch (v.getId()) {
            case R.id.use_wx:
                UiUtils.makeToast(this, "call wx");
                break;
            case R.id.submit_btn:
                UiUtils.startActivity(this,MainActivity.class,true);
                break;
            default:
                super.onClick(v);
                break;
        }
    }

}
