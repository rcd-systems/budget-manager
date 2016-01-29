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
      $('#bm-combo-year option:last').prop('selected', true);
  }
  
  return $.ajax({
      url: "../json/years",
      dataType: "json",
      success: callback
  });
}

function refreshTypesCombobox() {
  var callback = function (data) {
        var typesComboboxHtml = '<option>All types</option>';
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

function refreshAccountsComboboxes() {
  var callback = function (data) {
        var accountComboboxHtml = '<option>All account</option>';
        $.each( data, function() {
            accountComboboxHtml += '<option>' + this + '</option>';
        });
        $('#bm-combo-account').html(accountComboboxHtml);   
        $('#bm-combo-account-from').html(accountComboboxHtml);
        $('#bm-combo-account-to').html(accountComboboxHtml);
    }
    
    return $.ajax({
        url: "../json/accounts",
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
  var allTypes = $('#bm-combo-type').prop("selectedIndex") == 0;
  var type = $('#bm-combo-type').val();
  var allAccounts = $('#bm-combo-account').prop("selectedIndex") == 0;
  var account = $('#bm-combo-account').val();
  var fromAllAccounts = $('#bm-combo-account-from').prop("selectedIndex") == 0;
  var fromAccount = $('#bm-combo-account-from').val();
  var toAllAccounts = $('#bm-combo-account-to').prop("selectedIndex") == 0;
  var toAccount = $('#bm-combo-account-to').val();
  $.ajax({
      url: '../json/transfers',
      data: {
        year: year,
        month: month == 0 ? undefined : month,
        type: allTypes ? undefined : type,
        account: allAccounts ? undefined : account,
        fromAccount: fromAllAccounts ? undefined : fromAccount,
        toAccount: toAllAccounts ? undefined : toAccount,
      },
      dataType: "json",
      success: callback
  });
}

var months = ['January', 'February', 'March', 'April', 'May', 'June', 'July', 'August', 'September', 'Obtober', 'November', 'December'];
function refreshAccountsGraph() {
  var callback = function (data) {
  
  console.log("test");
    /*var dataArray = [['Month','Delta']];    
    $.each(data.deltas, function (index) {
      dataArray.push([months[index], this])
    });*/
    
    var dataArray =[
          ['Year', 'Sales', 'Expenses'],
          ['2004',  1000,      400],
          ['2005',  1170,      460],
          ['2006',  660,       1120],
          ['2007',  1030,      540]
        ]
  
    var dataTable = google.visualization.arrayToDataTable(dataArray);

    var options = {
      title: 'Account',
      curveType: 'function',
      legend: { position: 'bottom' }

    };

    var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

    chart.draw(dataTable, options);
  }

  var year = $('#bm-combo-year').val();
  var month = $('#bm-combo-month').prop("selectedIndex");
  var allAccounts = $('#bm-combo-account').prop("selectedIndex") == 0;
  var account = $('#bm-combo-account').val();
  $.ajax({
      url: '../json/deltas',
      data: {
        year: year,
        month: month == 0 ? undefined : month,
        account: allAccounts ? "AxaCurrent" : account,
      },
      dataType: "json",
      success: callback
  });
}

$("#bm-nav-transfers").click(() => displayDetails("#bm-transfers-details"));
$("#bm-nav-accounts").click(() => displayDetails("#bm-accounts-details"));

$( ".bm-menu-combo" ).change(function() {
    refreshTransfersTable();
});

$.when( refreshYearsCombobox(), refreshTypesCombobox(), refreshAccountsComboboxes() ).done(() => {
  refreshTransfersTable();
  
  google.charts.load('current', {'packages':['corechart']});
  google.charts.setOnLoadCallback(refreshAccountsGraph);  
});

(function() {
  

  function drawChart() {
    var data = google.visualization.arrayToDataTable([
      ['Year', 'Sales', 'Expenses'],
      ['2004',  1000,      400],
      ['2005',  1170,      460],
      ['2006',  660,       1120],
      ['2007',  1030,      540]
    ]);

    var options = {
      title: 'Company Performance',
      curveType: 'function',
      legend: { position: 'bottom' }
    };

    var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

    chart.draw(data, options);
  }
})();




