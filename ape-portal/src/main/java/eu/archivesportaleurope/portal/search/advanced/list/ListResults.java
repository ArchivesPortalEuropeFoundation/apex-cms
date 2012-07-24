package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.Iterator;
import java.util.List;

import org.apache.solr.client.solrj.response.FacetField;

import eu.archivesportaleurope.portal.search.common.Results;

public class ListResults extends Results {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8610205871799887309L;
	private SolrDocumentListHolder items;
	private List<FacetField> facetFields;
	private List<FacetField> facetDates;
	private long totalNumberOfPages;
	private Integer pageSize;
	public Iterator<SearchResult> getItems() {
		return items.iterator();
	}
	public void setItems(SolrDocumentListHolder items) {
		this.items = items;
	}
	public List<FacetField> getFacetFields() {
		return facetFields;
	}
	public void setFacetFields(List<FacetField> facetFields) {
		this.facetFields = facetFields;
	}
	public List<FacetField> getFacetDates() {
		return facetDates;
	}
	public void setFacetDates(List<FacetField> facetDates) {
		this.facetDates = facetDates;
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
	
}
