(function($) {
	"use strict";

	/*** Toggle the side navigation ***/
	$("#sidebarToggle").on("click", function(e) {
		e.preventDefault();
		$("body").toggleClass("sb-sidenav-toggled");
	});

	$(document).on('click', '.card-header span.clickable', function(e) {
		var $this = $(this);
		if (!$this.hasClass('card-collapsed')) {
			$this.addClass('card-collapsed');
			$this.find('i').removeClass('fas fa-angle-down').addClass('fas fa-angle-up');
		} else {
			$this.removeClass('card-collapsed');
			$this.find('i').removeClass('fas fa-angle-up').addClass('fas fa-angle-down');
		}
	});

	/*** Initial date picker setting ***/
	if ($('.input-group.date').length !== 0) {
		$('.input-group.date').datepicker({
			format: "dd/mm/yyyy",
			autoclose: true,
			todayHighlight: true,
			startDate: "01/01/1900"
		});
	}

	/*** Initial data table setting (Only search)***/
	if ($("#idTableOnlySearch").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableOnlySearch").DataTable({
			"paging": false,
			"searching": true,
			"language": { search: '', searchPlaceholder: "Search..." },
			"info": false,
			"order": [[0, "asc"]]
		});
	}

	/*** Initial data table setting RMSearchModal(Only search)***/
	if ($("#idTableOnlySearchRM").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableOnlySearchRM").DataTable({
			"paging": false,
			"searching": false,
			"info": false,
			"order": [[0, "asc"]]
		});
	}

	/*** Initial data table setting for PRSearchModal(Only search)***/
	if ($("#idTableOnlySearchPR").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableOnlySearchPR").DataTable({
			"paging": false,
			"searching": false,
			"info": false,
			"order": [[0, "asc"]]
		});
	}

	/*** Initial data table setting (No Feature)***/
	if ($("#idTableNoFeature").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableNoFeature").DataTable({
			"paging": false,
			"searching": false,
			"info": false,
			"order": [[0, "desc"]]
		});
	}

	/*** Initial data table setting (No Checkbox)***/
	if ($("#idTableNoBox").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableNoBox").DataTable({
			"pagingType": "full_numbers",
			"searching": false,
			"lengthMenu": [[10, 25, 50, 100, -1], ["10 rows", "25 rows", "50 rows", "100 rows", "All rows"]],
			"oLanguage": { "sLengthMenu": "_MENU_" },
			"order": [[0, "asc"]]
		});

		$(".dataTables_length").addClass("bs-select");
		$(".dataTables_paginate").addClass("float-right");
	}

	/*** Initial data table setting (No Checkbox) and arrange by descending***/
	if ($("#idTableNoBoxDesc").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableNoBoxDesc").DataTable({
			"pagingType": "full_numbers",
			"searching": false,
			"lengthMenu": [[10, 25, 50, 100, -1], ["10 rows", "25 rows", "50 rows", "100 rows", "All rows"]],
			"oLanguage": { "sLengthMenu": "_MENU_" },
			"order": [[0, "desc"]]
		});

		$(".dataTables_length").addClass("bs-select");
		$(".dataTables_paginate").addClass("float-right");
	}
	
	/*** Initial data table setting (With Checkbox)***/
	if ($("#idTable").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTable").DataTable({
			"pagingType": "full_numbers",
			"searching": false,
			"lengthMenu": [[10, 25, 50, 100, -1], ["10 rows", "25 rows", "50 rows", "100 rows", "All rows"]],
			"oLanguage": { "sLengthMenu": "_MENU_" },
			"columnDefs": [{
				orderable: false,
				className: "select-checkbox",
				targets: 0
			}],
			"order": [[1, "asc"]]
		});

		$(".dataTables_length").addClass("bs-select");
		$(".dataTables_paginate").addClass("float-right");
	
		var table = $('#idTable').DataTable();
		var rows = table.rows({
			'search': 'applied'
		}).nodes();
		$('#checkAll').on('click', function() {
			$('input[type="checkbox"]', rows).prop('checked', this.checked);
			delButtonToggle();
		});
	}
	
	/*** Initial data table setting (With Checkbox) and arrange by descending***/
	if ($("#idTableDesc").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableDesc").DataTable({
			"pagingType": "full_numbers",
			"searching": false,
			"lengthMenu": [[10, 25, 50, 100, -1], ["10 rows", "25 rows", "50 rows", "100 rows", "All rows"]],
			"oLanguage": { "sLengthMenu": "_MENU_" },
			"columnDefs": [{
				orderable: false,
				className: "select-checkbox",
				targets: 0
			}],
			"order": [[1, "desc"]]
		});

		$(".dataTables_length").addClass("bs-select");
		$(".dataTables_paginate").addClass("float-right");
	
		var table = $('#idTableDesc').DataTable();
		var rows = table.rows({
			'search': 'applied'
		}).nodes();
		$('#checkAll').on('click', function() {
			$('input[type="checkbox"]', rows).prop('checked', this.checked);
			delButtonToggle();
		});
	}
	
	if ($("#idTableMatlList").length !== 0) {
		/***
		FSGS) Faiz - 216 - fix date sorting START
		refer to https://datatables.net/blog/2014-12-18
		***/
		$.fn.dataTable.moment('DD/MM/YYYY hh:mm:ss a');
		// FSGS) Faiz - 216 - fix date sorting END

		$("#idTableMatlList").DataTable({
			"pagingType": "full_numbers",
			"searching": false,
			"lengthMenu": [[10, 25, 50, 100, -1], ["10 rows", "25 rows", "50 rows", "100 rows", "All rows"]],
			"oLanguage": { "sLengthMenu": "_MENU_" },
			"columnDefs": [{
				orderable: false,
				className: "select-checkbox",
				targets: 0
			}],
			"order": [[2, "asc"]]
		});

		$(".dataTables_length").addClass("bs-select");
		$(".dataTables_paginate").addClass("float-right");
	
		var table = $('#idTableMatlList').DataTable();
		var rows = table.rows({
			'search': 'applied'
		}).nodes();
		$('#checkAll').on('click', function() {
			$('input[type="checkbox"]', rows).prop('checked', this.checked);
			delButtonToggle();
		});
	}
})(jQuery);

/*** Enabled delete button ***/
function delButtonToggle() {
	var enabled = false;
	var tableRow = document.getElementsByName("tableRow");
	for (var i = 0; i < tableRow.length; i++) {
		if (tableRow[i].checked) {
			enabled = true;
		}
	}

	if (enabled) {
		$('#btnDelete').prop('disabled', false);
	} else {
		$('#btnDelete').prop('disabled', true);
	}
}

function showAlert(alert) {
	var msg = "";

	if (alert === 1) {
		msg = 'Not allow file size more than 100MB!';
	} else {
		msg = 'Incorrect extension file uploaded!';
	}

	swal({
		text: msg,
		button: {
			text: "OK",
			value: true,
			visible: true,
			className: "btn btn-primary"
		}
	})
}

function checkExt(filePath) {
	var ext = filePath.match(/\.([^\.]+)$/)[1];
	var allow = true;

	switch (ext) {
		case 'xls':
		case 'xlsx':
		case 'txt':
		case 'csv': allow = true; break;
		default: allow = false;
	}

	return allow;
}