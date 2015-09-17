package cn.walkpos.wpospad.login;

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


public class LoginIdentyActivity extends BaseActivity {

    private EditText nameEdt;
    private EditText IDcardEdt;

    private TextView submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_identy);

        nameEdt = (EditText)this.findViewById(R.id.real_name);
        IDcardEdt = (EditText)this.findViewById(R.id.id_card);


        submitBtn = (TextView)this.findViewById(R.id.submit_btn);
        submitBtn.setOnClickListener(this);

    }




    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.submit_btn:
                UiUtils.makeToast(this, "Succ");
                UiUtils.startActivity(this,MainActivity.class,true);
                break;
            default:
                super.onClick(v);
                break;
        }

    }

}
