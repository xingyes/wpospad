package cn.walkpos.wpospad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;

/**
 * Created by xingyao on 15-8-27.
 */
public class AccountAdapter extends BaseAdapter
{
    private BaseActivity  mActivity;
    private int pickIdx = -1;
    private ArrayList<String> dataSet;
    private LayoutInflater mInflater;
    public interface DelListener
    {
        public void onDelItem(int pos);
    };
    private DelListener  dListener;

    public AccountAdapter(BaseActivity activity,DelListener alistener){
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
        dListener = alistener;
    }

    public int getPick()
    {
        return pickIdx;
    }
    public void setPick(int pick)
    {
        pickIdx = pick;
    }
    public void setData(ArrayList<String> alist) {
        if (null == dataSet)
            dataSet = new ArrayList<String>();
        dataSet.clear();
        dataSet.addAll(alist);
    }

    @Override
    public int getCount() {
        return (null==dataSet ? 0 : dataSet.size());
    }

    @Override
    public Object getItem(int position) {
        return (null==dataSet ? 0 : dataSet.get(position));
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemHolder holder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_account, null);
            holder = new ItemHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.info);
            holder.del = (ImageView)convertView.findViewById(R.id.del_account);
            holder.del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    if(null!=obj && obj instanceof Integer && dListener!=null)
                        dListener.onDelItem((Integer)obj);
                }
            });
            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.del.setTag(position);
        holder.tv.setText(dataSet.get(position));
        return convertView;
    }

    public class ItemHolder
    {
        public TextView tv;
        public ImageView del;
    }

}