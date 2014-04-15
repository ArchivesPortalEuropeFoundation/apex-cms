
/*function displayEacDetails(displayEacUrl, databaseId, type, namespace) {
	if(!($(".multilanguageLang").length>0)){
		$(".multilanguageNoLang").removeClass("multilanguageNoLang");
	}
	multiLanguage();
	$("#eaccpfcontent").html("<div class='icon_waiting'></div>");
	var params = {};
	if (type != undefined) {
		params[namespace + "type"] = type;
	}
	params[namespace + "databaseId"] = databaseId;
console.log("displayEacUrl: " + displayEacUrl);
console.log("databaseId: " + databaseId);
	$.get(displayEacUrl, params, function(data) {
		$("#eaccpfcontent").html(data);

	});
	logAction(document.title, displayEacUrl);
}*/
function init(){
/*	$("#eaccpfcontent").html("<div class='icon_waiting'></div>");*/
	multiLanguage();
/*	$.get(displayEacUrl, function(data) {
		$("#eaccpfcontent").html(data);

	});
	logAction(document.title, displayEacUrl);*/
	
}
function multiLanguage(){
	if ($("div[class^='multilanguageLang_']").length>0) { //if class^='multilanguageLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("div[class^='multilanguageLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageLang"+firstLang){
				$(this).show();
			}
		});
	}
}
/*function initExpandableParts(){
	$("#expandableContent h2").removeClass("expanded").addClass("collapsed");
	$("#expandableContent h2").next().hide();
	$('#expandableContent h2').click(function() {
		if ($(this).hasClass("expanded")) {
			$(this).removeClass("expanded").addClass("collapsed");
			$(this).next().hide();
		} else {
			$(this).removeClass("collapsed").addClass("expanded");
			$(this).next().show();
		}
	});	
	$( "em" ).each(function() {
		console.log( + $(this).text() );
		var contentWithHighlightedItems = $(this).closest('.ead-content');
		contentWithHighlightedItems.show();
		if (contentWithHighlightedItems.prev().hasClass("collapsed")) {
			contentWithHighlightedItems.prev().removeClass("collapsed").addClass("expanded");
		}
	});
	$('ul.daolist a').click(function(event) {
		$(this).children().css( "opacity", "0.4" );
	});	
}
function updatePageNumber(displayEadUrl) {
	var params = {};
	$.get(displayEadUrl, params, function(data) {
		$("#right-pane").html(data);
	});
}
function getMoreTreeData(namespace, dtnode) {
	var data = {};
	if (dtnode.data.parentID != "undefined") {
		data[namespace + "parentId"] = dtnode.data.parentId;
	}
	data[namespace + "orderId"] = dtnode.data.orderId;
	data[namespace + "more"] = dtnode.data.more;
	data[namespace + "max"] = dtnode.data.max;
	return data;
}
function getTreeData(namespace, dtnode) {
	var data = {};
	data[namespace + "parentId"] = dtnode.data.id;
	return data;

}*/

/*function initPanes() {
	var leftPane = $("#left-pane");
	var rightPane = $("#right-pane");
	var splitter = $("#splitter");
	var header = $("#banner");
	var totalWidth = header.outerWidth(true);
	var marge = 10;
	rightPane.width(totalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge);
	
	$(splitter).draggable({
		containment : "#eadcontent",
		scroll : false,
		axis : "x",
		stop : function(event, ui) {
			var newTotalWidth = header.outerWidth(true);
			var left = $("#splitter").offset().left;
			if(left>750)
				left=550;
			leftPane.width(left - 1);
			splitter.css("left", 0);
			var newRightPaneWidth = newTotalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge;
			rightPane.width(newRightPaneWidth);
		}
	});
	
	$(window).resize(function() {
		var newTotalWidth = header.outerWidth(true);
		var newRightPaneWidth = newTotalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge;
		if (newRightPaneWidth < 300) {
			newRightPaneWidth = 300;
		}
		rightPane.width(newRightPaneWidth);
		resizePage();
	});
	resizePage();
}
function resizePage() {
	$("#eadcontent").height(($(window).height()-$("#eadcontent").offset().top));
}*/

function printEacDetails(url) {
	var preview = window.open(url, 'printpreview',
			'width=1100,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
	preview.focus();

}

