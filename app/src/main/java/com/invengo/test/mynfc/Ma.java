package com.invengo.test.mynfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Ma extends AppCompatActivity {
	private final String TAG = "--Ma--";
	private Button rb;
	private Button wb;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ma);

		rb = (Button) findViewById(R.id.readBtn);
		wb = (Button) findViewById(R.id.wrtBtn);

		if (NfcUtils.NfcCheck(this) == null) {
			// 设备不支持 NFC 功能 ， 关闭按钮功能
			rb.setEnabled(false);
			wb.setEnabled(false);
		} else {
			init();
		}
	}

	// 初始化
	private void init () {
		rb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent t = new Intent(Ma.this, ReadNfcAct.class);
				startActivity(t);
			}
		});

		wb.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent t = new Intent(Ma.this, WrtNfcAct.class);
				startActivity(t);
			}
		});

		findViewById(R.id.fmBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent t = new Intent(Ma.this, FmNfcAct.class);
				startActivity(t);
			}
		});

		findViewById(R.id.szBtn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent t = new Intent(Ma.this, SizeNfcAct.class);
				startActivity(t);
			}
		});
	}

	//在onNewIntent中处理由NFC设备传递过来的intent
	@Override
	protected void onNewIntent(Intent intent) {
		return;
	}

}
