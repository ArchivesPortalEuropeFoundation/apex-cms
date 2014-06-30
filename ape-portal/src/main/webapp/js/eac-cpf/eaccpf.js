
function init(){
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
		}else if ($(this).find('pre').length > 3){
			$(this).find('.displayLinkShowMore').removeClass("hidden");
			$(this).find('pre').each(function(index){
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
		$('#eacCpfDisplayPortlet').css({'margin-right':'0px', 'margin-left':'0px'});
		$('#eacCpfDisplayPortlet h1').css({'width':'98%'});
		$("#eacCpfDisplayPortlet #details").css({'width':'98%'});
		$("#eacCpfDisplayPortlet #relations").css({'float':'none', 'clear':'both', 'padding-top':'20px', 'width':'98%'});
		$('#eacCpfDisplayPortlet a').each(function(){
			$(this).css({'pointer-events':'none', 'cursor':'default'});
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

function enableFeedback(){
    $(".container").after($("#feedbackArea"));
    $("#content").after($("#feedbackArea"));
}