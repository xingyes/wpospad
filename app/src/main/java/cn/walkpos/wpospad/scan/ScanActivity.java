package cn.walkpos.wpospad.scan;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateExpandableAdapter;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.store.CategoryManageActivity;


public class ScanActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pro);

        //管理分类

    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            default:
                super.onClick(v);
                break;
        }

    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
