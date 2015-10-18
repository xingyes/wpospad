package cn.walkpos.wpospad.store;

import android.content.DialogInterface;
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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.CheckBox;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
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
    private int            pageno = 0;
    private boolean        reqFinish = false;
    private static final int pagesize = 10;
    private EditText       searchInputEt;

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
    private RadioGroup     sortRg;
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
                reqFinish = false;
                pageno = 1;
                UiUtils.makeToast(StoreManageActivity.this, "重新请求: " + gp.name + "," + it.name);
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
                    UiUtils.makeToast(StoreManageActivity.this, cateGroupArray.get(groupPosition).name);

                return true;
            }
        });

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
                    if(tail >= (proArray.size()-1) && !reqFinish)
                        loadProData(pageno+1);

                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });


        proArray = new ArrayList<GoodsModule>();
        proAdapter = new ProInfoAdapter(this,proArray,this);
        proAdapter.setHintCheck(stockHintCheck.isChecked());

        proListV.setAdapter(proAdapter);

        loadCateData();


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
        sortRg = (RadioGroup)this.findViewById(R.id.sort_rg);
        sortRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            private int lastid;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == lastid)
                    return;

                lastid = checkedId;
                reqFinish = false;
                pageno = 1;
                UiUtils.makeToast(StoreManageActivity.this,"改变排列顺序，重新请求数据");
            }
        });

        sortRg.check(R.id.by_stock_opt);

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

    /**
     *
     */
    private void loadProData(int pagno)
    {

        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_GOODSLIST);
        mAjax.setData("method", "goods.list");
//        mAjax.setData("store", WPosApplication.StockBn);
        mAjax.setData("store_bn", "S55FFA78EC7F56");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("current",pagno);
        mAjax.setData("page",pagesize);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }


    /**
     *
     */
    private void searchPro()
    {
        String key = searchInputEt.getText().toString();
        if(TextUtils.isEmpty(key))
            UiUtils.makeToast(StoreManageActivity.this,"搜索词为空");
        else {
            reqFinish = false;
            pageno = 1;
            UiUtils.makeToast(StoreManageActivity.this, "搜索:" + key);
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
        mInstockDialog.setProperty("进货",proArray.get(mInstockProIdx).name,"进货数量","","", InputType.TYPE_CLASS_NUMBER);
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
    public void onRecyclerItemLongClick(View v,int pos){

    }


    /**
     * 进货dialog onclick 确认或者取消
     * @param nButtonId
     * @param num
     */
    @Override
    public void onDialogClick(int nButtonId, String num) {
        if(nButtonId == DialogInterface.BUTTON_POSITIVE)
            UiUtils.makeToast(this,"进货" + proArray.get(mInstockProIdx).name + ":"+num );
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

        if(response.getId() == WPosConfig.REQ_GOODSLIST) {
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
                reqFinish = false;
                proAdapter.notifyDataSetChanged();
            }
            else
                reqFinish = true;


        }
    }
}
