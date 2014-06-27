var commonNameSearchUrl, commonEadSearchUrl, commonEagSearchUrl;;
function setCommonUrls(nSUrl,eadSUrl,eagSUrl ) {
	commonNameSearchUrl = nSUrl;
	commonEadSearchUrl = eadSUrl;
	commonEagSearchUrl = eagSUrl;
}

function initCommon() {
	initCommonSearchOptionsHandlers();
}
function updateSourceTabs(){
	$("#sourceTabs").empty();
	$("#sourceTabs").html($("#NEWsourceTabs").html());
	$("#NEWsourceTabs").remove();
}
function changeSearch(type){
	var term = $("#updateCurrentSearch_term").val();
	var method = $("#updateCurrentSearch_method").val();
	var newForm = $("<form action='' method='POST'/>");
	if (term != undefined){
		newForm.append("<input type='hidden' name='term' value='" + term +"'/>");
	}
	if (method != undefined){
		newForm.append("<input type='hidden' name='method' value='" + method+"'/>");
	}
	if (type == "name-search"){
		newForm.attr("action", commonNameSearchUrl);
		newForm.appendTo("body");
		newForm.submit();
	}else if (type == "ead-search"){
		newForm.attr("action", commonEadSearchUrl);
		newForm.appendTo("body");
		newForm.submit();
	}else if (type == "institution-search"){
		newForm.attr("action", commonEagSearchUrl);
		newForm.appendTo("body");
		newForm.submit();
	}else {
		alert("Not implemented yet");
	}
	
}
function updatePageNumber(url) {
	var pageNumber = url.split("=")[1];
	$("#updateCurrentSearch_pageNumber").attr("value", pageNumber);
	updateCurrentSearchResults();
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

function activateAutocompletion(url, selector, sourceType) {
	function split(val) {
		return val.split(/\s+/);
	}
	function extractLast(term) {
		term=term.trim();
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
			$.getJSON(url, {
				term : extractLast(request.term),
				sourceType : sourceType
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
function commonClearSearch(){
	$("#searchTerms").val("");
	$('#checkboxMethod').attr('checked', false);
	$("#fromdate").val("");
	$("#todate").val("");
	$('#exactDateSearch').attr('checked', false);
}

function blockSearch(){
	$.blockUI.defaults.css = {}; 
    $(document).ajaxStop($.unblockUI); 
    
	$.blockUI({message: $("#loadingText").text(), overlayCSS: { backgroundColor: 'none'  } }); 
}

function updateSorting(fieldValue) {
	$("#updateCurrentSearch_order").attr("value", fieldValue);
	updateCurrentSearchResults();
}


function removeRefinement(fieldName, fieldValue) {
	var fieldId = "#updateCurrentSearch_" + fieldName;
    var pattern = /[^\w]/g;
	var newFieldValue = "";
	if (fieldValue != undefined) {
		var fieldValueToCompare = fieldValue.replace(pattern,"_");
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
		$("#" + fieldName + "_" + fieldValueToCompare).remove();
	}
	$(fieldId).attr("value", newFieldValue);
	
	updateCurrentSearchResults();
}
function addOnlyThisRefinement(fieldName, fieldValue, shortDescription, longDescription) {
	$("#selectedRefinements > ul").empty();
	$("#updateCurrentSearch_" + fieldName).attr("value", "");
	addRefinement(fieldName,fieldValue, shortDescription, longDescription);
}
function addRefinement(fieldName, fieldValue, longDescription) {
	$("#updateCurrentSearch_pageNumber").attr("value", "1");
	var fieldId = "#updateCurrentSearch_" + fieldName;
	var oldFieldValue = $(fieldId).val();
    var pattern = /[^\w]/g;
    var newFieldValue = fieldValue.replace(pattern,"_");
	if (oldFieldValue.length > 0) {
		oldFieldValue = oldFieldValue + "," + newFieldValue;
		$(fieldId).attr("value", oldFieldValue);
	} else {
		$(fieldId).attr("value", fieldValue);
	}
	var alreadyExist = $("#" + fieldName + "_" + newFieldValue);
	if (alreadyExist.length == 0) {
		var escapedLongDescription = longDescription.replace("'", "&#039");
		var addRemoveRefinement = "<li id='" + fieldName + "_" + newFieldValue + "'><a title='" + escapedLongDescription
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
function addDateRefinement(fieldName, fieldValue, longDescription) {
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
				var url = $(this).attr("data-url");
				$.get(url, function(data) {
					displayPreview("#search-preview",data);
					
				});
				//logAction("advanced-search-list-preview", url);
				
			});
	if ($(".list-searchresult").length > 0){
		var firstSearchResult = $(".list-searchresult").first();
		firstSearchResult.find(".list-searchresult-actions").removeClass("hidden");
		firstSearchResult.find(".preview-button-holder").addClass("preview-button-holder-show");
		var url =firstSearchResult.find(".preview-button-holder").attr("data-url");
		$.get(url, function(data) {
			displayPreview("#search-preview",data);
			
		});
	}
	makeRefinementsCollapsible();
}
function displayPreview (preview, data){
	var $previewDiv = $(preview);
	$previewDiv.html(data);
	var divTop = $previewDiv.parent().offset().top;
	var scrollTop = $(window).scrollTop();
	
	if (scrollTop > divTop){
		var marginTop = scrollTop - divTop +5;
		$previewDiv.css("margin-top", marginTop + "px");
	}else {
		$previewDiv.css("margin-top","0px");
	}
	var alwaysVisibleContainer = $(preview + " #alwaysVisibleContainer");
	if(alwaysVisibleContainer.length > 0) {
		var daos = $(preview + " .daolistContainer .dao a");
		if(daos.length > 0) {
			var alwaysVisible = $( "<div  id='alwaysVisible'/>" );
			var innerDiv = $( "<div class='dao'/>" );
			var firstDao = daos.first();
			firstDao.find("span").remove();
			innerDiv.html(firstDao.html());
			if(daos.length > 1) {
				innerDiv.append("<img id='more-daos' src='/Portal-theme/images/ape/icons/plus.gif'/>");
			}
			alwaysVisible.append(innerDiv);
			alwaysVisibleContainer.append(alwaysVisible);
			var daoParent = $(".daolistContainer").parent();	
			daoParent.prev().remove();
			daoParent.remove();
		}
		var otherfindingaids = $(preview + " .otherfindingaids");
		if (otherfindingaids.length > 0){
			var firstOtherfindingaids = otherfindingaids.first();
			alwaysVisibleContainer.append(firstOtherfindingaids.html());
			var parent = firstOtherfindingaids.parent();	
			parent.prev().remove();
			parent.remove();
		}
	}
	
	
	if ($(preview + " #realcontent").height() > $(preview + " #content").height()){
		$(preview + " #more-line").removeClass("hide-more-line").addClass("show-more-line");
	}
	$previewDiv.removeClass("preview-content").addClass("preview-content");
}

