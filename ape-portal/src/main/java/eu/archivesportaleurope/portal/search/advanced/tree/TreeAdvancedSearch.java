package eu.archivesportaleurope.portal.search.advanced.tree;

import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;

public class TreeAdvancedSearch extends AdvancedSearch {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8551327027173370522L;
	private String selectedNodes;
	private String searchType;
	private String start;
	private String parentId;
	private String level;

	public String getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(String selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public int getLevelInt() {
		if (level != null) {
			return Integer.parseInt(level);
		} else {
			return -1;
		}
	}

	public int getStartInt() {
		if (start != null) {
			return Integer.parseInt(start);
		} else {
			return 0;
		}
	}
}
