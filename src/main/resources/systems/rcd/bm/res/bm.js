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
            bmTbodytransfer += '<tr>';
            bmTbodytransfer += '<td>' + this.date + '</td>';
            bmTbodytransfer += '<td>' + this.type + '</td>';
            bmTbodytransfer += '<td>' + this.amount + ' ' + this.currency + '</td>';
                bmTbodytransfer += '<td>' + (this.srcAccount ? this.srcAccount : '-') + '</td>';
                bmTbodytransfer += '<td>' + (this.srcDate ? this.srcDate : '-') + '</td>';
                bmTbodytransfer += '<td>' + (this.tgtAccount ? this.tgtAccount : '-') + '</td>';
                bmTbodytransfer += '<td>' + (this.tgtDate ? this.tgtDate : '-') + '</td>';
                bmTbodytransfer += '<td>' + (this.comments ? this.comments : '-') + '</td>';
                bmTbodytransfer += '</tr>';    
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




