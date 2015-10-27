package cn.walkpos.wpospad.cashdesk;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingy.lib.IPageCache;
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

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CashCateAdapter;
import cn.walkpos.wpospad.adapter.ProBtnAdapter;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.GoodsModule;
import cn.walkpos.wpospad.util.WPosConfig;


public class CashdeskActivity extends BaseActivity implements OnSuccessListener<JSONObject>{

    private Ajax          mAjax;

    private RecyclerView  cateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateItemModule            curCateItem;
    private int                       curCatePos;
    private TextView                 backCateroot;
    private CashCateAdapter       cateAdapter;


    private View           proPaneLayout;
    private String         proFilter = "";
    private EditText       searchInputV;
    private int            pageno = 1;
    private boolean        allFetched = false;
    private RecyclerView   proListV;
    private GridLayoutManager proGridManager;
    private ProBtnAdapter  proBtnAdapter;
    private TextView       noproHintv;
    private ArrayList<GoodsModule> proArray;

    private RelativeLayout pickProLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashdesk);

        loadNavBar(R.id.cashdesk_nav);

        backCateroot = (TextView)this.findViewById(R.id.back_cate_level1);
        backCateroot.setVisibility(View.GONE);
        backCateroot.setOnClickListener(this);

        this.findViewById(R.id.search_input).setOnClickListener(this);

        //cate
        searchInputV = (EditText)this.findViewById(R.id.search_input);
        cateListV = (RecyclerView)this.findViewById(R.id.cate_list);
        if(null==cateGroupArray)
            cateGroupArray = new ArrayList<CateItemModule>();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        cateListV.setLayoutManager(lm);
        cateListV.setItemAnimator(new DefaultItemAnimator());
        cateAdapter = new CashCateAdapter(this,new CashCateAdapter.ItemClickListener() {
            @Override
            public void onRecyclerItemClick(View v, int pos) {
                if (null == curCateItem) {
                    pageno = 1;
                    curCatePos = pos;
                    curCateItem = cateGroupArray.get(pos);
                    cateAdapter.setDataset(curCateItem.subCateArray);
                    cateAdapter.setCateroot(false);
                    cateAdapter.notifyDataSetChanged();
                    refreshProducts(curCateItem, pageno);
                    cateListV.scrollToPosition(0);
                    backCateroot.setVisibility(View.VISIBLE);
                } else {
                    pageno = 1;
                    refreshProducts(curCateItem.subCateArray.get(pos), pageno);
                }
            }

            @Override
            public void onRecyclerItemLongClick(View v, int pos) {

            }
        });
        cateListV.setAdapter(cateAdapter);
        loadCateData(false);

        //product panel
        proPaneLayout = this.findViewById(R.id.pro_panel_layout);
        noproHintv = (TextView)this.findViewById(R.id.no_content_hint);
        proListV = (RecyclerView)this.findViewById(R.id.pro_panel);
        proGridManager = new GridLayoutManager(this,3);
        proGridManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        proListV.setLayoutManager(proGridManager);
        proListV.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE)
                {
                    int tail = proGridManager.findLastVisibleItemPosition();
                    if(tail >= (proArray.size()-1) && !allFetched)
                        refreshProducts(null, pageno + 1);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        proBtnAdapter = new ProBtnAdapter(this,new ProBtnAdapter.ItemClickListener() {
            @Override
            public void onRecyclerItemClick(View v, int pos) {

            }

            @Override
            public void onRecyclerItemLongClick(View v, int pos) {

            }
        });
        proArray = new ArrayList<GoodsModule>();
        proBtnAdapter.setDataset(proArray);
        proListV.setAdapter(proBtnAdapter);

        //pick products
        this.findViewById(R.id.to_buy_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    proPaneLayout.setVisibility(View.GONE);
                }
                return false;
            }
        });

    }



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


    private void refreshProducts(CateItemModule module,int pagno)
    {
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();
        proPaneLayout.setVisibility(View.VISIBLE);
        mAjax.setId(WPosConfig.REQ_GOODSLIST);
        mAjax.setData("method", "goods.search");
//        mAjax.setData("store", WPosApplication.StockBn);
        mAjax.setData("store_bn", "S55FFA78EC7F56");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("current",pagno);
        mAjax.setData("page",30);

        String searchKey = searchInputV.getText().toString();
        if(!TextUtils.isEmpty(searchKey))
            mAjax.setData("keywords", searchKey);
        if(null!=module && !TextUtils.isEmpty(module.cat_id))
            mAjax.setData("cat_id", module.cat_id);

        String newfilter = searchKey + module.cat_id;
        if(!proFilter.equals(newfilter))
        {
            proArray.clear();
            noproHintv.setVisibility(View.VISIBLE);
            proBtnAdapter.notifyDataSetChanged();
        }
        proFilter = newfilter;

        UiUtils.makeToast(this,"Search Key:" + searchKey + ",cateid:" + module.cat_id + ",pageno:" + pagno);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_cate_level1:
                curCateItem = null;
                backCateroot.setVisibility(View.GONE);
                cateAdapter.setDataset(cateGroupArray);
                cateAdapter.setCateroot(true);
                cateAdapter.notifyDataSetChanged();
                cateListV.scrollToPosition(curCatePos);
                break;
            case R.id.search_input:
                pageno = 1;
                refreshProducts(null,pageno);
                break;
            default:
                break;

        }
        super.onClick(v);
    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();

        int errno = jsonObject.optInt("response_code",-1);
        if(errno!=0)
        {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this, msg);
            return;
        }

        if(response.getId() == WPosConfig.REQ_LOAD_CATEGORY)
        {
            JSONArray array = jsonObject.optJSONArray("data");
            if (null == array) {
                String msg = jsonObject.optString("res", getString(R.string.network_error));
                UiUtils.makeToast(this, msg);
                return;
            }
            IPageCache cache = new IPageCache();
            cache.set(CateItemModule.CACHEKEY_CATEGORY,array.toString(),86400*7);
            refreshCateData(array);
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
                if(array.length() < 30)
                {
                    allFetched = true;
                }
                else {
                    pageno++;
                    allFetched = false;
                }
                proBtnAdapter.notifyDataSetChanged();
            }
            else
                allFetched = true;

            noproHintv.setVisibility(proArray.size()>0 ? View.INVISIBLE : View.VISIBLE);

        }
    }



}
