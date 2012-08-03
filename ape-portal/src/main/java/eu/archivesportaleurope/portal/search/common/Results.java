package eu.archivesportaleurope.portal.search.common;

import java.io.Serializable;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

public class Results implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1960734220972318520L;
	private long totalNumberOfResults;
	private boolean showSuggestions;
	private List<FacetField> facetFields;
	private List<FacetField> facetDates;
	private String errorMessage;
	private SpellCheckResponse spellCheckResponse;
	
	public void setSolrResponse(QueryResponse solrResponse) {
		totalNumberOfResults = solrResponse.getResults().getNumFound();
		spellCheckResponse = solrResponse.getSpellCheckResponse();
		facetFields = solrResponse.getFacetFields();
		facetDates = solrResponse.getFacetDates();
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
	public List<FacetField> getFacetFields() {
		return facetFields;
	}
	public List<FacetField> getFacetDates() {
		return facetDates;
	}
	
}
