package eu.archivesportaleurope.portal.search.advanced.altree;

import java.util.Arrays;
import java.util.List;

import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.TreeType;

public class AlTreeParams {
	private String key;
	private String[] expandedNodes;
	private String[] selectedNodes;
	private Integer aiId;


	
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
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	
}
