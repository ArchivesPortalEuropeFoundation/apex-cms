
function init(){
	eraseNameTitle();
	$(".displayLinkShowLess").addClass("hidden");
	$(".moreDisplay").each(function(index){
		if ($(this).find('p').length > 2){
			$(this).find('.displayLinkShowMore').removeClass("hidden");
			$(this).find('p').each(function(index){
				if(index > 2){
					$(this).addClass("hidden");
				}
			});
		}else{
			$(this).find('.displayLinkShowMore').addClass("hidden");
		}
	});
}

/**
 * Function to delete the name show in title form the alternatives names.
 */
function eraseNameTitle() {
	var titleName = $("div#eaccpfcontent span#nameTitle").text();
	$("div#alternativeName").children().each(function() {
		if ($(this).text().trim() == titleName.trim()) {
			$(this).remove();
		}
	});
}

function printEacDetails(url) {
	var preview = window.open(url, 'printpreview',
			'width=1100,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
	preview.focus();

}
function recoverRelatedInstitution(relatedAIId) {
	$("#dynatree-id-aieag_" + relatedAIId).trigger('click');
}
function makeRelationsCollapsible() {
	$('#relations .boxtitle').each(function(index) {
		$(this).click(function() {
			//var expanded = false;
			if ($(this).find(".collapsibleIcon").hasClass("expanded")) {
				$(this).find(".collapsibleIcon").removeClass("expanded").addClass("collapsed");
				$(this).parent().find('ul').addClass("hidden");
				$(this).parent().find('.whitespace').addClass("hidden");
				$(this).parent().find('.displayLinkShowLess').addClass('hidden');
				$(this).parent().find('.displayLinkShowMore').addClass('hidden');
			} else {
				$(this).find(".collapsibleIcon").removeClass("collapsed").addClass("expanded");
				$(this).parent().find('ul').removeClass("hidden");
				
				if ($(this).parent().find('li').length > 2){
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
	//	$('.box .displayLinkShowLess').addClass('hidden');
		if ($(this).parent().find('li').length > 2){
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
function showLessRelation(clazz, id){
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
}
function showMoreRelation(clazz, id){
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
}