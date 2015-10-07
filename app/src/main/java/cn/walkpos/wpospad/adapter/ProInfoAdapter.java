package cn.walkpos.wpospad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xingy.lib.model.ProvinceModel;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.ProModule;

/**
 * Created by xingyao on 15-8-27.
 */
public class ProInfoAdapter extends RecyclerView.Adapter<ProInfoAdapter.contHolder>
{
    private ArrayList<ProModule> prolist;
    private BaseActivity  mActivity;
    public ProInfoAdapter(BaseActivity activity,ArrayList<ProModule> alist)
    {
        mActivity = activity;
        prolist = alist;
    }

    @Override
    public ProInfoAdapter.contHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mActivity.getLayoutInflater().inflate(R.layout.item_pro, null);
            return new ProInfoAdapter.contHolder(v);
    }

    @Override
    public void onBindViewHolder(ProInfoAdapter.contHolder holder, int position) {
        ProModule pro = prolist.get(position);

//        holder.titleV.setText(pro.title);
//        holder.priceinV.setText("" + pro.pricein);
//        holder.priceoutV.setText("" + pro.pricein);
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

        public contHolder(View itemView) {
            super(itemView);
            titleV = (TextView)itemView.findViewById(R.id.title);
            priceinV = (TextView)itemView.findViewById(R.id.price_in);
            priceoutV = (TextView)itemView.findViewById(R.id.price_out);
        }
    }


}

