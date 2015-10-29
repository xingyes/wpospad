package cn.walkpos.wpospad.adapter;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;
import java.util.HashSet;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.GoodsModule;

/**
 * Created by xingyao on 15-8-27.
 */
public class BuyProAdapter extends RecyclerView.Adapter<BuyProAdapter.contHolder>
{
    private HashSet<GoodsModule> chooseProIdSet;
    private ArrayList<GoodsModule> prolist;
    private BaseActivity  mActivity;

    public interface InfoChangedListener{
        public void onInfoChanged(View v, int pos);
        public void onInfoRemoved(View v, int pos);
    };
    private InfoChangedListener  infoListener;
    public void chooseAll()
    {
        for(GoodsModule pmod : prolist)
        {
            chooseProIdSet.add(pmod);
        }
    }

    public void chooseNone()
    {
        chooseProIdSet.clear();
    }
    public BuyProAdapter(BaseActivity activity, InfoChangedListener listener) {
        mActivity = activity;
        prolist = new ArrayList<GoodsModule>();
        if (null == chooseProIdSet)
            chooseProIdSet = new HashSet<GoodsModule>();

        infoListener = listener;
    }

    public void addBuyItem(GoodsModule addItem)
    {
        for(int i=0; i < prolist.size(); i++) {
            GoodsModule item = prolist.get(i);
            if (item.goods_id.equals(addItem.goods_id)) {
                item.buy_num++;
                return;
            }
        }
        GoodsModule buyitem = new GoodsModule(addItem);
        prolist.add(buyitem);
        chooseProIdSet.add(buyitem);
    }

