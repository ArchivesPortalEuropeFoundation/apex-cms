var globalEmbeddedMapsUrl, globalMapsUrl, selectedCountryCode, selectedAiname;
function initDirectory(directoryTreeUrl, directoryTreeAIUrl, aiDetailsUrl,embeddedMapsUrl, mapsUrl) {
	globalEmbeddedMapsUrl = embeddedMapsUrl;
	globalMapsUrl = mapsUrl;
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
				if (node.data.countryCode) {
					selectedCountryCode = node.data.countryCode;
				}else {
					selectedCountryCode = null;
				}
				if( node.data.aiId ) {
					selectedAiname = node.data.title;
					$("#directory-column-right-content").empty();
					$("#directory-column-right-content").append("<div id='waitingImage'></div>");
					$("#directory-column-right-content").load(aiDetailsUrl +"&id=" + node.data.aiId, function() {
						initEagDetails();
					});

				}else if (node.data.googleMapsAddress){
					selectedAiname = null;
					displayMaps(node.data.googleMapsAddress);
				}else {
					selectedAiname = null;
				}
		},

		// Generate id attributes like <span id='dynatree-id-KEY'>
		generateIds: true,
	});

}
function displayMaps(googleMapsAddress, archivalInstitutionName){
	// geocoder
	var geocoder = new google.maps.Geocoder();
	var input_address = googleMapsAddress;
	geocoder.geocode( { address: input_address, region: selectedCountryCode }, function(results, status) {
		if (status == google.maps.GeocoderStatus.OK) {
			var lat = results[0].geometry.location.lat();
			var lng = results[0].geometry.location.lng();
			var span = results[0].geometry.viewport.toSpan();
			var spanLat = span.lat();
			var spanLng = span.lng();
			var parameters = "&ll=" + lat +"," + lng;
			parameters = parameters + "&spn=" + spanLat +"," + spanLng;
			if (archivalInstitutionName){
				parameters = parameters + "&q=" + encodeURIComponent(archivalInstitutionName) +",+" + selectedCountryCode;
			}
			$("#maps").attr("src", globalEmbeddedMapsUrl+ parameters);
			$("#externalMap").attr("href", globalMapsUrl+ parameters);
		} 
		else {
			alert("Google Maps not found address!");
			}
		});
}

function displayRepository(id){
	closeAllRepositories();
	showRepository("#repository_" + id + " ");
    $('html, body').animate({
        scrollTop: $("#repository_" + id + " .repositoryName").offset().top
         }, 2000);
}
function seeLess(clazz,identifier){
	if (identifier){
		prefix = "#repository_" + identifier + " ." +clazz + " ";
	} else {
		prefix = "." +clazz + " ";
	}
	$(prefix + ".displayLinkSeeMore").removeClass("hidden");
	$(prefix + ".displayLinkSeeLess").addClass("hidden");
	$(prefix + ".longDisplay").hide();
}
function seeMore(clazz,identifier){
	if (identifier){
		prefix = "#repository_" + identifier + " ." +clazz + " ";
	} else {
		prefix = "." +clazz + " ";
	}
	$(prefix + ".displayLinkSeeLess").removeClass("hidden");
	$(prefix + ".displayLinkSeeMore").addClass("hidden");
	$(prefix + ".longDisplay").show();
}
function initEagDetails(){
	$(".displayLinkSeeLess").addClass("hidden");
	$(".longDisplay").hide();
	showRepositoryOnMap("#repository_1");
	closeAllRepositories();
	$('h3.repositoryName').click(function() {
		if ($(this).hasClass("expanded")) {
			$(this).removeClass("expanded").addClass("collapsed");
			$(this).next().hide();
		} else {
			closeAllRepositories();
			showRepository("#" + $(this).parent().attr("id"));

		}
	});
}
function showRepository(identifier){
	$(identifier + " .repositoryName").removeClass("collapsed").addClass("expanded");
	$(identifier + " .repositoryInfo").show();
	showRepositoryOnMap(identifier);
}
function showRepositoryOnMap(prefix){
	if ($(prefix + " .repositoryName").length > 0){
		repoName = $(prefix + " .repositoryName").html();		
	}else {
		repoName = selectedAiname;
	}
	var address =  $(prefix + " .address").html();
    if($(prefix + " .address").length == 0) {
    	address = $(prefix + ".postalAddress").html();
    } 
	displayMaps(address, repoName);
}
function closeAllRepositories(){
	if ($(".repositoryName").length > 0){
		$(".repositoryName").removeClass("expanded").addClass("collapsed");
		$(".repositoryInfo").hide();
		$(".repositoryInfo .longDisplay").hide();
	}
}

function printEagDetails(hi){
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

function recoverRelatedInstitution(relatedAIId) {
	$("#dynatree-id-aieag_" + relatedAIId).trigger('click');
}
