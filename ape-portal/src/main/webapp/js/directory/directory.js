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
					var eagDetailsUrl = aiDetailsUrl +"&id=" + node.data.aiId;
					$("#directory-column-right-content").load(eagDetailsUrl, function() {
						initEagDetails(selectedCountryCode,node);
					});
					logAction(document.title, eagDetailsUrl);

				}else if (node.data.googleMapsAddress){
					selectedAiname = null;
					displayMaps(node.data.googleMapsAddress);
				}else {
					selectedAiname = null;
				}
		},

		// Generate id attributes like <span id='dynatree-id-KEY'>
		generateIds: true
	});

}
function printEagByURL(url){
	var preview = window.open(url, 'printeag',
	'width=1100,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
	preview.focus();
}
function displayMaps(googleMapsAddress, archivalInstitutionName, selectedCountryCode,node){
	// geocoder
	var geocoder = new google.maps.Geocoder();
	var input_address = googleMapsAddress;

	// If necessary, recover the first element in visitors address element.
	if (input_address.indexOf("<p>") != '-1') {
		input_address = input_address.substring((input_address.indexOf("<p>") + 3), input_address.indexOf("</p>"));
	}
	geocoder.geocode( { address: input_address, region: selectedCountryCode  }, function(results, status) {
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
		} else if(node){
			var found = false;
			while((node = node.getParent()) && !found){ //try to get the country parent node
				if (node.data.googleMapsAddress){
					found = true;
					displayMaps(node.data.googleMapsAddress);
				}
			}
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
function initEagDetails(selectedCountryCode,node){
	$(".displayLinkSeeLess").addClass("hidden");
	$(".longDisplay").hide();
	showRepositoryOnMap("#repository_1",selectedCountryCode,node);
	closeAllRepositories();
	if(!($(".emaillang").length>0)){
		$(".emailsnolang").removeClass("emailsnolang");
	}
	if(!($(".webpagelang").length>0)){
		$(".webpagesnolang").removeClass("webpagesnolang");
	}
	multiLanguage();
	$('h3.repositoryName').click(function() {
		if ($(this).hasClass("expanded")) {
			$(this).removeClass("expanded").addClass("collapsed");
			$(this).next().hide();
		} else {
			closeAllRepositories();
			showRepository("#" + $(this).parent().attr("id"));

		}
	});
	$('html, body').stop().animate({
        'scrollTop': $("a#eagDetails").offset().top
    }, 900, 'swing', function () {
    	logAction("scroll moved to: ", $("#eagDetails").offset().top);
    });
}
function showRepository(identifier){
	$(identifier + " .repositoryName").removeClass("collapsed").addClass("expanded");
	$(identifier + " .repositoryInfo").show();
	showRepositoryOnMap(identifier);
}
function showRepositoryOnMap(prefix,selectedCountryCode,node){
	if ($(prefix + " .repositoryName").length > 0){
		repoName = $(prefix + " .repositoryName").html();		
	}else {
		repoName = selectedAiname;
	}
	var address =  $(prefix + " .address").html();
    if($(prefix + " .address").length == 0) {
    	address = $(prefix + ".postalAddress").html();
    } 
	displayMaps(address, repoName, selectedCountryCode, node);
}
function closeAllRepositories(){
	if ($(".repositoryName").length > 0){
		$(".repositoryName").removeClass("expanded").addClass("collapsed");
		$(".repositoryInfo").hide();
		$(".repositoryInfo .longDisplay").hide();
	}
}
function multiLanguage(){
	if ($("div[class^='emailLang_']").length>0) { //if class^='emailLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("div[class^='emailLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="emailLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("div[class^='webpageLang_']").length>0) { //if class^='webpageLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("div[class^='webpageLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="webpageLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("div[class^='webpageExhibitionLang_']").length>0) { //if class^='webpageExhibitionLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("div[class^='webpageExhibitionLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="webpageExhibitionLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageExhibitionLang_']").length>0) { //if class^='multilanguageExhibitionLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageExhibitionLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageExhibitionLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("div[class^='webpageToursSessionsLang_']").length>0) { //if class^='webpageToursSessionsLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("div[class^='webpageToursSessionsLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="webpageToursSessionsLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageToursSesiionsLang_']").length>0) { //if class^='multilanguageToursSesiionsLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageToursSesiionsLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageToursSessionsLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("div[class^='webpageOtherServicesLang_']").length>0) { //if class^='webpageOtherServicesLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("div[class^='webpageOtherServicesLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="webpageOtherServicesLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageOtherServicesLang_']").length>0) { //if class^='multilanguageOtherServicesLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageOtherServicesLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageOtherServicesLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageRelatedResourcesLang_']").length>0) { //if class^='multilanguageRelatedResourcesLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageRelatedResourcesLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageRelatedResourcesLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageAssociatedrepositoriesLang_']").length>0) { //if class^='multilanguageAssociatedrepositoriesLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageAssociatedrepositoriesLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageAssociatedrepositoriesLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='addressLang_']").length>0) { //if class^='addressLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='addressLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="addressLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='postalAddressLang_']").length>0) { //if class^='postalAddressLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='postalAddressLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="postalAddressLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='nonpreformLang_']").length>0) { //if class^='nonpreformLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='nonpreformLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="nonpreformLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageLang_']").length>0) { //if class^='multilanguageLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageAccessibilityLang_']").length>0) { //if class^='multilanguageAccessibilityLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageAccessibilityLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageAccessibilityLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageRestaccessLang_']").length>0) { //if class^='multilanguageRestaccessLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageRestaccessLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageRestaccessLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageOpeningLang_']").length>0) { //if class^='multilanguageOpeningLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageOpeningLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageOpeningLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageClosingLang_']").length>0) { //if class^='multilanguageClosingLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageClosingLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageClosingLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageWithChildsLang_']").length>0) { //if class^='multilanguageWithChildsLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageWithChildsLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageWithChildsLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageTermsofuseLang_']").length>0) { //if class^='multilanguageTermsofuseLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageTermsofuseLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageTermsofuseLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageReadersTicketLang_']").length>0) { //if class^='multilanguageReadersTicketLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageReadersTicketLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageReadersTicketLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageAdvancedOrdersLang_']").length>0) { //if class^='multilanguageAdvancedOrdersLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageAdvancedOrdersLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageAdvancedOrdersLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageComputerPlacesLang_']").length>0) { //if class^='multilanguageComputerPlacesLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageComputerPlacesLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageComputerPlacesLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageRefreshmentLang_']").length>0) { //if class^='multilanguageRefreshmentLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageRefreshmentLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageRefreshmentLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageResearchServicesLang_']").length>0) { //if class^='multilanguageResearchServicesLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageResearchServicesLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageResearchServicesLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageReproductionserLang_']").length>0) { //if class^='multilanguageReproductionserLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageReproductionserLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageReproductionserLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageInternetaccessLang_']").length>0) { //if class^='multilanguageInternetaccessLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageInternetaccessLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageInternetaccessLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageConservationLaboratoryLang_']").length>0) { //if class^='multilanguageConservationLaboratoryLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageConservationLaboratoryLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageConservationLaboratoryLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageHoldingsLang_']").length>0) { //if class^='multilanguageHoldingsLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageHoldingsLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageHoldingsLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageRepositorhistLang_']").length>0) { //if class^='multilanguageRepositorhistLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageRepositorhistLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageRepositorhistLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageRepositorfoundLang_']").length>0) { //if class^='multilanguageRepositorfoundLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageRepositorfoundLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageRepositorfoundLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageRepositorsupLang_']").length>0) { //if class^='multilanguageRepositorsupLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageRepositorsupLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageRepositorsupLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageAdminunitLang_']").length>0) { //if class^='multilanguageAdminunitLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageAdminunitLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageAdminunitLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageBuildingLang_']").length>0) { //if class^='multilanguageBuildingLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageBuildingLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageBuildingLang"+firstLang){
				$(this).show();
			}
		});
	}
	if ($("p[class^='multilanguageParformLang_']").length>0) { //if class^='multilanguageParformLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("p[class^='multilanguageParformLang_']").each(function(){
			if(firstLang.length==0){
				firstLang = $(this).attr("class");
				firstLang = firstLang.substring(firstLang.indexOf("_"));
			}
			//hide all languages that are not the first lang
			$(this).hide();
			if($(this).attr("class")=="multilanguageParformLang"+firstLang){
				$(this).show();
			}
		});
	}
}

