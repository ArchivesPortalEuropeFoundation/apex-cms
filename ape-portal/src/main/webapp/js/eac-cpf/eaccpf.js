
function init(){
	changeButtonsDiv();
	eraseData();
	$(".displayLinkShowLess").addClass("hidden");
	$('.displayLinkShowMore').addClass("hidden");
	$(".moreDisplay").each(function(index){
		if ($(this).find('p').length > 3){
			$(this).find('.displayLinkShowMore').removeClass("hidden");
			$(this).find('p').each(function(index){
				if(index > 2){
					$(this).addClass("hidden");
				}
			});
		}else if ($(this).find('li.item').length > 3){
			$(this).find('.displayLinkShowMore').removeClass("hidden");
			$(this).find('li.item').each(function(index){
				if(index > 2){
					$(this).addClass("hidden");
				}
			});
		}else{
			$(this).find('.displayLinkShowMore').addClass("hidden");
		}
	});
	
}
function initPrint(){
	eraseData();
	try{
		$("body").css("cursor", "progress");
		$(".displayLinkShowMore").each(function(){
			$(this).remove();
		});
		$(".displayLinkShowLess").each(function(){
			$(this).remove();
		});

		self.print();
	}
	catch (e) {
		$("body").css("cursor", "default");
	}
	$("body").css("cursor", "default");
	
}
function printEacDetails(url) {
	try{
		$("body").css("cursor", "progress");
		var preview = window.open(url, 'printeaccpf', 'width=1100,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
		preview.focus();
	}catch (e) {
		$("body").css("cursor", "default");
	}
	$("body").css("cursor", "default");
}

/**
 * Function to expand or collapsed the relations
 */
function makeRelationsCollapsible() {
	$('#relations .boxtitle').each(function(index) {
		$(this).click(function() {
			if ($(this).find(".collapsibleIcon").hasClass("expanded")) {
				$(this).find(".collapsibleIcon").removeClass("expanded").addClass("collapsed");
				$(this).parent().find('ul').addClass("hidden");
				$(this).parent().find('.whitespace').addClass("hidden");
				$(this).parent().find('.displayLinkShowLess').addClass('hidden');
				$(this).parent().find('.displayLinkShowMore').addClass('hidden');
			} else {
				$(this).find(".collapsibleIcon").removeClass("collapsed").addClass("expanded");
				$(this).parent().find('ul').removeClass("hidden");
				if ($(this).parent().find('li').length > 3){
					$(this).parent().find('.whitespace').removeClass("hidden");
					$(this).parent().find('.displayLinkShowMore').removeClass("hidden");
					$(this).parent().find('li').each(function(index){
						if (index > 2){
							$(this).addClass("hidden");
						}
					});
				}else{
					$(this).parent().find('.whitespace').addClass("hidden");
					$(this).parent().find('.displayLinkShowMore').addClass("hidden");
				}
			}
		});
		if ($(this).parent().find('li').length > 3){
			$(this).parent().find('.whitespace').removeClass("hidden");
			$(this).parent().find('.displayLinkShowMore').removeClass("hidden");
			$(this).parent().find('li').each(function(index){
				if (index > 2){
					$(this).addClass("hidden");
				}
			});
		}else{
			$(this).parent().find('.whitespace').addClass("hidden");
			$(this).parent().find('.displayLinkShowMore').addClass("hidden");
		}
	});
}

/**
 * Function to show more eac-cpf details
 * @param clazz
 * @param id
 */
function showLess(clazz, id){
	var prefix = "#" + clazz + " ";
	$(prefix + ".displayLinkShowLess").click(function(){
		$(this).addClass("hidden");
		$(prefix + ".displayLinkShowMore").removeClass("hidden");
		$(prefix + id).each(function(index){
			if (index > 2){
				$(this).addClass("hidden");
			}
		});
	});
	$(prefix + ".displayLinkShowLess").trigger("click");
}

/**
 * Function to show less eac-cpf details
 * @param clazz
 * @param id
 */
function showMore(clazz, id){
	var prefix = "#" + clazz + " ";
    $(prefix + ".displayLinkShowMore").click(function(){
    	$(this).addClass("hidden");
    	$(prefix + ".displayLinkShowLess").removeClass("hidden");
    	$(prefix + id).each(function(index){
    		if(index > 2){
    			$(this).removeClass("hidden");
    		}
    	});
    });
    $(prefix + ".displayLinkShowMore").trigger("click");
}

/**
 * Function to set correctly the position for the "Translations" and for the
 * "Print" button.
 */
function changeButtonsDiv(){
//	$("#eacCpfDisplayPortlet #bookmarkAnswer").after($("#eacCpfDisplayPortlet h1.blockHeader"));
	$("#eacCpfDisplayPortlet #buttonsDiv").before($("#eacCpfDisplayPortlet h1.blockHeader"));
}

/**
 * Function to call the actions for translate the content of the page to the
 * selected language contained in the file.
 * 
 * @param url URL for the translate action.
 * @param selector Select object in which determine the language selected to display.
 */
function translateContent(url, selector) {
	try{
		$("body").css("cursor", "progress");
		// Call the function that fix the language parameter in the URL.
		url = fixUrlLang(url, $("#" + selector.attr("id") + " option:selected").val());

		// Refresh the page with the new information.
		var transPage = window.open(url, '_self');
		transPage.focus();
	}catch (e) {
		$("body").css("cursor", "default");
	}
	$("body").css("cursor", "default");
}

/**
 * Function that fix the value of the translation selected by the user in the
 * URL used to reload the page.
 *
 * @param url URL used to reload the page.
 * @param lang Translation language selected by the user.
 *
 * @returns Fixed URL.
 */
function fixUrlLang(url, lang) {
	var fixedUrl = url;
	var param = "translationLanguage";
	if (fixedUrl.indexOf(param) > -1) {
		var urlInit = fixedUrl.substring(0, (fixedUrl.indexOf(param) + param.length + 1));
		var transValue = fixedUrl.substring(fixedUrl.indexOf(param) + param.length + 1);
		var urlFinal = "";

		if (transValue.indexOf("&") > -1) {
			urlFinal = transValue.substring(0, fixedUrl.indexOf("&"));
		}

		fixedUrl = urlInit + lang + urlFinal;
	} else {
		fixedUrl = fixedUrl + "&_eaccpfdisplay_WAR_Portal_translationLanguage=" + lang;
	}

	return fixedUrl;
}