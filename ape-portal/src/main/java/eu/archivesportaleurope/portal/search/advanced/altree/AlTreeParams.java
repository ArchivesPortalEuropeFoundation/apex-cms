package eu.archivesportaleurope.portal.search.advanced.altree;

import java.util.Arrays;
import java.util.List;

import eu.archivesportaleurope.portal.common.al.AlType;

public class AlTreeParams {
	private String parentId;
	private String type;
	private String[] expandedNodes;
	private String[] selectedNodes;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public boolean existInSelectedNodes(AlType alType, Integer id){
		if (selectedNodes != null && selectedNodes.length > 0){
			List<String> selectedNodesList = Arrays.asList(selectedNodes);
			return selectedNodesList.contains(alType + ""+ id);
		}
		return false;
	}
	public boolean existInExpandedNodes(AlType alType, Integer id){
		if (expandedNodes != null && expandedNodes.length > 0){
			List<String> expandedNodesList = Arrays.asList(expandedNodes);
			return expandedNodesList.contains(alType + ""+ id);
		}
		return false;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
