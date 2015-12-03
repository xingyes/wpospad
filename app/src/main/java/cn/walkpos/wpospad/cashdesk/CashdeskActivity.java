package cn.walkpos.wpospad.cashdesk;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.walktech.mposlib.mposService;
import com.xingy.lib.IPageCache;
import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.ServiceConfig;
import com.xingy.util.StringUtil;
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
import cn.walkpos.wpospad.main.SettingActivity;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.GoodsModule;
import cn.walkpos.wpospad.ui.CardPayDialog;
import cn.walkpos.wpospad.ui.CashPayDialog;
import cn.walkpos.wpospad.ui.FlingDown2GoneLayout;
import cn.walkpos.wpospad.ui.InStockDialog;
import cn.walkpos.wpospad.ui.OtherPayDialog;
import cn.walkpos.wpospad.util.WPosConfig;
import cn.walkpos.wpospad.zxing.android.CaptureActivity;
import cn.walkpos.wpospad.zxing.android.Intents;


public class CashdeskActivity extends BaseActivity implements OnSuccessListener<JSONObject>,
                BuyProAdapter.InfoChangedListener {

    private Ajax mAjax;

    private RecyclerView cateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateItemModule curRootCateItem;//返回是  当前分类置空
    private CateItemModule curSubCateItem;//返回是  当前分类置空

    private int curCatePos;
    private TextView backCaterootv;
    private CashCateAdapter cateAdapter;


    private FlingDown2GoneLayout goodsPaneLayout;
    private EditText searchInputV;
    private int pageno = 0;
    private int ajaxPageno;
    private static final int PAGE_SZ = 27;
    private boolean allFetched = false;
    private RecyclerView goodsListV;
    private GridLayoutManager proGridManager;
    private ProBtnAdapter proBtnAdapter;
    private TextView noproHintv;
    private ArrayList<GoodsModule> proArray;


    private android.widget.CheckBox allCheckV;
    private RecyclerView buyListV;
    private BuyProAdapter buyAdapter;

    private TextView billTotalv;
    private TextView billDiscountv;
    private TextView incomeTotalv;
    private Handler mHandler = new Handler();
    private Runnable updateIncomeRunnable = new Runnable() {
        @Override
        public void run() {
            if (null != buyAdapter)
                updateBillTotal(buyAdapter.getTotalPrice());
        }
    };

    private static final int CHECK_WX_DELAY = 4000;
    private Runnable checkWxSecretPay = new Runnable() {
        @Override
        public void run() {

            UiUtils.makeToast(CashdeskActivity.this, "等待微信用户扫码/输入支付密码");

            mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
            if (null == mAjax)
                return;

            mAjax.setId(WPosConfig.REQ_WX_ORDER_QUERY);
            mAjax.setData("method", "order.query");
            mAjax.setData("order_id", orderId);
            mAjax.setOnSuccessListener(CashdeskActivity.this);
            mAjax.send();

        }
    };
    private CashPayDialog payCashDialog;
    private CardPayDialog payCardDialog;
//    private mposService mmposService;
    private OtherPayDialog payOtherDialog;
    private InStockDialog payQuickDialog;
    private InStockDialog payDiscountDialog;

    private double totalAmount;
    private double totalDis;
    private String orderId;
    private String orderAmount;
    private int orderPayMethod;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashdesk);

        loadNavBar(R.id.cashdesk_nav);
