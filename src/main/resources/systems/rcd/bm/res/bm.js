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

function refreshTransfersData(data) {
  // Clears the transfers table
  $('#bm-tbody-transfers').html("")
  
  // Fills the transfers table
  var sum = 0;
  $.each( data, function() {
    $('#bm-tbody-transfers')
        .append('<tr>')
          .append('<td>' + this.date + '</td>')
          .append('<td>' + this.type + '</td>')
          .append('<td>' + this.amount + ' ' + this.currency + '</td>')
          .append('<td>' + (this.srcAccount ? this.srcAccount : '-') + '</td>')
          .append('<td>' + (this.srcDate ? this.srcDate : '-') + '</td>')
          .append('<td>' + (this.tgtAccount ? this.tgtAccount : '-') + '</td>')
          .append('<td>' + (this.tgtDate ? this.tgtDate : '-') + '</td>')
          .append('<td>' + (this.comments ? this.comments : '-') + '</td>')
        .append('</tr>');
    sum = sum + this.amount;    

	displaySection("#bm-section-transfers");
  });  
}

function displaySectionTransfers() {
	var year = $('#bm-transfers-menu-combo-year').val();
	var month = $('#bm-transfers-menu-combo-month').prop("selectedIndex");
	$.ajax({
		  url: "../json/transfer/" + year + (month == 0 ? "" : "/" + month),
		  dataType: "json",
		  success: refreshTransfersData
		});
}




$("#bm-nav-button-transfers").click(function(){	
	displaySectionTransfers();
});

$( ".bm-transfers-menu-combo" ).change(function() {
	displaySectionTransfers(); //TODO Improve calls and functions design.
});



