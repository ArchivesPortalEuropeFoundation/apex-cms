package eu.archivesportaleurope.portal.search.saved;

import java.io.Serializable;
import java.util.Date;

import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;

public class SavedSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1125380681178677726L;
	private String id;
	private String description;
	private String searchTerm;
	private Date modifiedDate;
	private boolean containsSimpleSearchOptions;
	private boolean containsAdvancedSearchOptions;	
	private boolean containsAlSearchOptions;
	private boolean containsRefinements;
	private boolean publicSearch;	
	private boolean template;	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public String getSearchTerm() {
		return searchTerm;
	}
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;
	}
	public boolean isContainsSimpleSearchOptions() {
		return containsSimpleSearchOptions;
	}
	public void setContainsSimpleSearchOptions(boolean containsSimpleSearchOptions) {
		this.containsSimpleSearchOptions = containsSimpleSearchOptions;
	}
	public boolean isContainsAdvancedSearchOptions() {
		return containsAdvancedSearchOptions;
	}
	public void setContainsAdvancedSearchOptions(boolean containsAdvancedSearchOptions) {
		this.containsAdvancedSearchOptions = containsAdvancedSearchOptions;
	}
	public boolean isContainsAlSearchOptions() {
		return containsAlSearchOptions;
	}
	public void setContainsAlSearchOptions(boolean containsAlSearchOptions) {
		this.containsAlSearchOptions = containsAlSearchOptions;
	}
	public boolean isSearchAllSearch(){
		return AdvancedSearch.SEARCH_ALL_STRING.equals(searchTerm);
	}
	
	public boolean isPublicSearch() {
		return publicSearch;
	}
	public void setPublicSearch(boolean publicSearch) {
		this.publicSearch = publicSearch;
	}
	public boolean isTemplate() {
		return template;
	}
	public void setTemplate(boolean template) {
		this.template = template;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public boolean isContainsRefinements() {
		return containsRefinements;
	}
	public void setContainsRefinements(boolean containsRefinements) {
		this.containsRefinements = containsRefinements;
	}
	
}
