package com.invengo.test.mynfc;

import android.content.Intent;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 写入NFC标签 - NDEF
 * Created by LZR on 2019/8/22.
 */

public class WrtNfcAct extends AppCompatActivity {
	private final String TAG = "--Read--";
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

			if (txt.length() > 0) {
				NfcUtils.writeNFCToTag(txt, intent);	// 往卡中写数据
				Toast.makeText(this, "写入成功", Toast.LENGTH_SHORT).show();
				wrtv.setText("");
			}
		} catch (UnsupportedEncodingException e) {
			Toast.makeText(this, "读取失败", Toast.LENGTH_LONG).show();
		} catch (FormatException e) {
			Toast.makeText(this, "写入失败-1", Toast.LENGTH_LONG).show();
		} catch (IOException e) {
			Toast.makeText(this, "写入失败-2", Toast.LENGTH_LONG).show();
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