//        mmposService = new mposService(CashdeskActivity.this);
        backCaterootv = (TextView) this.findViewById(R.id.back_cate_level1);
        backCaterootv.setVisibility(View.GONE);
        backCaterootv.setOnClickListener(this);

        this.findViewById(R.id.search_input).setOnClickListener(this);

        //cate
        searchInputV = (EditText) this.findViewById(R.id.search_input);
        searchInputV.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    UiUtils.makeToast(CashdeskActivity.this, "搜索:" + searchInputV.getText().toString());
                }
                return false;
            }
        });
        cateListV = (RecyclerView) this.findViewById(R.id.cate_list);
        if (null == cateGroupArray)
            cateGroupArray = new ArrayList<CateItemModule>();
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        cateListV.setLayoutManager(lm);
        cateListV.setItemAnimator(new DefaultItemAnimator());
        cateAdapter = new CashCateAdapter(this, new CashCateAdapter.ItemClickListener() {
            @Override
            public void onRecyclerItemClick(View v, int pos) {
                if (cateAdapter.isCateroot() || null == curRootCateItem) {
                    curCatePos = pos;
                    curRootCateItem = cateGroupArray.get(pos);
                    curSubCateItem = null;
                    if (curRootCateItem.subCateArray.size() <= 0) {
                        cateAdapter.setPickIdx(pos);
                    } else {
                        cateAdapter.setDataset(curRootCateItem.subCateArray);
                        cateAdapter.setCateroot(false);
                        cateAdapter.setPickIdx(-1);
                        cateListV.scrollToPosition(0);
                        backCaterootv.setVisibility(View.VISIBLE);
                    }
                } else {
                    cateAdapter.setPickIdx(pos);
                    curSubCateItem = curRootCateItem.subCateArray.get(pos);
                }
                cateAdapter.notifyDataSetChanged();
                pageno = 0;
                loadProductPanel(pageno + 1);
            }

            @Override
            public void onRecyclerItemLongClick(View v, int pos) {

            }
        });
        cateListV.setAdapter(cateAdapter);


        //product panel
        goodsPaneLayout = (FlingDown2GoneLayout) this.findViewById(R.id.goods_panel_layout);
        noproHintv = (TextView) this.findViewById(R.id.no_content_hint);
        goodsListV = (RecyclerView) this.findViewById(R.id.pro_panel);
        proGridManager = new GridLayoutManager(this, 3);
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

        goodsPaneLayout.setVisibility(View.GONE);
        proBtnAdapter = new ProBtnAdapter(this, new ProBtnAdapter.ItemClickListener() {
            @Override
            public void onRecyclerItemClick(View v, int pos) {
                GoodsModule item = proArray.get(pos);
                buyAdapter.addBuyItem(item);
                buyAdapter.notifyDataSetChanged();
                updateBillTotal(buyAdapter.getTotalPrice());
            }

            @Override
            public void onRecyclerItemLongClick(View v, int pos) {

            }
        });
        proArray = new ArrayList<GoodsModule>();
        proBtnAdapter.setDataset(proArray);
        goodsListV.setAdapter(proBtnAdapter);

        allCheckV = (android.widget.CheckBox) this.findViewById(R.id.buy_choose);
        allCheckV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    buyAdapter.chooseAll();
                else
                    buyAdapter.chooseNone();
                buyAdapter.notifyDataSetChanged();
                updateBillTotal(buyAdapter.getTotalPrice());
            }
        });
        buyListV = (RecyclerView) this.findViewById(R.id.buy_pro_list);
        buyListV.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        LinearLayoutManager lm2 = new LinearLayoutManager(this);
        lm2.setOrientation(LinearLayoutManager.VERTICAL);
        buyListV.setLayoutManager(lm2);

        buyAdapter = new BuyProAdapter(this, this);
        buyListV.setAdapter(buyAdapter);

        //右侧操作
        billTotalv = (TextView) this.findViewById(R.id.bill_total);
        billDiscountv = (TextView) this.findViewById(R.id.bill_discount);
