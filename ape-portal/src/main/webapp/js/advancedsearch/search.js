var newSearchUrl, autocompletionUrl;
function setUrls(nUrl, aUrl) {
	newSearchUrl = nUrl;
	autocompletionUrl = aUrl;

}

function init() {
	initSearchOptionsHandlers();
	// initNavigatedTree();
	initSearchOptions();
	// $("#archivalLandscapeTree").dynatree("getTree").reload();
	initTabs();
	// initHelpFunction();
	$("#searchTerms").focus();
	activateAutocompletion("#searchTerms");
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
function activateAutocompletion(selector) {
	function split(val) {
		return val.split(/\s+/);
	}
	function extractLast(term) {
		return split(term).pop();
	}

	$(selector).bind("keydown", function(event) {
		if (event.keyCode === $.ui.keyCode.TAB && $(this).data("autocomplete").menu.active) {
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
	$("#tabs").tabs({
		selected : selectedTab,
		select : function(event, ui) {
			var tabName = $(ui.tab).attr("href");
			if (tabName == '#tabs-list') {
				$('#checkboxHierarchy').attr('checked', false);
				$('#resultsperpageRow').removeClass('hidden');
				// $('#resultsperpageRow :input').removeAttr('disabled');
				$('#updateCurrentSearch_view').val('');

			} else {
				$('#checkboxHierarchy').attr('checked', true);
				// $('#resultsperpageRow :input').attr('disabled', true);
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
		} else {
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
		$("#updateCurrentSearch_pageNumber").attr("value","1");
		updateCurrentSearchResults();
	});
	$(".list-searchresult").mouseenter(function() {
		$(this).find(".list-searchresult-actions").removeClass("hidden");
		$(this).find(".preview-button-holder").addClass("preview-button-holder-show");
	});
	$(".list-searchresult").mouseleave(function() {
		$(this).find(".list-searchresult-actions").addClass("hidden");
		$(this).find(".preview-button-holder").removeClass("preview-button-holder-show");
	});
	$(".preview-button-holder").mouseenter(
			function() {
				var holderPosition = $(this).position();
				var width = $(this).width();
				var url = $(this).attr("url");
				$("#preview-absolute").removeClass("preview-content preview-nocontent").addClass("preview-content")
						.css("top", holderPosition.top + "px");
				$.get(url, function(data) {
					$("#preview-absolute").html(data);
				});
			});
	// initTabHandlers();
}

function initSearchOptions() {
	var advanced = $("#advanced").val();
	if (advanced != null && advanced == "true") {
		$('#navigatedSearchOptionsHeader').removeClass("collapsed").addClass("expanded");
		$('#navigatedSearchOptionsContent').show();
	} else {
		$('#navigatedSearchOptionsHeader').removeClass("expanded").addClass("collapsed");
		$('#navigatedSearchOptionsContent').hide();
	}
	$('#navigatedSearchOptionsHeader').click(function() {
		if ($('#navigatedSearchOptionsHeader').hasClass("expanded")) {
			$('#navigatedSearchOptionsHeader').removeClass("expanded").addClass("collapsed");
			$('#navigatedSearchOptionsContent').hide();
		} else {
			$('#navigatedSearchOptionsHeader').removeClass("collapsed").addClass("expanded");
			$('#navigatedSearchOptionsContent').show();
		}
	});

}
function performNewSearch() {

	// The Navigation Tree nodes selected are stored
	fillInputFromNavTree();
	initTabs();
	if ($("#checkboxHierarchy").is(':checked')) {
		;
		selectedTabsSelector = "#tabs-context";
	} else {
		selectedTabsSelector = "#tabs-list";
	}
	$("#tabs-context").empty();
	$("#tabs-list").empty();
	$(selectedTabsSelector).html("<div class='icon_waiting'></div>");
	$("#resultsContainer").removeClass("hidden");
	$("#mode").val("new-search");
	$.post(newSearchUrl, $("#newSearchForm").serialize(), function(data) {
		$(selectedTabsSelector).html(data);
		updateSuggestions();
		makeRefinementsCollapsible();
		document.getElementById("resultsContainer").scrollIntoView(true);
	});
}

function updateCurrentSearchResults(addRemoveRefinement) {
	if ($("#updateCurrentSearch_view").val() == "hierarchy") {
		selectedTabsSelector = "#tabs-context";
	} else {
		selectedTabsSelector = "#tabs-list";
	}
	$(selectedTabsSelector).append("<div class='icon_waiting'></div>");
	$("#resultsContainer").removeClass("hidden");
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
		makeRefinementsCollapsible();
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
function removeRefinement(fieldName, fieldValue) {
	var fieldId = "#updateCurrentSearch_" + fieldName;

	var newFieldValue = "";
	if (fieldValue != undefined) {
		var oldFieldValue = $(fieldId).val();
		var ids = oldFieldValue.split(",");
		var first = true;
		for ( var i in ids) {
			var id = ids[i];
			if (id == fieldValue) {

			} else {
				if (first) {
					first = false;
					newFieldValue = newFieldValue + id;
				} else {
					newFieldValue = newFieldValue + "," + id;
				}
			}

		}
	}
	$(fieldId).attr("value", newFieldValue);
	$("#" + fieldName + "_" + fieldValue).remove();
	updateCurrentSearchResults();
}
function addOnlyThisRefinement(fieldName, fieldValue, shortDescription, longDescription) {
	$("#selectedRefinements > ul").empty();
	$("#updateCurrentSearch_" + fieldName).attr("value", "");
	addRefinement(fieldName,fieldValue, shortDescription, longDescription);
}
function addRefinement(fieldName, fieldValue, shortDescription, longDescription) {
	$("#updateCurrentSearch_pageNumber").attr("value", "1");
	var fieldId = "#updateCurrentSearch_" + fieldName;
	var oldFieldValue = $(fieldId).val();
	if (oldFieldValue.length > 0) {
		oldFieldValue = oldFieldValue + "," + fieldValue;
		$(fieldId).attr("value", oldFieldValue);
	} else {
		$(fieldId).attr("value", fieldValue);
	}
	var alreadyExist = $("#" + fieldName + "_" + fieldValue);
	if (alreadyExist.length == 0) {
		var escapedLongDescription = longDescription.replace("'", "&#039");
		var addRemoveRefinement = "<li id='" + fieldName + "_" + fieldValue + "'><a title='" + escapedLongDescription
				+ "' href=\"javascript:removeRefinement('" + fieldName + "','" + fieldValue + "')\">"
				+ escapedLongDescription + "<span class='close-icon'></span></a></li>";
		updateCurrentSearchResults(addRemoveRefinement);
	}
}
function removeDateRefinement(fieldName) {
	$("#updateCurrentSearch_" + fieldName).attr("value", "");
	$("#" + fieldName).remove();
	updateCurrentSearchResults();
}
function addDateRefinement(fieldName, fieldValue, shortDescription, longDescription) {
	$("#updateCurrentSearch_pageNumber").attr("value", "1");
	var fieldId = "#updateCurrentSearch_" + fieldName;
	$(fieldId).attr("value", fieldValue);
	$("#" + fieldName).remove();
	var escapedLongDescription = longDescription.replace("'", "&#039");
	var addRemoveRefinement = "<li id='" + fieldName + "'><a title='" + escapedLongDescription
			+ "' href=\"javascript:removeDateRefinement('" + fieldName + "')\">" + escapedLongDescription
			+ "<span class='close-icon'></span></a></li>";
	updateCurrentSearchResults(addRemoveRefinement);
}
function addMoreFacets(fieldName) {
	var fieldId = "#updateCurrentSearch_facetSettings";

	var newFieldValue = "";
	var oldFieldValue = $(fieldId).val();
	var ids = oldFieldValue.split(",");
	var first = true;
	for ( var i in ids) {
		var id = ids[i];
		if (id.indexOf(fieldName) == 0) {
			var values = id.split(":");
			id = values[0] + ":" + (parseInt(values[1])+10) + ":" + values[2];
		}
		if (first) {
			first = false;
			newFieldValue = newFieldValue + id;
		} else {
			newFieldValue = newFieldValue + "," + id;
		}
	}
	$(fieldId).attr("value", newFieldValue);
	updateCurrentSearchResults();
}
function collapseOrExpand(fieldName, expanded) {
	var fieldId = "#updateCurrentSearch_facetSettings";

	var newFieldValue = "";
	var oldFieldValue = $(fieldId).val();
	var ids = oldFieldValue.split(",");
	var first = true;
	for ( var i in ids) {
		var id = ids[i];
		if (id.indexOf(fieldName) == 0) {
			var values = id.split(":");
			id = values[0] + ":" + values[1] + ":" + expanded;
		}
		if (first) {
			first = false;
			newFieldValue = newFieldValue + id;
		} else {
			newFieldValue = newFieldValue + "," + id;
		}
	}
	$(fieldId).attr("value", newFieldValue);
}

function makeRefinementsCollapsible() {
	$('#refinements .boxtitle').each(function(index) {
		$(this).click(function() {
			var expanded = false;
			if ($(this).find(".collapsibleIcon").hasClass("expanded")) {
				$(this).find(".collapsibleIcon").removeClass("expanded").addClass("collapsed");
				$(this).parent().find('ul').addClass("hidden");
			} else {
				$(this).find(".collapsibleIcon").removeClass("collapsed").addClass("expanded");
				$(this).parent().find('ul').removeClass("hidden");
				expanded = true;
			}
			var id = $(this).parent().attr("id").split("_");
			collapseOrExpand(id[1], expanded);
			
		});
	});
	


}