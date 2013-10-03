function initContactForm() {
    $('#contact_label_cancel').click(function(){
        window.location.href = "/home";
    });
}

function getFile(){
	//this gets the full url
	var url = document.location.href;

	//if url contains "?" this means this comes from "User feedback"
	if(parseInt(url.indexOf("?"))>1){
		url = url.substring(url.indexOf("?")+1, (url.length));
		//print the title of the document in the feedback text area
		document.getElementById('contact_feedbackText').value = unescape(url);
		//->contact_feedbackText.innerHTML = decodeURIComponent(url);
		//select the option in the dropdown and set Users feedback
		var select = document.getElementById("contact_topicSubject");
        select.options[4].selected = true;
        //hide headers for users feedback
        var headerMenu = document.getElementById("headerMenu");
        var headerBanner = document.getElementById("banner");
        var footerBanner = document.getElementById("footer");
        headerMenu.style.display = 'none';
        headerMenu.style.position = 'fixed';
        headerBanner.style.display = 'none';
        headerBanner.style.position = 'fixed';        
        footerBanner.style.display = 'none';
        footerBanner.style.position = 'fixed';        
        return true;
	}	
	else
		return false;
}