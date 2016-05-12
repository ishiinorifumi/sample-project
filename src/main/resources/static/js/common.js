var SppCommon = {
	submitInProgress : function(self) {
		self.disabled = true;
		self.form.submit();
		/*
		 $.blockUI({
			 message: '<img src="/spplogin/img/busy.gif" />',
			 css: { 
				 border: 'none', 
				 backgroundColor: ''
				 },
			 overlayCSS: { opacity: 0.2  }
		 	});
		 	*/
		}
};