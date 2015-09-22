package cn.walkpos.wpospad.login;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.AccountAdapter;


public class LoginActivity extends BaseActivity {

    private ListView accountListV;
    private ImageView     showAccountBtn;
    private View     delCurAccountBtn;
    private EditText accountEdt;

    private ArrayList<String> accountArray;
    private AccountAdapter    accountAdapter;
    private TextView loginBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        accountListV =(ListView)this.findViewById(R.id.account_list_v);
        accountListV.setVisibility(View.GONE);

        loginBtn = (TextView)this.findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        accountEdt = (EditText)this.findViewById(R.id.account);
        this.findViewById(R.id.forget_passwd).setOnClickListener(this);
        this.findViewById(R.id.register).setOnClickListener(this);
        accountEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(delCurAccountBtn.getVisibility()==View.VISIBLE && s.toString().length()<=0)
                    delCurAccountBtn.setVisibility(View.INVISIBLE);
                else if(delCurAccountBtn.getVisibility()!=View.VISIBLE && s.toString().length()>0)
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

        accountArray = new ArrayList<String>();

        accountAdapter = new AccountAdapter(this,new AccountAdapter.DelListener() {
            @Override
            public void onDelItem(int pos) {
                accountArray.remove(pos);
                refreshAccountList();
            }
        });
        accountListV.setAdapter(accountAdapter);
        accountListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                accountEdt.requestFocus();
                accountEdt.setText("");
                accountEdt.append(accountArray.get(position));
                accountListV.setVisibility(View.GONE);


            }
        });
    }


    @Override
    public void onResume()
    {
        super.onResume();
        refreshAccountList();
    }


    private void refreshAccountList()
    {
        showAccountBtn.setVisibility(accountArray.size()<=0 ? View.INVISIBLE : View.VISIBLE);
        accountAdapter.setData(accountArray);
        accountAdapter.notifyDataSetChanged();


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
                break;
            case R.id.forget_passwd:
                bundle = new Bundle();
                bundle.putInt(RegisterActivity.REGISTER_TYPE, RegisterActivity.TYPE_RESET_FORGET);
                UiUtils.startActivity(LoginActivity.this,RegisterActivity.class,bundle,true);
                break;
            case R.id.del_account:
                accountEdt.setText("");
                break;
            case R.id.show_account_list_btn:
                accountListV.setVisibility(accountListV.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                showAccountBtn.setImageResource(accountListV.getVisibility() == View.VISIBLE ?
                        R.mipmap.icon_arrow_up : R.mipmap.icon_arrow_down);
                break;
            case R.id.login_btn:
                String actStr = accountEdt.getText().toString();
                if(!TextUtils.isEmpty(actStr) && !accountArray.contains(actStr))
                {
                    accountArray.add(0, actStr);
                }
                UiUtils.startActivity(this,LoginIdentyActivity.class,true);
                break;
            default:
                super.onClick(v);
                break;
        }

    }

}
