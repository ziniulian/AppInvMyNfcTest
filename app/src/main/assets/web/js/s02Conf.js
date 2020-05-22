var conf = {
	cont: [	// 内容
		{
			nam: "标签头",
			size: 3,
			v: "YWG"
		},
		{
			nam: "协议版本号",
			size: 1,
			v: "1"
		},
		{
			nam: "类别",
			size: 1
		},
		{
			nam: "类型",
			size: 2
		},
		{
			nam: "型号",
			size: 2
		},
		{
			nam: "版本",
			size: 1
		},
		{
			nam: "序列号",
			size: 40
		},
		{
			nam: "生产人员",
			size: 5
		},
		{
			nam: "产品QC人员",
			size: 5
		},
		{
			nam: "最后写入时间",
			size: 14
		},
		{
			nam: "最后写入人",
			size: 5
		},
		{
			nam: "预留位",
			size: 29
		},
		{
			nam: "校验码",
			size: 2
		}
	],
	typ: {	// 设备类型
		D: {
			nam: "设备",
			sub: {}
		},
		F: {
			nam: "配件",
			sub: {
				ST:{
					nam: "天线",
					sub: {
						"9P":{
							nam: "900P",
							sub: {"1":1}
						}
					}
				},
				CY:{
					nam: "磁钢",
					sub: {
						"2B":{
							nam: "20B",
							sub: {"1":1}
						},
						"1A":{
							nam: "10A",
							sub: {"1":1}
						},
						"1B":{
							nam: "10B",
							sub: {"1":1}
						}
					}
				}
			}
		},
		B: {
			nam: "板卡",
			sub: {}
		}
	},
	mem: {	// 人员
		"00LZR": {	// 编号
			nam: "李泽荣",	// 姓名
			pw: "adm", // 登录密码
			cls: [2]	// 人员分类，对应 memCls
		},
		"04013": {
			nam: "郎斌",
			pw: "310",
			cls: [1]
		},
		"04043": {
			nam: "高金凤",
			pw: "340",
			cls: [0]
		},
		"04042": {
			nam: "杨建中",
			pw: "240",
			cls: [0]
		},
		"04100": {
			nam: "郑晓红",
			pw: "001",
			cls: [1]
		},
		"04061": {
			nam: "张玲玲",
			pw: "160",
			cls: [1]
		},
		"04144": {
			nam: "高清顺",
			pw: "441",
			cls: [1]
		},
		"000KF": {	// 编号
			nam: "客服人员",	// 姓名
			pw: "123", // 登录密码
			cls: [3]	// 人员分类，对应 memCls
		}
	},
	memCls: [	// 人员分类
		"品质", "生产", "管理员", "客服"
	],
	bm: {	// 条码匹配
		"^ST": {
			m: ["F", "ST"]
		},
		"^CY": {
			m: ["F", "CY"],
			sub: {
				"^.{4}10A" : {m: ["1A"]},
				"^.{4}10B" : {m: ["1B"]},
				"^.{4}20B" : {m: ["2B"]}
			}
		}
	},

	ip: "192.169.0.150:888",
	ver: 1,	// 版本

	getCls: function (a, i) {
		if (!i) {
			i = 2;
		}
		if (a[i]) {
			return conf.typ[a[i]].nam;
		} else {
			return "";
		}
	},
	getTyp: function (a, i) {
		if (!i) {
			i = 3;
		}
		if (a[i - 1] && a[i]) {
			return conf.typ[a[i - 1]].sub[a[i]].nam;
		} else {
			return "";
		}
	},
	getMod: function (a, i) {
		if (!i) {
			i = 4;
		}
		if (a[i - 2] && a[i - 1] && a[i]) {
			return conf.typ[a[i - 2]].sub[a[i - 1]].sub[a[i]].nam;
		} else {
			return "";
		}
	},
	getVer: function (a, i) {
		if (!i) {
			i = 5;
		}
		if (a[i - 3] && a[i - 2] && a[i - 1] && a[i]) {
			return conf.typ[a[i - 3]].sub[a[i - 2]].sub[a[i - 1]].sub[a[i]];
		} else {
			return "";
		}
	},
	getSn: function (a, i) {
		if (!i) {
			i = 6;
		}
		return tools.trim(a[i]);
	},
	getMem: function (a, i) {
		var o = conf.mem[a[i]];
		if (o) {
			return o.nam;
		} else {
			if (i === 8 && a[i] === "00000") {
				return "<span class=\"nqc\">未检验</span>";
			} else {
				return "---";
			}
		}
	},
	getTim: function (a, i) {
		if (!i) {
			i = 9;
		}
		return "<br/>&nbsp; &nbsp; &nbsp; " +
			a[i].substr(0,4) + "-" +
			a[i].substr(4,2) + "-" +
			a[i].substr(6,2) + " " +
			a[i].substr(8,2) + ":" +
			a[i].substr(10,2) + ":" +
			a[i].substr(12,2);
	},
	crtCc: function (a) {	// 生成校验码
		return "99";
	},
	checkCc: function (a) {	// 检查校验码
		return a[12] === "99";
	},
	parseBm: function (s) {	// 条码解析
		var i, c, o, k; r = [];
		o = conf.bm;
		k = 1;	// 循环标记
		while (k) {
			for (c in o) {
				if (s.match(new RegExp(c))) {
					for (i = 0; i < o[c].m.length; i ++) {
						r.push(o[c].m[i]);
					}
					if (o[c].sub) {
						o = o[c].sub;
						k = 2;
					}
					break;
				}
			}
			if (k === 2) {
				k = 1;
			} else {
				k = 0;
			}
		}
		return r.length ? r : null;
	},
	split: function (s) {	// 拆分标签源码
		var i, n = 0, l, r = [];
		for (i = 0; i < conf.cont.length; i ++) {
			l = conf.cont[i].size;
			r[i] = s.substr(n, l);
			n += l;
		}
		return (n > s.length) ? null : r;
	},

	getUser: function (uid) {	// 获取用户信息
		return conf.mem[uid];
	},

	init: function (o) {
		if (o) {
			for (var s in o) {
				conf[s] = o[s];
			}
		}

		conf.cont[2].get = conf.getCls;
		conf.cont[3].get = conf.getTyp;
		conf.cont[4].get = conf.getMod;
		conf.cont[5].get = conf.getVer;
		conf.cont[6].get = conf.getSn;
		conf.cont[7].get = conf.getMem;
		conf.cont[8].get = conf.getMem;
		conf.cont[9].get = conf.getTim;
		conf.cont[10].get = conf.getMem;
	}
};
