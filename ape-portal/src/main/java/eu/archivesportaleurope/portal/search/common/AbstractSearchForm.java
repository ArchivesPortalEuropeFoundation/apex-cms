package eu.archivesportaleurope.portal.search.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.search.ead.list.ListFacetSettings;

public abstract class AbstractSearchForm implements Serializable{


	/**
	 * 
	 */
	private static final long serialVersionUID = -3610004893627484103L;
	public static final String SEARCH_ALL_STRING = "*:*";
	public static final String LIST_SEPARATOR = ",";
	public static final String MODE_NEW = "new";
	public static final String MODE_NEW_SEARCH = "new-search";
	public static final String MODE_UPDATE_SEARCH = "update-search";
	private String term;
	private String method;
	
	private String fromdate;
	private String todate;
	private String startdate;
	private String enddate;
	private String resultsperpage = "10";
	private List<String> resultsperpageValues = new ArrayList<String>();
	private String exactDateSearch;
	private String order;
	private String pageNumber = "1";
	private String facetField;
	private String facetOffset;

	private String keyPrefix;
	private String valueIsKey;
	private String hasId;
	private List<ListFacetSettings> facetSettingsList;
	private String publishedFromDate;
	private String publishedToDate;
	private String mode = MODE_NEW;
	public AbstractSearchForm(){
		resultsperpageValues.add("10");
		resultsperpageValues.add("20");
		resultsperpageValues.add("30");
		resultsperpageValues.add("50");
		resultsperpageValues.add("100");
	}
	
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


	public List<String> getResultsperpageValues() {
		return resultsperpageValues;
	}

	protected abstract List<ListFacetSettings> getDefaultListFacetSettings();

	public String getFacetField() {
		return facetField;
	}

	public void setFacetField(String facetField) {
		this.facetField = facetField;
	}

	public String getFacetOffset() {
		return facetOffset;
	}

	public void setFacetOffset(String facetOffset) {
		this.facetOffset = facetOffset;
	}
	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getValueIsKey() {
		return valueIsKey;
	}

	public void setValueIsKey(String valueIsKey) {
		this.valueIsKey = valueIsKey;
	}

	public String getHasId() {
		return hasId;
	}

	public void setHasId(String hasId) {
		this.hasId = hasId;
	}


	public String getFacetSettings() {
		String result = null;
		for (ListFacetSettings facetSettings: getFacetSettingsList()){
			if (result == null){
				result = facetSettings.toString();
			}else {
				result += LIST_SEPARATOR  + facetSettings;
			}
		}
		return result;
	}

	public void setFacetSettings(String facetSettings) {
		if (StringUtils.isNotBlank(facetSettings)){
			getFacetSettingsList().clear();
			String[] temp = facetSettings.split(LIST_SEPARATOR);
			for (String tempItem: temp){
				getFacetSettingsList().add(new ListFacetSettings(tempItem));
			}
		}else {
			facetSettingsList = getDefaultListFacetSettings();
		}
	}
	public List<ListFacetSettings> getFacetSettingsList(){
		if (facetSettingsList == null){
			facetSettingsList = getDefaultListFacetSettings();
		}
		return facetSettingsList;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getPublishedFromDate() {
		return publishedFromDate;
	}

	public void setPublishedFromDate(String publishedFromDate) {
		this.publishedFromDate = publishedFromDate;
	}

	public String getPublishedToDate() {
		return publishedToDate;
	}

	public void setPublishedToDate(String publishedToDate) {
		this.publishedToDate = publishedToDate;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
