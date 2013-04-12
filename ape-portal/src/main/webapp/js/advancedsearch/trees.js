function initContextTab(contextTreeUrl, previewUrl, namespace) {
	$("#contextTabTree").dynatree(
			{
				autoFocus : false,
				onLazyRead : function(dtnode) {
					// Retrieving all the search filters
					if (dtnode.data.more == "true") {
						dtnode.parent.appendAjaxWithoutRemove({
							url : contextTreeUrl,
							type : "POST",
							data : getSearchTreeData(dtnode)
						});
						var parent = dtnode.parent;
						dtnode.remove();
						var children = parent.getChildren();
						var index = children.length - 1;
						var lastChild = children[index];
						$('html, body').animate({
							scrollTop : $(lastChild.span).offset().top - 40
						}, 500);
					} else {
						dtnode.appendAjax({
							url : contextTreeUrl,
							type : "POST",
							data : getSearchTreeData(dtnode)

						});
						// addContextTreeHandler();
					}
				},
				onActivate : function(dtnode) {
					// Retrievieng all the search filters
					// Use our custom attribute to load the new target content:
					if (dtnode.data.parentId != undefined && dtnode.data.searchType != "ai" && dtnode.data.searchType != "hgfa") {
						var url = previewUrl + "&" + namespace + "id=" + dtnode.data.parentId;
						var element = $("#updateCurrentSearch_element").val();
						var term = $("#updateCurrentSearch_term").val();
						url = url + "&" + namespace + "element=" + element;
						if (dtnode.data.searchResult == "true") {
							url = url + "&" + namespace + "term=" + term;
						}
						$.get(url,
								function(data) {
									displayPreview("#search-preview", data);
								});
					} else {
						$("#search-preview").removeClass("preview-content");
						$("#search-preview").html("");

					}
				}

			});
	// addContextTreeHandler();

}

function getSearchTreeData(dtnode) {
	return {
		term : $("#updateCurrentSearch_term").val(),
		element : $("#updateCurrentSearch_element").val(),
		typedocument : $("#updateCurrentSearch_typedocument").val(),
		fromdate : $("#updateCurrentSearch_fromdate").val(),
		todate : $("#updateCurrentSearch_todate").val(),
		country : dtnode.data.country,
		parentId : dtnode.data.parentId,
		method : $("#updateCurrentSearch_method").val(),
		dao : $("#updateCurrentSearch_dao").val(),
		level : dtnode.data.level,
		oldlevel : dtnode.data.oldlevel,
		searchType : dtnode.data.searchType,
		start : dtnode.data.start,
		fondId : dtnode.data.fondId,
		selectedNodes : $("#updateCurrentSearch_selectedNodes").val()
	};
}

