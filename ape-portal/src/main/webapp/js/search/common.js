var commonNameSearchUrl, commonEadSearchUrl;
function setCommonUrls(eadSUrl, nSUrl) {
	commonNameSearchUrl = eadSUrl;
	commonEadSearchUrl = nSUrl;
}

function initCommon() {
	initCommonSearchOptionsHandlers();
}
function changeSearch(type){
	var form = $("#newSearchForm");
	if (type == "name-search"){
		form.attr("action", commonNameSearchUrl);
		form.submit();
	}else if (type == "ead-search"){
		form.attr("action", commonEadSearchUrl);
		form.submit();
	}else {
		alert("Not implemented yet");
	}
}

function initCommonSearchOptionsHandlers() {
	//activateAutocompletion("#searchTerms");
	$("#searchTerms").focus();
	//$("#searchTerms").keypress(function(event) {
	//	if (event.keyCode == 13) {
	//		$(this).data("autocomplete").destroy();
	//		activateAutocompletion("#searchTerms");
	//		performNewSearch();
	//	}
	//});
	//addSuggestionHandlers();
	//$("#searchButton").click(function(event) {
	//	event.preventDefault();
	//	performNewSearch();
	//});
	$("#sourceTabs .ui-tabs-selected").click(function(event) {
		event.preventDefault();

	});	
	
}
function commonClearSearch(){
	$("#searchTerms").val("");
	$('#checkboxMethod').attr('checked', false);
}