//        billDiscountv.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                mHandler.removeCallbacks(updateIncomeRunnable);
//                mHandler.postDelayed(updateIncomeRunnable, 1500);
//            }
//        });
        incomeTotalv = (TextView) this.findViewById(R.id.income_total);

        this.findViewById(R.id.pay_bank).setOnClickListener(this);
        this.findViewById(R.id.pay_cash).setOnClickListener(this);
        this.findViewById(R.id.pay_quick).setOnClickListener(this);
        this.findViewById(R.id.pay_discount).setOnClickListener(this);
        this.findViewById(R.id.pay_other).setOnClickListener(this);

        loadCateData(false);


    }


    private void updateBillTotal(double totalPrice) {
        billTotalv.setText("" + totalPrice);

        totalAmount = totalPrice;
        totalDis = 1.0;
        String info = billDiscountv.getText().toString();
        try {
            Double newdis = Double.valueOf(info);
            if (newdis >= 0 && newdis <= 1)
                totalDis = newdis;
        } catch (Exception e) {
            e.printStackTrace();
        }

        incomeTotalv.setText(StringUtil.formatMoney(totalPrice * totalDis));

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

    private void refreshCateData(JSONArray array) {
        if (null != array && array.length() > 0)
            cateGroupArray.clear();
        for (int i = 0; array != null && i < array.length(); i++) {
            CateItemModule cate = new CateItemModule();
            cate.parse(array.optJSONObject(i));
            cateGroupArray.add(cate);
        }
        cateAdapter.setDataset(cateGroupArray);
        if (null == curRootCateItem) {
            curCatePos = 0;
            curRootCateItem = cateGroupArray.get(0);
            curSubCateItem = null;
            cateAdapter.setDataset(curRootCateItem.subCateArray);
            cateAdapter.setCateroot(false);
            cateAdapter.setPickIdx(-1);
            cateListV.scrollToPosition(0);
            backCaterootv.setVisibility(View.VISIBLE);
        }
        cateAdapter.notifyDataSetChanged();
        pageno = 0;
        loadProductPanel(pageno + 1);
    }


    private void loadProductPanel(int apgno) {
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
        mAjax.setData("current", ajaxPageno);
        mAjax.setData("page", PAGE_SZ);

        String cid = "";
        if (null != curSubCateItem && !TextUtils.isEmpty(curSubCateItem.cat_id))
            cid = curSubCateItem.cat_id;
        else if (null != curRootCateItem && !TextUtils.isEmpty(curRootCateItem.cat_id))
            cid = curRootCateItem.cat_id;
        mAjax.setData("cat_id", cid);

        UiUtils.makeToast(this, "Search cateid:" + cid + ",pageno:" + ajaxPageno);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }


    private void createOrder(int method) {
        if (totalAmount <= 0) {
            UiUtils.makeToast(this, "没有订单金额");
            return;
        }
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        orderPayMethod = method;
        mAjax.setId(WPosConfig.REQ_CREATE_ORDER);
        mAjax.setData("method", "order.create");
        mAjax.setData("store_bn", "S55FFA78EC7F56");
//        mAjax.setData("store_bn", WPosApplication.StockBn);
//        mAjax.setData("token","aaa4170ae15df5dbef18edaf0548b1b2");
        mAjax.setData("token", WPosApplication.GToken);

        mAjax.setData("bn", "U56541CB88A79A");
//        mAjax.setData("bn", WPosApplication.account.bn);


        mAjax.setData("total_amount", totalAmount);
        mAjax.setData("discount", totalDis);

        mAjax.setData("goods_info", buyAdapter.getTotalGoodsJsonString());


        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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
            case R.id.pay_cash:
                createOrder(R.id.pay_cash);
                break;
            case R.id.pay_quick:
                if (null == payQuickDialog) {
                    payQuickDialog = new InStockDialog(this, new InStockDialog.WithEditNumClickListener() {
                        @Override
                        public void onDialogClick(int nButtonId, ArrayList<String> array) {
                            if (nButtonId == AppDialog.BUTTON_POSITIVE) {
                                if (null != array && array.size() > 0) {
                                    try {
                                        Double setprice = Double.valueOf(array.get(0));
                                        buyAdapter.clear();
                                        buyAdapter.notifyDataSetChanged();
                                        goodsPaneLayout.setVisibility(View.GONE);

                                        updateBillTotal(setprice);

                                    } catch (Exception e) {

                                    }
                                }
                            } else
                                payQuickDialog.dismiss();
                        }
                    });
                    payQuickDialog.setProperty("快速收款", "", "待收款", "", "", "", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
                payQuickDialog.show();
                break;
            case R.id.pay_discount:
                if (null == payDiscountDialog) {
                    payDiscountDialog = new InStockDialog(this, new InStockDialog.WithEditNumClickListener() {
                        @Override
                        public void onDialogClick(int nButtonId, ArrayList<String> array) {
                            if (nButtonId == AppDialog.BUTTON_POSITIVE) {
                                if (null != array && array.size() > 0) {
                                    try {
                                        Double newdis = Double.valueOf(array.get(0));
                                        if (newdis >= 0 && newdis <= 1) {
                                            billDiscountv.setText(array.get(0));
                                            updateBillTotal(buyAdapter.getTotalPrice());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else
                                payDiscountDialog.dismiss();
                        }
                    });
                    payDiscountDialog.setProperty("整单折扣", "", "折扣", "", "", "", InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
                }
                payDiscountDialog.show();
                break;
            case R.id.pay_other:
                createOrder(R.id.pay_other);
                break;
            case R.id.pay_bank:
                createOrder(R.id.pay_bank);
                break;


            default:
                super.onClick(v);
                break;

        }

    }

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();

        int errno = jsonObject.optInt("response_code", -1);
        if (errno != 0) {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this, msg);
            if (response.getId() == WPosConfig.REQ_WX_ORDER_QUERY) {
                mHandler.postDelayed(checkWxSecretPay, CHECK_WX_DELAY);
            }
            return;
        }

        if (response.getId() == WPosConfig.REQ_LOAD_CATEGORY) {
            JSONArray array = jsonObject.optJSONArray("data");
            if (null == array) {
                String msg = jsonObject.optString("res", getString(R.string.network_error));
                UiUtils.makeToast(this, msg);
                return;
            }
            IPageCache cache = new IPageCache();
            cache.set(CateItemModule.CACHEKEY_CATEGORY, array.toString(), 86400 * 7);
            refreshCateData(array);
        } else if (response.getId() == WPosConfig.REQ_GOODSLIST) {
            JSONObject data = jsonObject.optJSONObject("data");
            if (null == data) {
                String msg = jsonObject.optString("res", getString(R.string.network_error));
                UiUtils.makeToast(this, msg);
                return;
            }
            JSONArray array = data.optJSONArray("list");
            if (ajaxPageno <= 1) // first pg 清空panel
            {
                proArray.clear();
                proBtnAdapter.shiftBgcolor();
            }

            if (null != array && array.length() > 0) {
                for (int i = 0; i < array.length(); i++) {
                    GoodsModule goods = new GoodsModule();
                    goods.parse(array.optJSONObject(i));
                    proArray.add(goods);

                }
                if (array.length() < PAGE_SZ) {
                    allFetched = true;
                } else {
                    pageno++;
                    allFetched = false;
                }
                proBtnAdapter.notifyDataSetChanged();
            } else
                allFetched = true;

            noproHintv.setVisibility(proArray.size() > 0 ? View.INVISIBLE : View.VISIBLE);
        } else if (response.getId() == WPosConfig.REQ_CREATE_ORDER) {

            JSONObject data = jsonObject.optJSONObject("data");
            orderId = data.optString("order_id");
            orderAmount = data.optString("total_amount");

            payWithOrderId();
            return;
        } else if (response.getId() == WPosConfig.REQ_WX_SCAN) {
            JSONObject data = jsonObject.optJSONObject("data");
//            {"data":{"result_code":"P0001","pay_type":"hassecret"},"res":"成功","response_code":"0000"}
            String transaction_id = data.optString("transaction_id");
            String total_fee = data.optString("total_fee");
            String result_code = data.optString("result_code");
            String pay_type = data.optString("pay_type");
            if (result_code.equals("P0001") || pay_type.equals("hassecret")) {
                mHandler.postDelayed(checkWxSecretPay, CHECK_WX_DELAY);
                return;
            }
            UiUtils.makeToast(this, "支付订单流水 " + transaction_id + "\n金额:" + total_fee);

            clearOrder();
            payOtherDialog.dismiss();
        } else if (response.getId() == WPosConfig.REQ_WX_ORDER_QUERY) {
            JSONObject data = jsonObject.optJSONObject("data");
            if (data == null) {
                mHandler.postDelayed(checkWxSecretPay, CHECK_WX_DELAY);
                return;
            }
            int pay_status = data.optInt("pay_status");
            if (pay_status <= 0) //未来支付
            {
                mHandler.postDelayed(checkWxSecretPay, CHECK_WX_DELAY);
                return;
            }
            double payed = data.optDouble("payed");
            double final_amount = data.optDouble("final_amount");
            String payment = data.optString("payment");
            UiUtils.makeToast(this, "支付订单成功 金额:" + payed);

            clearOrder();
            payOtherDialog.dismiss();
        }
    }

    private void clearOrder() {
        buyAdapter.clear();
        buyAdapter.notifyDataSetChanged();
        totalAmount = 0.0;
        totalDis = 1.0;
        billDiscountv.setText("" + totalDis);

        incomeTotalv.setText("" + totalAmount);
        billTotalv.setText("" + totalAmount);

    }

    private void payWithOrderId() {
        switch (orderPayMethod) {
            case R.id.pay_cash:
                if (null == payCashDialog)
                    payCashDialog = new CashPayDialog(CashdeskActivity.this, new CashPayDialog.OnQueryListener() {
                        @Override
                        public void onQuery(String orderid) {
                            clearOrder();
                        }

                        @Override
                        public void onCancel(String orderid) {
                            mHandler.removeCallbacksAndMessages(null);
                            UiUtils.makeToast(CashdeskActivity.this,"主动取消支付");
                        }
                    },orderId, orderAmount);
                else
                    payCashDialog.setPayInfo(orderId, orderAmount);

                payCashDialog.show();
                break;
            case R.id.pay_bank:
                if(null==WPosApplication.GposService ||
                        WPosApplication.GposService.getState()!= mposService.STATE_CONNECTED)
                {
                    Bundle bundle = new Bundle();
                    bundle.putInt(SettingActivity.TAB_IDX,R.id.device_rb);
                    UiUtils.makeToast(CashdeskActivity.this,"请前往绑定MPOS设备");
                    UiUtils.startActivity(CashdeskActivity.this, SettingActivity.class,bundle,true);
                    return;
                }
                if (null == payCardDialog)
                    payCardDialog = new CardPayDialog(CashdeskActivity.this, new CardPayDialog.OnQueryListener() {
                        @Override
                        public void onQuery(String orderid) {
                            clearOrder();

                        }

                        @Override
                        public void onCancel(String orderid) {
                            mHandler.removeCallbacksAndMessages(null);
                            UiUtils.makeToast(CashdeskActivity.this,"主动取消支付");
                        }
                    },orderId, orderAmount);
                else
                    payCardDialog.setPayInfo(orderId, orderAmount);
                payCardDialog.show();
                payCardDialog.startThread();
                break;
            case R.id.pay_other:
                if (null == payOtherDialog)
                    payOtherDialog = new OtherPayDialog(CashdeskActivity.this, new OtherPayDialog.OnQueryListener(){
                        @Override
                        public void onQuery(final String orderid)
                        {
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.postDelayed(checkWxSecretPay, CHECK_WX_DELAY);
                        }

                        @Override
                        public void onCancel(String orderid) {
                            mHandler.removeCallbacksAndMessages(null);
                            UiUtils.makeToast(CashdeskActivity.this,"主动取消支付");
                        }
                    }, orderId, orderAmount);
                else
                    payOtherDialog.setPayInfo(orderId, orderAmount);
                payOtherDialog.show();

        }
    }

    @Override
    public void onInfoChanged(View v, int pos) {
        if (null == buyAdapter)
            return;
        buyAdapter.notifyDataSetChanged();
        updateBillTotal(buyAdapter.getTotalPrice());
    }

    @Override
    public void onInfoRemoved(View v, int pos) {
        if (null == buyAdapter)
            return;
        buyAdapter.notifyDataSetChanged();
        updateBillTotal(buyAdapter.getTotalPrice());
    }


    private void reqWxScan(Intent data) {
        String authcode = data.getStringExtra(Intents.Scan.RESULT);
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_WX_SCAN);
        mAjax.setData("method", "weixinpay.brushcard");
        mAjax.setData("auth_code", authcode);
        mAjax.setData("order_id", orderId);
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();


    }

    @Override
    public void onBackPressed() {
        if (payCardDialog != null && payCardDialog.isShowing())
            payCardDialog.dismiss();
        else if (payCashDialog != null && payCashDialog.isShowing())
            payCashDialog.dismiss();
        else if (payOtherDialog != null && payOtherDialog.isShowing())
            payOtherDialog.dismiss();
        else if (payQuickDialog != null && payQuickDialog.isShowing())
            payQuickDialog.dismiss();
        else if (payDiscountDialog != null && payDiscountDialog.isShowing())
            payDiscountDialog.dismiss();
        else
            super.onBackPressed();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CaptureActivity.REQ_SCAN_CODE) {
            if (resultCode == RESULT_CANCELED || data == null || payOtherDialog == null)
                UiUtils.makeToast(this, "扫码失败");
            else {
                if (payOtherDialog.scanType == OtherPayDialog.ALIPAY_SCAN) {

                } else if (payOtherDialog.scanType == OtherPayDialog.WX_SCAN) {
                    reqWxScan(data);
                }

            }
        } else
            super.onActivityResult(requestCode, resultCode, data);
    }





    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(null!=mHandler)
            mHandler.removeCallbacksAndMessages(null);
    }

}