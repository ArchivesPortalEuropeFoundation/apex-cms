package eu.archivesportaleurope.portal.eaccpf.search;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;
import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;
import eu.archivesportaleurope.portal.search.common.FacetType;

public class EacCpfSearch extends AbstractSearchForm {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1707315274413272934L;


	private String element;
	private String entityType;
	private String nameType;
	private String placesFacet;
	private String occupationFacet;

	private String country;
	private String ai;

	private String dateType;

	public String getCountry() {
		return country;
	}

	// public List<String> getCountryList(){
	// if (StringUtils.isNotBlank(country)){
	// return Arrays.asList(country.split(LIST_SEPARATOR));
	// }else {
	// return null;
	// }
	// }
	// public List<String> getAiList(){
	// if (StringUtils.isNotBlank(ai)){
	// return Arrays.asList(ai.split(LIST_SEPARATOR));
	// }else {
	// return null;
	// }
	// }
	// public List<String> getDateTypeList(){
	// if (StringUtils.isNotBlank(dateType)){
	// return Arrays.asList(dateType.split(LIST_SEPARATOR));
	// }else {
	// return null;
	// }
	// }
	public List<String> getPlacesFacetList() {
		if (StringUtils.isNotBlank(placesFacet)) {
			return Arrays.asList(placesFacet.split(LIST_SEPARATOR));
		} else {
			return null;
		}
	}
	public List<String> getOccupationFacetList() {
		if (StringUtils.isNotBlank(occupationFacet)) {
			return Arrays.asList(occupationFacet.split(LIST_SEPARATOR));
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

	public String getOccupationFacet() {
		return occupationFacet;
	}

	public void setOccupationFacet(String occupationFacet) {
		this.occupationFacet = occupationFacet;
	}



}
