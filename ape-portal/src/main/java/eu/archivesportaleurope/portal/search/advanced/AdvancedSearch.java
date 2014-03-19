package eu.archivesportaleurope.portal.search.advanced;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;
import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;
import eu.archivesportaleurope.portal.search.common.FacetType;

public class AdvancedSearch extends AbstractSearchForm{
	public static final String LIST_SEPARATOR = ",";
	public static final String VIEW_HIERARCHY = "hierarchy";
	public static final String METHOD_OPTIONAL = "optional";
	public static final String MODE_NEW = "new";
	public static final String MODE_NEW_SEARCH = "new-search";
	public static final String MODE_UPDATE_SEARCH = "update-search";

	/**
	 * 
	 */
	private static final long serialVersionUID = -8762103831853635524L;
	private String mode = MODE_NEW;
	private String view;
	private String element = "0";
	private String typedocument = "";
	private String fromdate;
	private String todate;

	private String resultsperpage = "10";
	private String simpleSearchDao;
	private String dao;
	private boolean advanced = true;
	private String exactDateSearch;

	private String country;
	private String ai;
	private String fond;

	private String roledao;
	private String type;
	private String level;
	private String dateType;
	private String order;
	private String startdate;
	private String enddate;
	private String facetField;
	private String facetOffset;
	private String pageNumber = "1";
	private String keyPrefix;
	private String valueIsKey;
	private String hasId;
	private List<ListFacetSettings> facetSettingsList = FacetType.getDefaultListFacetSettings();
	private String selectedNodes;
	private String publishedFromDate;
	private String publishedToDate;
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}


	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}



	public String getSimpleSearchDao() {
		return simpleSearchDao;
	}

	public void setSimpleSearchDao(String simpleSearchDao) {
		this.simpleSearchDao = simpleSearchDao;
	}

	public String getDao() {
		return dao;
	}

	public void setDao(String dao) {
		this.dao = dao;
	}

	public String getCountry() {
		return country;
	}
	
	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public List<String> getCountryList(){
		if (StringUtils.isNotBlank(country)){
			return Arrays.asList(country.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getAiList(){
		if (StringUtils.isNotBlank(ai)){
			return Arrays.asList(ai.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getTypeList(){
		if (StringUtils.isNotBlank(type)){
			return Arrays.asList(type.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getDateTypeList(){
		if (StringUtils.isNotBlank(dateType)){
			return Arrays.asList(dateType.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getRoledaoList(){
		if (StringUtils.isNotBlank(roledao)){
			return Arrays.asList(roledao.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getDaoList(){
		if (StringUtils.isNotBlank(dao)){
			return Arrays.asList(dao.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getFondList(){
		if (StringUtils.isNotBlank(fond)){
			return Arrays.asList(fond.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getSelectedNodesList(){
		if (StringUtils.isNotBlank(selectedNodes)){
			return Arrays.asList(selectedNodes.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}
	public List<String> getLevelList(){
		if (StringUtils.isNotBlank(level)){
			return Arrays.asList(level.split(LIST_SEPARATOR));
		}else {
			return null;
		}
	}

	public String getSelectedNodes() {
		return selectedNodes;
	}

	public void setSelectedNodes(String selectedNodes) {
		this.selectedNodes = selectedNodes;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getAi() {
		return ai;
	}

	public void setAi(String ai) {
		this.ai = ai;
	}

	public String getFond() {
		return fond;
	}

	public void setFond(String fond) {
		this.fond = fond;
	}

	public String getRoledao() {
		return roledao;
	}

	public void setRoledao(String roledao) {
		this.roledao = roledao;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDateType() {
		return dateType;
	}

	public void setDateType(String dateType) {
		this.dateType = dateType;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
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

	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public String getTypedocument() {
		return typedocument;
	}

	public void setTypedocument(String typedocument) {
		this.typedocument = typedocument;
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

	public boolean isAdvanced() {
		return advanced;
	}

	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}



	public String getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
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
		for (ListFacetSettings facetSettings: facetSettingsList){
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
			facetSettingsList.clear();
			String[] temp = facetSettings.split(LIST_SEPARATOR);
			for (String tempItem: temp){
				facetSettingsList.add(new ListFacetSettings(tempItem));
			}
		}else {
			facetSettingsList = FacetType.getDefaultListFacetSettings();
		}
	}
	public List<ListFacetSettings> getFacetSettingsList(){
		return facetSettingsList;
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



	
}
