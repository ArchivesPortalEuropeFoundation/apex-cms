$(document).ready(function() {
	loadGoogleAnalytics();
});

function loadGoogleAnalytics() {
	var hostname = window.location.hostname;
	  (function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
	  (i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
	  m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
	  })(window,document,'script','//www.google-analytics.com/analytics.js','ga');

	if (hostname == "www.archivesportaleurope.net" || hostname == "archivesportaleurope.net"){
		ga('create', 'UA-37520081-1', 'archivesportaleurope.net');
	}else if (hostname == "contentchecker.archivesportaleurope.net"){
		ga('create', 'UA-35748576-1', 'contentchecker.archivesportaleurope.net');
	}else {
		ga('create', 'UA-42421913-1', 'development.archivesportaleurope.net');
	}
	ga('send', 'pageview', window.location.pathname);
}

function logAction(title, url){
	var shortUrl = url.substring(url.indexOf('//')  +2);
	shortUrl = shortUrl.substring(shortUrl.indexOf('/'));
	ga('send', 'pageview', shortUrl);
}
