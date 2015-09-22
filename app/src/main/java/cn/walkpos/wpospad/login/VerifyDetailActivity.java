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
//        switch (v.getId()) {
//            case R.id.go_verify_btn:
//                bundle = new Bundle();
//                bundle.putInt(RegisterActivity.REGISTER_TYPE, RegisterActivity.TYPE_REGISTER_NEW);
//                UiUtils.startActivity(VerifyDetailActivity.this,RegisterActivity.class,bundle,true);
//                break;
//            case R.id.skip_verify_btn:
//                bundle = new Bundle();
//                bundle.putInt(RegisterActivity.REGISTER_TYPE, RegisterActivity.TYPE_RESET_FORGET);
//                UiUtils.startActivity(VerifyDetailActivity.this,RegisterActivity.class,bundle,true);
//                break;
//
//            default:
//                super.onClick(v);
//                break;
//        }

    }

}
