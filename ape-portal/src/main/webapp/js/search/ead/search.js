var newSearchUrl, autocompletionUrl, savedSearchUrl, archivalLandscapeTreeUrl,displayPreviewUrl, portletNamespace;
function setUrls(nUrl, aUrl, sUrl, treeUrl, pUrl, pNamespace, nSUrl) {
	newSearchUrl = nUrl;
	autocompletionUrl = aUrl;
	savedSearchUrl = sUrl;
	archivalLandscapeTreeUrl= treeUrl;
	displayPreviewUrl= pUrl;
	portletNamespace = pNamespace;
}

function init() {
	initSearchOptionsHandlers();
	initSearchOptions();
	initTabs();
	// initHelpFunction();
	$("#searchTerms").focus();
	activateAutocompletion("#searchTerms", "ead");
	hideTabsIfNoResults();
	countInitialSelectItems();
}

function initContextTabHandlers(contextTreeUrl, previewUrl, namespace) {
	initContextTab(contextTreeUrl, previewUrl, namespace);
	initTabHandlers();
}
function initTabHandlers() {
	// $("#updateCurrentSearch_saveSearchButton").click(function(event) {
	// event.preventDefault();
	// saveSearch();
	// });
}

function clearSearch(){
	commonClearSearch();
	$('#checkboxHierarchy').attr('checked', false);
	$('#checkboxDao').attr('checked', false);
	$("#element").val("");
	$("#typedocument").val("");
    $("#archivalLandscapeTree").dynatree("getRoot").visit(function(node){
        node.select(false);
      });

	
}

function initTabs() {
	if ($("#checkboxHierarchy").is(':checked')) {
		selectedTab = 1;
	} else {
		selectedTab = 0;

	}
	$("#tabs").tabs({
		selected : selectedTab,
		select : function(event, ui) {
			var tabName = $(ui.tab).attr("href");
			if (tabName == '#tabs-list') {
				$('#checkboxHierarchy').attr('checked', false);
				$('#updateCurrentSearch_view').val('');
				//if other tab contains information, it should be a tab switch
				if ($("#tabs-context").children().size() > 0){
					updateCurrentSearchResults();
				}

			} else {
				$('#checkboxHierarchy').attr('checked', true);
				$('#updateCurrentSearch_view').val('hierarchy');
				//if other tab contains information, it should be a tab switch
				if ($("#tabs-list").children().size() > 0){
					updateCurrentSearchResults();
				}

			}
			

		}
	});
}
function initSearchOptionsHandlers() {
	activateAutocompletion(autocompletionUrl, "#searchTerms","ead");
	$("#searchTerms").focus();
	$("#searchTerms").keypress(function(event) {
		if (event.keyCode == 13) {
			$(this).data("autocomplete").destroy();
			activateAutocompletion(autocompletionUrl, "#searchTerms", "ead");
			performNewSearch();
		}
	});
	addSuggestionHandlers();
	$("#searchButton").click(function(event) {
		event.preventDefault();
		performNewSearch();
	});
	$("#sourceTabs .ui-tabs-selected").click(function(event) {
		event.preventDefault();

	});	
	
}


function initSearchOptions() {

	$('#navigatedSearchOptionsHeader').click(function() {
		if ($('#navigatedSearchOptionsHeader').hasClass("expanded")) {
			$('#navigatedSearchOptionsHeader').removeClass("expanded").addClass("collapsed");
			$('#navigatedSearchOptionsContent').addClass("hidden");
		} else {
			if ($('#archivalLandscapeTree').is(':empty')){
				initArchivalLandscapeTree(archivalLandscapeTreeUrl,displayPreviewUrl, portletNamespace);
			}
			$('#navigatedSearchOptionsHeader').removeClass("collapsed").addClass("expanded");
			$('#navigatedSearchOptionsContent').removeClass("hidden");
		}
	});

}
function performNewSearch() {
	// The Navigation Tree nodes selected are stored
	var documentTitle = document.title;
	if ($("#checkboxHierarchy").is(':checked')) {
		selectedTabsSelector = "#tabs-context";
		documentTitle = documentTitle + " (new-context-search)";
	} else {
		selectedTabsSelector = "#tabs-list";
		documentTitle = documentTitle + " (new-list-search)";
	}
	$("#tabs-context").empty();
	$("#tabs-list").empty();
	fillInputFromNavTree();
	initTabs();
	blockSearch();
	$("#mode").val("new-search");
	$.post(newSearchUrl, $("#newSearchForm").serialize(), function(data) {
		$(selectedTabsSelector).html(data);
		updateSuggestions();
		updateSourceTabs();
		
		hideTabsIfNoResults();
		$("#searchResultsContainer").removeClass("hidden");
		//document.getElementById("searchResultsContainer").scrollIntoView(true);
	});
	logAction(documentTitle, newSearchUrl);
}

function hideTabsIfNoResults(){
	if ($("#noResults").length > 0){
		$("#tabs #tabscontainer").removeClass("hidden").addClass("hidden");
	}else {
		$("#tabs #tabscontainer").removeClass("hidden");
	}
}
function updateCurrentSearchResults(addRemoveRefinement) {
	if ($("#updateCurrentSearch_view").val() == "hierarchy") {
		selectedTabsSelector = "#tabs-context";
	} else {
		selectedTabsSelector = "#tabs-list";
	}
	$("#searchResultsContainer").removeClass("hidden");
	blockSearch();
	$.post(newSearchUrl, $("#updateCurrentSearch").serialize(), function(data) {
		var refinementsHtml = $("#selectedRefinements > ul").html();
		$("#tabs-context").empty();
		$("#tabs-list").empty();
		// update the page with the search results
		$(selectedTabsSelector).html(data);
		// keep the selected refinements
		$("#selectedRefinements > ul").html(refinementsHtml);
		if (addRemoveRefinement != undefined) {
			$("#selectedRefinements > ul").append(addRemoveRefinement);
		}
		//makeRefinementsCollapsible();
		document.getElementById("searchResultsContainer").scrollIntoView(true);
	});
	logAction(document.title + " (update-current-list-search)", newSearchUrl);
}


function saveSearch(savedId) {
	var form = $("#updateCurrentSearch").serialize();
	if (savedId != undefined){
		form = form + "&ownSavedSearchId=" + savedId;
	}
	$.post(savedSearchUrl, form, function(dataResponse) {
		if (dataResponse.answerCode != "") {
			$('#answerMessageSavedSearch').removeClass("success").removeClass("failure");
			$('#answerMessageSavedSearch').html(dataResponse.answerMessage);		
			if (dataResponse.answerCode == "true") {
				$('#answerMessageSavedSearch').addClass("success");
			} else {
				$('#answerMessageSavedSearch').addClass("failure");
			}
			$('#answerMessageSavedSearch').show();
		}
		$('#answerMessageSavedSearch').delay(3000).fadeOut('slow');

	});
}
