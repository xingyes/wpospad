package cn.walkpos.wpospad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashSet;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.GoodsModule;

/**
 * Created by xingyao on 15-8-27.
 */
public class ProInfoAdapter extends RecyclerView.Adapter<ProInfoAdapter.contHolder>
{
    private boolean    bStockHint = false;
    private HashSet<String> chooseProIdSet;
    private ArrayList<GoodsModule> prolist;
    private BaseActivity  mActivity;
    private boolean       batOptMod = false;

    public interface ItemClickListener{
        public void onRecyclerItemClick(View v,int pos);
        public void onRecyclerItemLongClick(View v,int pos);
        public void onInStock(int pos);
    }
    private ItemClickListener clickListener;

    public void chooseAll()
    {
        for(GoodsModule pmod : prolist)
        {
            chooseProIdSet.add(pmod.goods_id);
        }
    }
    public void chooseNone()
    {
        chooseProIdSet.clear();
    }
    public void setHintCheck(boolean check)
    {
        bStockHint = check;
    }
    public HashSet<String> getChooseProSet()
    {
        return chooseProIdSet;
    }
    public void setBatOptMod(boolean optmod)
    {
        batOptMod = optmod;
    }
    public ProInfoAdapter(BaseActivity activity,ArrayList<GoodsModule> alist,ItemClickListener listener)
    {
        mActivity = activity;
        prolist = alist;
        if(null == chooseProIdSet)
            chooseProIdSet = new HashSet<String>();
        clickListener = listener;
    }

    @Override
    public ProInfoAdapter.contHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mActivity.getLayoutInflater().inflate(R.layout.item_pro, null);
            return new ProInfoAdapter.contHolder(v);
    }

    @Override
    public void onBindViewHolder(ProInfoAdapter.contHolder holder, int position) {
        GoodsModule pro = prolist.get(position);

        holder.codeV.setText(pro.goods_id);
        holder.titleV.setText(pro.name);
        holder.titleSV.setText(pro.name);

        holder.stockV.setText(String.valueOf(pro.stock));

        holder.priceinV.setText(pro.pricein);
        holder.priceoutV.setText(pro.priceout);

        holder.discountV.setText(String.valueOf(pro.discount));
        holder.instockV.setTag(position);
        holder.chooseV.setTag(pro.goods_id);
        holder.chooseV.setChecked(chooseProIdSet.contains(pro.goods_id));
        if(batOptMod)
        {
            holder.instockV.setVisibility(View.GONE);
            holder.chooseV.setVisibility(View.VISIBLE);
        }
        else{
            holder.instockV.setVisibility(View.VISIBLE);
            holder.chooseV.setVisibility(View.GONE);
        }

        holder.stockV.setText(""+pro.stock);
        holder.stockV.setTextColor((bStockHint && pro.down_warn>0 && pro.stock < pro.down_warn) ?
                    mActivity.getResources().getColor(R.color.btn_wpos_red) :
                    mActivity.getResources().getColor(R.color.global_text_gray));

    }



    @Override
    public int getItemCount() {
        return (null == prolist ? 0 : prolist.size());
    }



    public class contHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
    {

        public TextView   categoryV;
        public TextView   codeV;
        public TextView   titleV;
        public TextView   titleSV;
        public TextView   priceinV;
        public TextView   priceoutV;
        public TextView   stockV;
        public TextView   discountV;
        public TextView   instockV;
        public android.widget.CheckBox  chooseV;

        public contHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            categoryV = (TextView)itemView.findViewById(R.id.category);
            codeV = (TextView)itemView.findViewById(R.id.code);
            titleV = (TextView)itemView.findViewById(R.id.title);
            titleSV = (TextView)itemView.findViewById(R.id.title_short);
            priceinV = (TextView)itemView.findViewById(R.id.price_in);
            priceoutV = (TextView)itemView.findViewById(R.id.price_out);
            stockV = (TextView)itemView.findViewById(R.id.stock);
            discountV = (TextView)itemView.findViewById(R.id.discount);

            chooseV = (android.widget.CheckBox)itemView.findViewById(R.id.choose);
            instockV = (TextView)itemView.findViewById(R.id.instock);
            chooseV.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Object obj = buttonView.getTag();
                    if(null!=obj && obj instanceof String )
                    {
                        if(isChecked)
                            chooseProIdSet.add((String)obj);
                        else
                            chooseProIdSet.remove((String) obj);
                    }
                }
            });
            instockV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    if(null!=obj && obj instanceof Integer ) {
                        if (null != clickListener)
                            clickListener.onInStock((Integer)obj);
                    }
                }
            });
        }


        @Override
        public void onClick(View v) {
            if(null!=clickListener)
                clickListener.onRecyclerItemClick(v,getPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            if(null!=clickListener)
                clickListener.onRecyclerItemLongClick(v, getPosition());
            return true;
        }
    }


}

