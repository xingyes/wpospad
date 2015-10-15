package cn.walkpos.wpospad.util;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.xingy.lib.ui.UiUtils;
import com.xingy.share.ShareInfo;
import com.xingy.util.activity.BaseActivity;

import java.util.HashMap;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.main.MainActivity;
import cn.walkpos.wpospad.main.WPosApplication;

public class ShareUtil {

	private final static String TAG = "ShareUtil";

	public final static String F_QQ = "qq";
	public final static String F_QZONE = "qzone";
	public final static String F_WEIBO = "weibo";
	public final static String F_WEIXIN = "weixin";
    public final static String F_TIMELINE = "timeline";

	private static HashMap<String, Object> mCallbackList = new HashMap<String, Object>();

	/**
	 * 供分享回调的接口
	 */
	public interface CallbackListener {
		void onComplete(Object obj);
		void onError(String msg);
		void onCancel();
	}


	/**
	 * 发起分享
	 *
	 * @param shareInfo
	 * @param channel
	 * @param listener
	 */
	public static void sendShare(BaseActivity activity,ShareInfo shareInfo, String channel, CallbackListener listener,ImageLoader imgloader) {
		if (TextUtils.isEmpty(shareInfo.url)) {
			return;
        }

		WeiboUtil.createWBApi(WPosApplication.app);

		setShareInfo(shareInfo, listener);

		doShare(activity,shareInfo, channel,imgloader);
	}

	/**
	 * 分享完成回调
	 *
	 * @param transaction
	 * @param obj
	 */
	public static void shareComplete(String transaction, Object obj) {
		getCallbackListener(transaction).onComplete(obj);
	}

	/**
	 * 分享失败回调
	 *
	 * @param transaction
	 * @param msg
	 */
	public static void shareError(String transaction, String msg, String flag) {
		getCallbackListener(transaction).onError(msg);
	}

	/**
	 * 分享取消回调
	 *
	 * @param transaction
	 */
	public static void shareCancel(String transaction, String flag) {
		getCallbackListener(transaction).onCancel();
	}

	/**
	 * 验证分享图片
	 *
	 * @param shareInfo
	 * @param limit
	 */
	public static void checkShareLogo(ShareInfo shareInfo, int limit) {
		if (shareInfo.shareLogo == null || shareInfo.shareLogo.length > limit) {
			Drawable drawable = WPosApplication.app.getResources().getDrawable(R.mipmap.ic_launcher);
			shareInfo.setShareLogoDefault(((BitmapDrawable) drawable).getBitmap(), limit);
		}
		if (shareInfo.shareLogo.length > limit) {
			UiUtils.makeToast(WPosApplication.app, "The share image is too big.");
		}
	}



	/**
	 * 梳理分享内容
	 *
	 * @param shareInfo
	 * @param listener
	 */
	private static void setShareInfo(ShareInfo shareInfo, CallbackListener listener) {
		// 若为空设置默认值
		if (TextUtils.isEmpty(shareInfo.title)) {
			shareInfo.title=(WPosApplication.app.getResources()
					.getString(R.string.app_name));
		}
		if (TextUtils.isEmpty(shareInfo.summary)) {
			shareInfo.summary = WPosApplication.app.getResources()
                    .getString(R.string.share_defaut_summary);
		}
		if (TextUtils.isEmpty(shareInfo.wxcontent)) {
			shareInfo.wxcontent= shareInfo.summary;
		}
		if (TextUtils.isEmpty(shareInfo.wxMomentsContent)) {
			shareInfo.wxMomentsContent = shareInfo.summary;
		}
		if (TextUtils.isEmpty(shareInfo.normalText)) {
			shareInfo.normalText = shareInfo.title + " " + shareInfo.summary + " " + shareInfo.url;
		}


		shareInfo.transaction = String.valueOf(System.currentTimeMillis());
		// 存放回调接口
		if (listener != null) {
			mCallbackList.put(shareInfo.transaction, listener);
		}
	}


	/**
	 * 拉取分享图片后再进行分享
	 *
	 * @param shareInfo
	 * @param shareSend
	 */
	private static void loadImageShare(final ShareInfo shareInfo,
									   final int shareIconSize, final Runnable shareSend,
                                       ImageLoader imgLoader) {
		// 若已设置了分享的Bitmap或分享图片url为空，则提交分享
		if (shareInfo.getShareLogo() != null || TextUtils.isEmpty(shareInfo.iconUrl)) {
			shareSend.run();
			return;
		}


        Bitmap bitmap = null;
        final int nMaxSize = 80;
        imgLoader.get(shareInfo.iconUrl,new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap bm = response.getBitmap();
                if(bm==null)
                {
                    shareSend.run();
                    return;
                }
                bm = com.xingy.util.ImageLoader.resize(bm, nMaxSize);

                shareInfo.setShareLogo(bm);
                shareSend.run();

            }

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
	}

	/**
	 * 发起分享
	 *
	 * @param shareInfo
	 * @param channel
	 */
	private static void doShare(BaseActivity activity,final ShareInfo shareInfo, String channel,ImageLoader imgloader) {
		if (channel.equals(F_WEIXIN) && WeixinUtil.check()) {
			loadImageShare(shareInfo, WeixinUtil.WX_SHARE_IMG_THUMB_SIZE, new Runnable() {
				@Override
				public void run() {
					WeixinUtil.doWXShare(shareInfo, true);
				}
			},imgloader);
		} else if (channel.equals(F_TIMELINE) && WeixinUtil.check()) {
			loadImageShare(shareInfo, WeixinUtil.WX_SHARE_IMG_THUMB_SIZE, new Runnable() {
				@Override
				public void run() {
					WeixinUtil.doWXShare(shareInfo, false);
				}
			},imgloader);
		} else if (channel.equals(F_WEIBO) && WeiboUtil.check()) {
			loadImageShare(shareInfo, WeiboUtil.WB_SHARE_IMG_THUMB_SIZE, new Runnable() {
				@Override
				public void run() {
					WeiboUtil.doWBShare(shareInfo);
				}
			},imgloader);
		} else if (channel.equals(F_QQ) && QQUtil.check()) {
			QQUtil.shareToQQ(activity,shareInfo);
		} else if (channel.equals(F_QZONE) && QQUtil.check()) {
			QQUtil.shareToQZone(activity,shareInfo);
		}
	}

	/**
	 * 根据分享事务ID取回调处理，若无则用通用处理
	 *
	 * @param transaction
	 * @return
	 */
	private static CallbackListener getCallbackListener(String transaction) {
		CallbackListener callbackListener;
		if (mCallbackList.containsKey(transaction)) {
			callbackListener = (CallbackListener) mCallbackList.get(transaction);
			mCallbackList.remove(transaction); // 移除已回调过的
		} else {
			callbackListener = new CallbackListener() {
				@Override
				public void onComplete(Object obj) {
					UiUtils.makeToast(WPosApplication.app,"Share succ");
				}

				@Override
				public void onError(String msg) {
                    UiUtils.makeToast(WPosApplication.app,"Share fail");
                }

				@Override
				public void onCancel() {
                    UiUtils.makeToast(WPosApplication.app,"Share cancel");
                }
			};
		}
		return callbackListener;
	}



    public static String getIconUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return MainActivity.imgtesturl;

        } else {
            return url;
        }
    }
}
