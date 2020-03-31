function init () {
	tools.memo.bind(memoDom);
}

var dco = {
	onNew: function () {},

	// 检查有没有读写内存卡的权限
	check: function () {
		if (rfdo.checkSdPrm()) {
			location.href = "./wrt.html";
		} else {
			tools.memo.show("写入需要存储权限！");
		}
	},

	// 回退
	back: function () {
		tools.memo.exit("再按一次退出程序", "Exit");
	}
};
