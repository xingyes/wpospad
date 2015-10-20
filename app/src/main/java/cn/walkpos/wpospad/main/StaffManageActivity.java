package cn.walkpos.wpospad.main;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.nineoldandroids.view.ViewHelper;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.DPIUtil;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ToolUtil;
import com.xingy.util.UploadPhotoUtil;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.login.WposAccount;
import cn.walkpos.wpospad.util.WPosConfig;


public class StaffManageActivity extends BaseActivity implements ViewPager.OnPageChangeListener,OnSuccessListener<JSONObject>{


    private Ajax          mAjax;
    private ViewPager     staffVpger;
    private StaffVPgAdapter  staffAdapter;
    private ArrayList<WposAccount> staffArray;
    private WposAccount      newStaff;
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
        staffArray = new ArrayList<WposAccount>();
        staffVpger.setCurrentItem(0);

        RequestQueue mQueue = Volley.newRequestQueue(this);
        mImgLoader = new ImageLoader(mQueue, WPosApplication.globalMDCache);

        loadStaff();

        delListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Object obj = v.getTag();
                if (null != obj && obj instanceof Integer) {
                    int pos = (Integer)obj;
                    WposAccount staff = staffArray.remove(pos);
                    if(TextUtils.isEmpty(staff.bn)) {
                        newStaff = null;
                        UiUtils.makeToast(StaffManageActivity.this, "放弃新增店员");
                        staffVpger.removeAllViews();
                        staffVpger.setAdapter(staffAdapter);
                    }
                    else
                    {
                        delStaff(staff);
                    }
                }
            }
        };
    }


    /**
     *
     */
    private void loadStaff()
    {
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_LOAD_STAFFARRAY);
        mAjax.setData("method", "store.users");
        mAjax.setData("store_bn", WPosApplication.StockBn);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }


    private void modifyOrAddStaff(WposAccount staff)
    {
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(TextUtils.isEmpty(staff.bn) ? WPosConfig.REQ_ADD_STAFF : WPosConfig.REQ_MODIFY_STAFF);
        mAjax.setData("method", (TextUtils.isEmpty(staff.bn) ? "passport.register" : "user.edit"));
        if(!TextUtils.isEmpty(staff.bn))
            mAjax.setData("bn", staff.bn);

        mAjax.setData("imei", ToolUtil.getDeviceUid(this));
        mAjax.setData("store_bn", WPosApplication.StockBn);
        mAjax.setData("card", staff.card_number);

        mAjax.setData("name", staff.name);
        mAjax.setData("password", staff.passwd);
        mAjax.setData("mobile", staff.mobile);
        mAjax.setData("discount",staff.bdiscount ? "true" : "false");
        mAjax.setData("manage",staff.bmanage ? "true" : "false");

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }

    private void delStaff(WposAccount toDelStaff)
    {
        mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
        if (null == mAjax)
            return;

        showLoadingLayer();

        mAjax.setId(WPosConfig.REQ_DEL_STAFF);
        mAjax.setData("method", "user.delete");
        mAjax.setData("store_bn", WPosApplication.StockBn);
        mAjax.setData("bn", toDelStaff.bn);

        mAjax.setOnSuccessListener(this);
        mAjax.setOnErrorListener(this);
        mAjax.send();
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode != RESULT_OK) {
            return;
        }

        if (requestCode == UploadPhotoUtil.PHOTO_PICKED_WITH_DATA && null != data)
        {
            // 雷军个傻逼
//            mIsProcessing = true;
            ToolUtil.showClipIntentWithData(this, data.getData());
        }
        else if (requestCode == UploadPhotoUtil.CAMERA_WITH_DATA)
        {
//            mIsProcessing = true;
            showLoadingLayer();
            Uri imgUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            String path = UploadPhotoUtil.getImgPath(this, requestCode, resultCode, data);
            if(ToolUtil.isEmpty(path))
            {
                UiUtils.makeToast(this, "照片路径获取失败");
                return;
            }

            File file = new File(path);
            Uri fileUri = Uri.fromFile(file);
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                    fileUri));

            Cursor cursor =null;
            Uri uri = null;
            int tryount = 0;
            try{
                while(cursor==null || cursor.getCount()<=0 && tryount < 10) {
                    cursor = getContentResolver().query(imgUri, null,
                            MediaStore.Images.Media.DISPLAY_NAME + "='"
                                    + file.getName() + "'",
                            null, null);
                    android.util.Log.e("open pic","time:" + tryount + " cursor null?" + (cursor==null));
                    if(cursor != null && cursor.getCount() > 0) {
                        android.util.Log.e("open pic","moteToLast");
                        cursor.moveToLast();
                        long id = cursor.getLong(0);
                        uri = ContentUris.withAppendedId(imgUri, id);
                        break;
                    }
                    else {
                        tryount++;
                        Thread.sleep(300);
                    }

                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                if (null != cursor && !cursor.isClosed())
                    cursor.close();
            }
            ToolUtil.showClipIntentWithData(this,uri);
        }
        else if(requestCode == ToolUtil.GO_CROP_ACTIVITY)
        {
            closeLoadingLayer();

            //final AutoHeightImageView curImg = (AutoHeightImageView) mPicImages.get(mCurPicIdx);
            //String localPath = curImg.mCustomInfo.get("localPath");

            final Bitmap bitmap =  (null == data) ? null : (Bitmap)data.getParcelableExtra("data");
            if (null != bitmap) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                int pos = staffVpger.getCurrentItem();
                staffArray.get(pos).logobm = bitmap;
                staffAdapter.notifyDataSetChanged();

//				File file = new File(localPath);
//			    FileOutputStream fOut = new FileOutputStream(file);
//				bitmap.compress(CompressFormat.JPEG, 100, fOut);
//				fOut.flush();
//				fOut.close();

//                Ajax ajax = ServiceConfig.getAjax(braConfig.URL_IMAGE_STREAM_UPLOAD);
//                if( null != ajax ) {
//                    showLoadingLayer();
//                    ajax.setData("token",account.token);
//                    ajax.setFile("uavatar", byteArray, "img.jpg");
//
////                    ajax.setFile("uavatar", byteArray);
//                    ajax.setOnSuccessListener(this);
////                    ajax.setId(AJAX_UPLOAD_SNAPSHOT);
//                    ajax.setOnErrorListener(this);
//                    addAjax(ajax);
//                    ajax.send();
//                }
            }

