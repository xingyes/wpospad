package cn.walkpos.wpospad.store;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.xingy.lib.ui.RadioDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateAdapter;
import cn.walkpos.wpospad.adapter.DividerItemDecoration;
import cn.walkpos.wpospad.main.SettingActivity;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.ui.InStockDialog;


public class CategoryManageActivity extends BaseActivity implements InStockDialog.WithEditNumClickListener{


    private RecyclerView   cateRootListV;
    private RecyclerView   subcateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateAdapter cateRootAdapter;
    private CateAdapter subcateAdapter;

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

        loadCateData();

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

        cateRootAdapter = new CateAdapter(this,new CateAdapter.ItemClickListener() {
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
        cateRootAdapter.setDateset(cateGroupArray);
        cateRootAdapter.setPickIdx(0);
        cateRootListV.setAdapter(cateRootAdapter);

        subcateAdapter = new CateAdapter(this,new CateAdapter.ItemClickListener() {
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
        subcateAdapter.setPickIdx(0);
        subcateListV.setAdapter(subcateAdapter);

        if(null ==cateSetDialog)
        {
            cateSetDialog = new InStockDialog(this,this);
        }
        cateSetDialog.setProperty("设置分类","","分类名","","");

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
    public void onDialogClick(int nButtonId, String inputStr) {
        if(cateSetLevel==0)
        {
            if(modifFlag)
                modifyCateItem(cateRootAdapter.getPickidx(),-1,inputStr);
            else
                addCateItem(-1,inputStr);
        }
        else if(cateSetLevel == 1)
        {
            if(modifFlag)
                modifyCateItem(cateRootAdapter.getPickidx(),subcateAdapter.getPickidx(),inputStr);
            else
                addCateItem(cateRootAdapter.getPickidx(), inputStr);
        }

    }



    private void delCateItem(int rootCateIdx, int subidx)
    {
        CateItemModule rootcate = cateGroupArray.get(rootCateIdx);
        if(subidx<0)
        {
            cateGroupArray.remove(rootCateIdx);
            cateRootAdapter.setPickIdx(0);
            cateRootAdapter.notifyDataSetChanged();
        }
        else
        {
            rootcate.subCateArray.remove(subidx);
            subcateAdapter.setPickIdx(0);
            subcateAdapter.notifyDataSetChanged();
        }
    }

    private void modifyCateItem(int rootCateIdx, int subidx, final String cateName)
    {
        CateItemModule rootcate = cateGroupArray.get(rootCateIdx);
        if(subidx<0)
        {
            rootcate.name = cateName;
            cateRootAdapter.notifyDataSetChanged();
        }
        else
        {
            CateItemModule cate = rootcate.subCateArray.get(subidx);
            cate.name = cateName;
            subcateAdapter.notifyDataSetChanged();
        }
    }

    /**
     *
     * @param rootCateIdx
     * @param cateName
     */
    private void addCateItem(int rootCateIdx, final String cateName)
    {
        if(rootCateIdx>=0) {
            CateItemModule rootcate = cateGroupArray.get(rootCateIdx);

            CateItemModule cate = new CateItemModule();
            cate.name = cateName;
            rootcate.subCateArray.add(0, cate);

            subcateAdapter.setDateset(rootcate.subCateArray);
            subcateAdapter.setPickIdx(0);
            subcateAdapter.notifyDataSetChanged();
        }
        else
        {
            CateItemModule  cate = new CateItemModule();
            cate.name = cateName;
            cateGroupArray.add(0,cate);
            cateRootAdapter.setPickIdx(0);
            cateRootAdapter.notifyDataSetChanged();

            subcateAdapter.setDateset(cate.subCateArray);
            subcateAdapter.setPickIdx(0);
            subcateAdapter.notifyDataSetChanged();
        }

    }
}
