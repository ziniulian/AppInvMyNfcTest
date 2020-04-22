package tk.ziniulian.job.nfc;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.os.Build;
import android.os.Parcelable;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Locale;

/**
 * Nfc工具类
 * Created by LZR on 2019/8/22.
 */

public class NfcUtils {
	//nfc
	private final String TAG = "--NfcUtl--";
	public static NfcAdapter mNfcAdapter;
	public static IntentFilter[] mIntentFilter = null;
	public static PendingIntent mPendingIntent = null;
	public static String[][] mTechList = null;

	/**
	 * 检查NFC是否打开
	 */
	public static NfcAdapter NfcCheck(Activity activity) {
		NfcAdapter mNfcAdapter = NfcAdapter.getDefaultAdapter(activity);
		if (mNfcAdapter == null) {
//			Toast.makeText(activity, "设备不支持NFC功能!", Toast.LENGTH_SHORT).show();
			return null;
		} else {
			if (!mNfcAdapter.isEnabled()) {
				IsToSet(activity);
			} else {
//				Toast.makeText(activity, "NFC功能已打开!", Toast.LENGTH_SHORT).show();
			}
		}
		return mNfcAdapter;
	}

	/**
	 * 初始化nfc设置
	 */
	public static void NfcInit(Activity activity) {
		Intent intent = new Intent(activity, activity.getClass());
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		mPendingIntent = PendingIntent.getActivity(activity, 0, intent, 0);
		//做一个IntentFilter过滤你想要的action 这里过滤的是ndef
		IntentFilter filter = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		//如果你对action的定义有更高的要求，比如data的要求，你可以使用如下的代码来定义intentFilter
		//        IntentFilter filter2 = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
		//        try {
		//            filter.addDataType("*/*");
		//        } catch (IntentFilter.MalformedMimeTypeException e) {
		//            e.printStackTrace();
		//        }
		//        mIntentFilter = new IntentFilter[]{filter, filter2};
		//        mTechList = null;
		try {
			filter.addDataType("*/*");
		} catch (IntentFilter.MalformedMimeTypeException e) {
			e.printStackTrace();
		}
		mTechList = new String[][]{{MifareClassic.class.getName()},
				{NfcA.class.getName()}};
		//生成intentFilter
		mIntentFilter = new IntentFilter[]{filter};
	}

	/**
	 * 获取标签
	 */
	public static Tag getTag(Intent intent) {
		return intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
	}

	/**
	 * 读取标签ID
	 */
	public static String getId(Tag t) throws UnsupportedEncodingException {
		return ByteArrayToHexString(t.getId());
	}

	/**
	 * 读取NFC的数据
	 */
	public static String read(Intent intent) throws UnsupportedEncodingException {
		StringBuilder r = new StringBuilder();
		Tag tag = getTag(intent);
		String id = getId(tag);
		r.append("{\"ok\":true,\"id\":\"");
		r.append(id);
		r.append("\",\"dat\":\"");

		Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		if (rawArray != null) {
			// 标签可能存储了多个NdefMessage对象，一般情况下只有一个NdefMessage对象
			NdefMessage mNdefMsg = (NdefMessage) rawArray[0];

			// 程序中只考虑了1个NdefRecord对象，若是通用软件应该考虑所有的NdefRecord对象
			NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];

			if (mNdefRecord != null) {
				r.append(parseTextRecord(mNdefRecord));
			}
		}

