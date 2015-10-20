package cn.walkpos.wpospad.main;


import android.text.TextUtils;

import com.android.volley.utils.MemDiskImageCache;
import com.xingy.lib.AppStorage;
import com.xingy.util.MyApplication;
import com.xingy.util.ServiceConfig;
import com.xingy.util.ajax.Ajax;
import com.xingy.util.ajax.OnSuccessListener;
import com.xingy.util.ajax.Response;

import org.json.JSONObject;

import cn.walkpos.wpospad.login.WposAccount;
import cn.walkpos.wpospad.util.WPosConfig;

public class WPosApplication extends MyApplication {

    public static MemDiskImageCache globalMDCache;
    private Ajax mAjax;
    public static WposAccount account;
    public static String    StockBn;
    public void onCreate()
    {
        globalMDCache = new MemDiskImageCache(MyApplication.app);
        super.onCreate();
        initParams();
        GToken =  AppStorage.getData(WPosApplication.APPSTORAGE_KEY_TOKEN);


//        checkToken();

    }

    private void checkToken()
    {
        if(!TextUtils.isEmpty(GToken))
        {
            mAjax = ServiceConfig.getAjax(WPosConfig.URL_API_ALL);
            if (null == mAjax)
                return;

            mAjax.setData("method", "token.check");
            mAjax.setData("store", GToken);

            mAjax.setOnSuccessListener(new OnSuccessListener<JSONObject>() {
                @Override
                public void onSuccess(JSONObject jsonObject, Response response) {

                }
            });
            mAjax.send();
        }


    }
    private void initParams() {

//        globalBasicParams = new BasicParamModel();
//        globalBasicParams.loadCache();
//
//        {
//            mAjax = ServiceConfig.getAjax(braConfig.URL_BASIC_PARAMS);
//            if (null == mAjax)
//                return;
//            if(null!=globalBasicParams) {
//                mAjax.setData("bv",globalBasicParams.brandModel.ver);
//                mAjax.setData("bfv",globalBasicParams.funcModel.ver);
//                mAjax.setData("prv",globalBasicParams.pricerangeModel.ver);
//                mAjax.setData("gv",globalBasicParams.guideModel.ver);
//                mAjax.setData("otv",globalBasicParams.optiontypeModel.ver);
////                mAjax.setData("csv",mSearchParams.brandModel.ver);
//                mAjax.setData("smv",globalBasicParams.storeMapdModel.ver);
//                mAjax.setData("adv", globalBasicParams.areaVer);
//            }
//            mAjax.setOnSuccessListener(new OnSuccessListener<JSONObject>() {
//                @Override
//                public void onSuccess(JSONObject jsonObject, Response response) {
//                    globalBasicParams.parse(jsonObject);
//                }
//            });
//            mAjax.send();
//        }
    }

    public static void startPush() {
		//Context pContext = MyApplication.app.getApplicationContext();
		//PushAssistor.setTask(pContext, true);
	}
	
}
