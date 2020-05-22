package com.invengo.test.mynfc.entity;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.invengo.test.mynfc.Ma;
import com.invengo.test.mynfc.enums.EmUrl;

import tk.ziniulian.job.nfc.NfcUtils;
import tk.ziniulian.util.dao.DbLocal;
import tk.ziniulian.util.dao.HttpAjax;

/**
 * 业务接口
 * Created by LZR on 2019/4/15.
 */

public class Web {
	private Ma ma;
	private Handler h;
	private HttpAjax ajx;
	private Intent itn = null;
	private DbLocal db;

	public Web (Ma m) {
		this.ma = m;
		this.h = m.getHd();
		this.ajx = new HttpAjax(this.h);
		this.db = new DbLocal(m, 1);
	}

	public void setItn(Intent t) {
		itn = t;
	}

	@JavascriptInterface
	public void log (String txt) {
		Log.i("---web---", txt);
	}

	/**
	 * 标签操作
	 */
	private String doTag (int stu, String txt) {
		String r;
		if (itn == null) {
			r = "{\"ok\":false,\"msg\":\"未发现标签！\"}";
		} else {
			try {
				switch (stu) {
					case 1:
						r = NfcUtils.read(itn);
						break;
					case 2:
						r = NfcUtils.wrt(txt, itn);
						break;
					case 3:
						r = NfcUtils.tagSize(itn);
						break;
					case 4:
						r = NfcUtils.formatNdefToTag(itn);
						break;
					case 5:
						r = NfcUtils.lockNdefToTag(itn);
						break;
					default:
						r = "{\"ok\":false}";
						break;
				}
			} catch (Exception e) {
				r = "{\"ok\":false,\"msg\":\"" + e.getMessage() + "\"}";
			}
		}
		return r;
	}

	/**
	 * 读标签
	 */
	@JavascriptInterface
	public String read () {
		return doTag(1, "");
	}

	/**
	 * 写标签
	 */
	@JavascriptInterface
	public String wrt (String txt) {
		return doTag(2, txt);
	}

	/**
	 * 标签大小
	 */
	@JavascriptInterface
	public String tagSize () {
		return doTag(3, "");
	}

	/**
	 * 标签格式化
	 */
	@JavascriptInterface
	public String tagFormat () {
		return doTag(4, "");
	}

	/**
	 * 标签格式化
	 */
	@JavascriptInterface
	public String tagLock () {
		return doTag(5, "");
	}

	/**
	 * 标签准备
	 */
	@JavascriptInterface
	public void rdy () {
		itn = null;
	}

	/**
	 * 扫码
	 */
	@JavascriptInterface
	public void scan () {
		ma.scan();
	}

	/**
	 * 登录
	 */
	@JavascriptInterface
	public boolean login (String user, String pw) {
		return false;
	}

	/**
	 * 登出
	 */
	@JavascriptInterface
	public void logout () {
	}

	/**
	 * 记录标签日志
	 */
	@JavascriptInterface
	public void tagLog (String txt) {
		this.db.addTag(txt);
	}

	@JavascriptInterface
	public String kvGet (String k) {
		return this.db.kvGet(k);
	}

	@JavascriptInterface
	public void kvSet (String k, String v) {
		this.db.kvSet(k, v);
	}

	@JavascriptInterface
	public void kvDel (String k) {
		this.db.kvDel(k);
	}

	/**
	 * 网络连接
	 */
	@JavascriptInterface
	public void ajx (String url, boolean isGet, String dat) {
		if (isGet) {
			this.ajx.get(url);
		} else {
			this.ajx.post(url, dat);
		}
	}

	/**
	 * 上传数据
	 */
	@JavascriptInterface
	public void push (String ip) {
		String d = this.db.tagGet();
		if (d.length() > 3) {
			this.ajx(ip, false, "o=" + d);
		} else {
			ma.sendUrl(EmUrl.AjaxCb, "{\"ok\":true,\"dat\":null,\"msg\":\"无数据\"}");
		}
	}

	/**
	 * 清空数据
	 */
	@JavascriptInterface
	public void tagDel () {
		this.db.tagDel();
	}

}
