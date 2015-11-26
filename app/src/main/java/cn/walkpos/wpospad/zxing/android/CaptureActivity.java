package cn.walkpos.wpospad.zxing.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.FloatMath;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.xingy.lib.ui.MyVerticalSeekBar;
import com.xingy.lib.ui.UiUtils;
import com.xingy.util.activity.BaseActivity;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.nio.charset.Charset;
import java.util.Vector;

import cn.walkpos.wpospad.R;
import cn.walkpos.wpospad.zxing.android.camera.CameraManager;


/**
 * Initial the camera
 * 
 * 
 */
public class CaptureActivity extends BaseActivity implements Callback {
    private static String TAG = "Barcode_CaptureActivity";
    private static final float BEEP_VOLUME = 0.10f;

    public static final int REQ_SCAN_CODE = 101;

    private Camera    mCamera;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private MyVerticalSeekBar zoomSeekBar;
	private Button zoomPlus;
	private Button zoomMinus;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	protected static final int REQUEST_CODE = 0;
	private boolean vibrate;
	private boolean isZoomSupport = false;
    private boolean isCameraReady = false;
    private ProgressDialog mProgressDialog;
    private boolean isUserCancelThread = false; // 标识用户是否手动取消的从相册识别的线程

    private Handler  mHandler = new Handler();
    private enum State {
        PREVIEW, SUCCESS, DONE
    }

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_capture);

        loadNavBar(R.id.scan_nav);
        Intent intent = getIntent();

		Window window = getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		CameraManager.init(getApplication());
		viewfinderView = (ViewfinderView) findViewById(R.id.viewfinder_view);
		zoomSeekBar = (MyVerticalSeekBar) findViewById(R.id.scan_zoom_seekbar);
		zoomPlus = (Button) findViewById(R.id.scan_zoom_btn_plus);
		zoomMinus = (Button) findViewById(R.id.scan_zoom_btn_minus);


		((SurfaceView) findViewById(R.id.preview_view)).setOnTouchListener(new OnTouchListener(){
			private float oldDist = 0;
			@TargetApi(Build.VERSION_CODES.ECLAIR) @SuppressLint("ClickableViewAccessibility")
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (!isCameraReady) {
					return true;
				}

				if (event.getPointerCount() != 2) {
					return true;
				}
				switch(event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        oldDist = spacing(event);
                        break;

                    case MotionEvent.ACTION_MOVE:
                        float newDist = spacing(event);
                        if (oldDist > 0) {
                                if (newDist - oldDist >= 10 || newDist - oldDist <= -10) {
                                int value = (int)(newDist - oldDist)/10;
                                try{
                                    CameraManager.get().setZoom(value);
                                    zoomSeekBar.setProgress(CameraManager.get().getCurrentZoomValue());
                                } catch (Exception e){
                                    e.printStackTrace();
                                }
                                oldDist = newDist;
                            }
                        } else {
                            oldDist = newDist;
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_POINTER_UP:
                        oldDist = 0;
                        break;
				}
				return true;
			}
			
			/** 
			 * 求两点间距离： 
			 */
			@TargetApi(Build.VERSION_CODES.ECLAIR) 
			private float spacing(MotionEvent event){
				float x = event.getX(0) - event.getX(1);
				float y = event.getY(0) - event.getY(1);
				return FloatMath.sqrt(x * x + y * y);
			}

			/** 
			 * 求两点间中点 
			 */
			@TargetApi(Build.VERSION_CODES.ECLAIR) @SuppressLint("NewApi") 
			private void midPoint(PointF point, MotionEvent event) {
				float x = event.getX(0) + event.getX(1);
				float y = event.getY(0) + event.getY(1);
				point.set(x/2, y/2);
			}
		});
		
		zoomSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (!isCameraReady) {
					return;
				}
                try{
                    CameraManager.get().setZoomValue(progress);
                }catch (Exception e){
                    e.printStackTrace();
                    return;
                }
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// TODO Auto-generated method stub
			}
			
		});
		
		hasSurface = false;
		inactivityTimer = new InactivityTimer(this);

        setResult(RESULT_CANCELED);

    }


	@Override
	protected void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) findViewById(R.id.preview_view);
		viewfinderView.setVisibility(View.VISIBLE);
		final SurfaceHolder surfaceHolder = surfaceView.getHolder();
		if (hasSurface) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    initCamera(surfaceHolder,false);
                }
            });
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getSystemService(AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL) {
			playBeep = false;
		}

        try{
            initBeepSound();
        }
        catch (IllegalStateException e){
            e.printStackTrace();
        }

        vibrate = true;

        if (!isCameraReady) {
            zoomSeekBar.setVisibility(View.INVISIBLE);
            zoomPlus.setVisibility(View.INVISIBLE);
            zoomMinus.setVisibility(View.INVISIBLE);
        }

    }

	@Override
	protected void onPause() {
		super.onPause();
		if (handler != null) {
			handler.quitSynchronously();
			handler.clear();
			handler = null;
		}

        try{
            CameraManager.get().closeDriver();
        }catch (Exception e){
            e.printStackTrace();
        }
	}

	/*
	 * （非 Javadoc）
	 * 
	 * @see android.app.Activity#onStop()
	 */
	@Override
	protected void onStop() {
		// TODO 自动生成的方法存根
		super.onStop();

        //onstop 来进行关闭。 618 活动web页取照片解决问题 yaoxing6
        inactivityTimer.shutdown();

    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
        //onstop 来进行关闭。 618 活动web页取照片解决问题  yaoxing6
//        inactivityTimer.shutdown();

        viewfinderView = null;
        if(null!=mediaPlayer)
		    mediaPlayer.release();
		mediaPlayer = null;
        if(null!=mProgressDialog && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
        mProgressDialog = null;
//		mCaptureActivity = null;
	}

	/**
	 * 处理扫描结果
	 * 
	 * @param result
	 */
	public void handleDecode(Result result) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String resultString = result.getText();
		String formatString = result.getBarcodeFormat().name();
		if (resultString.equals("")) {
            UiUtils.makeToast(CaptureActivity.this, "Scan failed!");
		} else {
			Intent resultIntent = new Intent();
			resultIntent.putExtra(Intents.Scan.RESULT, resultString);
            resultIntent.putExtra(Intents.Scan.RESULT_FORMAT, formatString);
			resultIntent.putExtra(Intents.Scan.ACTION, 1);
			setResult(RESULT_OK,resultIntent);
            finish();

        }

//		CaptureActivity.this.finish();
	}

    /**
     * 重启扫码线程
     *
     * NOTE:
     * 扫到文本或非白名单URL之后，在扫码页面弹出提示框，需要在提示框消失的时候重启扫码线程，在BarcodeUtils类中调用
     *
     */
    public void restartDecodeThread() {
        if (handler != null) {
            handler.quitSynchronously();
            handler.clear();
            handler = new CaptureActivityHandler(decodeFormats, characterSet);
        }
    }






    private void initCamera(SurfaceHolder surfaceHolder,boolean front) {
		try {

            CameraManager.get().openDriver(surfaceHolder,front);
            int maxSeekBarProgress = CameraManager.get().getMaxZoomValue();
            if (maxSeekBarProgress > 0) {
				isZoomSupport = true;
				zoomSeekBar.setMax(maxSeekBarProgress);
				zoomSeekBar.setProgress(0);
				viewfinderView.drawZoomBar(zoomSeekBar, zoomPlus, zoomMinus);
			}

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                zoomSeekBar.setVisibility(View.VISIBLE);
                zoomPlus.setVisibility(View.VISIBLE);
                zoomMinus.setVisibility(View.VISIBLE);
            }

            isCameraReady = true;
		} catch (IOException ioe) {
            UiUtils.makeToast(CaptureActivity.this, getString(R.string.please_check_camera_permission));
            isCameraReady = false;
			return;
		} catch (RuntimeException e) {
            UiUtils.makeToast(CaptureActivity.this, getString(R.string.please_check_camera_permission));
            isCameraReady = false;
			return;
		}
		if (handler == null) {
			handler = new CaptureActivityHandler(decodeFormats, characterSet);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(final SurfaceHolder holder) {
        if (!hasSurface) {
			hasSurface = true;
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    initCamera(holder,false);
                }
            });
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
        hasSurface = false;
	}

	public ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	public Handler getHandler() {
		return handler;
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();

	}

	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};
	protected String photo_path;

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
//			case REQUEST_CODE:
//                if(null != data) {
//                    Uri uri = data.getData();
//                    if (uri != null) {
//                        photo_path = getPath(CaptureActivity.this, uri);
//
//                        if (null == mProgressDialog) {
//                            mProgressDialog = new ProgressDialog(this);
//                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                            mProgressDialog.setMessage("R.string.alert_message_scanning");
//                            mProgressDialog.setCanceledOnTouchOutside(false);
//                            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                                @Override
//                                public void onCancel(DialogInterface dialog) {
//                                    isUserCancelThread = true;
//                                }
//                            });
//                        }
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                // TODO Auto-generated method stub
//                                isUserCancelThread = false;
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mProgressDialog.show();
//                                    }
//                                });
//
//                                Result result = scanningImage(photo_path);
//
//                                mHandler.post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mProgressDialog.dismiss();
//                                    }
//                                });
//
//                                if (!isUserCancelThread) {
//                                    playBeepSoundAndVibrate();
//                                    if (null == result) {
//                                        Looper.prepare();
//                                        UiUtils.makeToast(CaptureActivity.this, "R.string.camera_picture_error_format");
//                                        Looper.loop();
//                                    } else {
//                                        String recode = recode(result.toString());
//                                        String formatString = result.getBarcodeFormat().name();
//                                        UiUtils.makeToast(CaptureActivity.this,recode);
//                                    }
//                                }
//                            }
//                        }).start();
//                    } else {
//                        UiUtils.makeToast(CaptureActivity.this, "R.string.alert_message_wrong_file_select");
//                    }
//                }
//				break;
			}
		}
	}
	
	private String recode(String str) {
		String format = "";
		
		try {
			boolean ISO = Charset.forName("ISO-8859-1").newEncoder().canEncode(str);
			if (ISO) {
				format = new String(str.getBytes("ISO-8859-1"), "GB2312");
			} else {
				format = str;
			}
			
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return format;
	}

    protected Result scanningImage(String path) {
		if (TextUtils.isEmpty(path)) {
            return null;
		}

        File file = new File(path);
        if(null == file || !file.exists()){
            return null;
        }

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
		options.inJustDecodeBounds = false;

        MultiFormatReader reader = new MultiFormatReader();
        RGBLuminanceSource source = null;
        BinaryBitmap bitmap1 = null;

        /**
         * 如果文件大于2MB，就从8开始尝试，直到1；否则从1开始尝试直到8
         * 后续可以对这里的参数进行微调，比如尝试sampleSize=16等等
         */
        int[] arraySampleSizeForFileSmallerThan2MB = {1, 2, 4, 8};
        int[] arraySampleSizeForFileBiggerThan2MB = {8, 4, 2, 1};

        int[] arraySampleSize = (file.length() >= 2*1024*1024) ? arraySampleSizeForFileBiggerThan2MB : arraySampleSizeForFileSmallerThan2MB;

        Result result = null;
        for (int i = 0; i < arraySampleSize.length; i++) {
            if (isUserCancelThread) {
                break;
            }

            options.inSampleSize = arraySampleSize[i];
            try {
                scanBitmap = BitmapFactory.decodeFile(path, options);
                source = new RGBLuminanceSource(scanBitmap);
                bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
                scanBitmap.recycle();
                if (isUserCancelThread) {
                    break;
                }

                result = reader.decode(bitmap1);
            }
            catch (NotFoundException e) {
                result = null;
            }
            catch (Exception e) {
                UiUtils.makeToast(CaptureActivity.this, "解析图片失败");
                result = null;
            }
            catch (OutOfMemoryError e){
                UiUtils.makeToast(CaptureActivity.this, "内存空间不足");
                result = null;
            }

            if (null != result) {
                break;
            }
        }

		return result;
	}

    /**
     *  Convert uri to file path for 4.4 or later
     * @param context
     * @param uri
     * @return
     */
//    public static String getPath(final Context context, final Uri uri) {
//
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//
//        // DocumentProvider
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // ExternalStorageProvider
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//
//                // TODO handle non-primary volumes
//            }
//            // DownloadsProvider
//            else if (isDownloadsDocument(uri)) {
//
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);
//            }
//            // MediaProvider
//            else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[] {
//                        split[1]
//                };
//
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
//        }
//        // MediaStore (and general)
//        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//
//            // Return the remote address
//            if (isGooglePhotosUri(uri))
//                return uri.getLastPathSegment();
//
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//
//        return null;
//    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        }
        catch (IllegalStateException e){

        }
        finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }



    /**
     * This class handles all the messaging which comprises the state machine for
     * capture.
     */
    public final class CaptureActivityHandler extends Handler {

        private DecodeThread decodeThread;
        private State state;
        private SoftReference<CaptureActivity> softReference;

        public CaptureActivityHandler(Vector<BarcodeFormat> decodeFormats, String characterSet) {
            softReference = new SoftReference<CaptureActivity>(CaptureActivity.this);

            CaptureActivity captureActivity;
            if(softReference!=null){
                captureActivity = softReference.get();
                if(captureActivity == null){
                    captureActivity = CaptureActivity.this;
                    softReference = new SoftReference<CaptureActivity>(CaptureActivity.this);
                }
            }else{
                captureActivity = CaptureActivity.this;
                softReference = new SoftReference<CaptureActivity>(CaptureActivity.this);
            }

            decodeThread = new DecodeThread(captureActivity, decodeFormats, characterSet,
                    new ViewfinderResultPointCallback(CaptureActivity.this.getViewfinderView()));
            decodeThread.start();
            state = State.SUCCESS;
            // Start ourselves capturing previews and decoding.
            try{
                CameraManager.get().startPreview();
            }catch (Exception e){

            }
            restartPreviewAndDecode();
        }

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case R.id.auto_focus:
                    // Log.d(TAG, "Got auto-focus message");
                    // When one auto focus pass finishes, start another. This is the
                    // closest thing to
                    // continuous AF. It does seem to hunt a bit, but I'm not sure what
                    // else to do.
                    if (state == State.PREVIEW) {
                        try{
                            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                    break;
                case R.id.restart_preview:
                    restartPreviewAndDecode();
                    break;
                case R.id.decode_succeeded:
                    state = State.SUCCESS;
                    Bundle bundle = message.getData();

                    /***********************************************************************/
                    // Bitmap barcode = bundle == null ? null : (Bitmap) bundle
                    // .getParcelable(DecodeThread.BARCODE_BITMAP);

                    CaptureActivity.this.handleDecode((Result) message.obj);
                    break;
                case R.id.decode_failed:
                    // We're decoding as fast as possible, so when one decode fails,
                    // start another.
                    state = State.PREVIEW;
                    try{
                        CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                    }catch (Exception e){
                        e.printStackTrace();
                        return;
                    }
                    break;
                case R.id.return_scan_result:
                    CaptureActivity.this.setResult(Activity.RESULT_OK, (Intent) message.obj);
                    CaptureActivity.this.finish();
                    break;
                case R.id.launch_product_query:
                    String url = (String) message.obj;
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    CaptureActivity.this.startActivity(intent);
                    break;
            }
        }

        public void quitSynchronously() {
            state = State.DONE;
            try{
                CameraManager.get().stopPreview();
            }catch (Exception e){
                e.printStackTrace();
            }
            Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
            quit.sendToTarget();
            try {
                decodeThread.join();
            } catch (InterruptedException e) {
                // continue
            }

            // Be absolutely sure we don't send any queued up messages
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
        }

        private void restartPreviewAndDecode() {
            if (state == State.SUCCESS) {
                state = State.PREVIEW;
                try{
                    CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }catch (Exception e){
                    e.printStackTrace();
                }
                CaptureActivity.this.drawViewfinder();
            }
        }


        public void clear()
        {
            if(null!=decodeThread)
                decodeThread.interrupt();
            decodeThread = null;
        }
    }
}