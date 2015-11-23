package cn.walkpos.wpospad.money;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
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
import cn.walkpos.wpospad.main.WPosApplication;
import cn.walkpos.wpospad.module.BCardModule;
import cn.walkpos.wpospad.ui.VerifyCodeDialog;
import cn.walkpos.wpospad.util.WPosConfig;


public class MoneyManageActivity extends BaseActivity implements DrawerLayout.DrawerListener,
        OnSuccessListener<JSONObject>{

    private ImageLoader    mImgLoader;
    private Ajax           mAjax;
    private DrawerLayout   cardDrawer;

    //left 卡片添加
    private LinearLayout   leftLayout;
    private EditText       accountNamev;
    private EditText       accountCodev;
    private EditText       accountPhonev;
    private CheckBox       accountAgreev;


//    right  卡片管理
    private TextView       cardDelBtn;
    private LinearLayout   rightLayout;
    private ListView       cardListV;
    private BandcardAdapter cardAdapter;
    private BCardModule     curCard;

    private ArrayList<BCardModule>  cardArray;

    private TextView incomeTotalv;
    private EditText transferAmountv;

    private TextView accountInfov;
    private VerifyCodeDialog verifyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent ait = getIntent();
        if(ait == null || null==WPosApplication.account)
        {
            finish();
            return;
        }

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, WPosApplication.globalMDCache);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_money);

        loadNavBar(R.id.money_manage_nav);

        cardDrawer = (DrawerLayout)this.findViewById(R.id.money_drawer);
        cardDrawer.closeDrawers();

        //main part
        incomeTotalv = (TextView)this.findViewById(R.id.income_total);
        this.findViewById(R.id.check_detail).setOnClickListener(this);
        transferAmountv = (EditText)this.findViewById(R.id.amount);

        accountInfov = (TextView)this.findViewById(R.id.account_info);
        accountInfov.setOnClickListener(this);
        this.findViewById(R.id.open_cardraw_btn).setOnClickListener(this);
        this.findViewById(R.id.money_transfer_btn).setOnClickListener(this);

        //left
        leftLayout = (LinearLayout)this.findViewById(R.id.bind_card_layout);
        accountNamev = (EditText)this.findViewById(R.id.account_name);
        accountCodev = (EditText)this.findViewById(R.id.account_code);
        accountPhonev = (EditText)this.findViewById(R.id.account_phone);
        accountAgreev = (CheckBox)this.findViewById(R.id.agree_btn);
        this.findViewById(R.id.agreement_info).setOnClickListener(this);
        this.findViewById(R.id.bind_account_btn).setOnClickListener(this);


        //right
        cardDelBtn = (TextView)this.findViewById(R.id.card_del);
        cardDelBtn.setOnClickListener(this);
        rightLayout = (LinearLayout)this.findViewById(R.id.manage_card_layout);
        cardAdapter = new BandcardAdapter();


        cardListV = (ListView)this.findViewById(R.id.card_listv);
        cardListV.setAdapter(cardAdapter);
        cardListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BCardModule pcard = cardArray.get(position);
                if(TextUtils.isEmpty(pcard.bank_card))
                {
                    if(!cardDrawer.isDrawerVisible(leftLayout))
                    {
                        cardDrawer.openDrawer(leftLayout);
                    }
                    cardDrawer.closeDrawer(rightLayout);
                    UiUtils.makeToast(MoneyManageActivity.this, "增加银行卡对话框");
                    return;
                }
                cardAdapter.notifyDataSetChanged();
                curCard = pcard;
                String info = curCard.account_bank;
                String code = curCard.bank_card;
                cardAdapter.setChooseId(curCard.bank_card);
                info = info + " " + code.substring(0,4) + " **** **** " + code.substring(12);
                accountInfov.setText(info);
                cardDrawer.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        cardDrawer.closeDrawers();
                    }
                },500);

            }
        });

        cardArray = new ArrayList<BCardModule>();
        loadCardData();

    }


    /**
     *
     */
    private void loadCardData() {
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_BUSINESS);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_BINDED_CARDS);
        mAjax.setData("method", "businescenter.bindquery");
        mAjax.setData("card_number", "320911198912046021X");
