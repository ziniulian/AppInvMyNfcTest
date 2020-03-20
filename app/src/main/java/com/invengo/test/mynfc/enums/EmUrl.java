package com.invengo.test.mynfc.enums;

/**
 *
 * Created by LZR on 2019/4/15.
 */

public enum EmUrl {
	// 主页
	Home("file:///android_asset/web/s02/home.html"),
	Exit("file:///android_asset/web/s02/home.html"),
	Err("file:///android_asset/web/s01/err.html"),
	Onn("javascript: dco.onNew(\"<0>\");"),
	Memo("javascript: tools.memo.show(\"<0>\");"),
	Back("javascript: dco.back();");

	private final String url;
	EmUrl(String u) {
		url = u;
	}

	@Override
	public String toString() {
		return url;
	}
}
