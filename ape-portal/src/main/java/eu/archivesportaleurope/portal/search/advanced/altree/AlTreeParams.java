package eu.archivesportaleurope.portal.search.advanced.altree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.TreeType;

public class AlTreeParams {
	private final static Logger LOGGER = Logger.getLogger(ArchivalLandscapeTreeJSONWriter.class);
	private static final String SEPARATOR = ",";
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
		for (String expandedNode: expandedNodesList){
			LOGGER.info(expandedNode);
		}
		this.expandedNodesList = expandedNodesList;
	}
	public List<String> getSelectedNodesList() {
		if (selectedNodesList == null && containSelectedNodes){
			selectedNodesList = Arrays.asList(selectedNodes.split(SEPARATOR));
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
