// 资料下载测试服务
require("lzr");

LZR.load([
	"LZR.Node.Router.QryTmp",
	"LZR.Node.Router.ComTmp"
]);

var srv = new LZR.Node.Srv ({
	ip: "0.0.0.0",
	port: 888
});

srv.use("/NFC/", require("./Nfc"));

srv.ro.setStaticDir("/", "./web");

srv.use("*", function (req, res) {
	res.status(404).send("404");
});
srv.start();
console.log("服务已运行 ...");

// // 开启 HTTPS 协议
// var https = require("https");
// var fs = require("fs");
// var httpsOp = {
// 	// passphrase: "123456",  // 生成密钥有密码时使用
// 	key: fs.readFileSync("./key/privatekey.pem"),
// 	cert: fs.readFileSync("./key/certificate.pem")
// };
// https.createServer(httpsOp, srv.so).listen(443, function() {
// 	console.log("HTTPS服务已运行 ...");
// });
