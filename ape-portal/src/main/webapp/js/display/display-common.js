/***
 * This function adds the Share button
 */
function initShareButtons(){
    addJsFileToHead("share","http://wd-edge.sharethis.com/button/buttons.js", "https://wd-edge.sharethis.com/button/buttons.js");
    var switchTo5x=true;
    stLight.options({publisher: "e059943f-766d-434b-84ea-1e0d4a91b7d4", doNotHash: true, doNotCopy: true, hashAddressBar: true, shorten:false, onhover : true, tracking : 'google'});
	stButtons.locateElements();	
}

/*
 * Common functions to display the user's feedback.
 */

/***
 * This function shows the contact form with the recaptcha validation<br/>
 * This method is called from eac-cpf/display/index and from ead/eaddetails
 * 
 * @param feedbackUrl email address
 * @param aiId archival institution id
 * @param documentTitle the title of the document
 * @param documentUrl Url of the related resouce
 * @param publicKey recaptche public key
 * 
 */
function showFeedback(feedbackUrl, aiId, documentTitle, documentUrl, publicKey) {
	if ($('#feedbackContent').is(':empty')){
		addJsFileToHead("recaptha", "http://www.google.com/recaptcha/api.js", "https://www.google.com/recaptcha/api.js");
		$(document).ready(function() {
		$.post(feedbackUrl, {aiId: aiId, 
			title: documentTitle, 
			url: documentUrl}, function(data) {
			$("#feedbackContent").html(data);
			
	        Recaptcha.create(publicKey, "recaptchaDiv", {
	            theme: "white",
	            callback: Recaptcha.focus_response_field});
		        $("#feedbackContent").removeClass("feedbackContent").addClass("feedbackContent");
		    	$("#contactFeedbackSend").click(function(event) {
		    		event.preventDefault();
		    		$("#contactFeedbackSend").attr('disabled', 'disabled');
		    		sendFeedback();
		    	});
			});
		});
	}
	logAction("SHOW FEEDBACK FORM", feedbackUrl);
	if ($("#feedbackContent").hasClass("hidden")) {
		$("#feedbackContent").removeClass("hidden");
	} else {
		$("#feedbackContent").addClass("hidden");
	}

}

/***
 * This function gets recaptcha key, enables/disables button and sends the mail with the feedback
 */
function sendFeedback(){
	var url = $("#contactForm").attr("action");
	var publicKey = $("#contactForm #recaptchaPubKey").attr("value");
	$.post(url, $("#contactForm").serialize(), function(data) {
		$("#feedbackContent").html(data);
        Recaptcha.create(publicKey, "recaptchaDiv", {
            theme: "white",
            callback: Recaptcha.focus_response_field});
        $("#feedbackContent").removeClass("feedbackContent").addClass("feedbackContent");
    	$("#contactFeedbackSend").click(function(event) {
    		event.preventDefault();
    		$("#contactFeedbackSend").attr('disabled', 'disabled');
    		sendFeedback();
    	});		
	});
	var aiName = $("#contactForm #aiName").html();	
	var aiRepoCode = $("#contactForm #aiRepoCode").attr("value");
	logAction("SEND FEEDBACK FORM TO: " + aiName + " (" + aiRepoCode + ")", url);
}

//Bookmarking feature
//_____________________


/***
 * This function is called from the second display<br/>
 * and shows the pop up with the last 20 available collections for thr current element
 * 
 * @param bookmarkUrl complete url with params to send to teh modelandview
 * @param documentTitle title of the documment
 * @param documentUrl complete resource url
 * @param typedocument "ead" or "eac-cpf" sets type of the resource
 */
function showBookmark(bookmarkUrl, documentTitle, documentUrl, typedocument) {	
	$.ajaxSetup({async: false});
	$.post(bookmarkUrl,
		{bookmarkName: documentTitle,
		persistentLink: documentUrl,
		typedocument: typedocument},
		function(data) {
			$("#bookmarkContent").html(data);
			$.ajaxSetup({async: true});
		}
	);
}

