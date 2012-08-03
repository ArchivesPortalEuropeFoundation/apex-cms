package eu.archivesportaleurope.portal.search.advanced.tree;

import java.util.ArrayList;
import java.util.List;

import eu.archivesportaleurope.portal.search.common.Results;

public class ContextResults extends Results {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7339553985314876214L;
	private List<TreeFacetValue> countries = new ArrayList<TreeFacetValue>();

	public List<TreeFacetValue> getCountries() {
		return countries;
	}

	public void setCountries(List<TreeFacetValue> countries) {
		this.countries = countries;
	}

}
