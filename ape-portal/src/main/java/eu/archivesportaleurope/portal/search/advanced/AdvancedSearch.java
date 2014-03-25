package eu.archivesportaleurope.portal.search.advanced;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;
import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;
import eu.archivesportaleurope.portal.search.common.FacetType;

public class AdvancedSearch extends AbstractSearchForm{

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
	private String dao;
	private boolean advanced = true;


	private String country;
	private String ai;
	private String fond;

	private String roledao;
	private String type;
	private String level;
	private String dateType;


	private String selectedNodes;


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



	
}