    @Override
    public BuyProAdapter.contHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = mActivity.getLayoutInflater().inflate(R.layout.item_buy_pro, null);
            return new BuyProAdapter.contHolder(v);
    }

    @Override
    public void onBindViewHolder(BuyProAdapter.contHolder holder, int position) {
        GoodsModule pro = prolist.get(position);

        holder.buy_item_code.setText(pro.goods_id);
        holder.buy_item_name.setText(pro.name_s);
        holder.buy_item_price.setText(pro.priceout);

        holder.buy_item_discount.removeTextChangedListener(holder.buy_discount_watcher);
        holder.buy_item_discount.setText(""+pro.discount);
        holder.buy_item_discount.addTextChangedListener(holder.buy_discount_watcher);

        holder.buy_item_num.removeTextChangedListener(holder.buy_num_wathcer);
        holder.buy_item_num.setText("" + pro.buy_num);
        holder.buy_item_num.addTextChangedListener(holder.buy_num_wathcer);

        holder.buy_item_choose.setTag(position);
        holder.buy_item_discount.setTag(position);
        holder.buy_item_num.setTag(position);
        holder.buy_num_up.setTag(position);
        holder.buy_num_down.setTag(position);
        holder.buy_item_del.setTag(position);

        holder.buy_item_choose.setOnCheckedChangeListener(null);
        holder.buy_item_choose.setChecked(chooseProIdSet.contains(pro));
        holder.buy_item_choose.setOnCheckedChangeListener(holder.buy_choose_checklistener);

        holder.buy_item_sum.setText(String.format("%.2f",(Double.valueOf(pro.priceout)*pro.buy_num*pro.discount)));

    }



    @Override
    public int getItemCount() {
        return (null == prolist ? 0 : prolist.size());
    }

    public double getTotalPrice()
    {
        double value = 0.0;
        for(GoodsModule goods : chooseProIdSet)
        {
            value += Double.valueOf(goods.priceout)*goods.buy_num* goods.discount;
        }
        return value;

    }

    public class contHolder extends RecyclerView.ViewHolder
    {
        public CompoundButton.OnCheckedChangeListener buy_choose_checklistener;
        public BuynumWatcher buy_num_wathcer;
        public DiscountWatcher buy_discount_watcher;
        public TextView   buy_item_code;
        public TextView   buy_item_name;
        public TextView   buy_item_price;
        public EditText   buy_item_num;
        public ImageView  buy_num_up;
        public ImageView  buy_num_down;
        public EditText   buy_item_discount;
        public TextView   buy_item_sum;

        public CheckBox   buy_item_choose;
        public RelativeLayout buy_item_del;

        public contHolder(View itemView) {
            super(itemView);

            buy_item_choose = (CheckBox)itemView.findViewById(R.id.buy_item_choose);
            buy_choose_checklistener = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Object obj = buttonView.getTag();
                    if(null!=obj && obj instanceof Integer)
                    {
                        GoodsModule item = prolist.get((Integer)obj);
                        if(isChecked)
                            chooseProIdSet.add(item);
                        else
                            chooseProIdSet.remove(item);
                        infoListener.onInfoChanged(buttonView,(Integer)obj);
                    }
                }
            };

            buy_item_code = (TextView)itemView.findViewById(R.id.buy_item_code);
            buy_item_name = (TextView)itemView.findViewById(R.id.buy_item_name);
            buy_item_price = (TextView)itemView.findViewById(R.id.buy_item_price);
            buy_item_num = (EditText)itemView.findViewById(R.id.buy_item_num);
            buy_num_wathcer = new BuynumWatcher(buy_item_num);
            buy_num_up = (ImageView)itemView.findViewById(R.id.buy_num_up);
            buy_num_up.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    if(null!=obj && obj instanceof Integer)
                    {
                        GoodsModule item = prolist.get((Integer)obj);
                        item.buy_num++;
                        infoListener.onInfoChanged(v,(Integer)obj);
//                        notifyDataSetChanged();
                    }
                }
            });
            buy_num_down = (ImageView)itemView.findViewById(R.id.buy_num_down);
            buy_num_down.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    if(null!=obj && obj instanceof Integer)
                    {
                        GoodsModule item = prolist.get((Integer)obj);
                        item.buy_num--;
                        if(item.buy_num<0)
                            item.buy_num=0;
                        infoListener.onInfoChanged(v,(Integer)obj);
//                        notifyDataSetChanged();
                    }
                }
            });
            buy_item_discount = (EditText)itemView.findViewById(R.id.buy_item_discount);
            buy_discount_watcher = new DiscountWatcher(buy_item_discount);

            buy_item_sum = (TextView)itemView.findViewById(R.id.buy_item_sum);
            buy_item_del = (RelativeLayout)itemView.findViewById(R.id.buy_item_del);
            buy_item_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Object obj = v.getTag();
                    if(null!=obj && obj instanceof Integer)
                    {
                        int idx = (Integer)obj;
                        GoodsModule delitem = prolist.remove(idx);
                        chooseProIdSet.remove(delitem);
                        infoListener.onInfoRemoved(v, (Integer) obj);
//                        notifyDataSetChanged();
                    }
                }
            });
        }
    }


    public class BuynumWatcher implements TextWatcher {
        private Handler mHandler = new Handler();
        private Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                Object obj = editText.getTag();
                if (null != obj && obj instanceof Integer) {
                    GoodsModule item = prolist.get((Integer) obj);
                    String info = editText.getText().toString();
                    if (!TextUtils.isEmpty(info) && TextUtils.isDigitsOnly(info)) {
                        item.buy_num = Integer.valueOf(info);
//                        notifyDataSetChanged();
                        infoListener.onInfoChanged(editText, (Integer) obj);
                    }
                }
            }
        };

        private EditText  editText;
        public BuynumWatcher(EditText editv)
        {
            editText = editv;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}

        @Override
        public void afterTextChanged(Editable s) {
            mHandler.removeCallbacks(updateRunnable);
            mHandler.postDelayed(updateRunnable,1500);
        }
    };

    public class DiscountWatcher implements TextWatcher {
        private Handler mHandler = new Handler();
        private Runnable updateRunnable = new Runnable() {
            @Override
            public void run() {
                Object obj = editText.getTag();
                if (null != obj && obj instanceof Integer) {
                    GoodsModule item = prolist.get((Integer) obj);
                    String info = editText.getText().toString();
                    try {
                        Double newdis = Double.valueOf(info);
                        if(newdis >=0 && newdis <=1)
                            item.discount = newdis;
                        infoListener.onInfoChanged(editText, (Integer) obj);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        private EditText editText;

        public DiscountWatcher(EditText editv) {
            editText = editv;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mHandler.removeCallbacks(updateRunnable);
            mHandler.postDelayed(updateRunnable, 1500);
        }
    };



}

