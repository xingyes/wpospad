package cn.walkpos.wpospad.main;

import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.nineoldandroids.view.ViewHelper;
import com.xingy.lib.ui.AutoHeightImageView;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.activity.BaseActivity;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.module.StaffModule;


public class StaffManageActivity extends BaseActivity implements ViewPager.OnPageChangeListener {


    private ViewPager     staffVpger;
    private StaffVPgAdapter  staffAdapter;
    private ArrayList<StaffModule> staffArray;
    private ImageLoader      mImgLoader;
    private View.OnTouchListener  editListener;
    private View.OnClickListener  delListener;
    private int              vpIdex  = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_manage);
        loadNavBar(R.id.staff_nav);
        this.findViewById(R.id.add_staff_btn).setOnClickListener(this);
        this.findViewById(R.id.setting_btn).setOnClickListener(this);


        staffVpger = (ViewPager)findViewById(R.id.viewpager);
        staffVpger.setClipChildren(false);
        staffVpger.setOffscreenPageLimit(2);
        staffVpger.setPageTransformer(true, new ZoomPopinTransformer());
        staffVpger.setPageMargin(-DPIUtil.dip2px(400));
        staffVpger.setOnPageChangeListener(this);
        staffAdapter = new StaffVPgAdapter();
        staffVpger.setAdapter(staffAdapter);
        staffArray = new ArrayList<StaffModule>();
        staffVpger.setCurrentItem(0);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, WPosApplication.globalMDCache);
        loadStaff();

//        editListener = new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if(editing && v instanceof EditText && event.getAction() == MotionEvent.ACTION_DOWN)
//                {
//                    editing = true;
//                    staffAdapter.notifyDataSetChanged();
//                }
//                return false;
//            }
//        };

        delListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj = v.getTag();
                if (null != obj && obj instanceof Integer) {
                    int pos = (Integer)obj;
                    if(pos>=0)
                    {
                        StaffModule staf = staffArray.remove(pos);
                        UiUtils.makeToast(StaffManageActivity.this,"remove:" + pos + "," + staf.name);
                        staffVpger.removeAllViews();
                        staffVpger.setAdapter(staffAdapter);
                    }
                    else
                    {
                        staffAdapter.setAdding(false);
                        UiUtils.makeToast(StaffManageActivity.this,"back from adding modle");
                        staffVpger.removeAllViews();
                        staffVpger.setAdapter(staffAdapter);
                    }
                }
            }
        };

    }

    private void loadStaff()
    {
        for(int i=0 ; i< 4; i++)
        {
            StaffModule staff = new StaffModule();
            staff.name = "店员" + i;
            staff.imgurl = "http://avatar.csdn.net/B/6/9/1_diy534.jpg";
            staffArray.add(staff);
        }

        staffAdapter.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.add_staff_btn) {
            if (staffAdapter.getAdding()) {
                staffVpger.setCurrentItem(0);
            } else {
                staffAdapter.setAdding(true);
                staffVpger.setAdapter(staffAdapter);
            }
        }
        else if(v.getId() == R.id.setting_btn)
        {
            UiUtils.startActivity(StaffManageActivity.this,SettingActivity.class,true);
        }
        else
            super.onClick(v);
    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if(vpIdex != position) {
            vpIdex = position;
//            editing = false;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    public class StaffVPgAdapter extends PagerAdapter {

        private boolean bAdding = false;

        public boolean getAdding()
        {
            return bAdding;
        }
        public void setAdding(boolean flag)
        {
            bAdding = flag;
        }
        // 界面列表
//        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        /**
         * 获得当前界面数
         */
        @Override
        public int getCount() {
            return (staffArray==null ? 0 : (bAdding ? staffArray.size() +1 : staffArray.size()));
        }

        @Override
        public void startUpdate(ViewGroup container) {
            // 实现此抽象方法，防止出现AbstractMethodError
            super.startUpdate(container);
        }

        /**
         * 初始化position位置的界面
         */
        @Override
        public Object instantiateItem(View view, int position) {

            View v;
            View page = null;
            StaffViewHolder vholder = new StaffViewHolder();
            page = LayoutInflater.from(getBaseContext()).inflate(R.layout.staff_pg, null);
            vholder.imgV = (NetworkImageView) page.findViewById(R.id.head_img);
            vholder.codeV = (EditText) page.findViewById(R.id.code);
            vholder.nameV = (EditText) page.findViewById(R.id.name);
            vholder.phoneV = (EditText) page.findViewById(R.id.phone);
            vholder.passwdV = (EditText) page.findViewById(R.id.passwd);
            vholder.submitV = (TextView) page.findViewById(R.id.submit_btn);
            vholder.delV = (ImageView)page.findViewById(R.id.del_account);
            vholder.delV.setOnClickListener(delListener);

            vholder.delV.setTag(-1);
            StaffModule staff = null;
            if(bAdding)
            {
                if(position > 0) {
                    staff = staffArray.get(position - 1);
                    vholder.delV.setTag(position - 1);
                }
            }
            else {
                vholder.delV.setTag(position);
                staff = staffArray.get(position);
            }

            if(null!=staff) {
                vholder.imgV.setImageUrl(staff.imgurl, mImgLoader);
                vholder.codeV.setText(staff.code);
                vholder.nameV.setText(staff.name);
                vholder.phoneV.setText(staff.phone);
                vholder.passwdV.setText(staff.passwd);
            }else
            {
                vholder.codeV.setText("");
                vholder.nameV.setText("");
                vholder.phoneV.setText("");
                vholder.passwdV.setText("");
            }

            page.setOnTouchListener(editListener);
            vholder.submitV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = v.getTag();
                        if(null!=obj && obj instanceof  Integer)
                        {
                            UiUtils.makeToast(StaffManageActivity.this,"提交修改:" + (Integer)obj);
                        }
                    }
                });
//            vholder.submitV.setEnabled(editing);
            vholder.submitV.setTag(position);

            v = page;
            ((ViewPager) view).addView(page);

            return v;
        }

        /**
         * 判断是否由对象生成界面
         */
        @Override
        public boolean isViewFromObject(View view, Object arg1) {
            return (view == arg1);
        }

        /**
         * 销毁position位置的界面
         */
        @Override
        public void destroyItem(View view, int position, Object arg2) {
            ((ViewPager) view).removeView((View) arg2);
            System.gc();
        }

//		public void cleanData() {
//			pics = new int[0];
//		}
    }

    public class StaffViewHolder
    {
        ImageView delV;
        NetworkImageView imgV;
        EditText         codeV;
        EditText         nameV;
        EditText         phoneV;
        EditText         passwdV;
        TextView         submitV;

        CheckBox         discountAuthV;
        CheckBox         manageAuthV;
    }


    public class ZoomPopinTransformer implements ViewPager.PageTransformer {

        @Override
        public void transformPage(View page, float position) {
            if(position<-1)
                return;
            //[-1,0)
            else if(position>=-1 && position < 0)
            {
                ViewHelper.setScaleX(page, 0.6f + (position+1)*0.4f);
                ViewHelper.setScaleY(page,0.6f + (position+1)*0.4f);
                return;
            }
            //[-0,1)
            else if(position>=0 && position<1)
            {
                ViewHelper.setScaleX(page, 1f - (position)*0.4f);
                ViewHelper.setScaleY(page,1f - (position)*0.4f);
                return;
            }
            else //position > 1
                return;
        }
    };

}
