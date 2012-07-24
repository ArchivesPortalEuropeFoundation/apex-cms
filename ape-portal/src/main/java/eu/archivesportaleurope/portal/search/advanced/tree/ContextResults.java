package eu.archivesportaleurope.portal.search.advanced.tree;

import java.util.ArrayList;
import java.util.List;

import eu.archivesportaleurope.portal.search.common.Results;

public class ContextResults extends Results {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7339553985314876214L;
	private List<FacetValue> countries = new ArrayList<FacetValue>();
	public List<FacetValue> getCountries() {
		return countries;
	}
	public void setCountries(List<FacetValue> countries) {
		this.countries = countries;
	}
	

}
