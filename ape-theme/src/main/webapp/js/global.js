loadGoogleAnalytics();

function loadGoogleAnalytics() {
	var gId = 'UA-42421913-1';
	var hostname = window.location.hostname;
	if (hostname == "www.archivesportaleurope.net"
			|| hostname == "archivesportaleurope.net") {
		gId = 'UA-37520081-1';
	} else if (hostname == "contentchecker.archivesportaleurope.net") {
		gId = 'UA-35748576-1';
	}
	var _gaq = _gaq || [];
	_gaq.push([ '_setAccount', gId ]);
	_gaq.push([ '_trackPageview' ]);

	(function() {
		var ga = document.createElement('script');
		ga.type = 'text/javascript';
		ga.async = true;
		ga.src = ('https:' == document.location.protocol ? 'https://ssl'
				: 'http://www')
				+ '.google-analytics.com/ga.js';
		var s = document.getElementsByTagName('script')[0];
		s.parentNode.insertBefore(ga, s);
	})();
}

function logAction(title, url) {
	var baseUrl = window.location.protocol + '//' + window.location.hostname;
	var shortUrl = url.replace(baseUrl, "");
	_gaq.push([ '_trackPageview', shortUrl ]);
	alert("yes");

}
