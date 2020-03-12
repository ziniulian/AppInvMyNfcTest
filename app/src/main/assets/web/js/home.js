function init () {
	tools.memo.bind(memoDom);
}

var dco = {
	onNew: function () {},

	// 回退
	back: function () {
		tools.memo.exit("再按一次退出程序", "Exit");
	}
};
