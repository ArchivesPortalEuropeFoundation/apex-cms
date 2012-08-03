package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.Iterator;

import eu.archivesportaleurope.portal.search.common.Results;

public class ListResults extends Results {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8610205871799887309L;
	private SolrDocumentListHolder items;
	private long totalNumberOfPages;
	private Integer pageSize;
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
	
}
