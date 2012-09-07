package eu.archivesportaleurope.portal.search.advanced.altree;

import java.util.Arrays;
import java.util.List;

import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.TreeType;

public class AlTreeParams {
	private String parentId;
	private String type;
	private String[] expandedNodes;
	private String[] selectedNodes;
	private Integer aiId;
	private Integer start = 0;

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}
	
	public boolean existInSelectedNodes(AlType alType, Number id, TreeType treeType){
		if (selectedNodes != null && selectedNodes.length > 0){
			List<String> selectedNodesList = Arrays.asList(selectedNodes);
			return selectedNodesList.contains(alType + "_"+ id + ":" + treeType);
		}
		return false;
	}
	public boolean existInExpandedNodes(AlType alType, Number id, TreeType treeType){
		if (expandedNodes != null && expandedNodes.length > 0){
			List<String> expandedNodesList = Arrays.asList(expandedNodes);
			return expandedNodesList.contains(alType + "_"+ id + ":" + treeType);
		}
		return false;
	}
	public boolean existInExpandedNodes(AlType alType, Number id, TreeType treeType, int start){
		if (expandedNodes != null && expandedNodes.length > 0){
			List<String> expandedNodesList = Arrays.asList(expandedNodes);
			return expandedNodesList.contains(alType + "_"+ id + ":" + treeType + ":" + start);
		}
		return false;
	}
	public Integer getAiId() {
		return aiId;
	}

	public void setAiId(Integer aiId) {
		this.aiId = aiId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getStart() {
		if (start == null){
			return null;
		}
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}
	
}
