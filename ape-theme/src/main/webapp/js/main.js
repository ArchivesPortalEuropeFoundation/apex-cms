AUI().ready(

	/*
	This function gets loaded when all the HTML, not including the portlets, is
	loaded.
	*/

		 'liferay-navigation-interaction',
		function(A) {
			var navigation = A.one('#navigation');

			if (navigation) {
				navigation.plug(Liferay.NavigationInteraction);
			}

		}
);

Liferay.Portlet.ready(

	/*
	This function gets loaded after each and every portlet on the page.

	portletId: the current portlet's id
	node: the Alloy Node object of the current portlet
	*/

	function(portletId, node) {
	}
);

Liferay.on(
	'allPortletsReady',
	/*
	This function gets loaded when everything, including the portlets, is on
	the page.
	*/

	function() {
	}
);