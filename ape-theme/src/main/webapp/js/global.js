
function logAction(title, url) {
	var baseUrl = window.location.protocol + '//' + window.location.hostname;
	var shortUrl = url.replace(baseUrl, "");
	_gaq.push([ '_trackPageview', shortUrl ]);
}
