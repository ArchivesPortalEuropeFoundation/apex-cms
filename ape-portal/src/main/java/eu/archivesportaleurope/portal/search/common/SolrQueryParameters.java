package eu.archivesportaleurope.portal.search.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.apenet.commons.solr.eads.EadSolrField;

public final class SolrQueryParameters {
	private String term;
	private boolean matchAllWords;
	private Map<String, List<String>> andParameters = new HashMap<String, List<String>>();
	private Map<String, List<String>> orParameters = new HashMap<String, List<String>>();
	private List<EadSolrField> solrFields = new ArrayList<EadSolrField>();
	public Map<String, List<String>> getAndParameters() {
		return andParameters;
	}
	public void setAndParameters(Map<String, List<String>> andParameters) {
		this.andParameters = andParameters;
	}
	public Map<String, List<String>> getOrParameters() {
		return orParameters;
	}
	public void setOrParameters(Map<String, List<String>> orParameters) {
		this.orParameters = orParameters;
	}
	public List<EadSolrField> getSolrFields() {
		return solrFields;
	}
	public void setSolrFields(List<EadSolrField> solrFields) {
		this.solrFields = solrFields;
	}
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public boolean isMatchAllWords() {
		return matchAllWords;
	}
	public void setMatchAllWords(boolean matchAllWords) {
		this.matchAllWords = matchAllWords;
	}

	
	
}
