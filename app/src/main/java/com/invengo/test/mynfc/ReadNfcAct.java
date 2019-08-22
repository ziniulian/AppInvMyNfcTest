package com.invengo.test.mynfc;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;

/**
 * 读取NFC标签 - NDEF
 * Created by LZR on 2019/8/22.
 */

public class ReadNfcAct extends AppCompatActivity {
	private final String TAG = "--Read--";
	private TextView idtv;
	private TextView txtv;
	private int count = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_read);

		idtv = (TextView) findViewById(R.id.nfcId);
		txtv = (TextView) findViewById(R.id.nfcTxt);

		new NfcUtils(this);
	}

	//在onResume中开启前台调度
	@Override
	protected void onResume() {
		super.onResume();
		//设定intentfilter和tech-list。如果两个都为null就代表优先接收任何形式的TAG action。也就是说系统会主动发TAG intent。
		if (NfcUtils.mNfcAdapter != null) {
			NfcUtils.mNfcAdapter.enableForegroundDispatch(this, NfcUtils.mPendingIntent, NfcUtils.mIntentFilter, NfcUtils.mTechList);
		}
	}

	//在onNewIntent中处理由NFC设备传递过来的intent
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		count ++;
Toast.makeText(this, "----- " + count, Toast.LENGTH_SHORT).show();
		processIntent(intent);
	}

	//  这块的processIntent() 就是处理卡中数据的方法
	public void processIntent(Intent intent) {
		Parcelable[] rawmsgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage msg = (NdefMessage) rawmsgs[0];
		NdefRecord[] records = msg.getRecords();
		String resultStr = new String(records[0].getPayload());
		// 返回的是NFC检查到卡中的数据
		Log.e(TAG, "processIntent: "+resultStr );
		try {
			// 检测卡的id
			String id = NfcUtils.readNFCId(intent);
			idtv.setText(id);

			// NfcUtils中获取卡中数据的方法
			String result = NfcUtils.readNFCFromTag(intent);
			txtv.setText(result);

			Toast.makeText(this, "读取成功" + count, Toast.LENGTH_SHORT).show();
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(this, "读取失败" + count, Toast.LENGTH_LONG).show();
		}
	}

	// 在onPause()中做前台调度的取消
	@Override
	protected void onPause() {
		super.onPause();
		if (NfcUtils.mNfcAdapter != null) {
			NfcUtils.mNfcAdapter.disableForegroundDispatch(this);
		}
	}

	// 最后在销毁界面时
	@Override
	protected void onDestroy() {
		super.onDestroy();
		NfcUtils.mNfcAdapter = null;
	}
}
