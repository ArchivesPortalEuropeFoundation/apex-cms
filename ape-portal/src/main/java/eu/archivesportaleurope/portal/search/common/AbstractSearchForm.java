package eu.archivesportaleurope.portal.search.common;

import java.io.Serializable;

public class AbstractSearchForm implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3610004893627484103L;
	public static final String SEARCH_ALL_STRING = "*:*";
	
	private String term;
	private String method;
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	public String getTermWords() {
		return term;
	}
	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}
	public boolean matchAllWords() {
		// return !"optional".equals(method);
		if (this.method != null && !this.method.isEmpty())
			return !method.contains("optional");
		else
			return true;
	}
}
