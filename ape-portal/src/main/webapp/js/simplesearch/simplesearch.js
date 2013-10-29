function init(autocompletionUrl) {
	var selector = "#searchTerms";
	$(selector).focus();
	function split(val) {
		return val.split(/\s+/);
	}
	function extractLast(term) {
		return split(term).pop();
	}
	
	$(selector).bind(
			"keydown",
			function(event) {
				if (event.keyCode === $.ui.keyCode.TAB
						&& $(this).data("autocomplete").menu.active) {
					event.preventDefault();
				}
			});
	$(selector).autocomplete({
		minLength : 0,
		source : function(request, response) {
			$.getJSON(autocompletionUrl, {
				term : extractLast(request.term)
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
		}, 
		open: function(){
	        setTimeout(function () {
	            $('.ui-autocomplete').css('z-index', 99999999999999);
	        }, 0);
	    }
	});
}