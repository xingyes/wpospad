package cn.walkpos.wpospad.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.CateItemModule;

/**
 * Created by xingyao on 15-8-27.
 */
public class CateAdapter extends RecyclerView.Adapter<CateAdapter.CateViewHolder>
{
    private ArrayList<CateItemModule> cateArray;
    private boolean useCheckIcon = false;
    private int pickidx = -1;
    private BaseActivity  mActivity;
    public interface ItemClickListener{
        public void onItemClick(View v,int pos);
        public void onItemLongClick(View v,int pos);
    }
    private ItemClickListener mListener;
    public void setPickIdx(int pic)
    {
        pickidx = pic;
    }
    public int getPickidx(){return pickidx;}
    public CateAdapter(BaseActivity activity,ItemClickListener listener, boolean flag)
    {
        mActivity = activity;
        mListener = listener;
        useCheckIcon = flag;
    }

    public void setDateset(ArrayList<CateItemModule> list)
    {
        cateArray = list;
    }

    @Override
    public CateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mActivity.getLayoutInflater().inflate(R.layout.item_cate, null);
            return new CateViewHolder(v);
    }

    @Override
    public void onBindViewHolder(CateViewHolder holder, int position) {
        CateItemModule cate = cateArray.get(position);

        if(pickidx == position)
        {
            holder.nameV.setTextColor(mActivity.getResources().getColor(R.color.btn_wpos_red));
            if(useCheckIcon)
                holder.checkedV.setVisibility(View.VISIBLE);
        }
        else {
            holder.nameV.setTextColor(mActivity.getResources().getColor(R.color.global_text_info_color));
            holder.checkedV.setVisibility(View.INVISIBLE);
        }

        holder.nameV.setText(cate.cat_name);
    }



    @Override
    public int getItemCount() {
        return (null == cateArray ? 0 : cateArray.size());
    }



    public class CateViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        public View       rootV;
        public TextView   nameV;
        public ImageView  checkedV;

        public CateViewHolder(View itemView) {
            super(itemView);
            rootV = itemView.findViewById(R.id.root_layout);
            rootV.setOnClickListener(this);
            rootV.setOnLongClickListener(this);
            nameV = (TextView)itemView.findViewById(R.id.name);
            checkedV = (ImageView)itemView.findViewById(R.id.check_icon);
            checkedV.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onClick(View v) {
            pickidx = getPosition();
            if(null!=mListener)
                mListener.onItemClick(v,getPosition());
        }


        @Override
        public boolean onLongClick(View v) {
            pickidx = getPosition();
            if(null!=mListener)
                mListener.onItemLongClick(v, getPosition());
            return true;
        }
    }





}

