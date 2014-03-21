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
	
	private String fromdate;
	private String todate;

	private String resultsperpage = "10";
	private String exactDateSearch;
	private String pageNumber = "1";
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
	public String getFromdate() {
		return fromdate;
	}

	public void setFromdate(String fromdate) {
		this.fromdate = fromdate;
	}

	public String getTodate() {
		return todate;
	}

	public void setTodate(String todate) {
		this.todate = todate;
	}

	public String getResultsperpage() {
		return resultsperpage;
	}

	public void setResultsperpage(String resultsperpage) {
		this.resultsperpage = resultsperpage;
	}
	
	public String getExactDateSearch() {
		return exactDateSearch;
	}

	public void setExactDateSearch(String exactDateSearch) {
		this.exactDateSearch = exactDateSearch;
	}
	public boolean hasExactDateSearch(){
		return Boolean.parseBoolean(exactDateSearch);
	}
	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
}
