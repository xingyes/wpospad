package cn.walkpos.wpospad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashSet;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.CateItemModule;
import cn.walkpos.wpospad.module.GoodsModule;

/**
 * Created by xingyao on 15-8-27.
 */
public class CashCateAdapter extends RecyclerView.Adapter<CashCateAdapter.contHolder>
{
    private int pickIdx = -1;
    private BaseActivity mActivity;
    private boolean      cateroot = true;
    public void setCateroot(boolean flag){cateroot = flag;}
    public boolean isCateroot(){return cateroot;}
    public interface ItemClickListener{
        public void onRecyclerItemClick(View v,int pos);
        public void onRecyclerItemLongClick(View v,int pos);
    }
    private ItemClickListener clickListener;

    private ArrayList<CateItemModule> dataSet;
    public CashCateAdapter(BaseActivity activity,ItemClickListener listener)
    {
        mActivity = activity;
        clickListener = listener;
    }

    public void setPickIdx(int idx)
    {
        pickIdx = idx;
    }
    public void setDataset(ArrayList<CateItemModule> alist)
    {
        dataSet = alist;
    }
    @Override
    public contHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = mActivity.getLayoutInflater().inflate(R.layout.item_cash_cate, null);
        return new CashCateAdapter.contHolder(v);
    }

    @Override
    public void onBindViewHolder(contHolder holder, int position) {
        CateItemModule cate = dataSet.get(position);

        if(!cateroot) {
            holder.rootv.setBackgroundResource(pickIdx == position ? R.drawable.cash_cate_s : R.drawable.button_whitebg_gray_frame_shape);
            holder.titleV.setTextColor(mActivity.getResources().getColor(pickIdx == position ?
                    R.color.white : R.color.black));
        }
        else // 一级分类
        {
            holder.rootv.setBackgroundResource(pickIdx == position ? R.color.white : R.color.btn_wpos_gray_dark);
            holder.titleV.setTextColor(mActivity.getResources().getColor( pickIdx == position ?
                    R.color.black : R.color.white));
        }

        holder.titleV.setText(cate.cat_name);
    }

    @Override
    public int getItemCount() {
        return (null == dataSet? 0 : dataSet.size());
    }




    public class contHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener {

        public TextView titleV;
        public View rootv;
        public contHolder(View itemView) {
            super(itemView);
            rootv = itemView;
            titleV = (TextView)itemView.findViewById(R.id.name);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(null!=clickListener)
                clickListener.onRecyclerItemClick(v,getPosition());
        }

        @Override
        public boolean onLongClick(View v) {
            if(null!=clickListener)
                clickListener.onRecyclerItemClick(v,getPosition());
            return true;
        }
    }

}