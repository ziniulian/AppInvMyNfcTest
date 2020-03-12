// 通用工具
tools = {

	// 信息提示
	memo: {
		tid: 0,
		doe: null,
		til: "",
		timout: 3000,

		show: function (msg, t) {
			if (this.tid) {
				clearTimeout(this.tid);
			}
			this.doe.innerHTML = msg;
			if (t !== 0) {
				if (!t) {
					t = this.timout;
				}
				this.tid = setTimeout(this.hid_s, t);
			}
		},

		exit: function (msg, til) {
			if (!this.til) {
				this.til = document.title;
				document.title = til;
				this.show(msg);
			}
		},

		bind: function (d) {
			var self = this;
			this.doe = d;
			this.hid_s = function () {
				self.hid ();
			};
		},

		hid: function () {
			this.doe.innerHTML = "";
			this.tid = 0;
			if (this.til) {
				document.title = this.til;
				this.til = "";
			}
		}
	},

	// 获取GET参数
	getUrlReq: function () {
		var url = location.search;
		var theRequest = {};
		if (url.indexOf("?") != -1) {
			url = url.substr(1).split("&");
			for(var i = 0; i < url.length; i ++) {
				var str = url[i].split("=");
				theRequest[str[0]] = decodeURIComponent(str[1]);
			}
		}
		return theRequest;
	},

	// 固定宽度的字符
	strFormt: function (s, width, subs) {
		var r = s;
		var n = s.length;
		if (width > n) {
			n = width - n;
			for (var i = 0; i < n; i++) {
				r = subs + r;
			}
		}
		return r;
	},

	// 获取当前时间的字符串
	getTimStr: function () {
		var date = new Date();
		var s = date.getFullYear();
		s += "-";
		s += tools.strFormt((date.getMonth() + 1) + "", 2, "0");
		s += "-";
		s += tools.strFormt(date.getDate() + "", 2, "0");
		s += " ";
		s += tools.strFormt(date.getHours() + "", 2, "0");
		s += ":";
		s += tools.strFormt(date.getMinutes() + "", 2, "0");
		s += ":";
		s += tools.strFormt(date.getSeconds() + "", 2, "0");
		return s;
	},

	// DOM元素位置调整
	topDoe: function (out, doe) {
		if (doe) {
			out.scrollTop = doe.offsetTop;
		} else {
			out.scrollTop = 0;
		}
	}

};
