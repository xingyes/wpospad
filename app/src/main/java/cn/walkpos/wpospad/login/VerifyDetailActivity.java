package cn.walkpos.wpospad.login;

import android.os.Bundle;
import android.view.View;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import cn.walkpos.wpospad.R;


public class VerifyDetailActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_detail);
        this.findViewById(R.id.submit_btn).setOnClickListener(this);


    }


    @Override
    public void onResume()
    {
        super.onResume();
    }



    @Override
    public void onClick(View v)
    {
        Bundle  bundle = null;
        switch (v.getId()) {
            case R.id.submit_btn:
                UiUtils.startActivity(VerifyDetailActivity.this,VerifyPicActivity.class,bundle,true);
                break;
            default:
                super.onClick(v);
                break;
        }

    }

}