/***
 * This function is called whe the action to add a bookmark gives an error.<br/>
 * This function writes an error in the screen
 * 
 * @param message the error message
 */
function showError(message) {
	$('#bookmarkContent').html(message);
	$('#bookmarkContent').removeClass("success").removeClass("failure").removeClass("hidden");
	$('#bookmarkContent').addClass("failure");
	$('#bookmarkContent').show();
	$('#bookmarkContent').delay(3000).fadeOut('slow');
}

/***
 * This function prints a message in the screen
 * 
 * @param hasSaved True or false depending if there has been errors while saving a bookmark
 * @param message The message to print
 */
function hasSaved(hasSaved, message){
	$('#bookmarkContent').html(message);
	$('#bookmarkContent').removeClass("success").removeClass("failure").removeClass("hidden");	

	if (hasSaved)
		$('#bookmarkContent').addClass("success");
	else
		$('#bookmarkContent').addClass("failure");
	
	$('#bookmarkContent').show();
	$('#bookmarkContent').delay(3000).fadeOut('slow');
}

/***
 * This function is called from the button Add Bookmark, disables the button Add Bookmark and raises teh pop up
 * 
 * @param bookmark bookmark id
 * @param seeAvaiableCollectionsUrl complete url with parameters for the Request
 */
function showCollections(bookmark, seeAvaiableCollectionsUrl){
	$('#searchCollectionsButton').addClass("hidden");
	$('#searchButtonGrey').removeClass("hidden");
	var searchTerm = $('input#searchTerm').val();
	if (searchTerm == undefined) {
		searchTerm = "";
	} else {
		$('input#searchTerm').val("");
	}
	getShowCollections(bookmark, seeAvaiableCollectionsUrl, searchTerm);
}

/***
 * This function raises the pop up window with the available collections
 *  
 * @param bookmark the bookmark id
 * @param seeAvaiableCollectionsUrl the url with the parameters for teh Request
 */
function showAllCollections(bookmark, seeAvaiableCollectionsUrl){
	getShowCollections(bookmark, seeAvaiableCollectionsUrl, "");
}

/***
 * This function call to the java class to get the result of the available collections to show the pop up window
 * 
 * @param bookmark the bookmark id
 * @param seeAvaiableCollectionsUrl complete url with parameters for the Request
 * @param searchTerm search params to mach the name of the collections
 */
function getShowCollections(bookmark, seeAvaiableCollectionsUrl, searchTerm){
	$.ajaxSetup({async: false});
	$.post(seeAvaiableCollectionsUrl, 
		{bookmarkId: bookmark, 
		criteria: searchTerm},
		function(data) {
			$("div[aria-labelledby^='ui-']").each(function(){
				$(this).remove();
			});
			$("div#mycollectionPortletDiv").each(function(){
				$(this).remove();
			});
			$('#collection-details').empty();
			$("#collection-details").html(data);
			$('#collection-details').removeClass("hidden");
			$('#collection-details').show();
			$.ajaxSetup({async: true});
		}
	);
}

/***
 * This function is called from teh Add Bookmark button in the second display<br/>
 * to add the selected resource to the selected collections
 * 
 * @param seeAvaiableCollectionsUrl complete url with parameters to the Request
 * @param errorEmpty gets the error message if there is not selected at least one collectios to add the bookmark
 */
function saveBookmarkInCollections( seeAvaiableCollectionsUrl, errorEmpty){
	if($("[name^='collectionToAdd_']:checked").length>0){
		$("#error_message_collection").remove();
		$.ajaxSetup({async: false});
		$.post(seeAvaiableCollectionsUrl, 
			$("#frm").serialize(),
			function(data) {
				$("div[aria-labelledby^='ui-']").each(function(){
					$(this).remove();
				});
				$('#collection-details').empty();
				$("#collection-details").html(data);
				$('#collection-details').removeClass("hidden");
				enableBookmarkButton();
				$('#collection-details').show();
				$.ajaxSetup({async: true});
			}
		);
	}else{
		if($("#error_message_collection").length==0){
			$("#searchCollectionsButton").parent().parent().parent().parent().next().after("<div id=\"error_message_collection\" class=\"error\">"+errorEmpty+"</div>");
		}
	}
}

