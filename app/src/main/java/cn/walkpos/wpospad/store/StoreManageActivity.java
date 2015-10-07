package cn.walkpos.wpospad.store;

import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateExpandableAdapter;
import cn.walkpos.wpospad.adapter.ProInfoAdapter;
import cn.walkpos.wpospad.module.CateGroupModule;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.ProModule;


public class StoreManageActivity extends BaseActivity implements DrawerLayout.DrawerListener {


    private RecyclerView   proListV;
    private ProInfoAdapter proAdapter;
    private ArrayList<ProModule> proArray;

    private TextView       cateDrawerBtn;
    private DrawerLayout   cateDrawer;
    private ExpandableListView  cateListV;
    private ArrayList<CateGroupModule> cateGroupArray;
    private CateExpandableAdapter cateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(null == cateGroupArray)
            cateGroupArray = new ArrayList<CateGroupModule>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_manage);

        loadNavBar(R.id.pro_manage_nav);


        cateDrawerBtn = (TextView)this.findViewById(R.id.cate_drawer_btn);
        cateDrawer = (DrawerLayout)this.findViewById(R.id.cate_list_drawer);
        cateDrawer.closeDrawers();
        cateDrawerBtn.setOnClickListener(this);

        cateListV = (ExpandableListView)this.findViewById(R.id.cate_expand_list);
        cateAdapter = new CateExpandableAdapter(this,cateGroupArray);
        cateListV.setAdapter(cateAdapter);
        cateListV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                CateGroupModule gp = cateGroupArray.get(groupPosition);
                CateItemModule it = gp.subCateArray.get(childPosition);
                UiUtils.makeToast(StoreManageActivity.this, gp.name + "," + it.name);
                return false;
            }
        });

        cateListV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            private int lastIdx = -1;
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (lastIdx >= 0)
                    parent.collapseGroup(lastIdx);
                parent.smoothScrollToPosition(groupPosition);
                lastIdx = groupPosition;
                CateGroupModule gp = cateGroupArray.get(groupPosition);
                if (gp != null && gp.subCateArray.size() > 0)
                    parent.expandGroup(lastIdx, true);
                else
                    UiUtils.makeToast(StoreManageActivity.this, cateGroupArray.get(groupPosition).name);
                return true;
            }
        });

        proListV = (RecyclerView)this.findViewById(R.id.pro_list);
        proListV.setLayoutManager(new LinearLayoutManager(this));


        proArray = new ArrayList<ProModule>();
        proAdapter = new ProInfoAdapter(this,proArray);
        proListV.setAdapter(proAdapter);

        loadCateData();
        loadProData();

    }

    private void loadCateData()
    {
        cateGroupArray.clear();
        for(int i=0; i < 12; i++)
        {
            CateGroupModule gp = new CateGroupModule();
            gp.name = "一级分类" +i;
            Random rd = new Random();
            int x = rd.nextInt(7);
            for(int j=0; j < x; j++) {
                CateItemModule it = new CateItemModule();
                it.name = "子分类" + j;
                gp.subCateArray.add(it);
            }
            cateGroupArray.add(gp);
        }

        cateAdapter.notifyDataSetChanged();
    }
    private void loadProData()
    {
        for(int i=0; i < 30; i++)
        {
            ProModule item = new ProModule();
            item.title = "商品" + i;
            item.pricein = i;
            item.priceout = i+1;
            proArray.add(item);
        }

        proAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.cate_drawer_btn:
                if(cateDrawer.isDrawerVisible(cateListV))
                    cateDrawer.closeDrawer(cateListV);
                else
                    cateDrawer.openDrawer(cateListV);

                break;
//            case R.id.checkout_btn:
//                UiUtils.makeToast(this,"去结账页面");
//                break;
//            case R.id.pro_btn:
//                UiUtils.makeToast(this,"去管理商品界面");
//                break;
//            case R.id.staff_btn:
//                UiUtils.makeToast(this,"管理员工界面");
//                break;
//            case R.id.statistics_btn:
//                UiUtils.makeToast(this,"统计报表界面");
//                break;
//            case R.id.money_btn:
//                UiUtils.makeToast(this,"金额提现管理界面");
//                break;
//            case R.id.setting_btn:
//                UiUtils.makeToast(this,"基本设置界面");
//                break;
            default:
                super.onClick(v);
                break;
        }

    }


    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {

    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {

    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }
}
