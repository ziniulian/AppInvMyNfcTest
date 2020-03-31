package tk.ziniulian.util.dao;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import java.io.File;

/**
 * 数据库位置
 * Created by 李泽荣 on 2018/7/19.
 */

public class SdDb extends ContextWrapper {
	public SdDb(Context base) {
		super(base);
	}

	@Override
	public File getDatabasePath(String name) {
		String p = EmLocalCrtSql.sdDir.toString() + name;
		if (!p.endsWith(".db")) {
			p += ".db";
		}
		File dbfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), p);

		if (!dbfile.getParentFile().exists()) {
			dbfile.getParentFile().mkdirs();
		}
		return dbfile;
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
		return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
	}

	@Override
	public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
		return openOrCreateDatabase(name, mode, factory);
	}
}
