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
		$.post(feedbackUrl, {aiId: aiId, title: documentTitle, url: documentUrl}, function(data) {
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