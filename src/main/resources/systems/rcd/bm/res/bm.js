var currentSectionSelector = "#bm-section-home";

function display(selector) {
	$(selector).removeClass("hidden");
}

function hide(selector) {
	$(selector).addClass("hidden");
}

function displaySection(selector) {
	hide(currentSectionSelector);
	display(selector);
	currentSectionSelector = selector;
}

function displaySectionTransfers() {
	displaySection("#bm-section-transfers");
	
	$.ajax({
	  url: "test.html",
	  dataType: "html",
	  success: (data) => {
		  $("#bm-section-transfers").html(data);
	  }
	});
}




$("#bm-nav-button-transfers").click(function(){	
	displaySectionTransfers();
});



