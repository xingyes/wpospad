package cn.walkpos.wpospad.store;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xingy.lib.IPageCache;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.CheckBox;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateExpandableAdapter;
import cn.walkpos.wpospad.adapter.DividerItemDecoration;
import cn.walkpos.wpospad.adapter.ProInfoAdapter;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.GoodsModule;
import cn.walkpos.wpospad.ui.InStockDialog;
import cn.walkpos.wpospad.util.WPosConfig;


public class StoreManageActivity extends BaseActivity implements DrawerLayout.DrawerListener,
        ProInfoAdapter.ItemClickListener,InStockDialog.WithEditNumClickListener,OnSuccessListener<JSONObject> {

    private Ajax           mAjax;
    private int            pageno = 1;
    private boolean        allFetched = false;
    private static final int pagesize = 10;
    private EditText       searchInputEt;

    //search params
    private String         searchKey;
    private String         searchCateid;
    private static final int  SORT_UP = 0;
    private static final int  SORT_DOWN = 1;
    private String         searchSortKey;
    private int            discountSortValue;
    private int            stockSortValue;
    public static final String SORT_STOCK = "stores";
    public static final String SORT_DISCOUNT = "discount";
    private Drawable      uparrow;
    private Drawable      downarrow;

    private TextView       noProHint;
    private RecyclerView   proListV;
    private LinearLayoutManager proLinearManager;
    private ProInfoAdapter proAdapter;
    private ArrayList<GoodsModule> proArray;


//    分类选择
    private TextView       cateDrawerBtn;
    private DrawerLayout   cateDrawer;
    private ExpandableListView  cateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateExpandableAdapter cateAdapter;

    //排序sortRadioGroup
    private RadioButton    stockSortRb;
    private RadioButton    discountSortRb;

    private CheckBox       stockHintCheck;
    private LinearLayout   norTitleLayout;
    private TextView       batStartBtn;
    private TextView       batCancelBtn;
    private TextView       batChooseAllBtn;
    private TextView       batChooseNoneBtn;
    private TextView       batDelBtn;
    private AppDialog      batDelDialog;

    private InStockDialog mInstockDialog;
    private int           mInstockProIdx;
    private boolean       requesting = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(null == cateGroupArray)
            cateGroupArray = new ArrayList<CateItemModule>();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pro_manage);

        //添加商品
        findViewById(R.id.add_pro_btn).setOnClickListener(this);
        //管理分类
        findViewById(R.id.category_btn).setOnClickListener(this);
        //search
        findViewById(R.id.search_btn).setOnClickListener(this);

        loadNavBar(R.id.pro_manage_nav);
        searchInputEt = (EditText)this.findViewById(R.id.search_input);

        cateDrawerBtn = (TextView)this.findViewById(R.id.cate_drawer_btn);
        cateDrawer = (DrawerLayout)this.findViewById(R.id.cate_list_drawer);
        cateDrawer.closeDrawers();
        cateDrawerBtn.setOnClickListener(this);

        initTitleLayout();

        cateListV = (ExpandableListView)this.findViewById(R.id.cate_expand_list);
        cateAdapter = new CateExpandableAdapter(this,cateGroupArray);
        cateListV.setAdapter(cateAdapter);
        cateListV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                CateItemModule gp = cateGroupArray.get(groupPosition);
                CateItemModule it = gp.subCateArray.get(childPosition);
                searchCateid = it.cat_id;
                allFetched = false;
                pageno = 1;
                searchKey = "";
                loadProData(pageno);
                cateDrawer.closeDrawers();
                return false;
            }
        });

        cateListV.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            private int lastIdx = -1;
            @Override
            public boolean onGroupClick(ExpandableListView parent, final View v, final int groupPosition, long id) {
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
                else
                {
                    searchCateid = gp.cat_id;
                    allFetched = false;
                    pageno = 1;
                    searchKey = "";
                    loadProData(pageno);
                    cateDrawer.closeDrawers();

                }
                return true;
            }
        });

        noProHint = (TextView)this.findViewById(R.id.no_content_hint);
        noProHint.setVisibility(View.VISIBLE);
        proListV = (RecyclerView)this.findViewById(R.id.pro_list);
        proLinearManager = new LinearLayoutManager(this);
        proListV.setLayoutManager(proLinearManager);
        proListV.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        proListV.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    int tail = proLinearManager.findLastVisibleItemPosition();
                    if(tail >= (proArray.size()-1) && !allFetched)
                        loadProData(pageno+1);

                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        proArray = new ArrayList<GoodsModule>();
        proAdapter = new ProInfoAdapter(this,proArray,this);
        proAdapter.setHintCheck(stockHintCheck.isChecked());

        proListV.setAdapter(proAdapter);

        loadCateData(false);

        searchKey = "";
        searchCateid = "";
        loadProData(pageno);

    }

    /**
     * radioGroup + titleLayout
     */
    private void initTitleLayout()
    {
        stockHintCheck = (CheckBox)findViewById(R.id.stock_hint_check);
        stockHintCheck.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChange(Boolean isChecked) {
                if(null!=proAdapter) {
                    proAdapter.setHintCheck(isChecked);
                    proAdapter.notifyDataSetChanged();
                }
            }
        });
        stockHintCheck.setChecked(true);
        stockSortRb = (RadioButton)this.findViewById(R.id.by_stock_opt);
        stockSortRb.setOnClickListener(this);
        discountSortRb = (RadioButton)this.findViewById(R.id.by_discount_opt);
        discountSortRb.setOnClickListener(this);
        stockSortRb.setChecked(true);
        searchSortKey = SORT_STOCK;
        stockSortValue = SORT_UP;
        discountSortValue = SORT_DOWN;
        downarrow = getResources().getDrawable(R.mipmap.icon_arrow_down);
        downarrow.setBounds(0, 0, downarrow.getMinimumWidth(), downarrow.getMinimumHeight());
        uparrow = getResources().getDrawable(R.mipmap.icon_arrow_up);
        uparrow.setBounds(0, 0, uparrow.getMinimumWidth(), uparrow.getMinimumHeight());


        norTitleLayout = (LinearLayout)this.findViewById(R.id.nor_title_layout);
        batStartBtn = (TextView)this.findViewById(R.id.batch_start_btn);
        batCancelBtn = (TextView)this.findViewById(R.id.batch_cancel_btn);
        batDelBtn = (TextView)this.findViewById(R.id.batch_del_btn);
        batChooseAllBtn = (TextView)this.findViewById(R.id.pick_all_btn);
        batChooseNoneBtn = (TextView)this.findViewById(R.id.pick_none_btn);
        batStartBtn.setOnClickListener(this);
        batCancelBtn.setOnClickListener(this);
        batDelBtn.setOnClickListener(this);
        batChooseAllBtn.setOnClickListener(this);
        batChooseNoneBtn.setOnClickListener(this);

        batCancelBtn.setVisibility(View.GONE);
        batDelBtn.setVisibility(View.GONE);
        batChooseAllBtn.setVisibility(View.GONE);
        batChooseNoneBtn.setVisibility(View.GONE);
    }


    /**
     *
     * @param fromNet
     */
    private void loadCateData(boolean fromNet) {
        IPageCache cache = new IPageCache();
        final String catestr = cache.get(CateItemModule.CACHEKEY_CATEGORY);
        if (!fromNet && !TextUtils.isEmpty(catestr)) {
            try {
                JSONArray array = new JSONArray(catestr);
                refreshCateData(array);
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
            if (null == mAjax)
                return;

            showLoadingLayer();

            mAjax.setId(WPosConfig.REQ_LOAD_CATEGORY);
            mAjax.setData("method", "goods.cat");
            mAjax.setData("store_bn", WPosApplication.StockBn);
            mAjax.setOnSuccessListener(this);
            mAjax.setOnErrorListener(this);
            mAjax.send();
        }
    }
    private void refreshCateData(JSONArray array)
    {
        if(null!=array && array.length()>0)
            cateGroupArray.clear();
        for(int i = 0; array!=null && i < array.length(); i++)
        {
            CateItemModule cate = new CateItemModule();
            cate.parse(array.optJSONObject(i));
            cateGroupArray.add(cate);
        }
        cateAdapter.setDataset(cateGroupArray);
        cateAdapter.notifyDataSetChanged();
    }

    /**
     *
     */
    private void loadProData(int pagno)
    {
        if(requesting)
            return;
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        requesting = true;
        mAjax.setId(WPosConfig.REQ_GOODSLIST);
        mAjax.setData("method", "goods.search");
//        mAjax.setData("store", WPosApplication.StockBn);
        mAjax.setData("store_bn", "S55FFA78EC7F56");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("current",pagno);
        mAjax.setData("page",pagesize);

        if(!TextUtils.isEmpty(searchKey))
            mAjax.setData("keywords", searchKey);
        if(!TextUtils.isEmpty(searchCateid))
            mAjax.setData("cat_id", searchCateid);

        String sorttype = "";
        if(searchSortKey.equals(SORT_DISCOUNT))
            sorttype = discountSortValue==0 ? "up" : "down";
        else
            sorttype = stockSortValue==0 ? "up" : "down";

        UiUtils.makeToast(this,"Search Key:" + searchKey + ",cateid:" + searchCateid + ",sort:" +
                searchSortKey+" " + sorttype + ",pageno:" + pagno);

        mAjax.setData(searchSortKey, sorttype);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }


    private void instockProduct(final ArrayList<String>  array){
        if(requesting)
            return;
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        requesting = true;
        mAjax.setId(WPosConfig.REQ_INSTOCK_GOODS);
        mAjax.setData("method", "store.check");
        mAjax.setData("goods_type", "income");
//        mAjax.setData("store", WPosApplication.StockBn);
        mAjax.setData("store_bn", "S55FFA78EC7F56");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("goods_id",proArray.get(mInstockProIdx).goods_id);
        mAjax.setData("num",array.get(0));
        mAjax.setData("cost",array.get(1));

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }
    /**
     *
     */
    private void searchPro()
    {
        searchKey = searchInputEt.getText().toString();
        if(TextUtils.isEmpty(searchKey))
            UiUtils.makeToast(StoreManageActivity.this,"搜索词为空");
        else {
            allFetched = false;
            pageno = 1;
            searchCateid = "";
            loadProData(pageno);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.search_btn:
                searchPro();
                break;
            case R.id.cate_drawer_btn:
                if(cateDrawer.isDrawerVisible(cateListV))
                    cateDrawer.closeDrawer(cateListV);
                else
                    cateDrawer.openDrawer(cateListV);
                break;
            case R.id.by_discount_opt:
                stockSortRb.setChecked(false);
                discountSortRb.setChecked(true);
                if(searchSortKey.equals(SORT_DISCOUNT))
                    discountSortValue = (discountSortValue +1)%2;
                discountSortRb.setCompoundDrawables(null, null, discountSortValue == SORT_UP ? uparrow : downarrow, null);
                searchSortKey = SORT_DISCOUNT;
                pageno = 1;
                allFetched = false;
                loadProData(pageno);
                break;
            case R.id.by_stock_opt:
                stockSortRb.setChecked(true);
                discountSortRb.setChecked(false);
                if(searchSortKey.equals(SORT_STOCK))
                    stockSortValue = (stockSortValue +1)%2;
                stockSortRb.setCompoundDrawables(null, null, stockSortValue == SORT_UP ? uparrow : downarrow, null);
                searchSortKey = SORT_STOCK;
                pageno = 1;
                allFetched = false;
                loadProData(pageno);
                break;
            case R.id.batch_cancel_btn:
                norTitleLayout.setVisibility(View.VISIBLE);
                batCancelBtn.setVisibility(View.GONE);
                batDelBtn.setVisibility(View.GONE);
                batChooseAllBtn.setVisibility(View.GONE);
                batChooseNoneBtn.setVisibility(View.GONE);
                proAdapter.setBatOptMod(false);
                proAdapter.notifyDataSetChanged();
                break;
            case R.id.batch_start_btn:
                norTitleLayout.setVisibility(View.GONE);
                batCancelBtn.setVisibility(View.VISIBLE);
                batDelBtn.setVisibility(View.VISIBLE);
                batChooseAllBtn.setVisibility(View.VISIBLE);
                batChooseNoneBtn.setVisibility(View.VISIBLE);
                proAdapter.setBatOptMod(true);
                proAdapter.notifyDataSetChanged();
                break;
            case R.id.batch_del_btn:
                if(null == batDelDialog)
                {
                    batDelDialog = UiUtils.showDialog(StoreManageActivity.this,R.string.caption_hint,
                            R.string.bat_del_sure,R.string.btn_delete,R.string.btn_cancel,new AppDialog.OnClickListener() {
                                @Override
                                public void onDialogClick(int nButtonId) {
                                    if(nButtonId == AppDialog.BUTTON_POSITIVE)
                                    {
                                        UiUtils.makeToast(StoreManageActivity.this,R.string.del_succ);
                                    }
                                }
                            });
                }
                batDelDialog.show();
                break;
            case R.id.pick_all_btn:
                proAdapter.chooseAll();
                proAdapter.notifyDataSetChanged();
                break;
            case R.id.pick_none_btn:
                proAdapter.chooseNone();
                proAdapter.notifyDataSetChanged();
                break;
            case R.id.category_btn:
                UiUtils.startActivity(this,CategoryManageActivity.class,true);
                break;
            case R.id.add_pro_btn:
                UiUtils.startActivity(this,AddProductActivity.class,true);
                break;
//            case R.id.setting_btn:
//                UiUtils.makeToast(this,"基本设置界面");
//                break;
            default:
                super.onClick(v);
                break;
        }

    }


    @Override
    protected void onDestroy()
    {
        if(null!=batDelDialog && batDelDialog.isShowing())
            batDelDialog.dismiss();
        batDelDialog = null;
        super.onDestroy();
    }

    @Override
    public void onBackPressed()
    {
        if(null!=cateDrawer && cateDrawer.isDrawerVisible(cateListV))
            cateDrawer.closeDrawers();
        super.onBackPressed();;
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

    /**
     * adapter's 进货 click with proId
     * @param position
     */
    @Override
    public void onInStock(int position) {
        mInstockProIdx = position;
        if(null ==mInstockDialog)
        {
            mInstockDialog = new InStockDialog(this,this);
        }
        mInstockDialog.setProperty("进货",proArray.get(mInstockProIdx).name,"进货数量","进货价格","","", InputType.TYPE_CLASS_NUMBER);
        mInstockDialog.show();
    }

    @Override
    public void onRecyclerItemClick(View v,int pos)
    {
        if(pos >= (proArray.size()-1))
            return;
        GoodsModule goods = proArray.get(pos);
        Bundle bundle = new Bundle();
        bundle.putSerializable(AddProductActivity.GOODS_MODEL,goods);
        UiUtils.startActivity(StoreManageActivity.this,AddProductActivity.class,bundle,true);

    }

    @Override
    public void onRecyclerItemLongClick(View v,int pos){}


    /**
     * 进货dialog onclick 确认或者取消
     * @param nButtonId
     * @param strArray
     */
    @Override
    public void onDialogClick(int nButtonId, ArrayList<String> strArray) {
        if(nButtonId == DialogInterface.BUTTON_POSITIVE)
            instockProduct(strArray);

    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();

        requesting = false;
        int errno = jsonObject.optInt("response_code",-1);
        if(errno!=0)
        {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this,msg);
            return;
        }

        if(response.getId()==WPosConfig.REQ_INSTOCK_GOODS)
        {
            String msg = jsonObject.optString("res", getString(R.string.submit_succ));
            UiUtils.makeToast(this,msg);
            return;
        }
        else if(response.getId() == WPosConfig.REQ_GOODSLIST) {
            JSONObject data = jsonObject.optJSONObject("data");
            if (null == data) {
                String msg = jsonObject.optString("res", getString(R.string.network_error));
                UiUtils.makeToast(this, msg);
                return;
            }
            JSONArray array = data.optJSONArray("list");
            if(null!=array && array.length()>0)
            {
                for(int i = 0; i < array.length(); i++) {
                    GoodsModule goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);
                    goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);
                    goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);
                    goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);
                    goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);
                    goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);
                    goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);

                }
                pageno++;
                allFetched = false;
                proAdapter.notifyDataSetChanged();
            }
            else
                allFetched = true;

            noProHint.setVisibility(proArray.size() <=0 ? View.VISIBLE : View.GONE);
        }
    }
}
