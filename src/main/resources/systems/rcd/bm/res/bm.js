var currentSectionSelector = "#bm-section-home";

function display(selector) {
    $(selector).removeClass("hidden");
}

function hide(selector) {
    $(selector).addClass("hidden");
}

function displaySection(selector) {
    if (currentSectionSelector != selector) {
        hide(currentSectionSelector);
        display(selector);
        currentSectionSelector = selector;
    }    
}

function refreshYearsCombobox(data) {
    var callback = function (data) {
        var yearsComboboxHtml = "";
        $.each( data, function() {
            yearsComboboxHtml += '<option>' + this + '</option>';
        });
        $('#bm-transfers-menu-combo-year').html(yearsComboboxHtml);            
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
        $('#bm-tbody-transfers').html(bmTbodytransfer);
    }

    var year = $('#bm-transfers-menu-combo-year').val();
    var month = $('#bm-transfers-menu-combo-month').prop("selectedIndex");
    $.ajax({
        url: "../json/transfers/" + year + (month == 0 ? "" : "/" + month),
        dataType: "json",
        success: callback
    });
}

$("#bm-nav-button-transfers").click(function(){    
    displaySection("#bm-section-transfers");
});

$( ".bm-transfers-menu-combo" ).change(function() {
    refreshTransfersTable();
});

refreshYearsCombobox().done(function() {
    refreshTransfersTable();
});




