(function() {
	/**　空メール処理の開発用仮実装　ここから */
	$("#empty-mail-dialog").dialog({ autoOpen: false });
	$("#sendEmptyMail").click(function(event){
		$.getJSON("/spplogin/EmptyMail/genToAddress", function(ret) {
			//alert(ret.to_address);
			$("#to-address").text(ret.to_address);
			var resUrl = location.protocol + "//" + location.host + "/"
			+ "spplogin/Regist?form=" + ret.to_address.split("+")[1].split("@")[0];
			
			$("#response-url").html("<a href='" + resUrl + "'>" + resUrl + "</a>");
			$("#empty-mail-dialog").dialog("open");
		});
	});
	/**　空メール処理の開発用仮実装　ここまで */
})();