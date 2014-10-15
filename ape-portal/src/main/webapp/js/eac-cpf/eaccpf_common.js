
/**
 * Function to delete the data not necessary 
 */
function eraseData(){
	eraseComma();
	eraseNameTitle();
	eraseLocationPlace();
	eraseList();
	eraseEmptyLi();
	eraseEmptyTitleSection();
	eraseDuplicatedArchivalLi();
}

function eraseDuplicatedArchivalLi(){
	var group = $("div#archives").find("li");
	var targetTexts = new Array();
	group.each(function(){
		targetTexts.push($(this).text());
	});
	var deleted = false;
	for(var i=0;i<targetTexts.length;i++){
		var j = 0;
		$("div#archives").find("li:contains('"+targetTexts[i]+"')").each(function(){
			if(j>0){
				$(this).remove();
				deleted = true;
			}
			j++;
		});
	}
	if(deleted){ //it's needed change figure
		var textToBeChanged = $("div#archives .boxtitle").find("span.text").text();
		if($.inArray("(",textToBeChanged) && $.inArray(")",textToBeChanged)){
			textToBeChanged = textToBeChanged.substring(0,$.inArray("(",textToBeChanged));
			textToBeChanged += "("+$("div#archives").find("li").length+")";
			$("div#archives .boxtitle").find("span.text").html(textToBeChanged);
		}
	}
}

function eraseEmptyLi(){
	eraseEmptyLiByName("material");
	eraseEmptyLiByName("persons");
	eraseEmptyLiByName("archives");
	eraseEmptyLiByName("alternative");
}

function eraseEmptyLiByName(name){
	var counter = 0;
	$("#"+name).find("li:empty").each(function(){
		$(this).remove();
		counter++;
	});
	if(counter>0){
		var target = $("#"+name).find(".boxtitle").find(".text");
		var text = target.html();
		var figure = parseInt(text.substring(text.indexOf("(")+1,text.indexOf(")")));
		figure-=counter;
		text = text.substring(0,text.indexOf("("))+"("+figure+")";
		target.html(text);
	}
}
/**
 * Function to delete the first comma in the dates when there is not characters before it 
 */
function eraseComma(){
	$("div#eaccpfcontent span.nameEtryDates").each(function(){
		var stringDates = $(this).html();
		var startStringDates = "";
		var endStringDates = "";
		//strip span tags
		if(stringDates.length > 0 && stringDates.indexOf(">")!==undefined && stringDates.lastIndexOf("<")){
			startStringDates = stringDates.substring(0,stringDates.indexOf(">")+1);
			endStringDates = stringDates.substring(stringDates.lastIndexOf("<"),stringDates.length);
			stringDates = stringDates.substring(stringDates.indexOf(">")+1,stringDates.lastIndexOf("<"));
		}
		var firstCharacter=stringDates.charAt(0);
		if (firstCharacter == ','){
			stringDates=$.trim(stringDates.substring(1));
		}
		$(this).html(startStringDates+stringDates+endStringDates);
	});
}
/**
 * Function to delete the name show in title form the alternatives names.
 */
function eraseNameTitle() {
	var titleName = $.trim($("div#eaccpfcontent span#nameTitle").text());
	var found = false;
	$("div#alternativeName").children().each(function() {
		if ($.trim($(this).text()) == titleName && !found){
			$(this).remove();
			found = true;
		}
	});
	var textRightColumn = $.trim($("div#alternativeName").find("p").text());
	if (textRightColumn == ''){
		$("div#titleAlternativeName").remove();
	}
}
/**
 * Function to delete the location if there is'nt nothing to show.
 */
function eraseLocationPlace(){
	$("div#eaccpfcontent .locationPlace").each(function(){
		var textRightColumn = $.trim($(this).find(".rightcolumn p").text());
		if (textRightColumn == ''){
			$(this).remove();
		}
	});
}
/**
 * Function to delete the <ul class="level"> that has not <li class="item">
 */
function eraseList(){
	$("div#eaccpfcontent ul.level").each(function(){
		var textRightColumn = $.trim($(this).find("li.item").text());
		if (textRightColumn == ''){
			$(this).remove();
		}
	});
}
/**
 * Function to delete the titles when there aren't sections to display
 * 
 */
function eraseEmptyTitleSection(){
	
	$(".blockPlural").find(".blockSingular:empty").each(function(){
		removeNodeAndTitle($(this));
	});
	
	$(".section").each(function(){
		if($(this).children().length==0){
			removeNodeAndTitle($(this));
		}
	});
	
	$(".blockPlural").each(function(){
		if($(this).children().length==0){
			$(this).remove();
		}
	});
	
	$("div#details").children().each(function(){
		if($(this).is("h2.title")){
			var sectionTitle = $(this).next();
			if(!sectionTitle.is("div") || !( sectionTitle.hasClass("blockPlural") || sectionTitle.hasClass("section"))){
				$(this).remove();
			}
		}
	});
}

function removeNodeAndTitle(node){
	var parent = node.parent();
	var before = parent.prev();
	if(before.is("h2") && before.hasClass("title")){
		before.remove();
	}
	node.remove();
}