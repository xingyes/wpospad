package cn.walkpos.wpospad.store;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateAdapter;
import cn.walkpos.wpospad.adapter.DividerItemDecoration;
import cn.walkpos.wpospad.main.SettingActivity;
import cn.walkpos.wpospad.module.CateItemModule;


public class CategoryManageActivity extends BaseActivity{


    private RecyclerView   cateRootListV;
    private RecyclerView   subcateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateAdapter cateRootAdapter;
    private CateAdapter subcateAdapter;

    private CateItemModule curCateItem;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_manage);
        if(null==cateGroupArray)
            cateGroupArray = new ArrayList<CateItemModule>();

        loadCateData();

        loadNavBar(R.id.manage_cate_nav);
        this.findViewById(R.id.add_cate_1).setOnClickListener(this);
        this.findViewById(R.id.add_cate_2).setOnClickListener(this);

        this.findViewById(R.id.add_pro_btn).setOnClickListener(this);
        this.findViewById(R.id.basic_set_btn).setOnClickListener(this);

        cateRootListV = (RecyclerView)this.findViewById(R.id.cateroot_list);
        cateRootListV.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        subcateListV = (RecyclerView)this.findViewById(R.id.subcate_list);
        subcateListV.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        cateRootListV.setLayoutManager(new LinearLayoutManager(this));
        subcateListV.setLayoutManager(new LinearLayoutManager(this));

        cateRootAdapter = new CateAdapter(this,new CateAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                cateRootAdapter.setPickIdx(pos);
                cateRootAdapter.notifyDataSetChanged();
                curCateItem = cateGroupArray.get(pos);
                subcateAdapter.setDateset(curCateItem.subCateArray);
                subcateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View v, int pos) {

            }
        },false);
        cateRootAdapter.setDateset(cateGroupArray);
        cateRootListV.setAdapter(cateRootAdapter);

        subcateAdapter = new CateAdapter(this,new CateAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                subcateAdapter.setPickIdx(pos);
                subcateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View v, int pos) {

            }
        },true);
        subcateListV.setAdapter(subcateAdapter);

    }



    private void loadCateData()
    {
        cateGroupArray.clear();
        for(int i=0; i < 12; i++)
        {
            CateItemModule gp = new CateItemModule();
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

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.add_pro_btn:
                UiUtils.startActivity(this, AddProductActivity.class,true);
                break;
            case R.id.basic_set_btn:
                UiUtils.startActivity(this, SettingActivity.class,true);
                break;
            case R.id.add_cate_1:
                UiUtils.makeToast(this,"增加一级分类");
                break;
            case R.id.add_cate_2:
                UiUtils.makeToast(this,"增加二级分类");
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
