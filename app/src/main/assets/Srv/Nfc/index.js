
// 文件位置
var curPath = require.resolve("./index.js").replace("index.js", "");

LZR.load([
	"LZR.Node.Router.QryTmp",
	"LZR.Node.Router.ComTmp"
]);

// 创建路由
var r = new LZR.Node.Router ({
	path: curPath
});

var tools = {
	utJson: LZR.getSingleton(LZR.Base.Json),
	utTim: LZR.getSingleton(LZR.Base.Time),
	clsR: LZR.Node.Srv.Result,
	qryRo: new LZR.Node.Router.QryTmp({
		ro: r,
		conf: ("mongodb://localhost:27017/invTest"),	// 数据库连接字
		defTnam: "nfc",	// 表名
		pvs: {
			_id: 2,	// id
			sn: 0,	// 文字
			tim: 1,	// 文字
		}
	}),
	tmpRo: new LZR.Node.Router.ComTmp({		// 常用模板
		ro: r
	})
};

r.hdPost("/push/");
r.post("/push/", function (req, res, next) {
	var o = undefined;
	try {
		o = tools.utJson.toObj(req.body.o);
		if (o) {
			tools.qryRo.db.add(req, res, next, null, o);
		} else {
			res.json(tools.clsR.get(null, "参数错误"));
		}
	}  catch (e) {
		res.json(tools.clsR.get(null, "参数错误"));
	}
});

r.get("/conf/", function (req, res, next) {
	tools.qryRo.db.get(req, res, next, {"conf":{"$exists":true}}, {"_id":0}, true);
});

r.get("/conf/", function (req, res, next) {
	var r = LZR.fillPro(req, "qpobj.comDbSrvReturn");
	res.json(tools.clsR.get(tools.utJson.toObj(r[0].conf)));
});

/**** 小测试 ****/
r.get("/tim/", function (req, res, next) {
	res.json(tools.clsR.get(tools.utTim.getDayTimestamp()));
});

r.get("/test/", function (req, res, next) {
	tools.qryRo.db.count(req, res, next, {});
});

tools.qryRo.init("/");
tools.tmpRo.initTmp("/", "tmp", {utJson: tools.utJson});

module.exports = r;
