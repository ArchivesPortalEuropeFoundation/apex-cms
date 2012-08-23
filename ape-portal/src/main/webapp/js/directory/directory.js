function initDirectory(directoryTreeUrl, directoryTreeAIUrl, aiDetailsUrl) {
	$("#directoryTree").dynatree({
		//Navigated Search Tree for Countries, Archival Institution Groups and Archival Institutions configuration
		title: "Navigated Search Tree for Archival Landscape - Countries, Archival Insitution Groups and Archival Institutions",
		//rootVisible: false,
		fx: { height: "toggle", duration: 200 },
		selectMode: 1,
						
		//Handleing events
						
		//Tree initialization
		initAjax: {
				url: directoryTreeUrl
			},
						
			//Function to load only the part of the tree that the user wants to expand
			onLazyRead: function(node){
				            	
				node.appendAjax({
					url: directoryTreeAIUrl,
					data: {nodeId: node.data.key}
				});
			},
							
		//Function to load the EAG information in the right part of the page using AJAX
			onActivate: function(node) {
			if( node.data.aiId ) {
				$("#directory-column-right-content").empty();
				$("#directory-column-right-content").append("<div id='waitingImage'></div>");
				$("#directory-column-right-content").load(aiDetailsUrl +"&id=" + node.data.aiId);
			}
		}
							
	});

}
