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
						dtnode.remove();
						var children = parent.getChildren();
						var index = children.length - 1;
						var lastChild = children[index];
						var relativeTop = $('#eadTree').scrollTop() + $(lastChild.span).offset().top
								- $("#eadTree").offset().top - 40;
						$('#eadTree').animate({
							scrollTop : relativeTop
						}, 500);
					} else if (dtnode.data.more == "before") {
						dtnode.parent.insertBeforeAjaxWithoutRemove({
							url : eadTreeUrl,
							data : getMoreTreeData(namespace, dtnode)
						}, dtnode);
						dtnode.remove();
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

	});
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
	var header = $("#header");
	var totalWidth = header.outerWidth(true);
	var marge = 10;
	rightPane.width(totalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge);
	$(splitter).draggable({
		containment : "#eadcontent",
		scroll : false,
		axis : "x",
		stop : function(event, ui) {
			var left = $("#splitter").offset().left;
			leftPane.width(left - 1);
			splitter.css("left", 0);
			rightPane.width(totalWidth - leftPane.outerWidth(true) - splitter.outerWidth(true) - marge);
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
	$("#eadcontent").height($(window).height() - $("#eadcontent").offset().top);
}
function printEadDetails(url) {
	var preview = window.open(url, 'printpreview',
			'width=1000,height=600,left=10,top=10,menubar=0,toolbar=0,status=0,location=0,scrollbars=1,resizable=1');
	preview.focus();

}
