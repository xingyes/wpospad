package cn.walkpos.wpospad.store;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.xingy.lib.IPageCache;
import com.xingy.lib.ui.RadioDialog;
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
import cn.walkpos.wpospad.adapter.CateManagAdapter;
import cn.walkpos.wpospad.adapter.DividerItemDecoration;
import cn.walkpos.wpospad.main.SettingActivity;
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.ui.InStockDialog;
import cn.walkpos.wpospad.util.WPosConfig;


public class CategoryManageActivity extends BaseActivity implements InStockDialog.WithEditNumClickListener,
        OnSuccessListener<JSONObject>{

    private Ajax           mAjax;
    private RecyclerView   cateRootListV;
    private RecyclerView   subcateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateManagAdapter cateRootAdapter;
    private CateManagAdapter subcateAdapter;

    private CateItemModule curCateItem;
    private ImageView setHintV;

    private InStockDialog  cateSetDialog;
    private int            cateSetLevel = -1;
    private String[] listDialogOpt = {"修改","删除"};
    private boolean        modifFlag = false;
    private RadioDialog    rootRaDialog;
    private RadioDialog    subRaDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cate_manage);
        if(null==cateGroupArray)
            cateGroupArray = new ArrayList<CateItemModule>();


        loadNavBar(R.id.manage_cate_nav);
        this.findViewById(R.id.add_cate_1).setOnClickListener(this);
        this.findViewById(R.id.add_cate_2).setOnClickListener(this);
        setHintV = (ImageView)this.findViewById(R.id.set_hint);
        setHintV.setVisibility(View.GONE);

        this.findViewById(R.id.add_pro_btn).setOnClickListener(this);
        this.findViewById(R.id.basic_set_btn).setOnClickListener(this);

        cateRootListV = (RecyclerView)this.findViewById(R.id.cateroot_list);
        cateRootListV.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        subcateListV = (RecyclerView)this.findViewById(R.id.subcate_list);
        subcateListV.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        cateRootListV.setLayoutManager(new LinearLayoutManager(this));
        subcateListV.setLayoutManager(new LinearLayoutManager(this));

        cateRootAdapter = new CateManagAdapter(this,new CateManagAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                cateRootAdapter.setPickIdx(pos);
                cateRootAdapter.notifyDataSetChanged();
                curCateItem = cateGroupArray.get(pos);
                subcateAdapter.setDateset(curCateItem.subCateArray);
                subcateAdapter.setPickIdx(0);
                subcateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View v, int pos) {
                cateSetLevel = 0;
                if(null == rootRaDialog) {
                    rootRaDialog = UiUtils.showListDialog(CategoryManageActivity.this, listDialogOpt,
                            new RadioDialog.OnRadioSelectListener() {
                                @Override
                                public void onRadioItemClick(int which) {
                                    if (which == 0) {
                                        modifFlag = true;
                                        cateSetDialog.show();
                                    } else //del
                                    {
                                        delCateItem(cateRootAdapter.getPickidx(),-1);
                                    }
                                }
                            });
                }
                else {
                    rootRaDialog.setSelection(-1);
                    rootRaDialog.show();
                }

            }
        },false);


        subcateAdapter = new CateManagAdapter(this,new CateManagAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                subcateAdapter.setPickIdx(pos);
                subcateAdapter.notifyDataSetChanged();
            }

            @Override
            public void onItemLongClick(View v, int pos) {
                cateSetLevel = 1;
                if(null == subRaDialog) {
                    subRaDialog = UiUtils.showListDialog(CategoryManageActivity.this, listDialogOpt,
                            new RadioDialog.OnRadioSelectListener() {
                                @Override
                                public void onRadioItemClick(int which) {
                                    if (which == 0) {
                                        modifFlag = true;
                                        cateSetDialog.show();
                                    } else //del
                                    {
                                        delCateItem(cateRootAdapter.getPickidx(),subcateAdapter.getPickidx());
                                    }
                                }
                            });
                }
                else {
                    subRaDialog.setSelection(-1);
                    subRaDialog.show();
                }

            }
        },false);


        if(null ==cateSetDialog)
        {
            cateSetDialog = new InStockDialog(this,this);
        }
        cateSetDialog.setProperty("设置分类","","分类名","","");

        loadCateData(false);

    }


    /**
     *
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
                cateSetLevel = 0;
                modifFlag = false;
                cateSetDialog.show();
                break;
            case R.id.add_cate_2:
                cateSetLevel = 1;
                modifFlag = false;
                cateSetDialog.show();
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
    public void onDialogClick(int nButtonId, ArrayList<String> arrayList) {
        if(null==arrayList || arrayList.size()<=0)
            return;

        if(cateSetLevel==0)
        {
            if(modifFlag)
                modifyCateItem(cateRootAdapter.getPickidx(),-1,arrayList.get(0));
            else
                addCateItem(-1,arrayList.get(0));
        }
        else if(cateSetLevel == 1)
        {
            if(modifFlag)
                modifyCateItem(cateRootAdapter.getPickidx(),subcateAdapter.getPickidx(),arrayList.get(0));
            else
                addCateItem(cateRootAdapter.getPickidx(), arrayList.get(0));
        }

    }



    private void delCateItem(int rootCateIdx, int subidx)
    {

        CateItemModule rootcate = cateGroupArray.get(rootCateIdx);
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_DEL_CATEGORY);
        mAjax.setData("method", "cat.delete");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("store_bn", WPosApplication.StockBn);
        if(subidx<0)
        {
            mAjax.setData("cat_id",rootcate.cat_id);
        }
        else
        {
            CateItemModule sub = rootcate.subCateArray.get(subidx);
            mAjax.setData("cat_id",sub.cat_id);
            mAjax.setData("parent_id",rootcate.cat_id);
        }

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void modifyCateItem(int rootCateIdx, int subidx, final String cateName)
    {
        CateItemModule rootcate = cateGroupArray.get(rootCateIdx);
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_MODIFY_CATEGORY);
        mAjax.setData("method", "cat.update");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("store_bn", WPosApplication.StockBn);
        mAjax.setData("cat_name", cateName);
        if(subidx<0)
        {
            mAjax.setData("cat_id",rootcate.cat_id);
            mAjax.setData("parent_id","0");
        }
        else
        {
            CateItemModule sub = rootcate.subCateArray.get(subidx);
            mAjax.setData("cat_id",sub.cat_id);
            mAjax.setData("parent_id",rootcate.cat_id);
        }

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    /**
     *
     * @param rootCateIdx
     * @param cateName
     */
    private void addCateItem(int rootCateIdx, final String cateName) {

        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_ADD_CATEGORY);
        mAjax.setData("method", "cat.add");
        mAjax.setData("token", WPosApplication.GToken);
        mAjax.setData("store_bn", WPosApplication.StockBn);
        mAjax.setData("cat_name", cateName);
        if (rootCateIdx >= 0) {
            CateItemModule rootcate = cateGroupArray.get(rootCateIdx);
            mAjax.setData("parent_id", rootcate.cat_id);
        }
        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
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
        else if(response.getId() == WPosConfig.REQ_ADD_CATEGORY || response.getId() == WPosConfig.REQ_MODIFY_CATEGORY
                || response.getId() == WPosConfig.REQ_DEL_CATEGORY) {
            loadCateData(true);
        }
    }

    private void refreshCateData(JSONArray array)
    {

        for(int i = 0; array!=null && i < array.length(); i++)
        {
            CateItemModule cate = new CateItemModule();
            cate.parse(array.optJSONObject(i));
            cateGroupArray.add(cate);
        }

        cateRootAdapter.setDateset(cateGroupArray);
        cateRootAdapter.setPickIdx(0);
        curCateItem = cateGroupArray.get(cateRootAdapter.getPickidx());
        cateRootListV.setAdapter(cateRootAdapter);

        subcateAdapter.setDateset(curCateItem.subCateArray);
        subcateAdapter.setPickIdx(0);
        subcateListV.setAdapter(subcateAdapter);
    }
}
