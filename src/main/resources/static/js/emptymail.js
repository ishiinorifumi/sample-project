(function() {
	$("#sendEmptyMail").click(function(event){
		$.getJSON("/spplogin/SPPLogin/emptyMailAddress", function(ret) {
			alert(ret.to_address);
		});
	});
})();