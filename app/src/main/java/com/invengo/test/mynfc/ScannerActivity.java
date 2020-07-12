package com.invengo.test.mynfc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static com.google.zxing.BarcodeFormat.CODE_39;
import static com.google.zxing.BarcodeFormat.QR_CODE;

/**
 * 二维码页面
 * Created by LZR on 2020/3/25.
 */

public class ScannerActivity extends AppCompatActivity implements ZXingScannerView.ResultHandler {
	private ZXingScannerView mZXingScannerView;
	private List<BarcodeFormat> formats;	// 自定义扫码模式

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mZXingScannerView = new ZXingScannerView(this); // 将ZXingScannerView作为布局
		setContentView(mZXingScannerView);
		mZXingScannerView.setResultHandler(this); // 设置处理结果回调
		mZXingScannerView.setAutoFocus(true);	//自动对焦

		// 自定义扫码模式
		formats = new ArrayList<>();
		formats.add(CODE_39);	// 条形码
		formats.add(QR_CODE);	// 二维码
		mZXingScannerView.setFormats(formats);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mZXingScannerView.startCamera(); // 打开摄像头
	}

	@Override
	protected void onPause() {
		super.onPause();
		mZXingScannerView.stopCamera(); // 活动失去焦点的时候关闭摄像头
	}

	@Override
	public void handleResult(Result result) {	// 实现回调接口，将数据回传并结束活动
		Intent data = new Intent();
		data.putExtra("scnDat", result.getText());
		setResult(RESULT_OK, data);
		finish();
	}
}
