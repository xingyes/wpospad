package com.xingy.util;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.xingy.lib.AppStorage;
import com.xingy.lib.UExceptionHandler;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;
import com.xingy.util.db.DbFactory;

import java.util.ArrayList;

public class MyApplication extends Application {
    public static final String APPSTORAGE_KEY_TOKEN = "key_token";
	public static String    GToken = "";
	private static final String LOG_TAG =  MyApplication.class.getName();
	public static AppStorage mStorage = null;
	public static MyApplication app;
	public static boolean APP_RUNNING = false;
	public static int mVersionCode;
	public static String mReportTag = "";  //??��??tag�???��????��??�????
	
	public ArrayList<BaseActivity> activityList = new ArrayList<BaseActivity>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		MyApplication.app = this;
		
		DPIUtil.getDefaultDisplay(this.getApplicationContext());
		DPIUtil.setDensity(this.getResources().getDisplayMetrics().density);
        ServiceConfig.setContext(MyApplication.app);

        //CrashReport.initCrashReport(this);
		//String userId = ILogin.getLoginUid() + ""; // ??��??ID
		//CrashReport.setUserId(this, userId);
		
//		if (Config.DEBUG) // 正�?????�???��?��??�???��??
//        {
//			CrashReport.setLogAble(true, false);
//        }
		
		//setExceptionStrategy();

		/*
		if (Config.DEBUG) // 正�?????�???��?��??�???��??
        {
             Constants.IS_DEBUG = true; // �????eup log
             Constants.IS_CORE_DEBUG = true; // �???��?��??�???? eup log
             Constants.IS_USETESTSERVER = true; // 使�?��??�??????��??�???��??污�??正�?????�?

             // ???�???��?��??SDK�?????????�使??��??�???��??�??????��??常�??SDK??��?��?�建�?�????�????以�?��??使�?��???????��?????
             Constants.Is_AutoCheckOpen = true;
        }
		
		String userId = ILogin.getLoginUid()+""; // ???�????�?�?�???��??
        //SDK�???��?��?��?????UploadHandler�????doUpload??��??�???��?��??以�?��?????己�?????�?�???��??己�??�???��?��??????????��????��?��????????SDK�???��??
        //SDK???�?认�????��??以�??�?Analytics.getDefaultUpload(this)??��?????
         UploadHandler hanlder = ExceptionUpload.getDefaultUpload(this);

         // ??????�?次�????��????????MonitorUploadHandler
         // hanlder =createMonitorUploadHandler(this);
         
          // APP使�?��??Eup ??? Eup_Gray???jar???�?
           //???�????1�?
          ExceptionUpload eup = ExceptionUpload.getInstance(this, userId, true, hanlder); //�?认�??�????�?常�??并�?????
          
           //???�????2�?
          // ExceptionUpload eup = ExceptionUpload.getInstance(this, userId, isStartAfterQuery, hanlder,true); //�????�?常�??并�?????

          // ???�?�?常�????��??注�?????�????�???��????????�????????????��??�??????��?????
          setExceptionUpload();

          // ???�?�?常�????��??注�?????�????�???��????????�????????????��??�??????��?????
          eup.setIsUseEup(true);
          */
		
	}
	
	

//	private void setExceptionUpload() {
//		/* isDefaultEup true表示�?常�????��?��???????��?��?��?��?��??�?�???��??false???�?弹�??????????��?��?��?��?��????��?????�???��?? */
//        ExceptionUpload.setDefaultEUP(true);
//
//        /* ?????��?��???????��?��?????�?常�?��?????�?�?�????�????以�??�?�?�???��?��?�置 */
//        ExceptionUpload.setYourUncaughtExceptionHandler(new UncaughtExceptionHandler()
//        {
//
//             @Override
//             public void uncaughtException(Thread thread, Throwable ex)
//             {
//                  // �???????????????????
//                  // ???�?次�??常�???????��????????
//                  ex.printStackTrace();
//             }
//        });
//
//        /*
//        * ???�???��?��???????��??�?常�??????????????信�????��????��?��?��?��??sdcard�?�???�便对�?��??�???��?��?��????��?��?��??�? �????�????�?�????�?�???????�?�?�????�?�?
//        * isStoreEupLogSdcard = true,
//        * 并确�????己�??�???��?��??此�?????�?android.permission.WRITE_EXTERNAL_STORAGE
//        * �?常�????????�?�??????��??顺�??�?�????sdcard�?�?并�??设置�????件大�??????��??�?�?导�?��??件�?????�?大�??
//        */
//        Constants.isStoreEupLogSdcard = true;
//	}

	public static void start() {
		if (APP_RUNNING == true) {
			return;
		}
		
		APP_RUNNING = true;

		try {
			UExceptionHandler UEHandler = new UExceptionHandler();
			Thread.setDefaultUncaughtExceptionHandler(UEHandler);
		} catch (SecurityException ex) {
			android.util.Log.e(LOG_TAG, "onCreate|" + ex.getMessage());
		}

		
		// Retrieve the version code.
		Context pContext = MyApplication.app.getApplicationContext();

        MyApplication.getVersionCode(pContext);

//        IAreav2.getAreaModels();
		
		// Create new instance of AppStorage
		mStorage = new AppStorage(pContext);
		
		mReportTag = "";
		
	}
	
	public static void getVersionCode(Context aContext) {
		if( (mVersionCode > 0) || (null == aContext) )
			return ;
		
		PackageInfo pInfo = null;
		try 
		{
			pInfo = aContext.getApplicationContext().getPackageManager().getPackageInfo(aContext.getApplicationContext().getPackageName(), 0);
		}
		catch (NameNotFoundException e) 
		{
			e.printStackTrace();
			pInfo = null;
		}
		mVersionCode = (null != pInfo ? pInfo.versionCode : 0);
	}

	public static void exit() {
		APP_RUNNING = false;
		
		if( null != mStorage ) {
			mStorage.save();
			mStorage = null;
		}
		
		
		DbFactory.closeDataBase();
		
		
		//clear static stuff
		UiUtils.clear();
		
		mReportTag = "";
				
		//ReloginWatcher.clear();
				
		//FullDistrictHelper.clear();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		DbFactory.closeDataBase();
		
		
	}

	public static void addActivity(BaseActivity ac)
	{
		if(!app.activityList.contains(ac))
			app.activityList.add(ac);
	}
	
	
	public static void removeActivity(BaseActivity ac)
	{
		app.activityList.remove(ac);
	}
	
	
	public static void exitall()
	{
		BaseActivity ac ;
		int len = app.activityList.size();
		for(int i=0; i < len; i++)
		{
			ac = app.activityList.get(i);
			if(ac != null)
				ac.finish();
		}
		
		exit();
	}
//	{
//		Context cont = MyApplication.app.getApplicationContext();
//		
//		ActivityManager am = (ActivityManager)cont.getSystemService(Context.ACTIVITY_SERVICE);
//		int anum = am.getRunningTasks(1).get(0).numActivities;
//		for(int idx = 0; idx < anum; idx++)
//		{
//			ComponentName at = am.getRunningTasks(1).get(0).topActivity;
//		}
//		System.exit(0);
//		
//		exit();
//	}
}