//            mIsProcessing = false;
        }
    }

    @Override
    public void onClick(View v)
    {
        if(v.getId() == R.id.add_staff_btn) {
            if(null == newStaff)
            {
                newStaff = new WposAccount();
                staffArray.add(0,newStaff);
                staffVpger.setAdapter(staffAdapter);
            }
            else
                staffVpger.setCurrentItem(0);
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

    @Override
    public void onSuccess(JSONObject jsonObject, Response response) {
        closeLoadingLayer();

        int errno = jsonObject.optInt("response_code",-1);
        if(errno!=0)
        {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this,msg);
            return;
        }

        JSONObject data = jsonObject.optJSONObject("data");
        if (null == data) {
            String msg = jsonObject.optString("res", getString(R.string.network_error));
            UiUtils.makeToast(this, msg);
            return;
        }

        if(response.getId() == WPosConfig.REQ_LOAD_STAFFARRAY) {
            JSONArray array = data.optJSONArray("list");
            if(null!=array && array.length()>0) {
                staffArray.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject item = array.optJSONObject(i);
                    WposAccount staff = new WposAccount();
                    staff.parse(item);

                    staffArray.add(staff);
                }
                if (staffArray.size() > 0)
                    staffAdapter.notifyDataSetChanged();
            }
        }else if(response.getId() == WPosConfig.REQ_ADD_STAFF)
        {
            loadStaff();
            newStaff = null;
        }
        else if(response.getId() == WPosConfig.REQ_DEL_STAFF)
        {
            String msg = jsonObject.optString("res", "删除店员成功");
            UiUtils.makeToast(this,msg);
            loadStaff();
        }
    }


    /**
     *  StaffViewPager Adapter
     */
    public class StaffVPgAdapter extends PagerAdapter {

        // 界面列表
//        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);

        /**
         * 获得当前界面数
         */
        @Override
        public int getCount() {
            return (staffArray==null ? 0 : staffArray.size());
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
            final StaffViewHolder vholder = new StaffViewHolder();
            page = LayoutInflater.from(getBaseContext()).inflate(R.layout.staff_pg, null);
            vholder.editImgLayout = (RelativeLayout)page.findViewById(R.id.edit_logo);
            vholder.imgV = (NetworkImageView) page.findViewById(R.id.head_img);
            vholder.codeV = (EditText) page.findViewById(R.id.code);
            vholder.loginnameV = (TextView) page.findViewById(R.id.login_name);
            vholder.nameV = (EditText) page.findViewById(R.id.name);

            vholder.phoneV = (EditText) page.findViewById(R.id.phone);
            vholder.passwdV = (EditText) page.findViewById(R.id.passwd);
            vholder.submitV = (TextView) page.findViewById(R.id.staff_modify_btn);
            vholder.delV = (ImageView)page.findViewById(R.id.del_account);
            vholder.manageAuthV = (CheckBox)page.findViewById(R.id.manage_auth_check);
            vholder.discountAuthV = (CheckBox)page.findViewById(R.id.discount_auth_check);
            vholder.delV.setOnClickListener(delListener);

            WposAccount staff = staffArray.get(position);
            vholder.delV.setTag(position);
            vholder.editImgLayout.setTag(position);
            vholder.editImgLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UploadPhotoUtil.createUploadPhotoDlg(StaffManageActivity.this).show();

                }
            });
            if(!TextUtils.isEmpty(staff.bn)) {
                if(staff.logobm != null)
                {
                    vholder.imgV.setImageBitmap(null);
                    vholder.imgV.setBackgroundDrawable(new BitmapDrawable(staff.logobm));
                }

                else
                    vholder.imgV.setImageUrl(staff.logo, mImgLoader);
                vholder.codeV.setText(staff.bn);
                vholder.nameV.setText(staff.name);
                vholder.loginnameV.setText(staff.name);
                vholder.phoneV.setText(staff.mobile);
                vholder.passwdV.setText(staff.passwd);
                vholder.submitV.setText("确认修改");

                vholder.nameV.setFocusable(false);
                vholder.nameV.setFocusableInTouchMode(false);
            }else
            {
                vholder.codeV.setText("");
                vholder.nameV.setText("");
                vholder.phoneV.setText("");
                vholder.passwdV.setText("");
                vholder.submitV.setText("确认添加");

                vholder.nameV.setFocusable(true);
                vholder.nameV.setFocusableInTouchMode(true);
            }

            page.setOnTouchListener(editListener);
            vholder.submitV.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Object obj = v.getTag();
                        if(null!=obj && obj instanceof  Integer) {
                            WposAccount  act = new WposAccount();
                            act.name = vholder.nameV.getText().toString();
                            act.card_number = "330501199910100001";
                            act.bn = vholder.codeV.getText().toString();
                            act.passwd = vholder.passwdV.getText().toString();
                            act.mobile = vholder.phoneV.getText().toString();
                            act.bdiscount = vholder.discountAuthV.isChecked();
                            act.bmanage = vholder.manageAuthV.isChecked();
                            modifyOrAddStaff(act);
                        }

                    }
                });
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


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

//		public void cleanData() {
//			pics = new int[0];
//		}
    }

    public class StaffViewHolder
    {
        ImageView delV;
        NetworkImageView imgV;
        RelativeLayout   editImgLayout;
        EditText         codeV;
        TextView         loginnameV;
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
