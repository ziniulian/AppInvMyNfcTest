package tk.ziniulian.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

/**
 * URI 转换
 * Created by LZR on 2019/5/24.
 */

public class UriParser {
	public static String getPhotoPathFromContentUri(Context context, Uri uri) {
		String photoPath = "";
		if (context == null || uri == null) {
			return photoPath;
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
			String docId = DocumentsContract.getDocumentId(uri);
			if (isExternalStorageDocument(uri)) {
				String[] split = docId.split(":");
				if (split.length >= 2) {
					String type = split[0];
					if ("primary".equalsIgnoreCase(type)) {
						photoPath = Environment.getExternalStorageDirectory() + "/" + split[1];
					}
				}
			} else if (isDownloadsDocument(uri)) {
				if (docId.startsWith("raw:")) {
					photoPath = docId.replaceFirst("raw:", "");
				} else {
					Uri contentUri = uri;
					if (Build.VERSION.SDK_INT < 26) {	// android8.0 = 26
						contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
					}
					photoPath = getDataColumn(context, contentUri, null, null);
				}
			} else if (isMediaDocument(uri)) {
				String[] split = docId.split(":");
				if (split.length >= 2) {
					String type = split[0];
					Uri contentUris = null;
					if ("image".equals(type)) {
						contentUris = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					} else if ("video".equals(type)) {
						contentUris = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
					} else if ("audio".equals(type)) {
						contentUris = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
					}
					String selection = MediaStore.Images.Media._ID + "=?";
					String[] selectionArgs = new String[]{split[1]};
					photoPath = getDataColumn(context, contentUris, selection, selectionArgs);
				}
			}
		} else if ("file".equalsIgnoreCase(uri.getScheme())) {
			photoPath = uri.getPath();
		} else {
			photoPath = getDataColumn(context, uri, null, null);
		}

		return photoPath;
	}

	private static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	private static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	private static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	private static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = {column};
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if (cursor != null && !cursor.isClosed()) {
				cursor.close();
			}
		}
		return null;
	}


	/********************************************/

	/**
	 * Try to return the absolute file path from the given Uri
	 * 适用于 android 19 版以下
	 *
	 * @param context
	 * @param uri
	 * @return the file path or null
	 */
	public static String uri2Path(final Context context, final Uri uri) {
		if (null == uri) return null;
		final String scheme = uri.getScheme();
		String data = null;
		if (scheme == null)
			data = uri.getPath();
		else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
			data = uri.getPath();
		} else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
			Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
			if (null != cursor) {
				if (cursor.moveToFirst()) {
					int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
					if (index == -1) {
						index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					}
					if (index > -1) {
						data = cursor.getString(index);
					}
				}
				cursor.close();
			}
		}
		return data;
	}
}
