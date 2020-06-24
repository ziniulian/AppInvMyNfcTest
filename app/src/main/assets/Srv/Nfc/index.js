
// 文件位置
var curPath = require.resolve("./index.js").replace("index.js", "");
var cookieSession = require("cookie-session");

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
	conf: {},
	qryRo: new LZR.Node.Router.QryTmp({
		ro: r,
		conf: ("mongodb://localhost:27017/invTest"),	// 数据库连接字
		defTnam: "nfc",	// 表名
		pvs: {
			_id: 2,	// id
			sn: 0,	// 文字
			tim: 0,	// 文字
		}
	}),
	tmpRo: new LZR.Node.Router.ComTmp({		// 常用模板
		ro: r
	})
};

r.ro.use(cookieSession({
	name: "ziniulian.tk",
	keys: ["lzr'sJob:inv"],
	maxAge: 8*3600000 // 8小时
}));

r.hdPost("/push/", "300kb");
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
	tools.conf = tools.utJson.toObj(r[0].conf);
	res.json(tools.clsR.get(tools.conf));
});

r.get("/qry_nfc/", function (req, res, next) {
	if (req.session.usr) {
		// LZR.fillPro(req, "qpobj.tmpo").usr = req.session.usr;
		var o = LZR.fillPro(req, "qpobj.tmpo.qry");
		o.k = "tim";
		o.cond = "{\"sn\":{\"$exists\":true}}";
		o.sort = -1;
		o.size = 20;
		// o.cond = tools.utJson.toJson({typ: "info"});
		next();
	} else {
		res.redirect(req.baseUrl + "/signIn/");
	}
});

r.get("/qry_erp/", function (req, res, next) {
	if (req.session.usr) {
		for (var i = 0; i < req.session.usr.cls.length; i ++) {
			if (req.session.usr.cls[i] === 4) {
				LZR.fillPro(req, "qpobj.tmpo").usr = req.session.usr;
				var o = LZR.fillPro(req, "qpobj.tmpo.qry");
				o.k = "tim";
				o.cond = "{\"erp\":{\"$exists\":true}}";
				o.sort = -1;
				o.size = 20;
				next();
				return;
			}
		}
		res.redirect(req.baseUrl + "/hom/");
	} else {
		res.redirect(req.baseUrl + "/signIn/");
	}
});

r.get("/qry_erpInfo/", function (req, res, next) {
	var en = req.query.en;
	if (en && req.session.usr) {
		for (var i = 0; i < req.session.usr.cls.length; i ++) {
			if (req.session.usr.cls[i] === 4) {
				var o = LZR.fillPro(req, "qpobj.tmpo");
				o.usr = req.session.usr;
				o.en = en;

				o = LZR.fillPro(req, "qpobj.tmpo.qry");
				o.k = "tim";
				o.cond = "{\"en\":\"" + en + "\"}";
				o.sort = -1;
				o.size = 20;
				next();
				return;
			}
		}
		res.redirect(req.baseUrl + "/hom/");
	} else {
		res.redirect(req.baseUrl + "/hom/");
	}
});

// 登录
r.get("/signIn/:u?/:p?/", function (req, res, next) {
	if (req.session.usr) {
		res.redirect(req.baseUrl + "/hom/");
	} else if (req.params.u && req.params.p) {
		if (tools.conf) {
			var m = tools.conf.mem[req.params.u];
			if (m && (m.pw === req.params.p)) {
				req.session.usr = {
					uid: req.params.u,
					nam: m.nam,
					cls: m.cls
				};
				res.redirect(req.baseUrl + "/hom/");
			} else {
				res.redirect(req.baseUrl + "/signIn/err/");
			}
		} else {
			res.redirect(req.baseUrl + "/signIn/err/");
		}
	} else if (req.params.u === "err") {
		LZR.fillPro(req, "qpobj.tmpo").err = true;
		next();
	} else {
		next();
	}
});

// 登出
r.get("/signOut/", function (req, res, next) {
	delete req.session.usr;
	res.redirect(req.baseUrl + "/signIn/");
});

// 主页用户权限检查
r.get("/hom/", function (req, res, next) {
	if (req.session.usr) {
		LZR.fillPro(req, "qpobj.tmpo").usr = req.session.usr;
		next();
	} else {
		res.redirect(req.baseUrl + "/signIn/");
	}
});

// 出货页用户权限检查
r.get(/^\/((out)|(lend)|(rtn))\/$/, function (req, res, next) {
// console.log(req.params);
	if (req.session.usr) {
		for (var i = 0; i < req.session.usr.cls.length; i ++) {
			if (req.session.usr.cls[i] === 4) {
				LZR.fillPro(req, "qpobj.tmpo").usr = req.session.usr;
				next();
				return;
			}
		}
		res.redirect(req.baseUrl + "/hom/");
	} else {
		res.redirect(req.baseUrl + "/signIn/");
	}
});

// 数据库管理页权限检查
r.get("/qry_base/", function (req, res, next) {
	if (req.session.usr) {
		for (var i = 0; i < req.session.usr.cls.length; i ++) {
			if (req.session.usr.cls[i] === 2) {
				next();
				return;
			}
		}
		res.redirect(req.baseUrl + "/hom/");
	} else {
		res.redirect(req.baseUrl + "/signIn/");
	}
});

/**** 小测试 ****/
r.get("/tim/", function (req, res, next) {
	res.json(tools.clsR.get(tools.utTim.getDayTimestamp()));
});

r.get("/test/", function (req, res, next) {
	tools.qryRo.db.count(req, res, next, {});
});

tools.qryRo.init("/");
tools.tmpRo.initTmp("/", "tmp", {
	utJson: tools.utJson,
	utTim: tools.utTim
});

// r.use("*", function (req, res, next) {	// 用use会使路径无限叠加
r.get("*", function (req, res, next) {
// console.log(req.baseUrl);
// console.log(req.params);
	res.redirect(req.baseUrl + "/hom/");
});

// 配置信息初始化
tools.qryRo.db.get(tools.conf, null, function () {
	var r = tools.conf.qpobj.comDbSrvReturn[0];
	tools.conf = tools.utJson.toObj(r.conf);
}, {"conf":{"$exists":true}}, {"_id":0}, true);

module.exports = r;