/***
 * This function creates a new collection to save the curnt resource from the bookmark pop up in the second display
 * 
 * @param bookmark bookmark id
 * @param CollectionUrl complete url with parameters for the Request
 */
function createCollections(bookmark, CollectionUrl){
	$.ajaxSetup({async: false});
	$.post(CollectionUrl, 
		{bookmarkId: bookmark},
		function(data) {
			$("div[aria-labelledby^='ui-']").each(function(){
				$(this).remove();
			});
			$('#collection-details').empty();
			$("#collection-details").html(data);
			$('#collection-details').removeClass("hidden");
			$('#collection-details').show();
			$.ajaxSetup({async: true});
		}
	);
}

/***
 * Saves a collection and sets teh bookmark in it
 * 
 * @param bookmark bookmark ID
 * @param CollectionUrl collection ID
 * 
 * @returns the page with the bookmark saved into the new collection
 */
function saveCollections(bookmark, CollectionUrl){
	var title = $('input#collectionTitle').val();
	var description = $('textarea#collectionDescription').val();
	var isPublic = $('input#collectionField_public').is(":checked");
	var isEdit = $('input#collectionField_edit').is(":checked");
	
	if($.trim(title)!=""){
		$.post(CollectionUrl, 
			{bookmarkId: bookmark, 
			title: title, 
			description: description, 
			isPublic: isPublic, 
			isEdit: isEdit},
			function(data) {
				$("div[aria-labelledby^='ui-']").each(function(){
					$(this).remove();
				});
				$('#collectionCreateAction').empty();
				$("#collectionCreateAction").html(data);
				$('#collectionCreateAction').removeClass("hidden");
				enableBookmarkButton();
				$('#collectionCreateAction').show();
			}
		);
	}else{
		$('#errorName').removeClass("success").removeClass("failure").removeClass("hidden");
		$('#errorName').addClass("error");
		$('#errorName').show();
	}
}

//popup in the second display
//___________________________

/***
 * Function to load the popup to create a new collection
 * 
 */
function loadDialogSave(){
	disableBookmarkButton();
	var dialog = $('#mycollectionPortletDiv');    
    dialog.dialog({
    	resizable: false,
    	height: "auto",
    	beforeClose: function() {$('#mycollectionPortletDiv').addClass('hidden');
    							enableBookmarkButton(); },
    	close: function() {$(this).remove(); },
    	open: function() { $('#mycollectionPortletDiv').removeClass('hidden');}
    });
}

/***
 * Function that loads the popup with the available collections to bookmark
 * 
 * @param bookmark bookmarkId
 * @param seeAvaiableCollectionsUrl link url
 * 
 * @returns null
 */
function loadDialogShow(bookmark, seeAvaiableCollectionsUrl){

	loadDialogSave();
	
    $("#mycollectionPortletDiv").keypress(function(e) {
        if(e.which == 13) {
        	e.preventDefault();
        	showCollections(bookmark, seeAvaiableCollectionsUrl);
        }
    });	
    
    return null;
}

/***
 * This function enables the bookmark button hidding the disabled button
 */
function enableBookmarkButton(){
	$('#bookmarkEad').removeClass('hidden');
	$('#bookmarkEacCpf').removeClass('hidden');
	$('#bookmarkEadGrey').addClass('hidden');
	$('#bookmarkEacCpfGrey').addClass('hidden');
	$('#mycollectionPortletDiv').addClass('hidden');
}

/***
 * This function disables the bookmark button showing the disabled button
 */
function disableBookmarkButton(){
	$('#bookmarkEad').addClass('hidden');
	$('#bookmarkEacCpf').addClass('hidden');
	$('#bookmarkEadGrey').removeClass('hidden');
	$('#bookmarkEacCpfGrey').removeClass('hidden');
	$('#mycollectionPortletDiv').removeClass('hidden');
}