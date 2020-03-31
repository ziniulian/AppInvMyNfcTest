package tk.ziniulian.util.dao;

/**
 * SQL建表语句
 * Created by 李泽荣 on 2018/7/19.
 */

public enum EmLocalCrtSql {
	sdDir("Invengo/NFC/DB/"),	// 数据库存储路径

	dbNam("nfcTag.db"),	// 数据库名

	Bkv(	// 基本键值对表
		"create table Bkv(" +	// 表名
		"k text primary key not null, " +	// 键
		"v text)"),	// 值

	Tag(	// 标签日志
		"create table Tag(" +	// 表名
		"cod text primary key not null, " +	// 写入标签的信息
		"cls text," +	// 类别
		"typ text," +	// 类型
		"mod text," +	// 型号
		"ver text," +	// 版本
		"sn text," +	// 序列号
		"mak text," +	// 生产者
		"qc text," +	// QC
		"wrt text," +	// 写入人
		"tim text," +	// 写入时间
		"id text)");	// 标签ID

	private final String sql;
	EmLocalCrtSql(String s) {
		sql = s;
	}

	@Override
	public String toString() {
		return sql;
	}
}
