function initDirectory(directoryTreeUrl, directoryTreeAIUrl, aiDetailsUrl, mapsUrl) {
	$("#directoryTree").dynatree({
		//Navigated Search Tree for Countries, Archival Institution Groups and Archival Institutions configuration
		title: "Navigated Search Tree for Archival Landscape - Countries, Archival Insitution Groups and Archival Institutions",
		//rootVisible: false,
		fx: { height: "toggle", duration: 200 },
		selectMode: 1,
						
		//Handleing events
						
		//Tree initialization
		initAjax: {
				url: directoryTreeUrl
			},
						
			//Function to load only the part of the tree that the user wants to expand
			onLazyRead: function(node){
				            	
				node.appendAjax({
					url: directoryTreeAIUrl,
					data: {nodeId: node.data.key, countryCode: node.data.countryCode}
				});
			},
							
		//Function to load the EAG information in the right part of the page using AJAX
			onActivate: function(node) {
				if( node.data.aiId ) {
					$("#directory-column-right-content").empty();
					$("#directory-column-right-content").append("<div id='waitingImage'></div>");
					$("#directory-column-right-content").load(aiDetailsUrl +"&id=" + node.data.aiId, function() {
						displayMaps(mapsUrl, node.data.countryCode,$("#address").html());
					});
					
				}
				if (node.data.googleMapsAddress){
					displayMaps(mapsUrl, node.data.countryCode, node.data.googleMapsAddress);
				}
		}
							
	});

}
function displayMaps(mapsUrl, countryCode, googleMapsAddress){
	// geocoder
	var geocoder = new google.maps.Geocoder();
	var input_address = googleMapsAddress;
	geocoder.geocode( { address: input_address }, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var lat = results[0].geometry.location.lat();
			var lng = results[0].geometry.location.lng();
			var span = results[0].geometry.viewport.toSpan();
			var spanLat = span.lat();
			var spanLng = span.lng();
			mapsUrl = mapsUrl + "&ll=" + lat +"," + lng;
			mapsUrl = mapsUrl + "&spn=" + spanLat +"," + spanLng;
			$("#maps").attr("src", mapsUrl);
		} 
		else {
			alert("Google Maps not found address!");
			}
		});
}
