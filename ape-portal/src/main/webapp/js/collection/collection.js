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