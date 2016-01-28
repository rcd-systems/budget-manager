function display(selector) {
    $(selector).removeClass("hidden");
}

function hide(selector) {
    $(selector).addClass("hidden");
}

function displayDetails(selector) {
  hide("#bm-home");
  hide(".bm-details");
  display(selector); 
  display("#bm-main-interface");  
}

function refreshYearsCombobox() {
  var callback = function (data) {
      var yearsComboboxHtml = "";
      $.each( data, function() {
          yearsComboboxHtml += '<option>' + this + '</option>';
      });
      $('#bm-combo-year').html(yearsComboboxHtml);            
  }
  
  return $.ajax({
      url: "../json/years",
      dataType: "json",
      success: callback
  });
}

function refreshTypesCombobox() {
  var callback = function (data) {
        var typesComboboxHtml = "";
        $.each( data, function() {
            typesComboboxHtml += '<option>' + this + '</option>';
        });
        $('#bm-combo-type').html(typesComboboxHtml);            
    }
    
    return $.ajax({
        url: "../json/types",
        dataType: "json",
        success: callback
    });
}

function refreshTransfersTable() {
    var callback = function (data) {
        var bmTbodytransfer = "";
        $.each( data, function() {
            bmTbodytransfer += '<div class="rcd-row">';
            bmTbodytransfer += '<span class="bm-col-date">' + this.date + '</span>';
            bmTbodytransfer += '<span class="bm-col-type">' + this.type + '</span>';
            bmTbodytransfer += '<span class="bm-col-amount">' + this.amount + ' ' + this.currency + '</span>';
                bmTbodytransfer += '<span class="bm-col-account">' + (this.srcAccount ? this.srcAccount : '-') + '</span>';
                bmTbodytransfer += '<span class="bm-col-date">' + (this.srcDate ? this.srcDate : '-') + '</span>';
                bmTbodytransfer += '<span class="bm-col-account">' + (this.tgtAccount ? this.tgtAccount : '-') + '</span>';
                bmTbodytransfer += '<span class="bm-col-date">' + (this.tgtDate ? this.tgtDate : '-') + '</span>';
                bmTbodytransfer += '<span class="bm-col-comments">' + (this.comments ? this.comments : '-') + '</span>';
                bmTbodytransfer += '</div>';    
        }); 
        $('#bm-transfers-tbody').html(bmTbodytransfer);
    }

    var year = $('#bm-combo-year').val();
    var month = $('#bm-combo-month').prop("selectedIndex");
    var type = $('#bm-combo-type').val();
    $.ajax({
        url: "../json/transfers/" + year + (month == 0 ? "" : "/" + month),
        data: {type:type},
        dataType: "json",
        success: callback
    });
}

$("#bm-nav-transfers").click(() => displayDetails("#bm-transfers-details"));
$("#bm-nav-accounts").click(() => displayDetails("#bm-accounts-details"));

$( ".bm-menu-combo" ).change(function() {
    refreshTransfersTable();
});

$.when( refreshYearsCombobox(), refreshTypesCombobox() ).done(() => refreshTransfersTable());




