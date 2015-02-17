/***
 * This function is called from getEditForm(url1,url2,url3,url4)<br/>
 * This functions shows collection content to wiew
 *  
 * @param url1 complete url to load getBookmarks
 * @param url2 complete url to load getSearches
 * @param edit true if the page comes from edit collection, false if not
 */
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

/***
 * This function is called from collection/collection.jsp<br/>
 * This function loads the collection content to edit
 * 
 * @param url1 complete url to load getNewBookmarks 
 * @param url2 complete url to load getNewSearches
 * @param url3 complete url to load getBookmarks
 * @param url4 complete url to load getSearches
 * 
 */
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

/***
 * This function whites a hidden input with the id of the selected element<br/>
 * if the element has been checked in the Collection edition
 * 
 * @param inputVar the id of the element saved bookmark or saved search
 */
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

/***
 * This method sends the string "new_bookmark_" to the updateInputVar(inputVar) function
 */
function checkNewBookmarks(){
	updateInputVar("new_bookmark_");
}

/***
 * This method sends the string "new_search_" to the updateInputVar(inputVar) function
 */
function checkNewSearches(){
	updateInputVar("new_search_");
}

/***
 * This method sends the string "collection_bookmark_" to the updateInputVar(inputVar) function
 */
function checkBookmarks(){
	updateInputVar("collection_bookmark_");
}

/***
 * This method sends the string "selected_search_" to the updateInputVar(inputVar) function
 */
function checkSearches(){
	updateInputVar("selected_search_");
}

/***
 * This method is called from collection/collection.jsp<br/>
 * and from commons/collection.jsp. checks if title is filled before submit<br/>
 * and calls updateInputVar(inputVar) method
 *  
 * @returns true if the title has been filled, false if not
 */
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

/***
 * This function updates the results of the table "Saved searches out of collection" in <br/>
 * the collection in the edit collections page
 * 
 * @param url complete url to show the results
 */
function updatePageNumberCollectionSearches(url){

	checkNewSearches();
	$.post(url,function(e){
		$("#newCollectionSearches").html(e);
		updateCheckboxNewSearches();
		checkNewSearches();
	});
}

/***
 * This function updates the results of the table "Saved searches" in <br/>
 * the collection in the edit collections page
 * 
 * @param url complete url to show the results
 */
function updatePageNumberCurrentSearches(url){
	checkSearches();
	$.post(url,function(e){
		$("#collectionSearchFields").html(e);
		updateCheckboxSearches();
		checkSearches();
	});
}

/***
 * This function updates the results of the table "Saved bookmarks out of collection" in <br/>
 * the collection in the edit collections page
 * 
 * @param url complete url to show the results
 */
function updatePageNumberCollectionBookmarks(url){
	checkNewBookmarks();
	$.post(url,function(e){
		$("#newBookmarksDiv").html(e);
		updateCheckboxNewBookmarks();
		checkNewBookmarks();
	});
}

/***
 * This function updates the results of the table "Saved bookmarks" in <br/>
 * the collection in the edit collections page
 * 
 * @param url complete url to show the results
 */
function updatePageNumberCurrentBookmarks(url){
	checkBookmarks();
	$.post(url,function(e){
		$("#bookmarksDiv").html(e);
		updateCheckboxBookmarks();
		checkBookmarks();
	});
}

/***
 * This function gets the checked bookmarks out of collection when the user changes the page
 */
function updateCheckboxNewBookmarks(){
	$("#newBookmarksDiv").find("input").each(function(){
		updateIndividualCheckbox("new_bookmark_",$(this));
	});
}

/***
 * This function gets the checked bookmarks of collection when the user changes the page
 */
function updateCheckboxBookmarks(){
	$("#bookmarksDiv").find("input").each(function(){
		updateIndividualCheckbox("collection_bookmark_",$(this));
	});
}

/***
 * This function gets the checked searches out of collection when the user changes the page
 */
function updateCheckboxNewSearches(){
	$("#newCollectionSearches").find("input").each(function(){
		updateIndividualCheckbox("new_search_",$(this));
	});
}

/***
 * This function gets the checked searches of collection when the user changes the page
 */
function updateCheckboxSearches(){
	$("#collectionSearchFields").find("input").each(function(){
		updateIndividualCheckbox("selected_search_",$(this));
	});
}

/***
 * This function checks and unchecks the selected elemets in the collections content
 * 
 * @param targetName to set the checkbox name 
 * @param checkbox object to check
 */
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

/***
 * This function orders the results in the Collections table
 * 
 * @param orderUrl complete ur
 * @param cel the column to order
 */
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
 * order, and fill it correctly when needed
 *
 * @param order Current type of order to be applied
 * @param cel Current column to apply the order
 * @param orderUrl Current URL to check
 *
 * @returns Correct URL with the parameters correctly filled
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
 * Function to fill in the passed URL the order parameters
 *
 * @param orderUrl URL to fill with the order parameters
 * @param param Current parameter of order to be filled
 * @param value Current value of the parameter to be filled
 *
 * @returns URL with the parameter filled
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
 * Function to fill the current order and column in the pagination URLs
 *
 * @param orderType Current type of order to be applied
 * @param orderColumn Current column to apply the order
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
