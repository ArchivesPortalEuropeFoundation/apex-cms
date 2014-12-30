function getViewForm(url1,url2,edit){
	$.post(url1,function(e){
		$("#bookmarksDiv").html(e);
		if(edit){
			checkBookmarks();
		}
	});
	$.post(url2,function(e){
		$("#collectionSearchFields").html(e);
		if(edit){
			checkSearches();
		}
	});
}

function getEditForm(url1,url2,url3,url4){
	$.post(url1,function(e){
		$("#newBookmarksDiv").html(e);
		checkNewBookmarks();
	});
	$.post(url2,function(e){
		$("#newCollectionSearches").html(e);
		checkNewSearches();
	});
	getViewForm(url3,url4,true);
}

function updateInputVar(inputVar){
	$("input[id^='"+inputVar+"']").each(function(){
		var id = $(this).attr("id").substring(inputVar.length);
		var targetName = "hidden_"+inputVar+id;
		if($("#"+targetName).length>0){
			$("#"+targetName).val((($(this).is(":checked"))?"on":"off"));
		}else{
			var inputHidden = "<input type=\"hidden\" id=\""+targetName+"\" name=\""+targetName+"\" value=\""+(($(this).is(":checked"))?"on":"off")+"\" />";
			$("form#frmCollectionContent").append(inputHidden);
		}
	});
}

function checkNewBookmarks(){
	updateInputVar("new_bookmark_");
}

function checkNewSearches(){
	updateInputVar("new_search_");
}

function checkBookmarks(){
	updateInputVar("collection_bookmark_");
}

function checkSearches(){
	updateInputVar("selected_search_");
}

function prevSubmit(){
	if($("#collectionTitle").val().length==0){
		alert($("#hiddenEmptyTitleLabel").text());
		return false;
	}
	updateInputVar("new_bookmark_");
	updateInputVar("new_search_");
	updateInputVar("collection_bookmark_");
	updateInputVar("selected_search_");
	return true;
}

function updatePageNumberCollectionSearches(url){
	checkNewSearches();
	$.post(url,function(e){
		$("#newCollectionSearches").html(e);
		updateCheckboxNewSearches();
		checkNewSearches();
	});
}

function updatePageNumberCurrentSearches(url){
	checkSearches();
	$.post(url,function(e){
		$("#collectionSearchFields").html(e);
		updateCheckboxSearches();
		checkSearches();
	});
}

function updatePageNumberCollectionBookmarks(url){
	checkNewBookmarks();
	$.post(url,function(e){
		$("#newBookmarksDiv").html(e);
		updateCheckboxNewBookmarks();
		checkNewBookmarks();
	});
}

function updatePageNumberCurrentBookmarks(url){
	checkBookmarks();
	$.post(url,function(e){
		$("#bookmarksDiv").html(e);
		updateCheckboxBookmarks();
		checkBookmarks();
	});
}

function updateCheckboxNewBookmarks(){
	$("#newBookmarksDiv").find("input").each(function(){
		updateIndividualCheckbox("new_bookmark_",$(this));
	});
}

function updateCheckboxBookmarks(){
	$("#bookmarksDiv").find("input").each(function(){
		updateIndividualCheckbox("collection_bookmark_",$(this));
	});
}

function updateCheckboxNewSearches(){
	$("#newCollectionSearches").find("input").each(function(){
		updateIndividualCheckbox("new_search_",$(this));
	});
}

function updateCheckboxSearches(){
	$("#collectionSearchFields").find("input").each(function(){
		updateIndividualCheckbox("selected_search_",$(this));
	});
}

function updateIndividualCheckbox(targetName,checkbox){
	var id = checkbox.attr("id");
	id = id.substring(targetName.length);
	if($("#hidden_"+targetName+id).length>0){
		var targetVal = $("#hidden_"+targetName+id).val();
		if(targetVal=="on"){
			checkbox.attr("checked",true);
		}else{
			checkbox.removeAttr("checked");
		}
	}
}
	
