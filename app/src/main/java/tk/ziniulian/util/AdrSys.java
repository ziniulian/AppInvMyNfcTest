package tk.ziniulian.util;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * 安卓系统常用工具
 * Created by LZR on 2018/12/4.
 */

public class AdrSys {
	// 获取版本号
	public static int getVerCode(Context context) {
		int versionCode = 0;
		try {
			versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionCode;
	}

	// 获取版本号名称
	public static String getVerNam(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return verName;
	}
}
