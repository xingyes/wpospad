package cn.walkpos.wpospad.store;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Collections;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.ui.DragGridView;

import static cn.walkpos.wpospad.ui.DragGridView.OnChanageListener;


public class StoreActivity extends BaseActivity {

    public static final int MSG_DELAY = 2500;
    public static final int MSG_CHANGE_CATE = 101;
    private ArrayList<String> cateArray;
    private ListView      cateListV;
    private CateAdapter   cateAdapter;
    private int           cateItemViewHeight;
    private int           toCatePos;

    private DragGridView proGridV;
    private ArrayList<String> proArray;
    private CateAdapter   proAdapter;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg)
        {
            if(msg.what == MSG_CHANGE_CATE)
            {
                cateAdapter.setPick(toCatePos);
                cateAdapter.notifyDataSetChanged();
                loadCate(toCatePos);
            }
            super.handleMessage(msg);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);


        cateListV = (ListView)this.findViewById(R.id.cate_list);
        cateAdapter = new CateAdapter(this);
        cateListV.setAdapter(cateAdapter);
        cateListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                loadCate(position);
                cateAdapter.setPick(position);
                cateAdapter.notifyDataSetChanged();
            }
        });
        initCate();

        proGridV = (DragGridView)this.findViewById(R.id.pro_grid);
        proAdapter = new CateAdapter(this);
        proGridV.setAdapter(proAdapter);
        proGridV.setOnChangeListener(new OnChanageListener() {
            @Override
            public void onChange(int form, int to) {
                if (form > proArray.size() - 1)
                    form = proArray.size() - 1;
                if (form < to) {
                    for (int i = form; i < to && i < proArray.size() - 1; i++)
                        Collections.swap(proArray, i, i + 1);
                } else {
                    for (int i = form; i > to && form < proArray.size(); i--)
                        Collections.swap(proArray, i, i - 1);
                }
                proAdapter.setData(proArray);
                if (Build.VERSION.SDK_INT > 10)
                    proAdapter.notifyDataSetChanged();
                else
                    proGridV.setAdapter(proAdapter);
            }

            @Override
            public void onStop() {
                if (proGridV.getChildAt(proArray.size() - 1 - proGridV.getFirstVisiblePosition()) != null)
                    proGridV.getChildAt(proArray.size() - 1 - proGridV.getFirstVisiblePosition()).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPositon(int x, int y) {
                if (cateItemViewHeight <= 0)
                    cateItemViewHeight = cateListV.getChildAt(0).getHeight();

                if (x < 0) {
                    float listTopY = cateListV.getY();
                    float listHeight = cateListV.getHeight();
                    int curpos = 0;
                    //upwards
                    if (y < (listTopY + cateItemViewHeight / 2)) {
                        curpos = cateListV.getFirstVisiblePosition();
                        curpos = curpos-1 >=0 ? curpos-1 : 0;
                        cateListV.smoothScrollToPosition(curpos);
                    } else if (y > (listTopY + listHeight - cateItemViewHeight / 2)) {
                        //downwards
                        curpos = cateListV.getLastVisiblePosition();
                        curpos = curpos+1 < cateArray.size() ? curpos+1 : cateArray.size();
                        cateListV.smoothScrollToPosition(curpos);
                    } else {
                        int pos = cateListV.getFirstVisiblePosition();
                        int pickpos = pos +  (int)( (y - cateListV.getY()) / cateItemViewHeight);
                        if(pickpos!=toCatePos) {
                            toCatePos = pickpos;
                            mHandler.removeCallbacksAndMessages(null);
                            mHandler.sendEmptyMessageDelayed(MSG_CHANGE_CATE, MSG_DELAY);
                        }
                    }

                }


            }
        });
        loadCate(0);


    }

    private void initCate()
    {
        cateArray = new ArrayList<String>();
        cateArray.add("食品");
        cateArray.add("化妆品");
        cateArray.add("日用");
        cateArray.add("服装");
        cateArray.add("电器");
        cateArray.add("大件");
        cateArray.add("洗漱");
        cateArray.add("海鲜");
        cateArray.add("进口");
        cateArray.add("饮料1");
        cateArray.add("饮料2");
        cateArray.add("饮料3");
        cateArray.add("饮料4");
        cateArray.add("饮料5");
        cateArray.add("饮料6");

        cateAdapter.setData(cateArray);
        cateAdapter.notifyDataSetChanged();
    }


    private void loadCate(long cateid)
    {
        if(null == proArray) {
            proArray = new ArrayList<String>();
        }
        proArray.clear();

        for(int i = 0; i < 100; i++) {
            proArray.add(("" + cateid + "商品" + i));
        }

        proAdapter.setData(proArray);
        proGridV.setAdapter(proAdapter);
        proGridV.setTotalPosition(proArray.size());
        toCatePos = -1;
    }


    public class CateAdapter extends BaseAdapter
    {

        private int pickIdx = -1;
        private ArrayList<String> dataSet;
        private LayoutInflater mInflater;
        public CateAdapter(BaseActivity activity){
            mInflater = LayoutInflater.from(activity);
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
            holder.tv.setTextColor(getResources().getColor(pickIdx == position? R.color.red : R.color.black));
            return convertView;
        }
    }


    public class ItemHolder
    {
        public TextView tv;
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
//            case R.id.btn_0:
//                curEt.append("0");
//                break;

        }
        super.onClick(v);
    }
}
