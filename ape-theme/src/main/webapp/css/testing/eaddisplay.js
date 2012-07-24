function initPanes(){
	var leftPane =  $("#left-pane");
	var rightPane = $("#right-pane");
	var splitter = $("#splitter");
	var header =  $("#header");
	var totalWidth = header.outerWidth(true);
	var marge = 10;
	rightPane.width(totalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true)-marge);
	$( splitter ).draggable({ containment: "#eadcontent", scroll: false, axis: "x",
		stop: function (event, ui){
			var left = $("#splitter").offset().left;
			leftPane.width(left-1);
			splitter.css("left", 0);
			rightPane.width(totalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true)-marge);
		}
	});
	$(window).resize(function() {
		var newTotalWidth = header.outerWidth(true);
		var newRightPaneWidth = newTotalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true)-marge;
		if (newRightPaneWidth < 300){
			newRightPaneWidth = 300;
		}
		rightPane.width(newRightPaneWidth);
		resizePage();
	});
	resizePage();
}
function resizePage(){
	var marge = 10;
    $("#eadcontent").height($(window).height() - $("#eadcontent").offset().top);
    $('.childHeader').each(function(index) {
    	var parentWidth = $(this).width();
    	var unitidWidth = $(".expand-unitid", this).outerWidth(true);
    	var unitdateWidth = $(".expand-unitdate", this).outerWidth(true);
    	var unittitleWidth = parentWidth - unitidWidth-unitdateWidth -marge;
    	var unittitleElement = $(".expand-unittitle", this);
    	unittitleElement.width(unittitleWidth);
    });
}
