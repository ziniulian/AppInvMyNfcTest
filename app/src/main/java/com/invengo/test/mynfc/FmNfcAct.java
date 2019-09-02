package com.invengo.test.mynfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 写入NFC标签 - NDEF
 * Created by LZR on 2019/8/22.
 */

public class FmNfcAct extends AppCompatActivity {
	private final String TAG = "--Format--";
	private TextView idtv;
	private TextView txtv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_read);

		idtv = (TextView) findViewById(R.id.nfcId);
		txtv = (TextView) findViewById(R.id.nfcTxt);

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
		processIntent(intent, "");
	}

	//  这块的processIntent() 就是处理卡中数据的方法
	public void processIntent(Intent intent, String txt) {
		try {
			NfcUtils.formatNdefToTag(intent);
			Toast.makeText(this, "格式化成功！", Toast.LENGTH_SHORT).show();

			// 检测卡的id
			String id = NfcUtils.readNFCId(intent);
			idtv.setText(id);
			txtv.setText("");
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
