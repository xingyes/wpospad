package cn.walkpos.wpospad.store;

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


public class AddProductActivity extends BaseActivity implements DrawerLayout.DrawerListener{


    private DrawerLayout   cateDrawer;
    private ExpandableListView  cateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateExpandableAdapter cateAdapter;

    private TextView       cateNameV;
    private EditText       codeEt;
    private EditText       nameEt;
    private EditText       inPriceEt;
    private EditText       nameShortEt;
    private EditText       outPriceEt;
    private EditText       initStockEt;
    private EditText       discountEt;
    private EditText       stockHintNumEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(null == cateGroupArray)
            cateGroupArray = new ArrayList<CateItemModule>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pro);

        //管理分类
        findViewById(R.id.category_btn).setOnClickListener(this);

        loadNavBar(R.id.add_pro_nav);

        cateNameV = (TextView)this.findViewById(R.id.cate_info);
        cateNameV.setOnClickListener(this);
        codeEt = (EditText)this.findViewById(R.id.scan_code_info);
        this.findViewById(R.id.scan_code_btn).setOnClickListener(this);
        nameEt = (EditText)this.findViewById(R.id.pro_name);
        inPriceEt = (EditText)this.findViewById(R.id.pro_in_price);
        nameShortEt = (EditText)this.findViewById(R.id.pro_name_short);
        outPriceEt = (EditText)this.findViewById(R.id.pro_out_price);
        initStockEt = (EditText)this.findViewById(R.id.pro_init_stock);
        discountEt = (EditText)this.findViewById(R.id.pro_discount);
        stockHintNumEt = (EditText)this.findViewById(R.id.stock_hint_num);

        this.findViewById(R.id.submit_btn).setOnClickListener(this);

        cateDrawer = (DrawerLayout)this.findViewById(R.id.cate_list_drawer);
        cateDrawer.closeDrawers();


        cateListV = (ExpandableListView)this.findViewById(R.id.cate_expand_list);
        cateAdapter = new CateExpandableAdapter(this,cateGroupArray);
        cateListV.setAdapter(cateAdapter);
        cateListV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                CateItemModule gp = cateGroupArray.get(groupPosition);
                CateItemModule it = gp.subCateArray.get(childPosition);
                cateNameV.setText(gp.name + "," + it.name);
                cateDrawer.closeDrawers();
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
                CateItemModule gp = cateGroupArray.get(groupPosition);
                if (gp != null && gp.subCateArray.size() > 0)
                    parent.expandGroup(lastIdx, true);
                else {
                    cateNameV.setText(cateGroupArray.get(groupPosition).name);
                    cateDrawer.closeDrawers();
                }
                return true;
            }
        });

        loadCateData();
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

        cateAdapter.notifyDataSetChanged();
    }



    private void addProduct()
    {
        String codestr = codeEt.getText().toString();
        if(TextUtils.isEmpty(codestr))
        {
            UiUtils.makeToast(this,"编码不能为空");
            return;
        }
        String namestr = nameEt.getText().toString();
        if(TextUtils.isEmpty(namestr))
        {
            UiUtils.makeToast(this,"商品名字不能为空");
            return;
        }
        String nameShstr = nameShortEt.getText().toString();
        if(TextUtils.isEmpty(nameShstr))
            nameShstr = namestr.substring(0,6);
        String inpricestr = inPriceEt.getText().toString();
        if(TextUtils.isEmpty(inpricestr))
        {
            UiUtils.makeToast(this,"进货价格不能为空");
            return;
        }
        String outpricestr = outPriceEt.getText().toString();
        if(TextUtils.isEmpty(outpricestr))
        {
            UiUtils.makeToast(this,"售价不能为空");
            return;
        }
        String stockstr = initStockEt.getText().toString();
        if(TextUtils.isEmpty(stockstr))
        {
            stockstr = "0";
        }
        String discountstr = discountEt.getText().toString();
        if(TextUtils.isEmpty(discountstr))
        {
            discountstr = "1.0";
        }
        String stockhintNumstr = stockHintNumEt.getText().toString();
        if(TextUtils.isEmpty(stockhintNumstr))
            stockhintNumstr = "100";

        UiUtils.makeToast(this,"Submit succ");
        finish();

    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.cate_info:
                if(cateDrawer.isDrawerVisible(cateListV))
                    cateDrawer.closeDrawer(cateListV);
                else
                    cateDrawer.openDrawer(cateListV);
                break;
            case R.id.scan_code_btn:
                UiUtils.makeToast(this,"开始扫码界面");
                break;
            case R.id.submit_btn:
                addProduct();
                break;
            case R.id.category_btn:
                UiUtils.startActivity(this, CategoryManageActivity.class, true);
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
