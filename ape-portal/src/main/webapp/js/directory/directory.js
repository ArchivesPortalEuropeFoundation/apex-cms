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
					logAction("directory-eag", eagDetailsUrl);

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
	'width=1000,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
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