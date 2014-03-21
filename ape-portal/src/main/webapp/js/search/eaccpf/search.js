var autocompletionEacCpfUrl;
function setUrls(aUrl){
	autocompletionEacCpfUrl = aUrl;
}

function clearSearch(){
	commonClearSearch();

}

function init(){
	activateAutocompletion("#searchTerms");
	initCommon();
}

function activateAutocompletion(selector) {
	function split(val) {
		return val.split(/\s+/);
	}
	function extractLast(term) {
		term=term.trim();
		return split(term).pop();
	}

	$(selector).bind("keydown", function(event) {
		if (event.keyCode === $.ui.keyCode.TAB && $(this).data("autocomplete").menu.active) {
			event.preventDefault();
		}
	});
	
	$(selector).autocomplete({
		minLength : 0,
		source : function(request, response) {
			$.getJSON(autocompletionEacCpfUrl, {
				term : extractLast(request.term),
				sourceType : "eaccpf"
			}, response);
		},
		search : function() {
			// custom minLength
			var term = extractLast(this.value);
			if (term.length < 1) {
				return false;
			}
		},
		focus : function() {
			// prevent value inserted on focus
			return false;
		},
		select : function(event, ui) {
			var terms = split(this.value);
			// remove the current input
			terms.pop();
			// add the selected item
			terms.push(ui.item.value);
			// add placeholder to get the comma-and-space at the end
			terms.push("");
			this.value = terms.join(" ");
			return false;
		}
	});
}