		r.append("\"}");
		return r.toString();
	}

	/**
	 * 往nfc写入数据
	 */
	public static String wrt(String data, Intent intent) throws Exception {
		String r = "{\"ok\":false}";
		Tag tag = getTag(intent);

		// 创建要写入的NdefMessage对象
		NdefRecord ndefRecord = createTextRecord(data);
		NdefRecord[] records = {ndefRecord};
		NdefMessage ndefMessage = new NdefMessage(records);

		Ndef ndef = Ndef.get(tag);
		if (ndef == null) {
			NdefFormatable format = NdefFormatable.get(tag);
			if (format != null) {
				// NDEF 格式化
				try {
					format.connect();
					format.format(ndefMessage);
					r = "{\"ok\":true,\"id\":\"" + getId(tag) + "\"}";
					format.close();
				} catch (Exception e) {
					throw new Exception("非NDEF数据格式！");
				}
			} else {
				throw new Exception("标签不支持NDEF格式！");
			}
		} else {
			ndef.connect();
			if (ndef.isWritable()) {
				if (ndef.getMaxSize() < ndefMessage.toByteArray().length) {
					ndef.close();
					throw new Exception("NFC标签的空间不足！");
				} else {
					ndef.writeNdefMessage(ndefMessage);
					r = "{\"ok\":true,\"id\":\"" + getId(tag) + "\"}";
					ndef.close();
				}
			} else {
				ndef.close();
				throw new Exception("NFC标签是只读的！");
			}
		}
		return r;
	}

	/**
	 * 获取NFC的写入限制
	 */
	public static String tagSize(Intent intent) throws Exception {
		String r = "{\"ok\":false}";
		Tag tag = getTag(intent);

		Ndef ndef = Ndef.get(tag);
		if (ndef == null) {
			throw new Exception("非NDEF数据格式！");
		} else {
			ndef.connect();
			if (ndef.isWritable()) {
				r = "{\"ok\":true,\"id\":\"" + getId(tag) + "\",\"size\":" + ndef.getMaxSize() + "}";
				ndef.close();
			} else {
				ndef.close();
				throw new Exception("NFC标签是只读的！");
			}
		}
		return r;
	}

	/**
	 * 格式化 NFC 标签
	 */
	public static String formatNdefToTag(Intent intent) throws Exception {
		String r = "{\"ok\":false}";
		Tag tag = getTag(intent);

		// 获取可以格式化和向标签写入数据NdefFormatable对象
		NdefFormatable format = NdefFormatable.get(tag);
		if (format != null) {
			try {
				// 允许对标签进行IO操作
				format.connect();
				format.format(new NdefMessage(new NdefRecord[] {new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], new byte[] {0})}));
//				format.format(new NdefMessage(new NdefRecord[] {createTextRecord("NDEF")}));

				r = "{\"ok\":true,\"id\":\"" + getId(tag) + "\"}";
				format.close();
			} catch (Exception e) {
				throw new Exception("NDEF格式化失败！");
			}
		} else {
			throw new Exception("标签不支持NDEF格式！");
		}
		return r;
	}

	/**
	 * 锁定 NFC 标签
	 */
	public static String lockNdefToTag(Intent intent) throws Exception {
		String r = "{\"ok\":false}";
		Tag tag = getTag(intent);

		Ndef ndef = Ndef.get(tag);
		if (ndef == null) {
			throw new Exception("非NDEF数据格式！");
		} else {
			ndef.connect();
			if (ndef.canMakeReadOnly()) {
				if (ndef.makeReadOnly()) {
					ndef.close();
					r = "{\"ok\":true,\"id\":\"" + getId(tag) + "\"}";
				} else {
					ndef.close();
					throw new Exception("失败！失败！");
				}
			} else {
				ndef.close();
				throw new Exception("该标签无法锁定！");
			}
		}
		return r;
	}

	/**********************************/

	/**
	 * 将字节数组转换为字符串
	 */
	private static String ByteArrayToHexString(byte[] inarray) {
		int i, j, in;
		String[] hex = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F"};
		String out = "";
		for (j = 0; j < inarray.length; ++j) {
			in = (int) inarray[j] & 0xff;
			i = (in >> 4) & 0x0f;
			out += hex[i];
			i = in & 0x0f;
			out += hex[i];
		}
		return out;
	}

	private static void IsToSet(final Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setMessage("是否跳转到设置页面打开NFC功能");
//        builder.setTitle("提示");
		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				goToSet(activity);
				dialog.dismiss();
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private static void goToSet(Activity activity) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BASE) {
			// 进入设置系统应用权限界面
			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			activity.startActivity(intent);
			return;
		} else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {// 运行系统在5.x环境使用
			// 进入设置系统应用权限界面
			Intent intent = new Intent(Settings.ACTION_SETTINGS);
			activity.startActivity(intent);
			return;
		}
	}

	// 创建一个封装要写入文本的NdefRecord对象  
	private static NdefRecord createTextRecord(String text) {
		//生成语言编码的字节数组，中文编码  
		byte[] langBytes = Locale.CHINA.getLanguage().getBytes(Charset.forName("US-ASCII"));

		//将要写入的文本以UTF_8格式进行编码
		Charset utfEncoding = Charset.forName("UTF-8");

		//由于已经确定文本的格式编码为UTF_8，所以直接将payload的第1个字节的第7位设为0
		byte[] textBytes = text.getBytes(utfEncoding);
		int utfBit = 0;

		//定义和初始化状态字节
		char status = (char) (utfBit + langBytes.length);

		//创建存储payload的字节数组
		byte[] data = new byte[1 + langBytes.length + textBytes.length];

		//设置状态字节
		data[0] = (byte) status;

		//设置语言编码
		System.arraycopy(langBytes, 0, data, 1, langBytes.length);

		//设置实际要写入的文本
		System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);
//Log.i("------", ByteArrayToHexString(data));

		//根据前面设置的payload创建NdefRecord对象
		NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
		return record;
	}

	//  将纯文本内容从NdefRecord对象（payload）中解析出来  
	public static String parseTextRecord(NdefRecord record) throws UnsupportedEncodingException {
		//验证TNF是否为NdefRecord.TNF_WELL_KNOWN
		if (record.getTnf() != NdefRecord.TNF_WELL_KNOWN) return "";

		//验证可变长度类型是否为RTD_TEXT
		if (!Arrays.equals(record.getType(), NdefRecord.RTD_TEXT)) return "";

		//获取payload
		byte[] payload = record.getPayload();
		//下面代码分析payload：状态字节+ISO语言编码（ASCLL）+文本数据（UTF_8/UTF_16）
		//其中payload[0]放置状态字节：如果bit7为0，文本数据以UTF_8格式编码，如果为1则以UTF_16编码
		//bit6是保留位，默认为0
		/*
		* payload[0] contains the "Status Byte Encodings" field, per the
		* NFC Forum "Text Record Type Definition" section 3.2.1.
		*  
		* bit7 is the Text Encoding Field.
		*  
		* if (Bit_7 == 0): The text is encoded in UTF-8 if (Bit_7 == 1):
		* The text is encoded in UTF16
		*  
		* Bit_6 is reserved for future use and must be set to zero.
		*  
		* Bits 5 to 0 are the length of the IANA language code.
		*/
		String textEncoding = ((payload[0] & 0x80) == 0) ? "UTF-8" : "UTF-16";

		//处理bit5-0。bit5-0表示语言编码长度（字节数）
		int languageCodeLength = payload[0] & 0x3f;
		String languageCode = new String(payload, 1, languageCodeLength, "US-ASCII");

		// 解析出实际的文本数据
		String text = new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);

		// 返回文本数据
		return text;
	}

}
