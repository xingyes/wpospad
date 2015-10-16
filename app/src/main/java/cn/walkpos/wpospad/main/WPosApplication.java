package cn.walkpos.wpospad.main;


import com.android.volley.utils.MemDiskImageCache;
import com.xingy.util.MyApplication;
import com.xingy.util.ajax.Ajax;

import cn.walkpos.wpospad.login.WposAccount;

public class WPosApplication extends MyApplication {

    public static MemDiskImageCache globalMDCache;
    private Ajax mAjax;
    public static WposAccount account;
    public void onCreate()
    {
        globalMDCache = new MemDiskImageCache(MyApplication.app);
        super.onCreate();
        initParams();

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
