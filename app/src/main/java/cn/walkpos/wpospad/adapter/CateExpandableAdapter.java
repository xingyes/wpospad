package cn.walkpos.wpospad.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.CateItemModule;

/**
 * Created by xingyao on 15-8-27.
 */
public class CateExpandableAdapter extends BaseExpandableListAdapter
{

    private int           gpPickIdx = 0;
    private int           subPickIdx = -1;
    private BaseActivity  mActivity;
    private ArrayList<CateItemModule> cateGroupArray;

    public void setPickIdx(int gp,int sub)
    {
        gpPickIdx = gp;
        subPickIdx = sub;
    }
    public CateExpandableAdapter(BaseActivity activity, ArrayList<CateItemModule> aArray)
    {
        mActivity = activity;
        cateGroupArray = aArray;
    }
    @Override
    public int getGroupCount() {
        return (null == cateGroupArray ? 0 : cateGroupArray.size());
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if(null!=cateGroupArray && cateGroupArray.size() > groupPosition) {
            CateItemModule categp = cateGroupArray.get(groupPosition);
            return (null == categp.subCateArray ? 0 : categp.subCateArray.size());
        }
        else
            return 0;

    }


    @Override
    public void onGroupExpanded(int groupPosition) {
        gpPickIdx = groupPosition;
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public Object getGroup(int groupPosition) {
        if(null!=cateGroupArray && cateGroupArray.size() > groupPosition)
            return cateGroupArray.get(groupPosition);
        else
            return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if(null!=cateGroupArray && cateGroupArray.size() > groupPosition) {
            CateItemModule categp = cateGroupArray.get(groupPosition);
            if(null != categp.subCateArray && categp.subCateArray.size() > childPosition)
                return categp.subCateArray.get(childPosition);
            else
                return null;
        }
        else
            return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final CateGPHolder gpHolder;

        if ( null == convertView ) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_cate_expandlist, null);
            convertView.setBackgroundResource(R.color.btn_wpos_gray_dark);
            gpHolder = new CateGPHolder();
            gpHolder.namev= (TextView) convertView.findViewById(R.id.name);
            gpHolder.arrowv = (ImageView)convertView.findViewById(R.id.arrow);
//			optionHolder.pCheckBox = (CheckBox) convertView.findViewById(R.id.filteroption_checkbox);
            convertView.setTag(gpHolder);
        }
        else
        {
            gpHolder = (CateGPHolder) convertView.getTag();
        }

        CateItemModule gpMod = cateGroupArray.get(groupPosition);
        gpHolder.namev.setText(gpMod.name);
        if(groupPosition == gpPickIdx) {
            gpHolder.namev.setTextColor(mActivity.getResources().getColor(R.color.btn_wpos_red));
        }
        else
            gpHolder.namev.setTextColor(mActivity.getResources().getColor(R.color.white));
        if(gpMod.subCateArray.size()<=0)
            gpHolder.arrowv.setVisibility(View.GONE);
        else
            gpHolder.arrowv.setVisibility(View.VISIBLE);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final CateItHolder itHolder;

        if ( null == convertView ) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.item_cate_expandlist, null);
            itHolder = new CateItHolder();
            itHolder.namev= (TextView) convertView.findViewById(R.id.name);
            itHolder.arrowv = (ImageView)convertView.findViewById(R.id.arrow);
            itHolder.arrowv.setVisibility(View.GONE);
            convertView.setTag(itHolder);
        }
        else
        {
            itHolder = (CateItHolder) convertView.getTag();
        }

        if(childPosition == subPickIdx) {
            itHolder.namev.setTextColor(mActivity.getResources().getColor(R.color.btn_wpos_red));
        }
        else
            itHolder.namev.setTextColor(mActivity.getResources().getColor(R.color.white));


        CateItemModule gpMod = cateGroupArray.get(groupPosition);
        CateItemModule  itMod = gpMod.subCateArray.get(childPosition);
        itHolder.namev.setText(itMod.name);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    private static class CateGPHolder{
        TextView namev;
        ImageView arrowv;
    }

    private static class CateItHolder{
        TextView namev;
        ImageView arrowv;
    }
}

