package cn.walkpos.wpospad.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;

/**
 * Created by xingyao on 15-8-27.
 */
public class CateItemAdapter extends BaseAdapter
{
    private BaseActivity  mActivity;
    private int pickIdx = -1;
    private ArrayList<String> dataSet;
    private LayoutInflater mInflater;
    public CateItemAdapter(BaseActivity activity){
        mActivity = activity;
        mInflater = LayoutInflater.from(activity);
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
            convertView = mInflater.inflate(R.layout.item_tv, null);
            holder = new ItemHolder();
            holder.tv = (TextView) convertView.findViewById(R.id.info);

            convertView.setTag(holder);
        } else {
            holder = (ItemHolder) convertView.getTag();
        }

        holder.tv.setText(dataSet.get(position));
        holder.tv.setTextColor(mActivity.getResources().getColor(pickIdx == position? R.color.red : R.color.black));
        return convertView;
    }

    public class ItemHolder
    {
        public TextView tv;
    }

}