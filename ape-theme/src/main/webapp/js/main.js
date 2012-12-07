AUI().ready(

/*
 * This function gets loaded when all the HTML, not including the portlets, is
 * loaded.
 */

'liferay-navigation-interaction', function(A) {
	var navigation = A.one('#navigation');

	if (navigation) {
		navigation.plug(Liferay.NavigationInteraction);
	}

});

Liferay.Portlet.ready(

/*
 * This function gets loaded after each and every portlet on the page.
 * 
 * portletId: the current portlet's id node: the Alloy Node object of the
 * current portlet
 */

function(portletId, node) {
});

Liferay.on('allPortletsReady',
/*
 * This function gets loaded when everything, including the portlets, is on the
 * page.
 */

function() {
	loadSurvey();
	checkCookie("survey");
});

function loadSurvey() {
	$('#survey').removeClass("hidden");
	$('#survey').dialog({
		autoOpen : false,
		modal : true,
		width : 500,
		resizable : false,
		buttons : [ {
			text :$("#survey-url").html(),
			click : function() {
				setCookie('survey','remindlater',365);
				$(this).dialog("close");
				window.open($("#survey-url").attr("href"));
				
			}
		}, {
			text : "No thanks",
			click : function() {
				setCookie('survey','remindlater',365);
				$(this).dialog("close");
			}
		}, {
			text : "Remind me later",
			click : function() {
				setCookie('survey','remindlater',1);
				$(this).dialog("close");
			}
		} ]

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
		$('#survey').dialog('open');
	}
}

function deleteCookie(c_name) {
	document.cookie = c_name + '=; expires=Thu, 01-Jan-70 00:00:01 GMT;';
}