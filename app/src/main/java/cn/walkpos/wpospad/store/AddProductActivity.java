package cn.walkpos.wpospad.store;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateExpandableAdapter;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.GoodsModule;
import cn.walkpos.wpospad.util.WPosConfig;
import cn.walkpos.wpospad.zxing.android.CaptureActivity;


public class AddProductActivity extends BaseActivity implements DrawerLayout.DrawerListener,
        OnSuccessListener<JSONObject>{

    public static final String GOODS_MODEL = "goods_model";
    private GoodsModule   editGoods;
    public static final int REQ_SCAN_CODE = 101;
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
    private Ajax           mAjax;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(null == cateGroupArray)
            cateGroupArray = new ArrayList<CateItemModule>();

        Intent ait = getIntent();
        if(ait == null)
        {
            finish();
            return;
        }
        if(ait.hasExtra(GOODS_MODEL))
            editGoods = (GoodsModule)ait.getSerializableExtra(GOODS_MODEL);
        else
            editGoods = null;

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

        this.findViewById(R.id.pro_submit_btn).setOnClickListener(this);

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
                if(lastIdx == groupPosition)
                {
                    parent.collapseGroup(lastIdx);
                    lastIdx = -1;
                    return true;
                }
                lastIdx = groupPosition;
                CateItemModule gp = cateGroupArray.get(groupPosition);
                if (gp != null && gp.subCateArray.size() > 0)
                {
                    parent.expandGroup(lastIdx, true);
                    cateListV.setSelectedGroup(groupPosition);
                }
                else {
                    cateNameV.setText(cateGroupArray.get(groupPosition).name);
                    cateDrawer.closeDrawers();
                }
                return true;
            }
        });

        loadCateData();

        if(null!=editGoods)
        {
            cateNameV.setText("");
            codeEt.setText("");
            nameEt.setText("");
            inPriceEt.setText("");
            nameShortEt.setText("");
            outPriceEt.setText("");
            initStockEt.setText("");
            discountEt.setText("");
            stockHintNumEt.setText("");

            cateNameV.append(editGoods.cateid);
            codeEt.append(editGoods.goods_id);
            nameEt.append(editGoods.name);
            inPriceEt.append(editGoods.pricein);
            nameShortEt.append(editGoods.name);
            outPriceEt.append(editGoods.priceout);
            initStockEt.append(""+editGoods.stock);
            discountEt.append(""+editGoods.discount);
            stockHintNumEt.append(""+editGoods.minstock);
        }
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


        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if(mAjax == null)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_ADD_GOODS);
        mAjax.setData("method", "goods.add");
        mAjax.setData("token", WPosApplication.GToken);
//        mAjax.setData("token", "9e3a41bebf41ef55d492c2451a8b82f6");
//        mAjax.setData("store", WPosApplication.StockBn);
        mAjax.setData("store_bn", "S55FFA78EC7F56");

        mAjax.setData("cat_id",cateNameV.getText().toString());
        mAjax.setData("barcode",codestr);
        mAjax.setData("name",namestr);
        mAjax.setData("cost",inpricestr);
//        mAjax.setData("name",namestr);  简称
        mAjax.setData("price",outpricestr);
        mAjax.setData("store",stockhintNumstr);
        mAjax.setData("discount",discountstr);
        mAjax.setData("down_warn",stockhintNumstr);
        mAjax.setData("imei", ToolUtil.getDeviceUid(this));

        mAjax.setOnSuccessListener(this);
        mAjax.send();
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
                Intent ait = new Intent(AddProductActivity.this, CaptureActivity.class);
                startActivityForResult(ait,REQ_SCAN_CODE);
                break;
            case R.id.pro_submit_btn:
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

        if(response.getId() == WPosConfig.REQ_ADD_GOODS) {
            String msg = jsonObject.optString("res", "添加商品成功");
            UiUtils.makeToast(this, msg);
            finish();
        }
    }
}