//        mAjax.setData("card_number", WPosApplication.account.card_number);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();

    }

    private void delCardItem(int pos)
    {
        BCardModule card = cardArray.get(pos);
        if(card.bank_card.equals(cardAdapter.chooseId))
        {
            accountInfov.setText("");
        }
        cardArray.remove(pos);
        cardAdapter.notifyDataSetChanged();
    }

    private void bindNewCard()
    {
        BCardModule card = new BCardModule();
        Random rd = new Random();

        card.bank_card = accountCodev.getText().toString();
        if(TextUtils.isEmpty(card.bank_card))
        {
            UiUtils.makeToast(this,"卡号不能为空");
            return;
        }
        card.usrname = accountNamev.getText().toString();
        if(TextUtils.isEmpty(card.usrname))
        {
            UiUtils.makeToast(this,"持卡人姓名不能为空");
            return;
        }

        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_BUSINESS);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_BIND_NEW_CARD);
        mAjax.setData("method", "businescenter.bindcard");
        mAjax.setData("card_number", "320911198912046021X");
//        mAjax.setData("card_number", WPosApplication.account.card_number);


        mAjax.setData("bank_card", card.bank_card);
        mAjax.setData("account_bank", card.account_bank);
        mAjax.setData("user", card.usrname);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {
            case R.id.account_info:
            case R.id.open_cardraw_btn:
                if(!cardDrawer.isDrawerVisible(rightLayout)) {
                    cardDrawer.openDrawer(rightLayout);
                    cardAdapter.bDelstat = false;
                    cardAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.check_detail:
                UiUtils.makeToast(this,"查看资金明细");
                break;
            case R.id.money_transfer_btn:
                if(null == verifyDialog)
                {
                    verifyDialog = new VerifyCodeDialog(this,new VerifyCodeDialog.VerifyResultListener() {
                        @Override
                        public boolean onVerifyDialogDismiss(boolean result) {
                            if(result)
                                UiUtils.makeToast(MoneyManageActivity.this,"验证短信成功转移资金成功");
                            else
                                UiUtils.makeToast(MoneyManageActivity.this,"验证短信失败，无法转移资金");
                            return false;
                        }
                    });
                    verifyDialog.setProperty("验证码校验","信息已经发往您在银行绑定的手机",WPosApplication.account.mobile,"","");
                }
                verifyDialog.show();

                break;

//            right
            case R.id.card_del:
                cardDelBtn.setText(cardAdapter.bDelstat ? R.string.btn_delete :R.string.btn_done);
                cardAdapter.bDelstat = !cardAdapter.bDelstat;
                cardAdapter.notifyDataSetChanged();
                break;
            //left
            case R.id.agreement_info:
                UiUtils.makeToast(this,"展示服务协议");
                break;
            case R.id.bind_account_btn:
                bindNewCard();
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

        if(response.getId() == WPosConfig.REQ_BINDED_CARDS) {
            JSONObject data = jsonObject.optJSONObject("data");
            if (null == data)
                return;

            JSONArray array = data.optJSONArray("list");
            BCardModule cm;
            if(array==null || array.length()<=0)
                return;

            cardArray.clear();
            for (int i = 0; null != array && i < array.length(); i++) {
                cm = new BCardModule();
                cm.parse(array.optJSONObject(i));
                cardArray.add(cm);
            }

            //last empty card to add
            cm = new BCardModule();
            cardArray.add(cm);

            cardAdapter.notifyDataSetChanged();
        }
        else if(response.getId() == WPosConfig.REQ_BIND_NEW_CARD) {

            cardDrawer.closeDrawer(leftLayout);
            loadCardData();

            cardDrawer.openDrawer(rightLayout);
        }
    }


    @Override
    public void onBackPressed()
    {
        if(cardDrawer.isDrawerVisible(leftLayout) || cardDrawer.isDrawerVisible(rightLayout))
            cardDrawer.closeDrawers();
        else if(verifyDialog!=null && verifyDialog.isShowing())
            verifyDialog.dismiss();
        else
            super.onBackPressed();
    }

//    银行账户 adapter
    public class BandcardAdapter extends BaseAdapter {

        public boolean  bDelstat = false;
        public void setDelstat(boolean flag){bDelstat = flag;}
        public String  chooseId = "";
        public void setChooseId(String id){chooseId = id;}
        @Override
        public int getCount() {
            return (null == cardArray ? 0 : cardArray.size());
        }

        @Override
        public Object getItem(int position) {
            return (null == cardArray ? null : cardArray.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ItemHolder holder = null;


            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.item_bcard, null);
                holder = new ItemHolder();
                holder.pickedv = (ImageView)convertView.findViewById(R.id.card_check);
                holder.mainlayout = (RelativeLayout)convertView.findViewById(R.id.main_layout);
                holder.iconlayout = (RelativeLayout)convertView.findViewById(R.id.bank_icon_layout);
                holder.iconv = (NetworkImageView)convertView.findViewById(R.id.bank_icon);
                holder.banknamev = (TextView)convertView.findViewById(R.id.bank_name);
                holder.cardtypev = (TextView)convertView.findViewById(R.id.card_type);
                holder.codev = (TextView)convertView.findViewById(R.id.card_code);
                holder.delv = (ImageView)convertView.findViewById(R.id.del_card);
                holder.delv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = v.getTag();
                        if(null!=obj && obj instanceof Integer)
                        {
                            delCardItem((Integer)obj);
                        }
                    }
                });
                holder.addv = (ImageView)convertView.findViewById(R.id.add_img);
                convertView.setTag(holder);
            } else {
                holder = (ItemHolder) convertView.getTag();
            }


            BCardModule card = cardArray.get(position);
            boolean newadd = TextUtils.isEmpty(card.bank_card);
            holder.addv.setVisibility(newadd ? View.VISIBLE : View.GONE);
            holder.iconlayout.setVisibility(newadd ? View.INVISIBLE : View.VISIBLE);
            holder.iconv.setVisibility(newadd ? View.INVISIBLE : View.VISIBLE);
            holder.banknamev.setVisibility(newadd ? View.INVISIBLE : View.VISIBLE);
            holder.cardtypev.setVisibility(newadd ? View.INVISIBLE : View.VISIBLE);
            holder.codev.setVisibility(newadd ? View.INVISIBLE : View.VISIBLE);
            holder.delv.setVisibility( (bDelstat && !newadd) ? View.VISIBLE : View.INVISIBLE);

            holder.pickedv.setVisibility((!newadd && chooseId.equals(card.bank_card)) ? View.VISIBLE : View.INVISIBLE);

            if(newadd)
                holder.mainlayout.setBackgroundResource(R.mipmap.dash_frame);
            else {
                if (position % 3 == 0)
                    holder.mainlayout.setBackgroundResource(R.drawable.card_wpos_shape_1);
                else if (position % 3 == 1)
                    holder.mainlayout.setBackgroundResource(R.drawable.card_wpos_shape_2);
                else
                    holder.mainlayout.setBackgroundResource(R.drawable.card_wpos_shape_3);
            }

            if(!TextUtils.isEmpty(card.iconurl))
                holder.iconv.setImageUrl(card.iconurl,mImgLoader);
            holder.banknamev.setText(card.account_bank);
            holder.cardtypev.setText(card.cardtype);
            holder.codev.setText(card.bank_card);
            holder.delv.setTag(position);


            return convertView;
        }

    }
    public class ItemHolder {
        public RelativeLayout    mainlayout;
        public RelativeLayout    iconlayout;
        public NetworkImageView  iconv;
        public TextView banknamev;
        public TextView cardtypev;
        public TextView codev;
        public ImageView delv;

        public ImageView pickedv;
        public ImageView addv;
    }

}
