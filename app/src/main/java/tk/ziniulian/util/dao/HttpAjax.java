package tk.ziniulian.util.dao;

import android.os.Handler;

import com.invengo.test.mynfc.enums.EmUh;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import tk.ziniulian.util.Str;

/**
 * Ajax
 * Created by LZR on 2019/4/15.
 */

public class HttpAjax implements Runnable {
	private Handler h;
	private String url;
	private String method;
	private String content;	// POST的内容
	private int stu = 0;	// 状态
	private boolean busy = false;	// 繁忙

	public HttpAjax (Handler hd) {
		this.h = hd;
	}

	public void get(String u) {
		if (!this.busy) {
			this.busy = true;
			this.url = u;
			this.method = "GET";
			new Thread(this).start();
		}
	}

	public void post(String u, String c) {
		if (!this.busy) {
			this.busy = true;
			this.url = u;
			this.method = "POST";
			this.content = c;
			new Thread(this).start();
		}
	}

	@Override
	public void run() {
		try {
			URL u = new URL(this.url);
			HttpURLConnection c = (HttpURLConnection)u.openConnection();
			c.setRequestMethod(this.method);
			if (this.method.equals("POST")) {	// POST
				c.setDoInput(true);
				c.setUseCaches(false);
				c.setRequestProperty("Accept-Charset", "UTF-8");
				c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				c.connect();
				DataOutputStream o = new DataOutputStream(c.getOutputStream());
				if (this.content.length() > 0) {
					o.write(Str.Dat2Bytes(this.content));
				}
				o.flush();
				o.close();
			} else {	// GET
				c.connect();
			}
			if (c.getResponseCode() == 200) {
				InputStream is = c.getInputStream();
				BufferedReader bf = new BufferedReader(new InputStreamReader(is));
				StringBuffer sb = new StringBuffer();
				String s = "";
				do {	// 读取输入流
					sb.append(s);
					s = bf.readLine();
				} while (s != null);
				is.close();
				bf.close();
				c.disconnect();
//Log.i("u", this.url);
//Log.i("c", this.content == null ? "" : this.content);
//Log.i("r", sb.toString());
				this.h.sendMessage(this.h.obtainMessage(EmUh.Ajax.ordinal(), this.stu, 0, sb.toString()));
			}
		} catch (Exception e) {
//			e.printStackTrace();
			this.h.sendMessage(this.h.obtainMessage(EmUh.AjaxErr.ordinal(), this.stu, 0, "网络连接失败！"));
		} finally {
			this.busy = false;
		}
	}
}
