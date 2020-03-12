package com.invengo.test.mynfc;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
	private String wrtxt = "";	// 写入内容
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
//			sendUrl(EmUrl.Home);
			sendUrl(EmUrl.Err);
		} else {
			// NFC功能可用
			NfcUtils.NfcInit(this);
			sendUrl(EmUrl.Home);
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
		wv.loadUrl(Str.meg(EmUrl.Onn.toString()));
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
				case Err:
					wv.loadUrl(Str.meg(EmUrl.Memo.toString(), (String)msg.obj));
					break;
				default:
					break;
			}
		}
	}
}
