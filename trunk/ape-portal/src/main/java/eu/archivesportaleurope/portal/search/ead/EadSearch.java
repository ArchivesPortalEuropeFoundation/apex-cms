package eu.archivesportaleurope.portal.search.ead;

import eu.apenet.commons.solr.facet.FacetType;
import eu.apenet.commons.solr.facet.ListFacetSettings;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;

public class EadSearch extends AbstractSearchForm{

	public static final String VIEW_HIERARCHY = "hierarchy";
	public static final String METHOD_OPTIONAL = "optional";


	/**
	 * 
	 */
	private static final long serialVersionUID = -8762103831853635524L;

	private String view;
	private String element = "0";
	private String typedocument = "";

	private String simpleSearchDao;
	private String simpleSearchTopic;
	private String dao;
	private boolean advanced = true;


	private String country;
	private String ai;
	private String topic;	
	private String fond;
	private String language;
	private String roledao;
	private String type;
	private String level;
	private String dateType;
	private boolean showOnlyResults = false;
	private String savedSearchDescription;
	private Long savedSearchId;

	private String selectedNodes;

	private Refinement selectedSimpleSearchTopic;

	public String getView() {
		return view;
	}

	public void setView(String view) {
		this.view = view;
	}



	public String getSimpleSearchTopic() {
		return simpleSearchTopic;
	}

	public void setSimpleSearchTopic(String simpleSearchTopic) {
		this.simpleSearchTopic = simpleSearchTopic;
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

	
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
	public List<String> getTopicList(){
		if (StringUtils.isNotBlank(topic)){
			return Arrays.asList(topic.split(LIST_SEPARATOR));
		}else {
			return null;
		}
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
	public List<String> getLanguageList(){
		if (StringUtils.isNotBlank(language)){
			return Arrays.asList(language.split(LIST_SEPARATOR));
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



	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public boolean isAdvanced() {
		return advanced;
	}

	public void setAdvanced(boolean advanced) {
		this.advanced = advanced;
	}


	@Override
	protected List<ListFacetSettings> getDefaultListFacetSettings() {
		return FacetType.getDefaultEadListFacetSettings();
	}

	public Refinement getSelectedSimpleSearchTopic() {
		return selectedSimpleSearchTopic;
	}

	public void setSelectedSimpleSearchTopic(Refinement selectedSimpleSearchTopic) {
		this.selectedSimpleSearchTopic = selectedSimpleSearchTopic;
	}
	public String getSelectedSimpleSearchTopicCssClass() {
		if (selectedSimpleSearchTopic == null){
			return "hidden";
		}else {
			return "";
		}
	}

	public boolean isShowOnlyResults() {
		return showOnlyResults;
	}

	public void setShowOnlyResults(boolean showOnlyResults) {
		this.showOnlyResults = showOnlyResults;
	}

	public String getSavedSearchDescription() {
		return savedSearchDescription;
	}

	public void setSavedSearchDescription(String savedSearchDescription) {
		this.savedSearchDescription = savedSearchDescription;
	}

	public Long getSavedSearchId() {
		return savedSearchId;
	}

	public void setSavedSearchId(Long savedSearchId) {
		this.savedSearchId = savedSearchId;
	}

	@Override
	public boolean isAdvancedSearch() {
		if (StringUtils.isNotBlank(simpleSearchTopic)){
			return true;
		}else if (StringUtils.isNotBlank(simpleSearchDao)){
			return true;
		}else if (StringUtils.isNotBlank(typedocument)){
			return true;
		}else if (!"0".equals(element)){
			return true;
		}else if (StringUtils.isNotBlank(getFromdate())){
			return true;
		}else if (StringUtils.isNotBlank(getTodate())){
			return true;
		}else if (StringUtils.isNotBlank(getExactDateSearch())){
			return true;
		}else if (StringUtils.isNotBlank(selectedNodes)){
			return true;
		}
		return false;
	}


	
}
