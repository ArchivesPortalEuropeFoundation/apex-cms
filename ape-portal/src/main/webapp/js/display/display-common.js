function initShareButtons(){
    addJsFileToHead("share","http://wd-edge.sharethis.com/button/buttons.js", "https://wd-edge.sharethis.com/button/buttons.js");
    var switchTo5x=true;
    stLight.options({publisher: "e059943f-766d-434b-84ea-1e0d4a91b7d4", doNotHash: true, doNotCopy: true, hashAddressBar: true, shorten:false, onhover : true, tracking : 'google'});
	stButtons.locateElements();	
}

/*
 * Common functions to display the user's feedback.
 */

function showFeedback(feedbackUrl, aiId, documentTitle, documentUrl, publicKey) {
	if ($('#feedbackContent').is(':empty')){
		addJsFileToHead("recaptha", "http://www.google.com/recaptcha/api/js/recaptcha_ajax.js", "https://www.google.com/recaptcha/api/js/recaptcha_ajax.js");
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

function showBookmark(bookmarkUrl, documentTitle, documentUrl, description, typedocument) {	
	$.ajaxSetup({async: false});
	$.post(bookmarkUrl, 
		{bookmarkName: documentTitle, 
		persistentLink: documentUrl, 
		description: description, 
		typedocument: typedocument}, 
		function(data) {
			$("#bookmarkContent").html(data);
			$.ajaxSetup({async: true});
		}
	);
}

function showError(message) {
	$('#bookmarkContent').html(message);
	$('#bookmarkContent').removeClass("success").removeClass("failure").removeClass("hidden");
	$('#bookmarkContent').addClass("failure");
	$('#bookmarkContent').show();
	$('#bookmarkContent').delay(3000).fadeOut('slow');
}

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

function showAllCollections(bookmark, seeAvaiableCollectionsUrl){
	getShowCollections(bookmark, seeAvaiableCollectionsUrl, "");
}

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

function saveBookmarkInCollections( seeAvaiableCollectionsUrl){
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
}

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
 * @param bookmark bookmark ID
 * @param CollectionUrl collection ID
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
 * @returns null
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
 * @param bookmark bookmarkId
 * @param seeAvaiableCollectionsUrl link url
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

function enableBookmarkButton(){
	$('#bookmarkEad').removeClass('hidden');
	$('#bookmarkEacCpf').removeClass('hidden');
	$('#bookmarkEadGrey').addClass('hidden');
	$('#bookmarkEacCpfGrey').addClass('hidden');
	$('#mycollectionPortletDiv').addClass('hidden');
}

function disableBookmarkButton(){
	$('#bookmarkEad').addClass('hidden');
	$('#bookmarkEacCpf').addClass('hidden');
	$('#bookmarkEadGrey').removeClass('hidden');
	$('#bookmarkEacCpfGrey').removeClass('hidden');
	$('#mycollectionPortletDiv').removeClass('hidden');
}