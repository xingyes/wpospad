package cn.walkpos.wpospad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.xingy.lib.model.ProvinceModel;
import com.xingy.lib.ui.CheckBox;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashSet;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.ProModule;

/**
 * Created by xingyao on 15-8-27.
 */
public class ProInfoAdapter extends RecyclerView.Adapter<ProInfoAdapter.contHolder>
{
    private HashSet<String> chooseProIdSet;
    private ArrayList<ProModule> prolist;
    private BaseActivity  mActivity;
    private boolean       batOptMod = false;
    public interface InstockListener{
        public void onInStock(int pos);
    }
    private InstockListener stockListener;
    public void chooseAll()
    {
        for(ProModule pmod : prolist)
        {
            chooseProIdSet.add(pmod.code);
        }
    }
    public void chooseNone()
    {
        chooseProIdSet.clear();
    }

    public HashSet<String> getChooseProSet()
    {
        return chooseProIdSet;
    }
    public void setBatOptMod(boolean optmod)
    {
        batOptMod = optmod;
    }
    public ProInfoAdapter(BaseActivity activity,ArrayList<ProModule> alist,InstockListener listener)
    {
        mActivity = activity;
        prolist = alist;
        if(null == chooseProIdSet)
            chooseProIdSet = new HashSet<String>();
        stockListener = listener;
    }

    @Override
    public ProInfoAdapter.contHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mActivity.getLayoutInflater().inflate(R.layout.item_pro, null);
            return new ProInfoAdapter.contHolder(v);
    }

    @Override
    public void onBindViewHolder(ProInfoAdapter.contHolder holder, int position) {
        ProModule pro = prolist.get(position);


        holder.titleV.setText(pro.title);
        holder.priceinV.setText("" + pro.pricein);
        holder.priceoutV.setText("" + pro.pricein);

        holder.stockV.setTag(position);
        holder.chooseV.setTag(pro.code);
        holder.chooseV.setChecked(chooseProIdSet.contains(pro.code));
        if(batOptMod)
        {
            holder.stockV.setVisibility(View.GONE);
            holder.chooseV.setVisibility(View.VISIBLE);
        }
        else{
            holder.stockV.setVisibility(View.VISIBLE);
            holder.chooseV.setVisibility(View.GONE);
        }



    }



    @Override
    public int getItemCount() {
        return (null == prolist ? 0 : prolist.size());
    }



    public class contHolder extends RecyclerView.ViewHolder
    {

        public TextView   categoryV;
        public TextView   codeV;
        public TextView   titleV;
        public TextView   titleSV;
        public TextView   priceinV;
        public TextView   priceoutV;
        public TextView   stockV;
        public TextView   discountV;
        private android.widget.CheckBox  chooseV;

        public contHolder(View itemView) {
            super(itemView);
            titleV = (TextView)itemView.findViewById(R.id.title);
            priceinV = (TextView)itemView.findViewById(R.id.price_in);
            priceoutV = (TextView)itemView.findViewById(R.id.price_out);

            chooseV = (android.widget.CheckBox)itemView.findViewById(R.id.choose);
            stockV = (TextView)itemView.findViewById(R.id.instock);

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
            stockV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    if(null!=obj && obj instanceof Integer ) {
                        if (null != stockListener)
                            stockListener.onInStock((Integer)obj);
                    }
                }
            });
        }
    }


}

