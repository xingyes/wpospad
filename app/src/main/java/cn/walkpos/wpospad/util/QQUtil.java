package cn.walkpos.wpospad.util;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;

import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.xingy.lib.ui.UiUtils;
import com.xingy.share.ShareInfo;
import com.xingy.util.activity.BaseActivity;

import java.util.ArrayList;

import cn.walkpos.wpospad.main.MainActivity;
import cn.walkpos.wpospad.main.WPosApplication;

public class QQUtil {

	private static final String TAG = "QQUtil";

	private static final int QQ_SHARE_TITLE_LIMIT = 30; // 分享到QQ标题限制长度
	private static final int QQ_SHARE_SUMMARY_LIMIT = 30; // 分享到QQ摘要限制长度
	private static final int QZONE_SHARE_TITLE_LIMIT = 200; // 分享到QZone标题限制长度
	private static final int QZONE_SHARE_SUMMARY_LIMIT = 600; // 分享到QZone摘要限制长度

	/**
	 * 唯一标识，应用的id，在QQ开放平台注册时获得
	 */
	private static final String APP_ID = "100273020";

	private static Tencent mTencent;

	/**
	 * 获得Tencent实例
	 *
	 * @return
	 */
	public static Tencent getTencentInstance() {
		if (mTencent == null) {
			mTencent = Tencent.createInstance(APP_ID,
					WPosApplication.app);
		}
		return mTencent;
	}

	/**
	 * 检查是否可以分享，不能分享则给予提示
	 *
	 * @return
	 */
	public static boolean check() {
//		try {
//			WPosApplication.app.getPackageManager()
//					.getApplicationInfo("com.tencent.mobileqq",
//							PackageManager.GET_UNINSTALLED_PACKAGES);
//			return true;
//		} catch (PackageManager.NameNotFoundException e) {
//            UiUtils.makeToast(WPosApplication.app, "No QQ installed");
//			return false;
//		}
        return true;
	}

	/**
	 * 分享到QQ好友
	 *
	 * @param shareInfo
	 */
	public static void shareToQQ(BaseActivity activity,ShareInfo shareInfo) {
        if (shareInfo.title.length() > QQ_SHARE_TITLE_LIMIT) {
            shareInfo.title = shareInfo.title.substring(0, (QQ_SHARE_TITLE_LIMIT - 3)) + "...";
        }
        if (shareInfo.summary.length() > QQ_SHARE_SUMMARY_LIMIT) {
            shareInfo.summary = shareInfo.summary.substring(0, (QQ_SHARE_SUMMARY_LIMIT - 3)) + "...";
        }
		final Bundle params = new Bundle();
		params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
		params.putInt(QQShare.SHARE_TO_QQ_EXT_INT, QQShare.SHARE_TO_QQ_FLAG_QZONE_ITEM_HIDE);
		params.putString(QQShare.SHARE_TO_QQ_TARGET_URL,shareInfo.url);
		params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.title);
		params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.summary);
		params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, ShareUtil.getIconUrl(shareInfo.iconUrl));
		params.putString(QQShare.SHARE_TO_QQ_APP_NAME, "返回应用");

		BaseUiListener listener = new BaseUiListener();
		listener.flag = ShareUtil.F_QQ;
		listener.transaction = shareInfo.transaction;

		getTencentInstance().shareToQQ(activity, params, listener);
	}

	/**
	 * 分享到QQ空间
	 *
	 * @param shareInfo
	 */
	public static void shareToQZone(BaseActivity activity,ShareInfo shareInfo) {
        if (shareInfo.title.length() > QQ_SHARE_TITLE_LIMIT) {
            shareInfo.title = shareInfo.title.substring(0, (QQ_SHARE_TITLE_LIMIT - 3)) + "...";
        }
        if (shareInfo.summary.length() > QQ_SHARE_SUMMARY_LIMIT) {
            shareInfo.summary = shareInfo.summary.substring(0, (QQ_SHARE_SUMMARY_LIMIT - 3)) + "...";
        }

        ArrayList<String> arrayList = new ArrayList();
        arrayList.add(ShareUtil.getIconUrl(shareInfo.iconUrl));

        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareInfo.title);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareInfo.summary);
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, ShareUtil.getIconUrl(shareInfo.iconUrl));
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL,shareInfo.url);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, arrayList);

        BaseUiListener listener = new BaseUiListener();
        listener.flag = ShareUtil.F_QZONE;
        listener.transaction = shareInfo.transaction;

        getTencentInstance().shareToQzone(activity, params, listener);
    }


	/**
	 * 检查分享图片url，若为空则用默认的url
	 *
	 * @param url
	 */


	/**
	 * 分享回调接口
	 */
	private static class BaseUiListener implements IUiListener {
		public String flag; // 分享的渠道
		public String transaction; // 分享事务ID，作为回调凭证

		@Override
		public void onComplete(Object obj) {
			ShareUtil.shareComplete(transaction, flag);
		}

		@Override
		public void onError(UiError e) {
			ShareUtil.shareError(transaction, e.errorMessage, flag);
		}

		@Override
		public void onCancel() {
			ShareUtil.shareCancel(transaction, flag);
		}
	}
}
