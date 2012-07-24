var newSearchUrl, autocompletionUrl;
function setUrls(nUrl, aUrl){
	newSearchUrl = nUrl;
	autocompletionUrl = aUrl;

}

function init(){
	initSearchOptionsHandlers();
//	initNavigatedTree();
	initSearchOptions();
//	$("#archivalLandscapeTree").dynatree("getTree").reload();
	initTabs();
//	initHelpFunction();
	$("#searchTerms").focus();
	activateAutocompletion("#searchTerms");
}

function initContextTabHandlers() {
	initContextTab();
	initTabHandlers();
}
function initTabHandlers() {
//	$("#updateCurrentSearch_saveSearchButton").click(function(event) {
//		event.preventDefault();
//		saveSearch();
//	});
}
function activateAutocompletion(selector) {
	function split(val) {
		return val.split(/\s+/);
	}
	function extractLast(term) {
		return split(term).pop();
	}
	
	$(selector).bind(
			"keydown",
			function(event) {
				if (event.keyCode === $.ui.keyCode.TAB
						&& $(this).data("autocomplete").menu.active) {
					event.preventDefault();
				}
			});
	$(selector).autocomplete({
		minLength : 0,
		source : function(request, response) {
			$.getJSON(autocompletionUrl, {
				term : extractLast(request.term)
			}, response);
		},
		search : function() {
			// custom minLength
			var term = extractLast(this.value);
			if (term.length < 1) {
				return false;
			}
		},
		focus : function() {
			// prevent value inserted on focus
			return false;
		},
		select : function(event, ui) {
			var terms = split(this.value);
			// remove the current input
			terms.pop();
			// add the selected item
			terms.push(ui.item.value);
			// add placeholder to get the comma-and-space at the end
			terms.push("");
			this.value = terms.join(" ");
			return false;
		}
	});
}




function searchOnSuggestion(value) {
	$("#searchTerms").val(value);
	performNewSearch();
}

function updateSuggestions() {
	$("#suggestionSearch").empty();
	$("#suggestionSearch").html($("#NEWsuggestionSearch").html());
	$("#NEWsuggestionSearch").remove();
	addSuggestionHandlers();
}
function addSuggestionHandlers() {
	$('.suggestionLink').each(function(index) {
		$(this).attr("href", "javascript:searchOnSuggestion('" + $(this).text() + "');");
	});

}
function initTabs() {
	if ($("#checkboxHierarchy").is(':checked')) {
		selectedTab = 1;
	} else {
		selectedTab = 0;

	}
	$("#tabs").tabs({selected: selectedTab,
		select : function(event, ui) {
			var tabName = $(ui.tab).attr("href");
			if (tabName == '#tabs-list') {
				$('#checkboxHierarchy').attr('checked', false);
				$('#resultsperpageRow').removeClass('hidden');
				//$('#resultsperpageRow :input').removeAttr('disabled');
				$('#updateCurrentSearch_view').val('');
				
			}else {
				$('#checkboxHierarchy').attr('checked', true);
				//$('#resultsperpageRow :input').attr('disabled', true);
				$('#updateCurrentSearch_view').val('hierarchy');
				$('#resultsperpageRow').addClass('hidden');

			}
			updateCurrentSearchResults();

		}
	});
}
function initSearchOptionsHandlers() {
	$("#newSearchForm input").keypress(function(event) {
		if (event.keyCode == 13) {
			performNewSearch();
		}
	});
	$("#newSearchForm select").keypress(function(event) {
		if (event.keyCode == 13) {
			performNewSearch();
		}
	});
	$("#archivalLandscapeTree").keypress(function(event) {
		if (event.keyCode == 13) {
			performNewSearch();
		}
	});
	activateAutocompletion("#searchTerms");
	$("#searchTerms").focus();
	$("#searchTerms").keypress(function(event) {
		if (event.keyCode == 13) {
			$(this).data("autocomplete").destroy();
			activateAutocompletion("#searchTerms");
			performNewSearch();
		}
	});
	addSuggestionHandlers();
	$("#searchButton").click(function(event) {
		event.preventDefault();
		performNewSearch();
	});
	$("#checkboxHierarchy").change(function(event) {
		if ($(this).is(":checked")) {
			$('#resultsperpageRow').addClass('hidden');
		}else {
			$('#resultsperpageRow').removeClass('hidden');
		}
	});
}

