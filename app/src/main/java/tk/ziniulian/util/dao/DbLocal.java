package tk.ziniulian.util.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static tk.ziniulian.util.Str.meg;

/**
 * 本地数据库
 * Created by 李泽荣 on 2018/7/19.
 */

public class DbLocal extends SQLiteOpenHelper {
	public DbLocal(Context c, int version) {
		super(new SdDb(c), EmLocalCrtSql.dbNam.toString(), null, version);
	}

	// 数据库文件不存储在SD卡里的构造方法
	public DbLocal(Context c, int version, boolean noSd) {
		super(c, EmLocalCrtSql.dbNam.toString(), null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		for (EmLocalCrtSql e : EmLocalCrtSql.values()) {
			if (!((e.name().equals("sdDir")) || (e.name().equals("dbNam")))) {
				db.execSQL(e.toString());
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}

	// 执行多条SQL语句
	public void exe (String... args) {
		SQLiteDatabase db = this.getWritableDatabase();
		for (String s : args) {
			db.execSQL(s);
		}
		db.close();
	}

	// 执行SQL语句
	public void exe (EmLocalSql e, String... args) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL(meg(e.toString(), args));
		db.close();
	}

	// 获取键值对
	public String kvGet (String k) {
		return mkvGet(k, "Bkv");
	}

	// 设置键值对
	public void kvSet (String k, String v) {
		mkvSet(k, v, "Bkv");
	}

	// 删除键值对
	public void kvDel (String k) {
		mkvDel(k, "Bkv");
	}

	// 获取任意表的键值对
	public String mkvGet (String k, String tnam) {
		String r = null;
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(
				EmLocalSql.KvGet.toString(),
				k, tnam
		), null);

		if (c.moveToNext()) {
			r = c.getString(0);
		}

		c.close();
		db.close();
		return r;
	}

	// 设置任意表的键值对
	public void mkvSet (String k, String v, String tnam) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor c = db.rawQuery(meg(
				EmLocalSql.KvGet.toString(),
				k, tnam
		), null);
		boolean b = c.moveToNext();
		c.close();
		db.close();

		if (b) {
			exe(EmLocalSql.KvSet, k, v, tnam);
		} else {
			exe(EmLocalSql.KvAdd, k, v, tnam);
		}
	}

	// 删除任意表的键值对
	public void mkvDel (String k, String tnam) {
		exe(EmLocalSql.KvDel, k, tnam);
	}

	// 设置键值对
	public void addTag (String s) {
		exe(EmLocalSql.tagAdd, s.split(","));
	}

}