function initArchivalLandscapeTree(archivalLandscapeUrl, previewUrl, namespace) {
	$("#advancedSearchPortlet #archivalLandscapeTree").dynatree({
		// Navigated Search Tree for Countries, Archival
		// Institution Groups,Archival Institutions, Holdings
		// Guide and Finding Aid configuration
		title : "Tree for Archival Landscape - Countries, Archival Insitution Groups and Archival Institutions",
		// rootVisible: false,
		fx : {
			height : "toggle",
			duration : 200
		},
		checkbox : true,
		autoFocus : false,
		keyboard : true,
		selectMode : 3,

		// Handleing events

		// Tree initialization
		initAjax : {
			url : archivalLandscapeUrl,
			data : {
				expandedNodes : $("#advancedSearchPortlet #expandedNodes").val(),
				selectedNodes : $("#advancedSearchPortlet #selectedNodes").val()
			}
		},

		// Function to load only the part of the tree that the
		// user wants to expand
		onLazyRead : function(node) {
			//$("#expandedNodes").append("<option value='" + node.data.key + "' selected='selected'></option>");
			if (node.data.more == "true"){
				
				var moreNode = node;
				node.parent.appendAjaxWithoutRemove({
					url : archivalLandscapeUrl,
					data : {
						key : node.data.key,
						aiId: node.data.aiId
					},
					success : function(node) {
						// This function is activated
						// when the lazy read has been a
						// success
						// It is necessary to check the
						// upcomming siblings if some of
						// their
						// parents are checked too
						selectSiblings(moreNode);
					}

				});
				// Finally, the node More... is removed from the
				// Navigation Tree
				node.remove();
			}else {
				node.appendAjax({
					url : archivalLandscapeUrl,
					data : {
						key : node.data.key,
						aiId: node.data.aiId
					},
					success : function(node) {
						// This function is activated
						// when the lazy read has been a
						// success
						// It is necessary to check the
						// upcomming children if some of
						// their
						// parents are checked too
						if (node.isSelected()) {
							selectChildren(node);
						}
					}
				});
			}

		},

		// Function to open a new tab/window whan the user
		// clicks a node which has an url attached
		onActivate : function(node) {
			if (node.data.previewId != undefined) {
				var url = previewUrl + "&" + namespace + "id=" + node.data.previewId;
				$.get(url, function(data) {
					displayPreview("#al-preview", data);
				});
			} else {
				$("#al-preview").removeClass("preview-content ");
				$("#al-preview").html("");

			}
		},
		onDeactivate : function(node) {
			$("#al-preview").removeClass("preview-content preview-nocontent");
			$("#al-preview").html("");
		},

		// When the user uses the spacebar, he/she will
		// (de)select the checkbox
		onKeydown : function(node, event) {
			if (event.which == 32) {
				node.toggleSelect();
				return false;
			}
		}

	});

};
function getSelectedChildren(node){
	var result = "";
	var children = node.getChildren();
	for ( var i = 0; i < children.length; i++) {
		var child = children[i];
		if (i > 0){
			result = result + ",";
		}
		if (child.data.hideCheckbox){
			result = getSelectedChildren(child);
		}else {
			result = result + child.data.key;
		}
	}
	return result;

}
function selectChildren(node) {

	var children = node.getChildren();
	var nodeParent = node.parent;
	notFound = true;

	while (notFound) {
		typeNode = new String(nodeParent.data.key);
		typeNode = typeNode.substring(0, typeNode.indexOf('_'));

		if (nodeParent.isSelected) {
			// If the parent is selected, then it is necessary to select the
			// children
			for ( var i = 0; i < children.length; i++) {
				children[i].select(true);
			}

			notFound = false;
		}

		if (notFound) {
			if (typeNode == "country") {
				// The parent is a country
				notFound = false;
			} else {
				// The parent is not a country
				nodeParent = nodeParent.parent;
			}
		}
	}
}

function selectSiblings(node) {

	var nodeParent = node.parent;
	var children = nodeParent.getChildren();
	notFound = true;

	while (notFound) {
		typeNode = new String(nodeParent.data.key);
		typeNode = typeNode.substring(0, typeNode.indexOf('_'));

		if (nodeParent.isSelected()) {
			// If the parent is selected, then it is necessary to select the
			// children
			for ( var i = 0; i < children.length; i++) {
				if (!children[i].isSelected()) {
					children[i].select(true);
				}
			}
			notFound = false;
		}

		if (notFound) {
			if (typeNode == "country") {
				// The parent is a country
				notFound = false;
			} else {
				// The parent is not a country
				nodeParent = nodeParent.parent;
			}
		}

	}
}

function fillInputFromNavTree() {
	var archivalLandscapeTree = $("#advancedSearchPortlet #archivalLandscapeTree").dynatree("getTree");
	var selRootNodes = archivalLandscapeTree.getSelectedNodes(true);
    var text = "";
	for ( var i = 0; i < selRootNodes.length; i++) {
		var selectedNode = selRootNodes[i];
		if (i > 0){
			text = text + ",";
		}
		if (selectedNode.data.hideCheckbox){
			text = text + getSelectedChildren(selectedNode);
		}else {
			text = text + selectedNode.data.key;
		}
		
	}
	$("#advancedSearchPortlet #selectedNodes").val(text);
}