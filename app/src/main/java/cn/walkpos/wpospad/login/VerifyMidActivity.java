package cn.walkpos.wpospad.login;

import android.bluetooth.BluetoothAdapter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.AccountAdapter;
import cn.walkpos.wpospad.main.MainActivity;


public class VerifyMidActivity extends BaseActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_mid);

        this.findViewById(R.id.go_verify_btn).setOnClickListener(this);
        this.findViewById(R.id.skip_verify_btn).setOnClickListener(this);


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
            case R.id.go_verify_btn:
                UiUtils.startActivity(VerifyMidActivity.this,VerifyDetailActivity.class,true);
                break;
            case R.id.skip_verify_btn:
                UiUtils.startActivity(VerifyMidActivity.this,MainActivity.class,true);
                break;
            default:
                super.onClick(v);
                break;
        }

    }

}
