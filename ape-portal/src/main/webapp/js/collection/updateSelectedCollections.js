/***
 * This method is called from addSavedBookmarksForm.jsp and addSavedSearchesForm.jsp in My Pages space<br/>
 * when the user clicks over a checkbox to select a collection to add
 * 
 * @param id "collectionToAdd_" + collection id
 * @param list the list of the selected collections when the user changes page
 * @param page "search" or "bookmarks" indicates from which page comes
 */
function updateSelectedCollections (id, list, page){
	// Empty the div which contains the error message.
	$("div#noCollectionsSelected").empty();

	//get the list of selected collections
	var storeCollectionIds = $('input#storeCollectionIdsInput').val();
	if(storeCollectionIds==undefined || storeCollectionIds.length==0){
		storeCollectionIds=list;
	}
	//get the current selected colection
	var currentId = id.substring(id.lastIndexOf("_")+1, id.length);
	if ($("#" + id).is(':checked')){
		if (storeCollectionIds.length > 0){
			storeCollectionIds += ","+currentId;
		}else{
			storeCollectionIds=currentId;
		}
	}else{
		var listChecked = storeCollectionIds.split(",");
		var index = listChecked.indexOf(currentId);
		if(index != -1) {
			listChecked.splice(index, 1);
		}
		storeCollectionIds=listChecked.join();
	}
	//set the list of the selected collections with the new selected one 
	$('input#storeCollectionIdsInput').val(storeCollectionIds);
	updateSelectedCollectionsAdd(storeCollectionIds, page);
	updatePaging(storeCollectionIds, page);
}

/***
 * This function is called from updateSelectedCollections<br/>
 * This function updates the action with the list of the selected elements
 * 
 * @param storeCollectionIds the list of the selected collections to add the selected elements
 * @param page "search" or "bookmarks" indicates from which page comes
 */
function updateSelectedCollectionsAdd(storeCollectionIds, page){
	//complete url when user clicks save
	var targetUrl= $('form#frm').attr("action");
	//update url wall selected collections
	$('form#frm').attr('action',completeUrl (targetUrl, storeCollectionIds, page));
}

/***
 * This function is called from updateSelectedCollections<br/>
 * This function updates the list to avoid loose elements between pagination
 * 
 * @param storeCollectionIds the list of the selected collections to add the selected elements
 * @param page "search" or "bookmarks" indicates from which page comes
 */
function updatePaging(storeCollectionIds, page){
	//loop pagination to get the list of selected collections
	$("div#child-paging").find("li").each(function(){
		if($(this).find("a").length>0){
			var targetUrl= $(this).find("a[href]").attr("href");
			//update url with the selected collections
			$(this).find("a[href]").attr("href",completeUrl (targetUrl, storeCollectionIds, page));
		}	
	});
}

/***
 * This method is called from updateSelectedCollections and updateSelectedCollectionsAdd<br/>
 * This methos writes the the updated elements list
 * 
 * @param targetUrl complete url with old elements list
 * @param storeCollectionIds the list of the selected collections to update the selected elements
 * @param page "search" or "bookmarks" indicates from which page comes
 * 
 * @returns complete url with updated list of selected elements
 */
function completeUrl (targetUrl, storeCollectionIds, page){
	//get the collection id from the name of the collection 
	var paramAdd="";
	if (page=="bookmarks"){
		paramAdd="&_savedbookmarks_WAR_Portal_listChecked=";
	}else{
		paramAdd="&_savedsearch_WAR_Portal_listChecked=";
	}	
	//update the url whith the list of all selected collections
	if (targetUrl.indexOf(paramAdd)!=-1){
		var ini = targetUrl.substr(0, targetUrl.indexOf(paramAdd));
		var middle = targetUrl.substr((targetUrl.indexOf(paramAdd) + paramAdd.length), targetUrl.length);
		var end="";
		if (middle.indexOf("&")!=-1){
			end=middle.substr(middle.indexOf("&"), middle.length);
		}
		targetUrl=ini+paramAdd+storeCollectionIds+end;
	}else{
		targetUrl+=paramAdd+storeCollectionIds;		
	}
	return targetUrl;
}

/***
 * This method is called from addSavedBookmarksForm.jsp and addSavedSearchesForm.jsp in My Pages space<br/>
 * This method checks the related checkboxes in the list with the selected elements
 * 
 * @param list the list of the selected collections to check
 */
function showCheckedValuesInCollections(list){
	$("input[id^='collectionToAdd_']").each(function(){
		//check if the current checkboxes are in the list of selected collections
		var currentElement= $(this).attr("id");
		var currentId = currentElement.substring(currentElement.lastIndexOf("_")+1, currentElement.length);
		var listChecked = list.split(",");
		var index = listChecked.indexOf(currentId);
		//if element is there, select it 
		if(index != -1) {
			$(this).attr("checked",true);
		}
	});
}