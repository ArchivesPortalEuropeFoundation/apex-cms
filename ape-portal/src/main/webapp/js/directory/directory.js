function initDirectory(directoryTreeUrl, directoryTreeAIUrl, aiDetailsUrl,embeddedMapsUrl, mapsUrl) {
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
						var address =  $("#address").html();
                        if(address) {
                        	address = $("#postalAddress").html();
                        } 
						var archivalInstitutionName =$("#archivalInstitutionName").html();
						displayMaps(embeddedMapsUrl, mapsUrl, node.data.countryCode,address, archivalInstitutionName);
					});
					
				}else if (node.data.googleMapsAddress){
					if (node.data.countryCode){
						displayMaps(embeddedMapsUrl, mapsUrl, node.data.countryCode, node.data.googleMapsAddress);
					}else {
						displayMaps(embeddedMapsUrl, mapsUrl, null, node.data.googleMapsAddress);
					}
				}
		}
							
	});

}
function displayMaps(embeddedMapsUrl, mapsUrl, countryCode, googleMapsAddress, archivalInstitutionName){
	// geocoder
	var geocoder = new google.maps.Geocoder();
	var input_address = googleMapsAddress;
	geocoder.geocode( { address: input_address, region: countryCode }, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var lat = results[0].geometry.location.lat();
			var lng = results[0].geometry.location.lng();
			var span = results[0].geometry.viewport.toSpan();
			var spanLat = span.lat();
			var spanLng = span.lng();
			var parameters = "&ll=" + lat +"," + lng;
			parameters = parameters + "&spn=" + spanLat +"," + spanLng;
			if (archivalInstitutionName){
				parameters = parameters + "&q=" + encodeURIComponent(archivalInstitutionName) +",+" + countryCode;
			}
			$("#maps").attr("src", embeddedMapsUrl+ parameters);
			$("#externalMap").attr("href", mapsUrl+ parameters);
		} 
		else {
			alert("Google Maps not found address!");
			}
		});
}
function display(){
	$(".longDisplay").each(function(){
		$(this).toggle();
	});
}
