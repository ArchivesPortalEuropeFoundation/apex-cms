var selectedCountryCode, selectedAiname;
function initDirectory(directoryTreeUrl, directoryTreeAIUrl, aiDetailsUrl,embeddedMapsUrl, mapsUrl, directoryTreeMapsUrl) {
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
					displaySecondMap(directoryTreeMapsUrl,selectedCountryCode,node.data.aiId);
				}else if (node.data.googleMapsAddress){
					selectedAiname = null;
					displaySecondMap(directoryTreeMapsUrl,selectedCountryCode,null);
				}else {
					selectedAiname = null;
				}
		},

		// Generate id attributes like <span id='dynatree-id-KEY'>
		generateIds: true
	});
	displaySecondMap(directoryTreeMapsUrl,selectedCountryCode,null);
}
function printEagByURL(url){
	var preview = window.open(url, 'printeag',
	'width=1100,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
	preview.focus();
}

function displaySecondMap(directoryTreeMapsUrl,selectedCountryCode,aiId){
//	//Map limits
//	var strictBounds = new google.maps.LatLngBounds(
//	    new google.maps.LatLng(85, -180),           // top left corner of map
//	    new google.maps.LatLng(-85, 180)            // bottom right corner
//	);

	$.getJSON(directoryTreeMapsUrl,{ countryCode : selectedCountryCode, institutionID : aiId },function(data){
	    var markers = [];
	    var marker,i=0;
	    var infowindow = new google.maps.InfoWindow();
	    // load all repos with names and coords
	    var bounds = new google.maps.LatLngBounds();
	    $.each(data.repos,function(){
	    	var dataRepo = $(this);
	        var latLng = new google.maps.LatLng(dataRepo[0].latitude, dataRepo[0].longitude);
	        marker = new google.maps.Marker({ position: latLng, title: dataRepo[0].name });
	        bounds.extend(latLng);
	        markers.push(marker);
	        
	        google.maps.event.addListener(marker, 'click', (function(marker, i) {
	            return function() {
	                var content=dataRepo[0].name;
	                infowindow.setContent(content);
	                infowindow.open(map, marker);
	            }
	        })(marker, i));
	        i++;
	        
	    });
	    
	    if (aiId==null){
		    var map = new google.maps.Map(document.getElementById('map_div'), {
		      zoom: 7,
		      mapTypeId: google.maps.MapTypeId.ROADMAP
		    });
	    }
	    else{
		    var map = new google.maps.Map(document.getElementById('map_div'), {
			      zoom: 16,
			      mapTypeId: google.maps.MapTypeId.ROADMAP
			    });
    	}
	    map.fitBounds(bounds);
	    var markerCluster = new MarkerClusterer(map, markers);
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

function initPrint(selectedCountryCode,directoryTreeMapsUrl,selectedAiId){
	google.setOnLoadCallback(printSecondMap(selectedCountryCode,directoryTreeMapsUrl,selectedAiId));
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

function printSecondMap(selectedCountryCode,directoryTreeMapsUrl,selectedAiId){
	$.getJSON(directoryTreeMapsUrl,{ countryCode : selectedCountryCode, institutionID : selectedAiId },function(data){
	    var markers = [];
	    var marker,i=0;
	    var infowindow = new google.maps.InfoWindow();
	    var imageUrl = 'http://chart.apis.google.com/chart?cht=mm&chs=24x32&' +
	    'chco=FFFFFF,008CFF,000000&ext=.png';
	    var markerImage = new google.maps.MarkerImage(imageUrl, new google.maps.Size(24, 32));
	    
      var styles = [[{
            url: 'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/markerclusterer/images/people35.png',
            height: 35,
            width: 35,
            anchor: [16, 0],
            textColor: '#ff00ff',
            textSize: 10
          }, {
            url: 'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/markerclusterer/images/people45.png',
            height: 45,
            width: 45,
            anchor: [24, 0],
            textColor: '#ff0000',
            textSize: 11
          }, {
            url: 'http://google-maps-utility-library-v3.googlecode.com/svn/trunk/markerclusterer/images/people55.png',
            height: 55,
            width: 55,
            anchor: [32, 0],
            textColor: '#ffffff',
            textSize: 12
          }]];
	    
	    // load all repos with names and coords
	    var bounds = new google.maps.LatLngBounds();
	    $.each(data.repos,function(){
	    	var dataRepo = $(this);
	        var latLng = new google.maps.LatLng(dataRepo[0].latitude, dataRepo[0].longitude);
	        marker = new google.maps.Marker({ position: latLng, title: dataRepo[0].name });
	        bounds.extend(latLng);
	        markers.push(marker);
	        
	        google.maps.event.addListener(marker, 'click', (function(marker, i) {
	            return function() {
	                var content=dataRepo[0].name;
	                infowindow.setContent(content);
	                infowindow.open(map, marker);
	            }
	        })(marker, i));
	        i++;
	        
	    });
	    var map = new google.maps.Map(document.getElementById('map_div'), {
	      zoom: 16,
	      mapTypeId: google.maps.MapTypeId.ROADMAP
	    });
	    map.fitBounds(bounds);

	    // Whether to make the cluster icons printable. 
	    // Do not set to true if the url fields in the styles array refer to image sprite files. 
	    // The default value is false
	    var markerCluster = new MarkerClusterer(map, markers, {
	        ignoreHidden: true,
	        printable: true,
        	styles: styles[-1]
          });
	    
		google.maps.event.addListenerOnce(map, 'tilesloaded', function(){
			self.print();
		});
	}); //JSON
}
