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
	private String language;
	private String country;
	private String aiGroupsFacet;
	private String repositoryTypeFacet;
	private String repositoryType;
	private String onlyTitle;
	private String onlyPlace;
	private Map<String,String> repositoryTypeValues  = new LinkedHashMap<String,String>();


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



	public List<String> getLanguageList() {
		if (StringUtils.isNotBlank(language)) {
			return Arrays.asList(language.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}
	public List<String> getAiGroupsFacetList() {
		if (StringUtils.isNotBlank(aiGroupsFacet)) {
			return Arrays.asList(aiGroupsFacet.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}
	public List<String> getRepositoryTypeFacetList() {
		if (StringUtils.isNotBlank(repositoryTypeFacet)) {
			return Arrays.asList(repositoryTypeFacet.split(LIST_SEPARATOR));
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

	public String getAiGroupsFacet() {
		return aiGroupsFacet;
	}

	public String getRepositoryTypeFacet() {
		return repositoryTypeFacet;
	}

	public void setAiGroupsFacet(String aiGroupsFacet) {
		this.aiGroupsFacet = aiGroupsFacet;
	}

	public void setRepositoryTypeFacet(String repositoryTypeFacet) {
		this.repositoryTypeFacet = repositoryTypeFacet;
	}

	public String getRepositoryType() {
		return repositoryType;
	}

	public Map<String, String> getRepositoryTypeValues() {
		return repositoryTypeValues;
	}

	public void setRepositoryType(String repositoryType) {
		this.repositoryType = repositoryType;
	}

	public void setRepositoryTypeValues(Map<String, String> repositoryTypeValues) {
		this.repositoryTypeValues = repositoryTypeValues;
	}

	public String getOnlyTitle() {
		return onlyTitle;
	}

	public String getOnlyPlace() {
		return onlyPlace;
	}

	public void setOnlyTitle(String onlyTitle) {
		this.onlyTitle = onlyTitle;
	}

	public void setOnlyPlace(String onlyPlace) {
		this.onlyPlace = onlyPlace;
	}

}
