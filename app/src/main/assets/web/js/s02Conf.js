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
			nam: "生产人员ID",
			size: 5
		},
		{
			nam: "产品QC人员ID",
			size: 5
		},
		{
			nam: "NFC标签写入时间",
			size: 14
		},
		{
			nam: "NFC标签写入人员ID",
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
				CG:{
					nam: "磁钢",
					sub: {
						"2B":{
							nam: "20B",
							sub: {"1":1}
						},
						"1A":{
							nam: "10A",
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
		"00001": {
			nam: "李泽荣",	// 姓名
			cls: [0,1]	// 人员分类，对应 memCls
		},
		"00002": {
			nam: "童永成",	// 姓名
			cls: [0,1]	// 人员分类，对应 memCls
		}
	},
	memCls: [	// 人员分类
		"品质", "生产"
	],

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
		return conf.mem[a[i]].nam;
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
	split: function (s) {
		var i, n = 0, l, r = [];
		for (i = 0; i < conf.cont.length; i ++) {
			l = conf.cont[i].size;
			r[i] = s.substr(n, l);
			n += l;
		}
		return (n > s.length) ? null : r;
	},

	init: function () {
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
