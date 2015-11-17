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


public class LoginActivity extends BaseActivity implements OnSuccessListener<JSONObject>{

    public static final String STORAGE_ACCOUNT_ARRAY = "account_array";
    private Ajax     mAjax;
    private ListView accountListV;
    private ImageView     showAccountBtn;
    private View     delCurAccountBtn;
    private EditText accountEt;
    private EditText passwdEt;

    private ArrayList<String> accountArray;
    private AccountAdapter    accountAdapter;
    private TextView loginBtn;
    private WposAccount account;
    private String     mPhonestr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        WPosApplication.start();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountListV =(ListView)this.findViewById(R.id.account_list_v);
        accountListV.setVisibility(View.GONE);

        loginBtn = (TextView)this.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        accountEt = (EditText)this.findViewById(R.id.account);
        passwdEt = (EditText)findViewById(R.id.password);

        this.findViewById(R.id.forget_passwd).setOnClickListener(this);
        this.findViewById(R.id.register).setOnClickListener(this);
        accountEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (delCurAccountBtn.getVisibility() == View.VISIBLE && s.toString().length() <= 0)
                    delCurAccountBtn.setVisibility(View.INVISIBLE);
                else if (delCurAccountBtn.getVisibility() != View.VISIBLE && s.toString().length() > 0)
                    delCurAccountBtn.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {


            }
        });
        delCurAccountBtn = this.findViewById(R.id.del_account);
        delCurAccountBtn.setVisibility(View.GONE);
        showAccountBtn = (ImageView)this.findViewById(R.id.show_account_list_btn);
        showAccountBtn.setOnClickListener(this);
        delCurAccountBtn.setOnClickListener(this);

        loadHisAccount();

        accountAdapter = new AccountAdapter(this,new AccountAdapter.DelListener() {
            @Override
            public void onDelItem(int pos) {
                accountArray.remove(pos);
                String accountArrayStr = "";
                for(String name : accountArray)
                {
                    if(TextUtils.isEmpty(accountArrayStr))
                        accountArrayStr = name;
                    else
                        accountArrayStr += "," + name;
                }
                AppStorage.setData(STORAGE_ACCOUNT_ARRAY, accountArrayStr, true);
                refreshAccountList();
            }
        });
        accountListV.setAdapter(accountAdapter);
        accountListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                accountEt.requestFocus();
                accountEt.setText("");
                accountEt.append(accountArray.get(position));
                accountListV.setVisibility(View.GONE);
            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();
        refreshAccountList();

        accountEt.setText("");
        passwdEt.setText("");
        UiUtils.makeToast(this,"demo:1qazxsw2");
        accountEt.append("18016036868");
        passwdEt.append("123456m");
    }


    /**
     *
     */
    private void loadHisAccount()
    {
        accountArray = new ArrayList<String>();

        String accountArrayStr = AppStorage.getData(STORAGE_ACCOUNT_ARRAY);
        if(!TextUtils.isEmpty(accountArrayStr))
        {
            String items[] = accountArrayStr.split(",");
            if(null!=items && items.length>0)
            {
                for(String name: items)
                    accountArray.add(name);
            }
        }
    }


    private void refreshAccountList()
    {
        showAccountBtn.setVisibility(accountArray.size()<=0 ? View.INVISIBLE : View.VISIBLE);
        accountAdapter.setData(accountArray);
        accountAdapter.notifyDataSetChanged();
    }


    private void loginAccount()
    {
        mPhonestr = accountEt.getText().toString();
        if(TextUtils.isEmpty(mPhonestr))
        {
            UiUtils.makeToast(this,"用户名不能为空");
            return;
        }
        String passstr = passwdEt.getText().toString();
        if(TextUtils.isEmpty(passstr))
        {
            UiUtils.makeToast(this,"密码不能为空");
            return;
        }
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if(mAjax == null)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_LOGIN);
        mAjax.setData("method", "passport.login");
        mAjax.setData("login_name",mPhonestr);
        mAjax.setData("password",passstr);
        mAjax.setData("mpos_serial","");
        mAjax.setData("imei", ToolUtil.getDeviceUid(this));

        mAjax.setOnSuccessListener(this);
        mAjax.send();
    }

    @Override
    public void onClick(View v)
    {
        Bundle  bundle = null;
        switch (v.getId()) {
            case R.id.register:
                bundle = new Bundle();
                bundle.putInt(RegisterActivity.REGISTER_TYPE, RegisterActivity.TYPE_REGISTER_NEW);
                UiUtils.startActivity(LoginActivity.this,RegisterActivity.class,bundle,true);
                finish();
                break;
            case R.id.forget_passwd:
                bundle = new Bundle();
                bundle.putInt(RegisterActivity.REGISTER_TYPE, RegisterActivity.TYPE_RESET_FORGET);
                UiUtils.startActivity(LoginActivity.this,RegisterActivity.class,bundle,true);

                break;
            case R.id.del_account:
                accountEt.setText("");
                break;
            case R.id.show_account_list_btn:
                accountListV.setVisibility(accountListV.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                showAccountBtn.setImageResource(accountListV.getVisibility() == View.VISIBLE ?
                        R.mipmap.icon_arrow_up : R.mipmap.icon_arrow_down);
                break;
            case R.id.login_btn:
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

        if(response.getId() == WPosConfig.REQ_LOGIN)
        {
            JSONObject data = jsonObject.optJSONObject("data");
            WPosApplication.account = new WposAccount();

            WPosApplication.account.parse(data);
            WPosApplication.GToken = WPosApplication.account.token;
            AppStorage.setData(WPosApplication.APPSTORAGE_KEY_TOKEN,WPosApplication.account.token,true);
            if(WPosApplication.account.bSuperAdmin)
                WPosApplication.account.name = mPhonestr;
            if(!WPosApplication.account.bSuperAdmin &&
                    !accountArray.contains(WPosApplication.account.name))
            {
                accountArray.add(0, WPosApplication.account.name);
                String accountArrayStr = "";
                for(String name : accountArray)
                {
                    if(TextUtils.isEmpty(accountArrayStr))
                        accountArrayStr = name;
                    else
                        accountArrayStr += "," + name;
                }
                AppStorage.setData(STORAGE_ACCOUNT_ARRAY, accountArrayStr, true);
                refreshAccountList();
            }
            if(WPosApplication.account.bSuperAdmin)
            {
                UiUtils.startActivity(this,LoginIdentyActivity.class,true);
            }
            else
            {
                UiUtils.startActivity(this, MainActivity.class,true);
                finish();
            }
        }


    }
}
