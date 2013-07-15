package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import eu.apenet.commons.ResourceBundleSource;
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.Results;

public class ListResults extends Results {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8610205871799887309L;
	private SolrDocumentListHolder items;
	private long totalNumberOfPages;
	private Integer pageSize;
	private List<ListFacetContainer> facetContainers = new ArrayList<ListFacetContainer>();

	public Iterator<SearchResult> getItems() {
		return items.iterator();
	}

	public void setItems(SolrDocumentListHolder items) {
		this.items = items;
	}

	public long getTotalNumberOfPages() {
		return totalNumberOfPages;
	}

	public void setTotalNumberOfPages(long totalNumberOfPages) {
		this.totalNumberOfPages = totalNumberOfPages;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public void init (QueryResponse solrResponse, List<ListFacetSettings> facetSettingsList, AdvancedSearch advancedSearch,ResourceBundleSource resourceBundleSource){
		super.init(solrResponse);
		BeanWrapper advancedSearchBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(advancedSearch);
		for (ListFacetSettings facetSettings: facetSettingsList){
			FacetType facetType = facetSettings.getFacetType();
			String name = facetType.getName();
			FacetField facetField = null;
			if (facetType.isDate()){
				facetField = solrResponse.getFacetDate(name);
			}else {
				facetField = solrResponse.getFacetField(name);
			}
			if (facetField != null && facetField.getValueCount() > 0){
				ListFacetContainer facetContainer = null;
				
				if (facetSettings.getFacetType().isMultiSelect()){
					@SuppressWarnings("unchecked")
					List<String> selectedItems = (List<String>) advancedSearchBeanWrapper.getPropertyValue(name + "List");
					facetContainer = new ListFacetContainer(facetField, facetSettings, selectedItems,resourceBundleSource);
				}else {
					String selectedItem = (String) advancedSearchBeanWrapper.getPropertyValue(name);
					List<String> selectedItems = new ArrayList<String>();
					selectedItems.add(selectedItem);
					facetContainer = new ListFacetContainer(facetField, facetSettings,selectedItems, resourceBundleSource );
				}
				if (facetContainer.getValues().size() > 1){
					facetContainers.add(facetContainer);
				}
			}
		}
		
	}


	public List<ListFacetContainer> getFacetContainers() {
		return facetContainers;
	}


}
