
function init(){
	multiLanguage();
}
function multiLanguage(){
	if ($("div[class^='multilanguageLang_']").length>0) { //if class^='multilanguageLang_' exists, there is not eng lang
		var firstLang = ""; //storage for first language
		$("div[class^='multilanguageLang_']").each(function(){
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
}

function printEacDetails(url) {
	var preview = window.open(url, 'printpreview',
			'width=1100,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
	preview.focus();

}

