package eu.archivesportaleurope.portal.search.ead.list;

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
		this.facetSettings = facetSettings;
		boolean multiSelect = facetSettings.getFacetType().isMultiSelect();
		for (Count count: facetField.getValues()){
			// Display refinement only if it is multi select or if the number of result is higher than 0
			if (multiSelect || count.getCount() > 0){
				values.add(new FacetValue(facetField, count, facetSettings.getFacetType(), selectedItems,resourceBundleSource));
			}
		}
	}
	public ListFacetSettings getFacetSettings() {
		return facetSettings;
	}
	public String getName(){
		return facetSettings.getFacetType().getName();
	}
	public boolean isMultiSelect(){
		return facetSettings.getFacetType().isMultiSelect();
	}
	public int getLimit(){
		return facetSettings.getLimit();
	}
	public boolean isExpanded(){
		return facetSettings.isExpanded();
	}
	public List<FacetValue> getValues() {
		return values;
	}
	

	
}
