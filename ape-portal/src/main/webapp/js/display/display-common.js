/*
 * Common functions to display the user's feedback.
 */

function showFeedback(feedbackUrl, aiId, documentTitle, documentUrl, publicKey) {
	if ($('#feedbackContent').is(':empty')){
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