$( document ).ready(function() {
	checkCookie("survey2014");
});
function logAction(title, url) {
	var baseUrl = window.location.protocol + '//' + window.location.hostname;
	var shortUrl = url.replace(baseUrl, "");
	if (title != undefined && title.length > 0){
		_gaq.push(["_set", "title", title]);
	}
	_gaq.push([ '_trackPageview', shortUrl ]);
}
function loadSurvey(c_name) {
	$('#survey').removeClass("hidden");
	$("#survey #start").click(function(event) {
		event.preventDefault();
		setCookie(c_name,'remindlater',365);
		window.open($("#survey-url").attr("href"));
	});
	$("#survey #remove").click(function(event) {
		event.preventDefault();
		setCookie(c_name,'remindlater',365);
		$('#survey').addClass("hidden");
	});

}

function setCookie(c_name, value, expiredays) {
	var exdate = new Date();
	exdate.setDate(exdate.getDate() + expiredays);
	document.cookie = c_name + "=" + escape(value) + ((expiredays == null) ? "" : ";expires=" + exdate.toGMTString());
}

function getCookie(c_name) {
	if (document.cookie.length > 0) {
		c_start = document.cookie.indexOf(c_name + "=");
		if (c_start != -1) {
			c_start = c_start + c_name.length + 1;
			c_end = document.cookie.indexOf(";", c_start);
			if (c_end == -1)
				c_end = document.cookie.length;
			return unescape(document.cookie.substring(c_start, c_end));
		}
	}
	return "";
}

function checkCookie(c_name) {
	cookie_value = getCookie(c_name);
	if (cookie_value == "") {
		loadSurvey(c_name);
	}else {
		$('#survey').addClass("hidden");
	}
}

function deleteCookie(c_name) {
	document.cookie = c_name + '=; expires=Thu, 01-Jan-70 00:00:01 GMT;';
}