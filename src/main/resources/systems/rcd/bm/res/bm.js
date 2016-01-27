function display(selector) {
    $(selector).removeClass("hidden");
}

function hide(selector) {
    $(selector).addClass("hidden");
}

function displayDetails(selector) {
  hide("#bm-home")
  display(selector); 
  display("#bm-main-interface");  
}

function refreshYearsCombobox(data) {
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
    $.ajax({
        url: "../json/transfers/" + year + (month == 0 ? "" : "/" + month),
        dataType: "json",
        success: callback
    });
}

$("#bm-nav-transfers").click(function(){
    displayDetails("#bm-transfers-details");
});

$( ".bm-menu-combo" ).change(function() {
    refreshTransfersTable();
});

refreshYearsCombobox().done(function() {
    refreshTransfersTable();
});




