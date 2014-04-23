package eu.archivesportaleurope.portal.search.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse;

import eu.archivesportaleurope.portal.search.ead.list.ListFacetContainer;

public class Results implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1960734220972318520L;
	private long eadNumberOfResults = 0;
	private long eagNumberOfResults = 0;
	private long eacCpfNumberOfResults = 0;
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
	public long getEadNumberOfResults() {
		return eadNumberOfResults;
	}
	public void setEadNumberOfResults(long eadNumberOfResults) {
		this.eadNumberOfResults = eadNumberOfResults;
	}
	public long getEagNumberOfResults() {
		return eagNumberOfResults;
	}
	public void setEagNumberOfResults(long eagNumberOfResults) {
		this.eagNumberOfResults = eagNumberOfResults;
	}
	public long getEacCpfNumberOfResults() {
		return eacCpfNumberOfResults;
	}
	public void setEacCpfNumberOfResults(long eacCpfNumberOfResults) {
		this.eacCpfNumberOfResults = eacCpfNumberOfResults;
	}

	public String getEagNumberOfResultsClass(){
		return getCssClass(eagNumberOfResults);		
	}
	public String getEacCpfNumberOfResultsClass(){
		return getCssClass(eacCpfNumberOfResults);
	}
	public String getEadNumberOfResultsClass(){
		return getCssClass(eadNumberOfResults);
	}
	private String getCssClass(long number){
		if (number == 0){
			return "disabled";
		}
		return "";
	}
}
