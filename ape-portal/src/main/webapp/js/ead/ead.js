function initEadTree(eadTreeUrl, displayEadUrl, namespace) {
	$("#eadTree").dynatree(
			{
				title : "Ead",
				rootVisible : false,
				autoFocus : false,
				fx : {
					height : "toggle",
					duration : 200
				},
				// In real life we would call a URL on the server like this:
				initAjax : {
					url : eadTreeUrl
				},
				onActivate : function(dtnode) {
					if (dtnode.data.more != "undefined") {
						displayEadDetails(displayEadUrl, dtnode.data.id, dtnode.data.type, namespace);
					}
				},

				onLazyRead : function(dtnode) {
					if (dtnode.data.more == "after") {
						dtnode.parent.appendAjaxWithoutRemove({
							url : eadTreeUrl,
							data : getMoreTreeData(namespace, dtnode)
						});
						var parent = dtnode.parent;
                        parent.select(true);
						dtnode.remove();
						var relativeTop = $('#eadTree').scrollTop() + $(parent.span).offset().top - $("#eadTree").offset().top - 40;
                        if (parent.data.more != "undefined") {
                            displayEadDetails(displayEadUrl, parent.data.id, parent.data.type, namespace);
                        }

						$('#eadTree').animate({
							scrollTop : relativeTop
						}, 500);
					} else if (dtnode.data.more == "before") {
						dtnode.parent.insertBeforeAjaxWithoutRemove({
							url : eadTreeUrl,
							data : getMoreTreeData(namespace, dtnode)
						}, dtnode);
                        var parent = dtnode.parent;
                        parent.select(true);
						dtnode.remove();
                        if (parent.data.more != "undefined") {
                            displayEadDetails(displayEadUrl, parent.data.id, parent.data.type, namespace);
                        }
					}

					else {
						dtnode.appendAjax({
							url : eadTreeUrl,
							data : getTreeData(namespace, dtnode)

						});
					}
				},
				minExpandLevel : 2
			});

}

function displayEadDetails(displayEadUrl, id, type, namespace) {
	$("#right-pane").html("<div class='icon_waiting'></div>");
	var params = {};
	if (type != undefined) {
		params[namespace + "type"] = type;
	}
	params[namespace + "id"] = id;
	$.get(displayEadUrl, params, function(data) {
		$("#right-pane").html(data);
		initDAOs();

	});
	logAction(document.title, displayEadUrl);
}
function initExpandableParts(){
	$("#expandableContent h2").removeClass("expanded").addClass("collapsed");
	$("#expandableContent h2").next().hide();
	$('#expandableContent h2').click(function() {
		if ($(this).hasClass("expanded")) {
			$(this).removeClass("expanded").addClass("collapsed");
			$(this).next().hide();
		} else {
			$(this).removeClass("collapsed").addClass("expanded");
			$(this).next().show();
		}
	});	
	$( "em" ).each(function() {
		var contentWithHighlightedItems = $(this).closest('.ead-content');
		contentWithHighlightedItems.show();
		if (contentWithHighlightedItems.prev().hasClass("collapsed")) {
			contentWithHighlightedItems.prev().removeClass("collapsed").addClass("expanded");
		}
	});
	$('ul.daolist a').click(function(event) {
		$(this).children().css( "opacity", "0.4" );
	});	
}
function updatePageNumber(displayEadUrl) {
	var params = {};
	$.get(displayEadUrl, params, function(data) {
		$("#right-pane").html(data);
	});
}
function getMoreTreeData(namespace, dtnode) {
	var data = {};
	if (dtnode.data.parentID != "undefined") {
		data[namespace + "parentId"] = dtnode.data.parentId;
	}
	data[namespace + "orderId"] = dtnode.data.orderId;
	data[namespace + "more"] = dtnode.data.more;
	data[namespace + "max"] = dtnode.data.max;
	return data;
}
function getTreeData(namespace, dtnode) {
	var data = {};
	data[namespace + "parentId"] = dtnode.data.id;
	return data;

}

function initPanes() {
	var leftPane = $("#left-pane");
	var rightPane = $("#right-pane");
	var splitter = $("#splitter");
	var header = $("#banner");
	var totalWidth = header.outerWidth(true);
	var marge = 10;
	rightPane.width(totalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge);
	
	$(splitter).draggable({
		containment : "#eadcontent",
		scroll : false,
		axis : "x",
		stop : function(event, ui) {
			var newTotalWidth = header.outerWidth(true);
			var left = $("#splitter").offset().left;
			if(left>750){
				left=550;
			}
			else if (left<150){
				left=150;
			}
			leftPane.width(left - 1);
			splitter.css("left", 0);
			var newRightPaneWidth = newTotalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge;
			rightPane.width(newRightPaneWidth);
		}
	});
	
	$(window).resize(function() {
		var newTotalWidth = header.outerWidth(true);
		var newRightPaneWidth = newTotalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge;
		if (newRightPaneWidth < 300) {
			newRightPaneWidth = 300;
		}
		rightPane.width(newRightPaneWidth);
		resizePage();
	});
	resizePage();
}
function resizePage() {
	$("#eadcontent").height(($(window).height()-$("#eadcontent").offset().top));
}

function printEadDetails(url) {
	var preview = window.open(url, 'printpreview',
			'width=1100,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
	preview.focus();

}

function initDAOs(){
	addDAOS();
	$('#moreDaosButton').click(function(event) {
		addDAOS();
		document.getElementById("moreDaosButton").scrollIntoView(true);
	});	
	$(".dao img").error(function () {
	    $(this).unbind("error").attr("src", "/Portal-theme/images/ape/icons/dao_types/normal/not-found.png");
	}); 
}
function addDAOS(){
	var innerWidth =  $("#eaddetailsContent").innerWidth();
	var spendWidth = 0;
	var more = false;
	var daolist = $(".daolist");
	$( "#eaddetailsContent > .ead-content > .daolistContainer > .daolist-orig > div" ).each(function(index) {
		found = true;
		var width = 206;
		if (width+spendWidth < innerWidth){
			var innerDiv = $( "<div  class='dao'/>" );
			spendWidth = spendWidth+ width;
			var imgObject = $(this).find( "img" );
			if (imgObject.attr("data-src") !== undefined){
				imgObject.attr("src", imgObject.attr("data-src"));
				imgObject.removeAttr("data-src");				
			}
			innerDiv.append($(this).html());
			daolist.append(innerDiv);
			$(this).remove();
		}else {
			more = true;
			return false;
		}	
	});
	if (!more){
		$("#moreDaosButton").remove();	
	}else{
		var size = $( "#eaddetailsContent > .ead-content > .daolistContainer > .daolist-orig > div" ).length;
		 $( "#moreDaosButton span" ).html(size +"");
	}
}