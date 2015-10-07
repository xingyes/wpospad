package cn.walkpos.wpospad.login;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.main.MainActivity;


public class LoginIdentyActivity extends BaseActivity {

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
