package eu.archivesportaleurope.portal.search.ead.altree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.TreeType;

public class AlTreeParams {
	private String key;
	private String selectedNodes;
	private Integer aiId;
	private List<String> selectedNodesList = null;
	private List<String> expandedNodesList = new ArrayList<String>();
	private boolean containSelectedNodes = false;

	public boolean existInSelectedNodes(AlType alType, Number id, TreeType treeType){
		if (containSelectedNodes){
			return getSelectedNodesList().contains(AlType.getKey(alType, id, treeType));
		}else {
			return false;
		}
	}
	public boolean existInExpandedNodes(AlType alType, Number id, TreeType treeType){
		if (containSelectedNodes){
			String expandedNode = AlType.getKey(alType, id, treeType);
			return expandedNodesList.contains(expandedNode);
		}else {
			return false;
		}
		
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
	public String getSelectedNodes() {
		return selectedNodes;
	}
	public void setSelectedNodes(String selectedNodes) {
		this.selectedNodes = selectedNodes;
		this.containSelectedNodes = StringUtils.isNotBlank(selectedNodes);
	}
	public List<String> getExpandedNodesList() {
		return expandedNodesList;
	}
	public void setExpandedNodesList(List<String> expandedNodesList) {
		this.expandedNodesList = expandedNodesList;
	}
	public List<String> getSelectedNodesList() {
		if (selectedNodesList == null && containSelectedNodes){
			selectedNodesList = new ArrayList<String>();
			List<String> tempList = Arrays.asList(selectedNodes.split(AlType.LIST_SEPARATOR));
			for (String selectedNode: tempList){
				AlType alType = AlType.getAlType(selectedNode);
				Long id = AlType.getId(selectedNode);
				TreeType treeType = AlType.getTreeType(selectedNode);
				selectedNodesList.add(AlType.getKey(alType, id, treeType));
			}
		}else if (selectedNodesList == null){
			selectedNodesList = new ArrayList<String>();
		}
		return selectedNodesList;
	}
	public boolean isContainSelectedNodes() {
		return containSelectedNodes;
	}
	public void setContainSelectedNodes(boolean containSelectedNodes) {
		this.containSelectedNodes = containSelectedNodes;
	}
	
}