function recoverRelatedInstitution(relatedAIId) {
	$("#dynatree-id-aieag_" + relatedAIId).trigger('click');
}

function initPrint(selectedCountryCode,archivalInstitutionName,embebbedMapUrl,countryName){
	var input_address = "";
	$(".address").each(function(){
		input_address += $(this).html();
	});
	// If necessary, recover the first element in visitors address element.
	if (input_address.indexOf("<p>") != '-1') {
		input_address = input_address.substring((input_address.indexOf("<p>") + 3), input_address.indexOf("</p>"));
	}
	printMap(input_address,selectedCountryCode,archivalInstitutionName,embebbedMapUrl,countryName);
	//remove see-more/see-less
	$(".displayLinkSeeMore").each(function(){$(this).remove();});
	$(".displayLinkSeeLess").each(function(){$(this).remove();});
	$("th").each(function(){
		var html = $(this).html();
		if(html.indexOf("()")!=-1){
			$(this).html(html.replace("()",""));
		}
	});
	multiLanguage();
}

function printMap(input_address,selectedCountryCode,archivalInstitutionName,embebbedMapUrl,countryName){
	var geocoder = new google.maps.Geocoder();
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
			$("iframe#maps").attr("src", embebbedMapUrl + parameters);
			$("iframe#maps").load(function(){
				self.print();
			});
		}else if(countryName){
			printMap(countryName,selectedCountryCode,archivalInstitutionName,embebbedMapUrl,null);
		}
		});
}