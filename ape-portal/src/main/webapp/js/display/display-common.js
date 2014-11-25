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
	$.post(bookmarkUrl, 
		{bookmarkName: documentTitle, 
		persistentLink: documentUrl, 
		description: description, 
		typedocument: typedocument}, 
		function(data) {
			$("#bookmarkContent").html(data);
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
	$.post(seeAvaiableCollectionsUrl, 
		{bookmarkId: bookmark, 
		criteria: searchTerm},
		function(data) {
			$("div[aria-labelledby^='ui-']").each(function(){
				$(this).remove();
			});
			$('#collection-details').empty();
			$("#collection-details").html(data);
			$('#collection-details').removeClass("hidden");
			$('#collection-details').show();
		}
	);
}

function showAllCollections(bookmark, seeAvaiableCollectionsUrl){
	var searchTerm = "";
	$.post(seeAvaiableCollectionsUrl, 
		{bookmarkId: bookmark, 
		criteria: searchTerm},
		function(data) {
			$("div[aria-labelledby^='ui-']").each(function(){
				$(this).remove();
			});
			$('#collection-details').empty();
			$("#collection-details").html(data);
			$('#collection-details').removeClass("hidden");
			$('#collection-details').show();
		}
	);
}

function saveBookmarkInCollections( seeAvaiableCollectionsUrl){
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
		}
	);
}

function createCollections(bookmark, CollectionUrl){
	$.post(CollectionUrl, 
		{bookmarkId: bookmark},
		function(data) {
			$("div[aria-labelledby^='ui-']").each(function(){
				$(this).remove();
			});
			$('#collectionCreateAction').empty();
			$("#collectionCreateAction").html(data);
			$('#collectionCreateAction').removeClass("hidden");
			$('#collectionCreateAction').show();
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
function loadDialogSave(bookmarkId,CollectionUrl){
	$( "#mycollectionPortletDiv" ).dialog({
		resizable: false, 
		height: "auto", 
		beforeClose: function() {
			enableBookmarkButton();
		}
	 });
	return null;
}

/***
 * Function that loads the popup with the available collections to bookmark
 * @param bookmark bookmarkId
 * @param seeAvaiableCollectionsUrl link url
 * @returns null
 */
function loadDialogShow(bookmark, seeAvaiableCollectionsUrl){
	$( "#mycollectionPortletDiv" ).dialog({
		resizable: false,
		height: "auto",
		beforeClose: function() {
			enableBookmarkButton();
		},
        open: function() {
        	disableBookmarkButton();
        }
	 });

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
}

function disableBookmarkButton(){
	$('#bookmarkEad').addClass('hidden');
	$('#bookmarkEacCpf').addClass('hidden');
	$('#bookmarkEadGrey').removeClass('hidden');
	$('#bookmarkEacCpfGrey').removeClass('hidden');
}