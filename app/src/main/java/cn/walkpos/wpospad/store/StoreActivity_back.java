//package cn.walkpos.wpospad.store;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ListView;
//
//import com.xingy.util.DPIUtil;
//import com.xingy.util.activity.BaseActivity;
//
//import java.util.ArrayList;
//import java.util.Collections;
//
//import cn.walkpos.wpospad.R;
//import cn.walkpos.wpospad.adapter.BottonAdapter;
//import cn.walkpos.wpospad.ui.DragGridView;
//
//import static cn.walkpos.wpospad.ui.DragGridView.OnChanageListener;
//
//
//public class StoreActivity_back extends BaseActivity {
//
//    public static final int MSG_DELAY = 600;
//    public static final int MSG_CHANGE_CATE = 101;
//    private ArrayList<String> cateArray;
//    private ListView      cateListV;
//    private BottonAdapter   cateAdapter;
//    private int           cateItemViewHeight;
//    private int           fromCatePos;
//
//    private int           toCatePos;
//    private long           curCateIdx = -1;
//    private long          lastCateIdx = -1;
//
//    private DragGridView proGridV;
//    private ArrayList<String> proArray;
//    private BottonAdapter proAdapter;
//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg)
//        {
//            if(msg.what == MSG_CHANGE_CATE)
//            {
//                cateAdapter.setPick(toCatePos);
//                cateAdapter.notifyDataSetChanged();
//                loadCate(toCatePos);
//            }
//            super.handleMessage(msg);
//        }
//    };
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_store);
//
//
//        cateListV = (ListView)this.findViewById(R.id.cate_list);
//        cateAdapter = new BottonAdapter(this);
//        cateListV.setAdapter(cateAdapter);
//        cateListV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                loadCate(position);
//                cateAdapter.setPick(position);
//                cateAdapter.notifyDataSetChanged();
//
////                scrollPos(cateListV,view,position);
//            }
//        });
//        initCate();
//
//        proGridV = (DragGridView)this.findViewById(R.id.pro_grid);
//        proAdapter = new BottonAdapter(this);
//        proGridV.setAdapter(proAdapter);
//        int num = DPIUtil.getWidth()/DPIUtil.dip2px(100);
//        proGridV.setNumColumns(num-3);
//        proGridV.setOnChangeListener(new OnChanageListener() {
//            @Override
//            public void onChange(int form, int to) {
//                int curcate = cateAdapter.getPick();
//
//                if (form > proArray.size() - 1)
//                    form = proArray.size() - 1;
//                if (form < to) {
//                    for (int i = form; i < to && i < proArray.size() - 1; i++)
//                        Collections.swap(proArray, i, i + 1);
//                } else {
//                    for (int i = form; i > to && form < proArray.size(); i--)
//                        Collections.swap(proArray, i, i - 1);
//                }
//                proAdapter.setData(proArray);
//                if (Build.VERSION.SDK_INT > 10)
//                    proAdapter.notifyDataSetChanged();
//                else
//                    proGridV.setAdapter(proAdapter);
//            }
//
//            @Override
//            public void onDrapStop() {
//                if (proGridV.getChildAt(proArray.size() - 1 - proGridV.getFirstVisiblePosition()) != null)
//                    proGridV.getChildAt(proArray.size() - 1 - proGridV.getFirstVisiblePosition()).setVisibility(View.VISIBLE);
//            }
//
//            @Override
//            public void onDrapStart(int from)
//            {
//                fromCatePos = cateAdapter.getPick();
//            }
//            @Override
//            public void onPositon(int x, int y) {
//                if (cateItemViewHeight <= 0)
//                    cateItemViewHeight = cateListV.getChildAt(0).getHeight();
//
//                if (x < 0) {
//                    float listTopY = cateListV.getY();
//                    float listHeight = cateListV.getHeight();
//                    int curpos = 0;
//                    //upwards
//                    if (y < (listTopY + cateItemViewHeight / 2)) {
//                        curpos = cateListV.getFirstVisiblePosition();
//                        curpos = curpos-1 >=0 ? curpos-1 : 0;
//                        cateListV.smoothScrollToPosition(curpos);
//                    } else if (y > (listTopY + listHeight - cateItemViewHeight / 2)) {
//                        //downwards
//                        curpos = cateListV.getLastVisiblePosition();
//                        curpos = curpos+1 < cateArray.size() ? curpos+1 : cateArray.size();
//                        cateListV.smoothScrollToPosition(curpos);
//                    } else {
//                        int pos = cateListV.getFirstVisiblePosition();
//                        int offset = cateListV.getChildAt(0).getTop();
//                        int pickpos = pos +  (int)( (y - cateListV.getY() - offset) / cateItemViewHeight);
//                        if(pickpos!=toCatePos) {
//                            toCatePos = pickpos;
//                            mHandler.removeCallbacksAndMessages(null);
//                            mHandler.sendEmptyMessageDelayed(MSG_CHANGE_CATE, MSG_DELAY);
//                        }
//                    }
//
//                }
//
//
//            }
//        });
//        loadCate(0);
//
//
//    }
//
//    private void initCate()
//    {
//        cateArray = new ArrayList<String>();
//        cateArray.add("食品");
//        cateArray.add("化妆品");
//        cateArray.add("日用");
//        cateArray.add("服装");
//        cateArray.add("电器");
//        cateArray.add("大件");
//        cateArray.add("洗漱");
//        cateArray.add("海鲜");
//        cateArray.add("进口");
//        cateArray.add("饮料1");
//        cateArray.add("饮料2");
//        cateArray.add("饮料3");
//        cateArray.add("饮料4");
//        cateArray.add("饮料5");
//        cateArray.add("饮料6");
//
//        cateAdapter.setData(cateArray);
//        cateAdapter.notifyDataSetChanged();
//    }
//
//
//    private void loadCate(long cateid)
//    {
//        if(null == proArray) {
//            proArray = new ArrayList<String>();
//        }
//        proArray.clear();
//
//        for(int i = 1; i < 100; i++) {
//            proArray.add(("" + cateid + "商品" + i));
//        }
//
//        proAdapter.setData(proArray);
//        proGridV.setAdapter(proAdapter);
//        proGridV.setTotalPosition(proArray.size());
//        toCatePos = -1;
//
//        curCateIdx = cateid;
//        if(lastCateIdx <0)
//            lastCateIdx = cateid;
//
//    }
//
//
//
//
//    private void scrollPos(ListView listv,View childv,int pos)
//    {
//        if(Build.VERSION.SDK_INT >=8)
//            listv.smoothScrollBy(childv.getTop(),500);
//        else
//            listv.setSelection(pos);
//    }
//    @Override
//    public void onClick(View v)
//    {
//        switch (v.getId())
//        {
////            case R.id.btn_0:
////                curEt.append("0");
////                break;
//
//        }
//        super.onClick(v);
//    }
//}
