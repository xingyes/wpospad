package cn.walkpos.wpospad.cashdesk;

import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.R;


public class CashdeskActivity extends BaseActivity {

    private RecyclerView  cateListV;
    private ArrayList<String> cateArray;
    private BottonRecyclerAdapter cateAdatper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashdesk);

//        cateListV = (RecyclerView)this.findViewById(R.id.cate_list);
//
//        LinearLayoutManager lm = new LinearLayoutManager(this);
//        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
//
//        cateListV.setLayoutManager(lm);
//        cateListV.setItemAnimator(new DefaultItemAnimator());
//
//        cateAdatper = new BottonRecyclerAdapter();
//        cateListV.setAdapter(cateAdatper);

//          loadCatePro();
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


    public class BottonRecyclerAdapter extends RecyclerView.Adapter
    {
        private ArrayList<String> dataSet;

        public void setData(ArrayList<String> list)
        {
            if(null!=dataSet)
                dataSet = new ArrayList<String>();
            dataSet.clear();

            dataSet.addAll(list);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }
    }
}
