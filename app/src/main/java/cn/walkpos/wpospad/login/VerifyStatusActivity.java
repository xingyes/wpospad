package cn.walkpos.wpospad.login;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xingy.lib.AppStorage;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.MyApplication;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.AccountAdapter;
import cn.walkpos.wpospad.main.MainActivity;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.util.WPosConfig;


public class VerifyStatusActivity extends BaseActivity implements OnSuccessListener<JSONObject>{

    private ProgressBar   verifyBar;

    private TextView      userTitleV;
    private TextView      resultTv;
    private TextView      statusTv1;
    private TextView      statusTv2;
    private TextView      statusTv3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_status);

        loadNavBar(R.id.verify_status_nav);
        verifyBar= (ProgressBar)this.findViewById(R.id.verify_pro_bar);
        verifyBar.setMax(100);
        statusTv1 = (TextView)this.findViewById(R.id.status_tv1);
        statusTv2 = (TextView)this.findViewById(R.id.status_tv2);
        statusTv3 = (TextView)this.findViewById(R.id.status_tv3);
        this.findViewById(R.id.modify_verify_btn).setOnClickListener(this);

        resultTv =  (TextView)this.findViewById(R.id.verify_result);
        userTitleV =  (TextView)this.findViewById(R.id.x_user_title);

        verifyBar.setProgress(0);
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
            case R.id.modify_verify_btn:
                int prg = verifyBar.getProgress();
                if(prg>=100)
                {
                    UiUtils.startActivity(this,VerifyDetailActivity.class,true);
                    finish();
                }
                prg += 45;
                if(prg>=100)
                    prg = 100;
                verifyBar.setProgress(prg);
                break;
            default:
                super.onClick(v);
                break;
        }

    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();

        int errno = jsonObject.optInt("response_code",-1);
        if(errno!=0)
        {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this,msg);
            return;
        }




    }
}
