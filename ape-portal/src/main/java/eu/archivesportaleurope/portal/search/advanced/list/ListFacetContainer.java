package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;

import eu.apenet.commons.ResourceBundleSource;
import eu.archivesportaleurope.portal.search.common.FacetValue;

public class ListFacetContainer {
	private List<FacetValue> values = new ArrayList<FacetValue>();
	private ListFacetSettings facetSettings;
	public ListFacetContainer(FacetField facetField, ListFacetSettings facetSettings, List<String> selectedItems,ResourceBundleSource resourceBundleSource) {
		super();
		//this.facetField = facetField;
		this.facetSettings = facetSettings;
		for (Count count: facetField.getValues()){
			values.add(new FacetValue(count, facetSettings.getFacet(), selectedItems,resourceBundleSource));
		}
	}
	public ListFacetSettings getFacetSettings() {
		return facetSettings;
	}
	public String getName(){
		return facetSettings.getFacet().getName();
	}
	public boolean isMultiSelect(){
		return facetSettings.getFacet().isMultiSelect();
	}
	public int getLimit(){
		return facetSettings.getLimit();
	}
	public List<FacetValue> getValues() {
		return values;
	}
	

	
}
