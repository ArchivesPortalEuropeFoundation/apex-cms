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
						var address =  $("#address").html();
                        if($("#address").length == 0) {
                        	address = $("#postalAddress").html();
                        } 
						displayMaps(embeddedMapsUrl, mapsUrl, node.data.countryCode,address, node.data.title);
						displayrepository('1');
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


function displayrepository(repository){
	$("table[title]").each(function(){
		$(this).hide();
	});
	$("table[title='"+repository+"']").show();
}


function printEAG(){
	jQuery.fn.outerHTML = function(s) {
	    return this[0].outerHTML ? this[0].outerHTML :
		   s ? this.before(s).remove()
		     : jQuery("<p>").append(this.eq(0).clone()).html();
	};
	if($("#printDiv").length>0){
		window.print();
	}else{
		var title = $("h2").html();
		headerNodes = ""; //:D
		var links = document.getElementsByTagName("link");
		for(var i=0;i<links.length;i++){
			if(links[i].parentNode.nodeName.toUpperCase() == "HEAD"){
				headerNodes += "<link"//+links[i].nodeName;
				var attributes = links[i].attributes;
				for(var j=0;j<attributes.length;j++){
					headerNodes += " "+attributes[j].name+"=\""+attributes[j].value+"\"";
				}
				headerNodes += " >\n\t";
			}
		}
		var scripts = document.getElementsByTagName("script");
		for(var i=0;i<scripts.length;i++){
			if(scripts[i].parentNode.nodeName.toUpperCase() == "HEAD"){
				headerNodes += "<script" //+scripts[i].nodeName;
				var attributes = scripts[i].attributes;
				for(var j=0;j<attributes.length;j++){
					headerNodes += " "+attributes[j].name+"=\""+attributes[j].value+"\"";
				}
				headerNodes += " ></script>\n\t";
			}
		}
		var eagDiv = $("#directory-column-right-content").html();
		//var eagMap = $("#maps").html();
		var eagMapSrc = $("#maps").attr("src");
		var printDiv = "<div id=\"printDiv\"><div id=\"directoryPortlet\"><div id=\"directory-column-right-content\">"+eagDiv+"<div style=\"width:100%;height:20px;\">&nbsp;</div><iframe width=\"1000\" scrolling=\"no\" height=\"400\" frameborder=\"0\"  src=\""+eagMapSrc+"\" ></iframe></div></div></div>";
		var printWindow = window.open("","PrintWindow","width=1000,height=400,top=50,left=50,toolbars=no,scrollbars=yes,status=no,resizable=yes");
		var printableHTML = "<html>" +
					"<head>" +
						"<title>"+title+"</title>" + headerNodes +
					"</head>" +
					"<body>" + printDiv+"</body>" +
				"</html>";
		
		printWindow.document.write(printableHTML);
		printWindow.document.close();
		printWindow.focus();
	}
}


