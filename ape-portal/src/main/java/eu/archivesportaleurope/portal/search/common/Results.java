package eu.archivesportaleurope.portal.search.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

import eu.archivesportaleurope.portal.search.advanced.list.ListFacetContainer;

public class Results implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1960734220972318520L;
	private long totalNumberOfResults;
	private boolean showSuggestions;
	private String errorMessage;
	private SpellCheckResponse spellCheckResponse;
	

	public void init (QueryResponse solrResponse){
		totalNumberOfResults = solrResponse.getResults().getNumFound();
		spellCheckResponse = solrResponse.getSpellCheckResponse();
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
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public SpellCheckResponse getSpellCheckResponse() {
		return spellCheckResponse;
	}

	
}
