package eu.archivesportaleurope.portal.search.common;

import java.io.Serializable;

public class AbstractSearchForm implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3610004893627484103L;
	public static final String SEARCH_ALL_STRING = "*:*";
	
	private String term;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
}
