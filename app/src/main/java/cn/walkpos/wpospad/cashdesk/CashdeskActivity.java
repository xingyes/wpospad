package cn.walkpos.wpospad.cashdesk;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
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
import cn.walkpos.wpospad.adapter.BuyProAdapter;
import cn.walkpos.wpospad.adapter.CashCateAdapter;
import cn.walkpos.wpospad.adapter.DividerItemDecoration;
import cn.walkpos.wpospad.adapter.ProBtnAdapter;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.GoodsModule;
import cn.walkpos.wpospad.ui.CardPayDialog;
import cn.walkpos.wpospad.ui.CashPayDialog;
import cn.walkpos.wpospad.ui.FlingDown2GoneLayout;
import cn.walkpos.wpospad.ui.InStockDialog;
import cn.walkpos.wpospad.ui.OtherPayDialog;
import cn.walkpos.wpospad.util.WPosConfig;


public class CashdeskActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
                BuyProAdapter.InfoChangedListener{

    private Ajax          mAjax;

    private RecyclerView  cateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateItemModule            curRootCateItem;//返回是  当前分类置空
    private CateItemModule            curSubCateItem;//返回是  当前分类置空

    private int                       curCatePos;
    private TextView                  backCaterootv;
    private CashCateAdapter       cateAdapter;


    private FlingDown2GoneLayout goodsPaneLayout;
    private EditText       searchInputV;
    private int            pageno = 0;
    private int            ajaxPageno;
    private static final int PAGE_SZ = 27;
    private boolean        allFetched = false;
    private RecyclerView   goodsListV;
    private GridLayoutManager proGridManager;
    private ProBtnAdapter  proBtnAdapter;
    private TextView       noproHintv;
    private ArrayList<GoodsModule> proArray;


    private android.widget.CheckBox allCheckV;
    private RecyclerView   buyListV;
    private BuyProAdapter          buyAdapter;

    private TextView       billTotalv;
    private EditText       billDiscountv;
    private TextView       incomeTotalv;
    private Handler mHandler = new Handler();
    private Runnable       updateIncomeRunnable =  new Runnable(){
        @Override
        public void run() {
            updateBillTotal();
        }
    };

    private CashPayDialog payCashDialog;
    private CardPayDialog payCardDialog;
    private OtherPayDialog payOtherDialog;
    private InStockDialog payQuickDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashdesk);

        loadNavBar(R.id.cashdesk_nav);

        backCaterootv = (TextView)this.findViewById(R.id.back_cate_level1);
        backCaterootv.setVisibility(View.GONE);
        backCaterootv.setOnClickListener(this);

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
                if (null == curRootCateItem) {
                    curCatePos = pos;
                    curRootCateItem = cateGroupArray.get(pos);
                    curSubCateItem = null;
                    cateAdapter.setDataset(curRootCateItem.subCateArray);
                    cateAdapter.setCateroot(false);
                    cateAdapter.setPickIdx(-1);
                    cateListV.scrollToPosition(0);
                    backCaterootv.setVisibility(View.VISIBLE);
                } else {
                    cateAdapter.setPickIdx(pos);
                    curSubCateItem = curRootCateItem.subCateArray.get(pos);
                }
                cateAdapter.notifyDataSetChanged();
                pageno = 0;
                loadProductPanel(pageno+1);
            }

            @Override
            public void onRecyclerItemLongClick(View v, int pos) {

            }
        });
        cateListV.setAdapter(cateAdapter);
        loadCateData(false);

        //product panel
        goodsPaneLayout = (FlingDown2GoneLayout)this.findViewById(R.id.goods_panel_layout);
        noproHintv = (TextView)this.findViewById(R.id.no_content_hint);
        goodsListV = (RecyclerView)this.findViewById(R.id.pro_panel);
        proGridManager = new GridLayoutManager(this,3);
        proGridManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        goodsListV.setLayoutManager(proGridManager);
        goodsListV.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int tail = proGridManager.findLastVisibleItemPosition();
                    if (tail >= (proArray.size() - 1) && !allFetched)
                        loadProductPanel(pageno + 1);
                }
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        proBtnAdapter = new ProBtnAdapter(this,new ProBtnAdapter.ItemClickListener() {
            @Override
            public void onRecyclerItemClick(View v, int pos) {
                GoodsModule item = proArray.get(pos);
                buyAdapter.addBuyItem(item);
                buyAdapter.notifyDataSetChanged();
                updateBillTotal();
            }

            @Override
            public void onRecyclerItemLongClick(View v, int pos) {

            }
        });
        proArray = new ArrayList<GoodsModule>();
        proBtnAdapter.setDataset(proArray);
        goodsListV.setAdapter(proBtnAdapter);

        allCheckV = (android.widget.CheckBox)this.findViewById(R.id.buy_choose);
        allCheckV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    buyAdapter.chooseAll();
                else
                    buyAdapter.chooseNone();
                buyAdapter.notifyDataSetChanged();
                updateBillTotal();
            }
        });
        buyListV = (RecyclerView)this.findViewById(R.id.buy_pro_list);
        buyListV.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        LinearLayoutManager lm2 = new LinearLayoutManager(this);
        lm2.setOrientation(LinearLayoutManager.VERTICAL);
        buyListV.setLayoutManager(lm2);

        buyAdapter = new BuyProAdapter(this,this);
        buyListV.setAdapter(buyAdapter);

        //右侧操作
        billTotalv = (TextView)this.findViewById(R.id.bill_total);
        billDiscountv = (EditText)this.findViewById(R.id.bill_discount);
        billDiscountv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mHandler.removeCallbacks(updateIncomeRunnable);
                mHandler.postDelayed(updateIncomeRunnable,1500);
            }
        });
        incomeTotalv = (TextView)this.findViewById(R.id.income_total);

        this.findViewById(R.id.pay_bank).setOnClickListener(this);
        this.findViewById(R.id.pay_cash).setOnClickListener(this);
        this.findViewById(R.id.pay_quick).setOnClickListener(this);
        this.findViewById(R.id.pay_back).setOnClickListener(this);
        this.findViewById(R.id.pay_other).setOnClickListener(this);


    }


    private void updateBillTotal()
    {
        if(null==buyAdapter)
            return;
        buyAdapter.getTotalPrice();
        billTotalv.setText(""+buyAdapter.getTotalPrice());

        double billdis = 1.0;
        String info = billDiscountv.getText().toString();
        try {
            Double newdis = Double.valueOf(info);
            if(newdis >=0 && newdis <=1)
                billdis= newdis;
        }catch (Exception e) {
            e.printStackTrace();
        }

        incomeTotalv.setText(String.format("%.2f",buyAdapter.getTotalPrice()*billdis));
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


    private void loadProductPanel(int apgno)
    {
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        ajaxPageno = apgno;
        goodsPaneLayout.setVisibility(View.VISIBLE);

        mAjax.setId(WPosConfig.REQ_GOODSLIST);
        mAjax.setData("method", "goods.search");
//        mAjax.setData("store", WPosApplication.StockBn);
        mAjax.setData("store_bn", "S55FFA78EC7F56");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("current",ajaxPageno);
        mAjax.setData("page",PAGE_SZ);

        String cid = "";
        if(null!=curSubCateItem && !TextUtils.isEmpty(curSubCateItem.cat_id))
            cid = curSubCateItem.cat_id;
        else if(null!=curRootCateItem && !TextUtils.isEmpty(curRootCateItem.cat_id))
            cid = curRootCateItem.cat_id;
        mAjax.setData("cat_id", cid);

        UiUtils.makeToast(this,"Search cateid:" + cid + ",pageno:" + ajaxPageno);

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
                cateAdapter.setPickIdx(-1);
                curRootCateItem = null;
                curSubCateItem = null;
                backCaterootv.setVisibility(View.GONE);
                cateAdapter.setDataset(cateGroupArray);
                cateAdapter.setCateroot(true);
                cateAdapter.notifyDataSetChanged();
                cateListV.scrollToPosition(curCatePos);
                break;
            case R.id.search_input:
//                pageno = 1;
//                loadProductPanel(pageno);
                break;
            case R.id.pay_bank:
                if(null==payCardDialog)
                {
                    payCardDialog = new CardPayDialog(CashdeskActivity.this,incomeTotalv.getText().toString());
                }
                else
                    payCardDialog.setBill(incomeTotalv.getText().toString());
                payCardDialog.show();
                break;
            case R.id.pay_cash:
                if(null==payCashDialog)
                {
                    payCashDialog = new CashPayDialog(CashdeskActivity.this,incomeTotalv.getText().toString());
                }
                else
                    payCashDialog.setBill(incomeTotalv.getText().toString());
                payCashDialog.show();
                break;
            case R.id.pay_quick:
                if(null==payQuickDialog)
                {
                    payQuickDialog =  new InStockDialog(this,new InStockDialog.WithEditNumClickListener() {
                        @Override
                        public void onDialogClick(int nButtonId, ArrayList<String> array) {
                            if(null!=array && array.size()>0)
                                UiUtils.makeToast(CashdeskActivity.this,array.get(0));
                        }
                    });
                    payQuickDialog.setProperty("快速收款","","待收款","","");
                }
                payQuickDialog.show();
                break;
            case R.id.pay_back:
                UiUtils.makeToast(this,"退款");
                break;
            case R.id.pay_other:
                if(null==payOtherDialog)
                {
                    payOtherDialog = new OtherPayDialog(CashdeskActivity.this,incomeTotalv.getText().toString());
                }
                else
                    payOtherDialog.setBill(incomeTotalv.getText().toString());
                payOtherDialog.show();
                UiUtils.makeToast(this,"其他支付方式");
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
            if(ajaxPageno<=1) // first pg 清空panel
                proArray.clear();

            if(null!=array && array.length()>0)
            {
                for(int i = 0; i < array.length(); i++) {
                    GoodsModule goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);

                }
                if(array.length() < PAGE_SZ)
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


    @Override
    public void onInfoChanged(View v, int pos) {
        UiUtils.makeToast(this,"pos:" + pos + ":改变");
        buyAdapter.notifyDataSetChanged();
        updateBillTotal();
    }

    @Override
    public void onInfoRemoved(View v, int pos) {
        UiUtils.makeToast(this,"pos:" + pos + ":移除");
        buyAdapter.notifyDataSetChanged();
        updateBillTotal();
    }
}
