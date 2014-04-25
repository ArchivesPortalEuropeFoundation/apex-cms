package eu.archivesportaleurope.portal.search.eag;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.ead.list.ListFacetSettings;

public class EagSearch extends AbstractSearchForm {
	public static final String NOSELECTION = "all";
	/**
	 * 
	 */
	private static final long serialVersionUID = 1707315274413272934L;
	private String element = NOSELECTION;
	private Map<String,String> elementValues  = new LinkedHashMap<String,String>();
	private String entityType;
	private Map<String,String> entityTypeValues  = new LinkedHashMap<String,String>();
	private String nameType;
	private String placesFacet;
	private String occupationsFacet;
	private String functionsFacet;
	private String mandatesFacet;
	private String entityTypeFacet;
	private String language;
	private String country;
	private String ai;

	private String dateType;

	public String getCountry() {
		return country;
	}

	public List<String> getCountryList() {
		if (StringUtils.isNotBlank(country)) {
			return Arrays.asList(country.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}

	public List<String> getAiList() {
		if (StringUtils.isNotBlank(ai)) {
			return Arrays.asList(ai.split(LIST_SEPARATOR));
		} else {
			return null;
		}

	}

	public List<String> getDateTypeList() {
		if (StringUtils.isNotBlank(dateType)) {
			return Arrays.asList(dateType.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}

	public List<String> getPlacesFacetList() {
		if (StringUtils.isNotBlank(placesFacet)) {
			return Arrays.asList(placesFacet.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}

	public List<String> getOccupationsFacetList() {
		if (StringUtils.isNotBlank(occupationsFacet)) {
			return Arrays.asList(occupationsFacet.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}
	public List<String> getMandatesFacetList() {
		if (StringUtils.isNotBlank(mandatesFacet)) {
			return Arrays.asList(mandatesFacet.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}
	public List<String> getFunctionsFacetList() {
		if (StringUtils.isNotBlank(functionsFacet)) {
			return Arrays.asList(functionsFacet.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}
	public List<String> getEntityTypeFacetList() {
		if (StringUtils.isNotBlank(entityTypeFacet)) {
			return Arrays.asList(entityTypeFacet.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}
	public List<String> getLanguageList() {
		if (StringUtils.isNotBlank(language)) {
			return Arrays.asList(language.split(LIST_SEPARATOR));
		} else {
			return null;
		}
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

	public String getEntityType() {
		return entityType;
	}

	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}

	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameType) {
		this.nameType = nameType;
	}

	@Override
	protected List<ListFacetSettings> getDefaultListFacetSettings() {
		return FacetType.getDefaultEacCPfListFacetSettings();
	}


	public String getPlacesFacet() {
		return placesFacet;
	}

	public void setPlacesFacet(String placesFacet) {
		this.placesFacet = placesFacet;
	}



	public String getOccupationsFacet() {
		return occupationsFacet;
	}

	public void setOccupationsFacet(String occupationsFacet) {
		this.occupationsFacet = occupationsFacet;
	}

	public String getFunctionsFacet() {
		return functionsFacet;
	}

	public void setFunctionsFacet(String functionsFacet) {
		this.functionsFacet = functionsFacet;
	}

	public String getMandatesFacet() {
		return mandatesFacet;
	}

	public void setMandatesFacet(String mandatesFacet) {
		this.mandatesFacet = mandatesFacet;
	}

	public String getEntityTypeFacet() {
		return entityTypeFacet;
	}

	public void setEntityTypeFacet(String entityTypeFacet) {
		this.entityTypeFacet = entityTypeFacet;
	}

	public Map<String, String> getElementValues() {
		return elementValues;
	}

	public void setElementValues(Map<String, String> elementValues) {
		this.elementValues = elementValues;
	}

	public Map<String, String> getEntityTypeValues() {
		return entityTypeValues;
	}

	public void setEntityTypeValues(Map<String, String> entityTypeValues) {
		this.entityTypeValues = entityTypeValues;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
