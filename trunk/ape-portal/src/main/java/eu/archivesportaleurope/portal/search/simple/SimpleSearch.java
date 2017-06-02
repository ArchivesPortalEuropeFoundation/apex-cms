package eu.archivesportaleurope.portal.search.simple;

import java.io.Serializable;
import java.util.List;

public class SimpleSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8762103831853635524L;
	private String term;
	private String view;
	private String method;
	private String resultsperpage = "15";
	private String pageNumber = "1";
	private String dao;
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public String getView() {
		return view;
	}
	public void setView(String view) {
		this.view = view;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public String getResultsperpage() {
		return resultsperpage;
	}
	public void setResultsperpage(String resultsperpage) {
		this.resultsperpage = resultsperpage;
	}
	public String getDao() {
		return dao;
	}
	public void setDao(String dao) {
		this.dao = dao;
	}
	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

}
