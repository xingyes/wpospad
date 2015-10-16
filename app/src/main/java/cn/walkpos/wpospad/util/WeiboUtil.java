package cn.walkpos.wpospad.util;

import android.content.Context;

import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.utils.Utility;
import com.xingy.lib.ui.UiUtils;
import com.xingy.share.ShareInfo;

import cn.walkpos.wpospad.main.WPosApplication;

public class WeiboUtil {

	private static final String TAG = "WeiboUtil";

	public static final int WB_SHARE_IMG_THUMB_SIZE = 120; // 微博分享图片尺寸大小
	public static final int WB_SHARE_IMG_LIMIT = 32 * 1024; // 微博分享图片文件大小限制
	public static final int WB_SHARE_TITLE_LIMIT = 60; // 微信分享标题长度限制
	public static final int WB_SHARE_SUMMARY_LIMIT = 80; // 微信分享内容长度限制

	/**
	 * 唯一标识，应用的id，在微博开放平台注册时获得
	 */
	private static final String APP_ID = "3677796771";

	/**
	 * 微博微博分享接口实例
	 */
	private static IWeiboShareAPI mWBShareApi;

	/**
	 * 创建微博分享接口实例
	 * 注册第三方应用到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
	 * NOTE：请务必提前注册，即界面初始化的时候或是应用程序初始化时，进行注册
	 *
	 * @param context
	 */
	public static void createWBApi(Context context) {
		try {
			mWBShareApi = WeiboShareSDK.createWeiboAPI(context, APP_ID, false);
			mWBShareApi.registerApp();
		} catch (Exception e) {
				e.printStackTrace();
		}
	}

	/**
	 * 获取微博分享接口实例，若为空则新建
	 *
	 * @return
	 */
	public static IWeiboShareAPI getWBShareApi() {
		if (mWBShareApi == null) {
			createWBApi(WPosApplication.app);
		}
		return mWBShareApi;
	}

	/**
	 * 是否安装了微博客户端
	 *
	 * @return
	 */
	public static boolean isWBInstalled() {
		return getWBShareApi().isWeiboAppInstalled();
	}

	/**
	 * 当前微博客户端是否支持分享
	 *
	 * @return
	 */
	public static boolean isWBSupportShare() {
		return getWBShareApi().isWeiboAppSupportAPI();
	}

	/**
	 * 检查是否可以分享，不能分享则给予提示
	 *
	 * @return
	 */
	public static boolean check() {
		if (!WeiboUtil.isWBInstalled() || !WeiboUtil.isWBSupportShare()) {
			UiUtils.makeToast(WPosApplication.app, "Can't Share Weibo");
			return false;
		}
		return true;
	}

	/**
	 * 发起分享
	 *
	 * @param shareInfo
	 */
	public static void doWBShare(ShareInfo shareInfo) {
		if (shareInfo.title.length() > WeiboUtil.WB_SHARE_TITLE_LIMIT) {
			shareInfo.title = shareInfo.title.substring(0, (WeiboUtil.WB_SHARE_TITLE_LIMIT - 3)) + "...";
		}
		if (shareInfo.summary.length() > WeiboUtil.WB_SHARE_SUMMARY_LIMIT) {
			shareInfo.summary = shareInfo.summary.substring(0, (WeiboUtil.WB_SHARE_SUMMARY_LIMIT - 3)) + "...";
		}

		ShareUtil.checkShareLogo(shareInfo, WB_SHARE_IMG_LIMIT);

		String shareTitle = shareInfo.title;

		WebpageObject obj = new WebpageObject();
		obj.identify = Utility.generateGUID();
		obj.title = shareTitle;
		obj.actionUrl = shareInfo.url;
		obj.defaultText = shareTitle;
		obj.setThumbImage(shareInfo.getShareLogo());

		if (WeiboUtil.getWBShareApi().getWeiboAppSupportAPI() >= 10351) {
			TextObject textObject = new TextObject();
			textObject.text = shareInfo.summary;

			ImageObject imageObject = new ImageObject();
			imageObject.setImageObject(shareInfo.getShareLogo());

			obj.description = shareInfo.summary;

			sendMultiMessage(textObject, imageObject, obj, shareInfo.transaction);
		} else {
			// 为呈现需要的效果做适配处理
			obj.description = shareInfo.summary + shareTitle;

			sendMessage(obj, shareInfo.transaction);
		}
	}

	/**
	 * 当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时调用
	 *
	 * @param obj
	 * @param transaction
	 */
	private static void sendMessage(WebpageObject obj, String transaction) {
		WeiboMessage msg = new WeiboMessage();
		msg.mediaObject = obj;

		SendMessageToWeiboRequest req = new SendMessageToWeiboRequest();
		req.transaction = transaction;
		req.message = msg;

		WeiboUtil.getWBShareApi().sendRequest(req);
	}

	/**
	 * 当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时调用
	 *
	 * @param textObject
	 * @param imageObject
	 * @param obj
	 * @param transaction
	 */
	private static void sendMultiMessage(TextObject textObject, ImageObject imageObject,
								  WebpageObject obj, String transaction) {
		WeiboMultiMessage msg = new WeiboMultiMessage();
		msg.textObject = textObject;
		msg.imageObject = imageObject;
		msg.mediaObject = obj;

		SendMultiMessageToWeiboRequest req = new SendMultiMessageToWeiboRequest();
		req.transaction = transaction;
		req.multiMessage = msg;

		WeiboUtil.getWBShareApi().sendRequest(req);
	}
}
