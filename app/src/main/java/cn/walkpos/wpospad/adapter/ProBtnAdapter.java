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
public class ProBtnAdapter extends RecyclerView.Adapter<ProBtnAdapter.contHolder>
{
    private ArrayList<GoodsModule> prolist;
    private BaseActivity  mActivity;
    private static int BgShape[] = {R.drawable.button_goods_1,R.drawable.button_goods_2,R.drawable.button_goods_3};
    int bgcolorIdx = 0;
    public interface ItemClickListener{
        public void onRecyclerItemClick(View v, int pos);
        public void onRecyclerItemLongClick(View v, int pos);
    }
    private ItemClickListener clickListener;

    public ProBtnAdapter(BaseActivity activity,ItemClickListener listener)
    {
        mActivity = activity;
        clickListener = listener;
    }

    public void shiftBgcolor()
    {
        bgcolorIdx = (bgcolorIdx+1)% BgShape.length;
    }
    public void setDataset(ArrayList<GoodsModule> goods)
    {
        prolist = goods;
    }
    @Override
    public ProBtnAdapter.contHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mActivity.getLayoutInflater().inflate(R.layout.item_cash_pro, null);
            return new ProBtnAdapter.contHolder(v);
    }

    @Override
    public void onBindViewHolder(ProBtnAdapter.contHolder holder, int position) {
        GoodsModule pro = null;
        if(position < prolist.size()) {
            pro = prolist.get(position);
            holder.title1.setText(pro.name_s);
        }
        else
            holder.title1.setVisibility(View.INVISIBLE);
        holder.title1.setBackgroundResource(BgShape[bgcolorIdx]);

    }



    @Override
    public int getItemCount() {
        return (null == prolist ? 0 : prolist.size());
    }



    public class contHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener
    {

        public TextView   title1;

        public View       rootv;

        public contHolder(View itemView) {
            super(itemView);
            rootv = itemView;
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            title1 = (TextView)itemView.findViewById(R.id.name1);
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

