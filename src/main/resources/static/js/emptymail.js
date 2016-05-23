(function() {
	$("#sendEmptyMail").click(function(event){
		$.getJSON("/spplogin/EmptyMail/genToAddress", function(ret) {
			location.href = 'mailto:' + ret.to_address;
		});
	});
})();