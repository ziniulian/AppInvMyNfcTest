package com.invengo.test.mynfc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.invengo.test.mynfc.entity.Web;
import com.invengo.test.mynfc.enums.EmUh;
import com.invengo.test.mynfc.enums.EmUrl;

import tk.ziniulian.job.nfc.NfcUtils;
import tk.ziniulian.util.Str;

import static tk.ziniulian.job.nfc.NfcUtils.mNfcAdapter;

public class Ma extends AppCompatActivity {
	public static final int SCAN_ACTINT = 301;
	public static final int SCAN_PRM = 302; // 相机权限
	public static final int SDRW_PRM = 303;	// 内存卡读写权限

	private Handler uh = new UiHandler();
	private WebView wv;
	private Web w = new Web(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ma);

		wv = (WebView)findViewById(R.id.wv);
		wv = (WebView)findViewById(R.id.wv);
		WebSettings ws = wv.getSettings();
		ws.setDefaultTextEncodingName("UTF-8");
		ws.setJavaScriptEnabled(true);
		wv.addJavascriptInterface(w, "rfdo");
		wv.setWebViewClient(new WebViewClient() {
			@Override
			public boolean shouldOverrideUrlLoading(WebView v, String url) {
				v.loadUrl(url);
				return true;
			}
		});

		mNfcAdapter = NfcUtils.NfcCheck(this);
		if (mNfcAdapter == null) {
			// 设备不支持 NFC 功能
			sendUrl(EmUrl.Err);
		} else {
			// NFC功能可用
			NfcUtils.NfcInit(this);

			// 检查存储权限
			if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
				sendUrl(EmUrl.Home);
			} else {
				ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, SDRW_PRM);
				sendUrl(EmUrl.Err2);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (mNfcAdapter != null) {
			mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
		}
	}

	//在onNewIntent中处理由NFC设备传递过来的intent
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		w.setItn(intent);
		sendUrl(EmUrl.Onn);
	}

	// 接收扫码结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SCAN_ACTINT && resultCode == RESULT_OK) {
			sendUrl(EmUrl.Scan, data.getStringExtra("scnDat"));
		}
	}

	// 监听权限申请返回结果
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		switch (requestCode) {
			case SCAN_PRM:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					// 权限被用户同意，可以做你要做的事情了。
					scan();
				}
			break;
			case SDRW_PRM:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					sendUrl(EmUrl.Home);
				}
				break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				EmUrl e = getCurUi();
				if (e != null) {
					switch (e) {
						case Exit:
							return super.onKeyDown(keyCode, event);
						default:
							sendUrl(EmUrl.Back);
							return true;
					}
				} else if (wv.canGoBack()) {
					wv.goBack();
					return true;
				} else {
					return super.onKeyDown(keyCode, event);
				}
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mNfcAdapter = null;
	}

	// 扫描二维码
	public void scan () {
		// 检查相机权限是否打开
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, SCAN_PRM);
		} else {
			startActivityForResult(new Intent(this, ScannerActivity.class), SCAN_ACTINT);
		}
	}

	public Handler getHd() {
		return uh;
	}

	// 获取当前页面信息
	private EmUrl getCurUi () {
		try {
			return EmUrl.valueOf(wv.getTitle());
		} catch (Exception e) {
			return null;
		}
	}

	// 页面跳转
	public void sendUrl (String url) {
		uh.sendMessage(uh.obtainMessage(EmUh.Url.ordinal(), 0, 0, url));
	}

	// 页面跳转
	public void sendUrl (EmUrl e) {
		sendUrl(e.toString());
	}

	// 页面跳转
	public void sendUrl (EmUrl e, String... args) {
		sendUrl(Str.meg(e.toString(), args));
	}

	// 发送页面处理消息
	public void sendUh (EmUh e) {
		uh.sendMessage(uh.obtainMessage(e.ordinal()));
	}

	// 页面处理器
	private class UiHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			EmUh e = EmUh.values()[msg.what];
			switch (e) {
				case Url:
					wv.loadUrl((String)msg.obj);
					break;
				case Ajax:
					wv.loadUrl(Str.meg(EmUrl.AjaxCb.toString(), (String)msg.obj));
					break;
				case AjaxErr:
					wv.loadUrl(Str.meg(EmUrl.AjaxCb.toString(), "false"));
					break;
				case Err:
					wv.loadUrl(Str.meg(EmUrl.Memo.toString(), (String)msg.obj));
					break;
				default:
					break;
			}
		}
	}
}