function getval(orderUrl, cel) {
	var order = "orderAsc";
	var column = $("table#savedCollectionsTable th#"+cel).attr("class");	

	if(column.indexOf("Down")!=-1){
    	order = "orderAsc";
	}else if(column.indexOf("Up")!=-1){
    	order = "orderDesc";
	}else if(column.indexOf("header")!=-1){
    	order = "orderAsc";
	}

	// Checks if URL contains the order parameters.
	orderUrl = checkOrder(order, cel, orderUrl);
	
	$.post(orderUrl, function(e){
		$("#savedCollectionsPortlet").html(e);
		
		$('table#savedCollectionsTable tr#crown th').each (function() {
			$(this).removeClass("header");
			$(this).removeClass("headerSortUp");
			$(this).removeClass("headerSortDown");
			if ($(this).attr("id")!=cel && $(this).attr("id")!="actions"){
				$(this).addClass("header");
			}else{
				var currentOrder = $("input#orderToSet").val();
				if(currentOrder == "orderAsc"){
					$("table#savedCollectionsTable th#"+cel).addClass("headerSortUp");
				}else if(currentOrder == "orderDesc"){
					$("table#savedCollectionsTable th#"+cel).addClass("headerSortDown");
				} else {
					$("table#savedCollectionsTable th#"+cel).addClass("header");
				}
			}
		});
	});
}

/**
 * Function to check if the passed URL contains the information about the
 * order, and fill it correctly when needed.
 *
 * @param order Current type of order to be applied.
 * @param cel Current column to apply the order.
 * @param orderUrl Current URL to check.
 *
 * @returns Correct URL with the parameters correctly filled.
 */
function checkOrder(order, cel, orderUrl) {
	var targetUrl = orderUrl;
	var paramOrderType = "&_collections_WAR_Portal_orderType=";
	var paramOrderColum = "&_collections_WAR_Portal_orderColumn=";

	// Check parameters passed.
	if (order == null || order.trim().length == 0 || order == undefined) {
		order = "none";
	}
	if (cel == null ||cel.trim().length == 0 || cel == undefined) {
		cel = "none";
	}

	// Checks and builds the URL with the correct "orderType" parameter.
	targetUrl = fillOrderParams(targetUrl, paramOrderType, order);

	// Checks and builds the URL with the correct "orderColumn" parameter.
	targetUrl = fillOrderParams(targetUrl, paramOrderColum, cel);

	return targetUrl;
}

/**
 * Function to fill in the passed URL the order parameters.
 *
 * @param orderUrl URL to fill with the order parameters.
 * @param param Current parameter of order to be filled.
 * @param value Current value of the parameter to be filled.
 *
 * @returns URL with the parameter filled.
 */
function fillOrderParams(orderUrl, param, value) {
	var finalUrl = orderUrl;

	if (orderUrl.indexOf(param) != -1) {
		var firstPartURL = orderUrl.substr(0, orderUrl.indexOf(param));
		var middlePartURL = orderUrl.substr((orderUrl.indexOf(param) + param.length), orderUrl.length);
		var endPartURL="";

		if (middlePartURL.indexOf("&") != -1) {
			endPartURL = middlePartURL.substr(middlePartURL.indexOf("&"), middlePartURL.length);
		}

		finalUrl = firstPartURL + param + value + endPartURL;
	} else {
		finalUrl += param + value;
	}

	return finalUrl;
}

/**
 * Function to fill the current order and column in the pagination URLs.
 *
 * @param orderType Current type of order to be applied.
 * @param orderColumn Current column to apply the order.
 */
function keepOrderPagination(orderType, orderColumn) {
	var urlorder="&_collections_WAR_Portal_orderType="+ orderType;
	var urlcolum="&_collections_WAR_Portal_orderColumn="+ orderColumn;

	// Loop pagination to get the list of the URLs.
	$("div#child-paging").find("li").each(function() {
		if($(this).find("a").length > 0) {
			// Update current URL with the current order parameters.
			var targetUrl = $(this).find("a[href]").attr("href");
			var finalUrl = targetUrl+urlorder+urlcolum;

			$(this).find("a[href]").attr("href", finalUrl);
		}
	});
}
