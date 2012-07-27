function initContextTab(contextTreeUrl, previewUrl, namespace) {
	$("#contextTabTree").dynatree({
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
						//addContextTreeHandler();
					}
				},
				onActivate : function(dtnode) {
					// Retrievieng all the search filters
					// Use our custom attribute to load the new target content:
					if (dtnode.data.parentId != undefined
							&& dtnode.data.searchType != "ai") {
							var holderPosition = $(dtnode.span).position();
						var url = previewUrl + "&" + namespace + "id="
								+ dtnode.data.parentId;
						var element = $("#updateCurrentSearch_element").val();
						var term = $("#updateCurrentSearch_term").val();
						url = url + "&" + namespace + "element=" + element;
						if (dtnode.data.searchResult == "true") {
							url = url +"&" + namespace + "term=" + term;
							$("#preview-absolute").removeClass(
									"preview-content preview-nocontent")
									.addClass("preview-content");
						} else {
							$("#preview-absolute").removeClass(
									"preview-content preview-nocontent")
									.addClass("preview-nocontent");
						}
						$("#preview-absolute").css("top", holderPosition.top + "px");
						$.get(url, function(data) {
							$("#preview-absolute").html(data);
						});
					} else {
						$("#preview-absolute").removeClass(
								"preview-content preview-nocontent");
						$("#preview-absolute").html("");

					}
				}

			});
	//addContextTreeHandler();

}

function getSearchTreeData(dtnode){
	var selectedNodes = "";
	$(".updateCurrentSearch_navigationTreeNodesSelected").each(function(index, elem){
		if(index != 0){
			selectedNodes += ",";
		}
		selectedNodes += elem.value;
	});
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
		selectedNodes : selectedNodes
	};
}
function initNavigatedTree(navigatedTreeUrl, generateNavigatedTreeAiUrl,generateNavigatedTreeAiContentUrl, previewUrl, namespace ) {
	$("#advancedSearchPortlet #archivalLandscapeTree")
			.dynatree(
					{
						// Navigated Search Tree for Countries, Archival
						// Institution Groups,Archival Institutions, Holdings
						// Guide and Finding Aid configuration
						title : "Navigated Search Tree for Archival Landscape - Countries, Archival Insitution Groups and Archival Institutions",
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
							url : navigatedTreeUrl,
							data : {
								expandedNodes : function() {
									var elements = "";
									$("#expandedNodes option").each(
											function(index, elem) {
												if (index != 0) {
													elements += ",";
												}
												elements += elem.value;
											});
									return elements;
								},
								selectedNodes : function() {
									var elements = "";
									$("#navigationTreeNodesSelected option")
											.each(function(index, elem) {
												if (index != 0) {
													elements += ",";
												}
												elements += elem.value;
											});
									return elements;
								}
							}
						},

						// Function to load only the part of the tree that the
						// user wants to expand
						onLazyRead : function(node) {
							$("#expandedNodes")
									.append(
											"<option value='"
													+ node.data.key
													+ "' selected='selected'></option>");
							var typeNode = new String(node.data.key);
							typeNode = typeNode.substring(0, typeNode
									.indexOf('_'));

							if (typeNode == "country" || typeNode == "aigroup") {
								// The node which the user has clicked is a
								// country, an archival institution group or
								// subgroup
								node
										.appendAjax({
											url : generateNavigatedTreeAiUrl,
											data : {
												nodeId : node.data.key
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

							} else if (typeNode == "hggroupmore"
									|| typeNode == "hgmore"
									|| typeNode == "fafoldermore") {
								// The node which the user has clicked is More
								// after... node, so it is necessary to expand
								// the next results within the same parent node
								var moreNode = node;
								node.parent
										.appendAjaxWithoutRemove({
											url : generateNavigatedTreeAiContentUrl,
											data : {
												nodeId : node.data.key
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
							} else {

								// The node which the user has clicked is a
								// normal node and a node within the content of
								// an archival institution, so it is necessary
								// to expand the results in a nested level
								node
										.appendAjax({
											url : generateNavigatedTreeAiContentUrl,
											data : {
												nodeId : node.data.key
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
								var url = previewUrl + "&" + namespace + "id="
								+ node.data.previewId;
									$("#al-preview").removeClass(
											"preview-content preview-nocontent")
											.addClass("preview-nocontent");
								//var holderPosition = $(node.span).position();
								//$("#al-preview-absolute").css("top", holderPosition.top + "px");
								$.get(url, function(data) {
									$("#al-preview").html(data);
								});
							} else {
								$("#al-preview").removeClass(
										"preview-content preview-nocontent");
								$("#al-preview").html("");
		
							}
						},
						onDeactivate: function(node) {
							$("#al-preview").removeClass(
							"preview-content preview-nocontent");
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

function selectChildren(node) {

	var children = node.getChildren();
	var nodeParent = node.parent;
	notFound = true;

	while (notFound) {
		typeNode = new String(nodeParent.data.key);
		keyNode = new String(nodeParent.data.key);
		typeNode = typeNode.substring(0, keyNode.indexOf('_'));
		keyNode = keyNode.substring(keyNode.indexOf('_') + 1);

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
		keyNode = new String(nodeParent.data.key);
		typeNode = typeNode.substring(0, keyNode.indexOf('_'));
		keyNode = keyNode.substring(keyNode.indexOf('_') + 1);

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

function fillInputFromNavTree(){
	var archivalLandscapeTree = $("#advancedSearchPortlet #archivalLandscapeTree").dynatree(
	"getTree");
	var selectedNodes = archivalLandscapeTree.getSelectedNodes(true);
	var elementId = null;
	$('#navigationTreeNodesSelected').empty();
	// Second, all the nodes selected in the Navigation Tree are stored
	for ( var i = 0; i < selectedNodes.length; i++) {
		elementId = new Option(selectedNodes[i].data.key,
				selectedNodes[i].data.key, false, true);
		$('#navigationTreeNodesSelected').append(elementId);
	}
}