package eu.archivesportaleurope.portal.search.ead.tree;

import eu.archivesportaleurope.portal.search.ead.EadSearch;

public class TreeEadSearch extends EadSearch {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8551327027173370522L;
	private String searchType;
	private String start;
	private String parentId;
	private String level;



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

	public String getLevelName() {
		return level;
	}

	public void setLevelName(String level) {
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
