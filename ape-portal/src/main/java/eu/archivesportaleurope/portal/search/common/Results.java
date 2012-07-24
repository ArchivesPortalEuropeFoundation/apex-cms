package eu.archivesportaleurope.portal.search.common;

import java.io.Serializable;

import org.apache.solr.client.solrj.response.QueryResponse;

public class Results implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1960734220972318520L;
	private QueryResponse solrResponse;
	private long totalNumberOfResults;
	private boolean showSuggestions;
	private String errorMessage;
	
	public QueryResponse getSolrResponse() {
		return solrResponse;
	}
	public void setSolrResponse(QueryResponse solrResponse) {
		this.solrResponse = solrResponse;
		totalNumberOfResults = solrResponse.getResults().getNumFound();
	}
	public long getTotalNumberOfResults() {
		return totalNumberOfResults;
	}
	public boolean isShowSuggestions() {
		return showSuggestions;
	}
	public void setShowSuggestions(boolean showSuggestions) {
		this.showSuggestions = showSuggestions;
	}
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
