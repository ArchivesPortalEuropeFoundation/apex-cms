package eu.archivesportaleurope.portal.search.ead.tree;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.QueryResponse;

import eu.archivesportaleurope.portal.search.common.Results;

public class ContextResults extends Results {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7339553985314876214L;
	private List<TreeFacetValue> countries = new ArrayList<TreeFacetValue>();
	private String totalNumberOfResultsString;
	public List<TreeFacetValue> getCountries() {
		return countries;
	}

	public void setCountries(List<TreeFacetValue> countries) {
		this.countries = countries;
	}

	public void init (QueryResponse solrResponse, NumberFormat numberFormat){
		super.init(solrResponse);
		totalNumberOfResultsString = numberFormat.format(solrResponse.getResults().getNumFound());
	}

	public String getTotalNumberOfResultsString() {
		return totalNumberOfResultsString;
	}

	public void setTotalNumberOfResultsString(String totalNumberOfResultsString) {
		this.totalNumberOfResultsString = totalNumberOfResultsString;
	}

}
