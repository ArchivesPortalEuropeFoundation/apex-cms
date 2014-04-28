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
	private String nameType;
	private String placesFacet;
	private String language;
	private String country;


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



	public List<String> getPlacesFacetList() {
		if (StringUtils.isNotBlank(placesFacet)) {
			return Arrays.asList(placesFacet.split(LIST_SEPARATOR));
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


	public String getElement() {
		return element;
	}

	public void setElement(String element) {
		this.element = element;
	}


	public String getNameType() {
		return nameType;
	}

	public void setNameType(String nameType) {
		this.nameType = nameType;
	}

	@Override
	protected List<ListFacetSettings> getDefaultListFacetSettings() {
		return FacetType.getDefaultEagListFacetSettings();
	}


	public String getPlacesFacet() {
		return placesFacet;
	}

	public void setPlacesFacet(String placesFacet) {
		this.placesFacet = placesFacet;
	}




	public Map<String, String> getElementValues() {
		return elementValues;
	}

	public void setElementValues(Map<String, String> elementValues) {
		this.elementValues = elementValues;
	}



	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

}
