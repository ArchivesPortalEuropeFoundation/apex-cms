function initContactForm() {
    $('#contact_label_cancel').click(function(){
        window.location.href = "/home";
    });
}

function getFile(){
	//this gets the full url
	var url = document.location.href;

	if(parseInt(url.indexOf("?"))>1){
		//if url contains "?" this means this comes from "User feedback"
		url = url.substring(url.indexOf("?")+1, (url.length));
		document.getElementById('contact_feedbackText').value = unescape(url);
		//print the title of the document in the feedback text area
		//->contact_feedbackText.innerHTML = decodeURIComponent(url);
		//select the option in the dropdown and set Users feedback
		var select = document.getElementById("contact_topicSubject");
        select.options[4].selected = true;
	}	
}