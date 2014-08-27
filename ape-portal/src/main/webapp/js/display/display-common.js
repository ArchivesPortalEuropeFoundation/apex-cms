/*
 * Common functions to display the user's feedback.
 */

function showFeedback(feedbackUrl, documentTitle, documentUrl, publicKey) {
	if ($('#feedbackContent').is(':empty')){
		$.post(feedbackUrl, {title: documentTitle, url: documentUrl}, function(data) {
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

function showBookmark(bookmarkUrl, documentTitle, documentUrl) {

	if (logged){
		if ($('#bookmarkContent').is(':empty')){
			$.post(bookmarkUrl, {title: documentTitle, url: documentUrl}, function(data) {
				$("#bookmarkContent").html(data);
		        $("#bookmarkContent").removeClass("bookmarkContent").addClass("bookmarkContent");
		    	$("#bookmark_send").click(function(event) {
		    		event.preventDefault();
		    		click(bookmarkUrl);
		    	});
		    	if (data.answerCode == undefined) {
					$('#bookmarkContent').removeClass("success").removeClass("failure");
					$('#bookmarkContent').html(dataResponse.answerMessage);		
					$('#bookmarkContent').addClass("failure");
					$('#bookmarkContent').show();
				}
		    	
			});
		}
	}
	else{
		$('#answerMessageSavedSearch').html("Error, you may be logged in to bookmark a content");
		$('#answerMessageSavedSearch').removeClass("success").removeClass("failure");
		$('#answerMessageSavedSearch').addClass("failure");
		$('#answerMessageSavedSearch').show();
		$('#answerMessageSavedSearch').delay(3000).fadeOut('slow');
	}

	if ($("#bookmarkContent").hasClass("hidden")) {
		$("#bookmarkContent").removeClass("hidden");
	} else {
		$("#bookmarkContent").addClass("hidden");
	}
}

function click(bookmarkUrl) {
	$.post(bookmarkUrl, $("#bookmarkContent").serialize(), function(dataResponse) {
		if (dataResponse.answerCode != "") {
			$('#bookmarkContent').removeClass("success").removeClass("failure");
			$('#bookmarkContent').html(dataResponse.answerMessage);		
			if (dataResponse.answerCode == "true") {
				$('#bookmarkContent').addClass("success");
			} else {
				$('#bookmarkContent').addClass("failure");
			}
			$('#bookmarkContent').show();
		}
		if (dataResponse.answerCode == undefined) {
			$('#bookmarkContent').removeClass("success").removeClass("failure");
			$('#bookmarkContent').html(dataResponse.answerMessage);		
			$('#bookmarkContent').addClass("failure");
			$('#bookmarkContent').show();
		}
		$('#bookmarkContent').delay(3000).fadeOut('slow');

	});
}

function showError() {
	$('#answerMessageSavedSearch').html("Error, you may be logged in to bookmark a content");
	$('#answerMessageSavedSearch').removeClass("success").removeClass("failure");
	$('#answerMessageSavedSearch').addClass("failure");
	$('#answerMessageSavedSearch').show();
	$('#answerMessageSavedSearch').delay(3000).fadeOut('slow');
}