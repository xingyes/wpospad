package cn.walkpos.wpospad.util;

import android.content.Context;

import com.tencent.mm.sdk.constants.Build;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.xingy.lib.ui.UiUtils;
import com.xingy.share.ShareInfo;

import org.json.JSONException;
import org.json.JSONObject;

import cn.walkpos.wpospad.main.WPosApplication;

/**
 * 微信相关的API
 */
public class WeixinUtil {

	private static final String TAG = "WeixinUtil";

	/**
	 * 唯一标识，应用的id，在微信开放平台注册时获得
	 */
	private static final String APP_ID = "1212121";

	public static final int WX_SHARE_IMG_THUMB_SIZE = 120; // 微信分享图片尺寸大小
	public static final int WX_SHARE_IMG_LIMIT = 32 * 1024; // 微信分享图片文件大小限制，32K
	public static final int WX_SHARE_TITLE_LIMIT = 512; // 微信分享标题长度限制
	public static final int WX_SHARE_DESCRIPTION_LIMIT = 1024; // 微信分享内容长度限制

	/**
	 * 微信api接口类
	 */
	private static IWXAPI wxApi;

	/**
	 * 向微信创建注册app
	 *
	 * @param context
	 */
	public static void createAndRegisterWX(Context context) {
		try {
			wxApi = WXAPIFactory.createWXAPI(context, APP_ID);
			wxApi.registerApp(APP_ID);
		} catch (Exception e) {
	        e.printStackTrace();
		}
	}

	/**
	 * 向微信创建注册app，校验签名方式
	 *
	 * @param context
	 * @param checkSign
	 */
	public static void createAndRegisterWX(Context context, Boolean checkSign) {
		try {
			wxApi = WXAPIFactory.createWXAPI(context, APP_ID, checkSign);
			wxApi.registerApp(APP_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * 获得微信API接口
	 *
	 * @return
	 */
	public static IWXAPI getWXApi() {
		if (wxApi == null) {
			createAndRegisterWX(WPosApplication.app, true);
		}
		return wxApi;
	}

	/**
	 * 是否安装了微信客户端
	 *
	 * @return
	 */
	public static boolean isWXInstalled() {
		return getWXApi().isWXAppInstalled();
	}

	/**
	 * 当前微信客户端是否支持微信支付
	 *
	 * @return
	 */
	public static boolean isWXSupportPay() {
		return getWXApi().getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
	}

	/**
	 * 当前微信客户端是否支持分享
	 *
	 * @return
	 */
	public static boolean isWXSupportShare() {
		return getWXApi().isWXAppSupportAPI();
	}

	/**
	 * 检查是否可以分享，不能分享则给予提示
	 *
	 * @return
	 */
	public static boolean check() {
		if (!WeixinUtil.isWXInstalled() || !WeixinUtil.isWXSupportShare()) {
            UiUtils.makeToast(WPosApplication.app, com.xingy.R.string.install_newest_weixin);
			return false;
        }
        return true;
	}

	/**
	 * 微信登录
	 */
	public static void doWXLogin() {
		SendAuth.Req req = new SendAuth.Req();
		req.scope = "snsapi_userinfo";
		req.state = "jdlogin";

		getWXApi().sendReq(req);
	}

	/**
	 * 微信支付
	 *
	 * @param json
	 */
	public static void doWXPay(final JSONObject json) {
		PayReq req = new PayReq();
		req.appId = APP_ID;
		req.partnerId = json.optString("partnerId");//商家向财付通申请的商家id
		req.prepayId = json.optString("prepayId");//预支付订单
		req.nonceStr = json.optString("nonceStr");//随机串，防重发
		req.timeStamp = json.optString("timeStamp");//时间戳，防重发
		req.packageValue = json.optString("package");//商家根据财付通文档填写的数据和签名
		req.sign = json.optString("sign");//商家根据微信开放平台文档对数据做的签名

		getWXApi().sendReq(req);
	}

	/**
	 * 微信分享
	 *
	 * @param shareInfo
	 * @param isScene, true:好友; false:朋友圈
	 */
	public static void doWXShare(ShareInfo shareInfo, boolean isScene) {
		if (shareInfo.title.length() > WX_SHARE_TITLE_LIMIT) {
			shareInfo.title = shareInfo.title.substring(0, (WX_SHARE_TITLE_LIMIT - 3)) + "...";
		}
		if (shareInfo.wxcontent.length() > WX_SHARE_DESCRIPTION_LIMIT) {
			shareInfo.wxcontent = (shareInfo.wxcontent
					.substring(0, (WX_SHARE_DESCRIPTION_LIMIT - 3)) + "...");
		}
		if (shareInfo.wxMomentsContent.length() > WX_SHARE_DESCRIPTION_LIMIT) {
			shareInfo.wxMomentsContent = (shareInfo.wxMomentsContent
					.substring(0, (WX_SHARE_DESCRIPTION_LIMIT - 3)) + "...");
		}

		ShareUtil.checkShareLogo(shareInfo, WX_SHARE_IMG_LIMIT);

		String webpageUrl, description;
		if (isScene) {
			description = shareInfo.wxcontent;
		} else {
			description = shareInfo.wxMomentsContent;
		}

		WXWebpageObject webPageObj = new WXWebpageObject();
		webPageObj.webpageUrl = shareInfo.url;

		WXMediaMessage wxMsg = new WXMediaMessage();
		wxMsg.mediaObject = webPageObj;
		wxMsg.title = shareInfo.title;
		wxMsg.thumbData = shareInfo.shareLogo;
		wxMsg.description = description;

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = shareInfo.transaction;
		req.message = wxMsg;
		req.scene = isScene ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;

		getWXApi().sendReq(req);
	}
}
