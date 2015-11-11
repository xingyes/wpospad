package cn.walkpos.wpospad.login;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.main.MainActivity;
import cn.walkpos.wpospad.util.WPosConfig;


public class LoginIdentyActivity extends BaseActivity implements OnSuccessListener<JSONObject>{

    private Ajax     mAjax;
    private EditText nameEdt;
    private EditText IDcardEdt;

    private TextView submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_identy);

        loadNavBar(R.id.login_identy_nav);

        nameEdt = (EditText)this.findViewById(R.id.real_name);
        IDcardEdt = (EditText)this.findViewById(R.id.id_card);

        submitBtn = (TextView)this.findViewById(R.id.identy_submit_btn);
        submitBtn.setOnClickListener(this);
    }

    @Override
    public void onResume()
    {
        super.onResume();
        nameEdt.setText("");
        IDcardEdt.setText("");
        nameEdt.append("王小二");
        IDcardEdt.append("310911198912046012");
    }

    private void loginAccount()
    {
        String namestr = nameEdt.getText().toString();
        if(TextUtils.isEmpty(namestr))
        {
            UiUtils.makeToast(this,"请填入真实姓名");
            return;
        }
        String idcardstr = IDcardEdt.getText().toString();
        if(TextUtils.isEmpty(idcardstr))
        {
            UiUtils.makeToast(this,"请填入身份证号码");
            return;
        }
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if(mAjax == null)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_LOGIN_IDENTITY);
        mAjax.setData("method", "passport.userverify");
        mAjax.setData("name",namestr);
        mAjax.setData("card_number",idcardstr);

        mAjax.setOnSuccessListener(this);
        mAjax.send();
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.identy_submit_btn:
                loginAccount();
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

        JSONObject data = jsonObject.optJSONObject("data");
        boolean bstatus = data.optBoolean("status");
        if(bstatus) {
            UiUtils.makeToast(this, jsonObject.optString("res", "校验成功"));
            UiUtils.startActivity(this, MainActivity.class, true);
            finish();
        }
        else
            UiUtils.makeToast(this, jsonObject.optString("res", "校验失败请重新输入"));
    }
}
