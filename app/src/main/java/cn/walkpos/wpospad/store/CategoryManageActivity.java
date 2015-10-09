package cn.walkpos.wpospad.store;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateExpandableAdapter;
import cn.walkpos.wpospad.adapter.ProInfoAdapter;
import cn.walkpos.wpospad.main.StaffManageActivity;
import cn.walkpos.wpospad.module.CateGroupModule;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.ProModule;
import cn.walkpos.wpospad.ui.InStockDialog;


public class CategoryManageActivity extends BaseActivity{




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_manage);
        loadNavBar(R.id.manage_cate_nav);
        this.findViewById(R.id.manage_staff_btn).setOnClickListener(this);
    }


    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.manage_staff_btn:
                UiUtils.startActivity(this, StaffManageActivity.class,true);
                break;
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