function initListTabHandlers() {
	$("#updateCurrentSearch_pageNumber").keypress(function(event) {
		if (event.keyCode == 13) {
			event.preventDefault();
			updateCurrentSearchResults();
		}
	});

	$("#updateCurrentSearch_resultsperpage").change(function(event) {
		updateCurrentSearchResults();
	});
//	$("a.facet-more").click(function(event) {
//		event.preventDefault();
//		getMoreFacetFieldValues(this);
//	});
	$(".list-searchresult").mouseenter(function() {
		$(this).find(".preview-button-holder").addClass("preview-button-holder-show");
	});
	$(".list-searchresult").mouseleave(function() {
		$(this).find(".preview-button-holder").removeClass("preview-button-holder-show");
	});
	$(".preview-button-holder").mouseenter(
			function() {
				var holderPosition = $(this).position();
				var width = $(this).width();
				var url = $(this).attr("url");
				$("#preview-absolute").removeClass("preview-result preview-noresult").addClass("preview-result").css(
						"top", holderPosition.top + "px").css("left", (holderPosition.left + width) + "px");
				$.get(url, function(data) {
					$("#preview-absolute").html(data);
				});
			});
//	initTabHandlers();
}

function initSearchOptions(){
	var advanced = $("#advanced").val();
	if (advanced != null && advanced == "true"){
		$('#navigatedSearchOptionsHeader').removeClass("collapsed").addClass("expanded");
		$('#navigatedSearchOptionsContent').show();
	}else {
		$('#navigatedSearchOptionsHeader').removeClass("expanded").addClass("collapsed");
		$('#navigatedSearchOptionsContent').hide();		
	}
	$('#navigatedSearchOptionsHeader').click(function(){
		if($('#navigatedSearchOptionsHeader').hasClass("expanded")){
			$('#navigatedSearchOptionsHeader').removeClass("expanded").addClass("collapsed");
			$('#navigatedSearchOptionsContent').hide();
		}else{
			$('#navigatedSearchOptionsHeader').removeClass("collapsed").addClass("expanded");
			$('#navigatedSearchOptionsContent').show();
		}
	});

}

function performNewSearch() {
	
	// The Navigation Tree nodes selected are stored
	fillInputFromNavTree();
	initTabs();
	if ($("#checkboxHierarchy").is(':checked')) {;
		selectedTabsSelector = "#tabs-context";
	} else {
		selectedTabsSelector = "#tabs-list";
	}
	$("#tabs-context").empty();
	$("#tabs-list").empty();
	$(selectedTabsSelector).html("<div class='waiting-icon'></div>");
	$("#resultsContainer").removeClass("hidden");
	$("#mode").val("new-search");
	alert($('#navigationTreeNodesSelected').val());
	$.post(newSearchUrl, $("#newSearchForm").serialize(), function(data) {
		$(selectedTabsSelector).html(data);
		updateSuggestions();
		document.getElementById("resultsContainer").scrollIntoView(true);
	});
}

function updateCurrentSearchResults() {
	if ($("#updateCurrentSearch_view").val() == "hierarchy") {
		selectedTabsSelector = "#tabs-context";
	} else {
		selectedTabsSelector = "#tabs-list";
	}
	$(selectedTabsSelector).append("<div class='waiting-icon'></div>");
	$("#resultsContainer").removeClass("hidden");
	$.post(newSearchUrl, $("#updateCurrentSearch").serialize(), function(data) {
		$("#tabs-context").empty();
		$("#tabs-list").empty();
		$(selectedTabsSelector).html(data);
		document.getElementById("resultsContainer").scrollIntoView(true);
	});
}

function updateSorting(fieldValue) {
	$("#updateCurrentSearch_order").attr("value", fieldValue);
	updateCurrentSearchResults();
}

function updatePageNumber(url) {
	var pageNumber = url.split("=")[1];
	$("#updateCurrentSearch_pageNumber").attr("value", pageNumber);
	updateCurrentSearchResults();
}
function removeRefinement(fieldName) {
	$("#updateCurrentSearch_" + fieldName).attr("value", "");
	updateCurrentSearchResults();
}

function addRefinement(fieldName, fieldValue) {
	$("#updateCurrentSearch_pageNumber").attr("value", "1");
	$("#updateCurrentSearch_" + fieldName).attr("value", fieldValue);
	updateCurrentSearchResults();
}