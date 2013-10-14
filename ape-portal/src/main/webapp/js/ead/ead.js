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
		console.log( + $(this).text() );
		var contentWithHighlightedItems = $(this).closest('.ead-content');
		contentWithHighlightedItems.show();
		if (contentWithHighlightedItems.prev().hasClass("collapsed")) {
			contentWithHighlightedItems.prev().removeClass("collapsed").addClass("expanded");
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

function sendFeedback(docId) {
	var preview = window.open("/web/guest/contact?"+docId, 'usersFeedback',
			'width=1020,height=525,left=10,top=5,menubar=0,toolbar=0,status=0,location=0,scrollbars=0,resizable=1');
	preview.focus();
}

function sendFeedback() {
	var email = $("input#email-input").val();
	var feedback = $("textarea#feedback-textarea").val();
	
//	var link = "mailto:fernando.vicente@mecd.es"
//        + "?cc=fernando.vicente@mecd.es"
//        + "&subject=feedback from " + escape(email)
//        + "&body=" + escape(feedback);
	
	if (validateForm(email, feedback)){
//		window.location.href = link;
//		EmailSender.sendEmail(3, email, escape(feedback));
    }
	else{
		alert("fields wrong");
	}
}

function validateForm(email, feedback)
{
    var format = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;  
    var ok=true;
	
    if (feedback=="" || feedback==null) {
        ok=false;
    }
    if(!email.match(format)){        
    	ok=false;
    }
    
    return ok;
}

function showFeedback() {
	if ($("#contactForm").hasClass("feedback")) {
		$("#contactForm").removeClass("feedback");
	} else {
		$("#contactForm").addClass("feedback");
	}
}