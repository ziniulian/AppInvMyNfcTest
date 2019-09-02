package com.invengo.test.mynfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 写入NFC标签 - NDEF
 * Created by LZR on 2019/8/22.
 */

public class WrtNfcAct extends AppCompatActivity {
	private final String TAG = "--Wrt--";
	private TextView idtv;
	private TextView txtv;
	private EditText wrtv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_wrt);

		idtv = (TextView) findViewById(R.id.nfcId);
		txtv = (TextView) findViewById(R.id.nfcTxt);
		wrtv = (EditText) findViewById(R.id.nfcWrt);

		new NfcUtils(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (NfcUtils.mNfcAdapter != null) {
			NfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
		}
	}

	//在onNewIntent中处理由NFC设备传递过来的intent
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		processIntent(intent, wrtv.getText().toString());
	}

	//  这块的processIntent() 就是处理卡中数据的方法
	public void processIntent(Intent intent, String txt) {
		try {
			// 检测卡的id
			String id = NfcUtils.readNFCId(intent);
			idtv.setText(id);

//			// 获取卡中原数据
//			String result = NfcUtils.readNFCFromTag(intent);
//			txtv.setText(result);

			if (txt.length() > 0) {
				NfcUtils.writeNFCToTag(txt, intent);	// 往卡中写数据
				Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
				wrtv.setText("");
				txtv.setText("");
			}
		} catch (Exception e) {
			String es = "Error : " + e.getMessage();
			txtv.setText(es);
			Toast.makeText(this, es, Toast.LENGTH_LONG).show();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (NfcUtils.mNfcAdapter != null) {
			NfcUtils.mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NfcUtils.mNfcAdapter = null;
	}
}
