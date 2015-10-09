package cn.walkpos.wpospad.store;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.xingy.lib.ui.AppDialog;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Random;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.adapter.CateExpandableAdapter;
import cn.walkpos.wpospad.adapter.ProInfoAdapter;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.ProModule;
import cn.walkpos.wpospad.ui.InStockDialog;


public class StoreManageActivity extends BaseActivity implements DrawerLayout.DrawerListener,
        ProInfoAdapter.InstockListener,InStockDialog.WithEditNumClickListener {


    private RecyclerView   proListV;
    private ProInfoAdapter proAdapter;
    private ArrayList<ProModule> proArray;

//    分类选择
    private TextView       cateDrawerBtn;
    private DrawerLayout   cateDrawer;
    private ExpandableListView  cateListV;
    private ArrayList<CateItemModule> cateGroupArray;
    private CateExpandableAdapter cateAdapter;

    //排序sortRadioGroup
    private RadioGroup     sortRg;
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

        loadNavBar(R.id.pro_manage_nav);


        cateDrawerBtn = (TextView)this.findViewById(R.id.cate_drawer_btn);
        cateDrawer = (DrawerLayout)this.findViewById(R.id.cate_list_drawer);
        cateDrawer.closeDrawers();
        cateDrawerBtn.setOnClickListener(this);


        cateListV = (ExpandableListView)this.findViewById(R.id.cate_expand_list);
        cateAdapter = new CateExpandableAdapter(this,cateGroupArray);
        cateListV.setAdapter(cateAdapter);
        cateListV.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                CateItemModule gp = cateGroupArray.get(groupPosition);
                CateItemModule it = gp.subCateArray.get(childPosition);
                UiUtils.makeToast(StoreManageActivity.this, gp.name + "," + it.name);
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
                else
                    UiUtils.makeToast(StoreManageActivity.this, cateGroupArray.get(groupPosition).name);
                return true;
            }
        });

        proListV = (RecyclerView)this.findViewById(R.id.pro_list);
        proListV.setLayoutManager(new LinearLayoutManager(this));


        proArray = new ArrayList<ProModule>();
        proAdapter = new ProInfoAdapter(this,proArray,this);
        proListV.setAdapter(proAdapter);

        loadCateData();

        initTitleLayout();
        loadProData();

    }

    /**
     * radioGroup + titleLayout
     */
    private void initTitleLayout()
    {
        sortRg = (RadioGroup)this.findViewById(R.id.sort_rg);
        sortRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            private int lastid;
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == lastid)
                    return;

                lastid = checkedId;
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
    private void loadProData()
    {
        for(int i=0; i < 30; i++)
        {
            ProModule item = new ProModule();
            item.code = ""+i;
            item.title = "商品" + i;
            item.pricein = i;
            item.priceout = i+1;
            proArray.add(item);
        }

        proAdapter.notifyDataSetChanged();
    }
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
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
        mInstockDialog.setProperty("进货",proArray.get(mInstockProIdx).title,"","");
        mInstockDialog.show();

    }

    /**
     * 进货dialog onclick 确认或者取消
     * @param nButtonId
     * @param num
     */
    @Override
    public void onDialogClick(int nButtonId, long num) {
        if(nButtonId == DialogInterface.BUTTON_POSITIVE)
            UiUtils.makeToast(this,"进货" + proArray.get(mInstockProIdx).title + ":"+num );
    }
}
