package eu.archivesportaleurope.portal.search.common;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import eu.archivesportaleurope.portal.search.eaccpf.EacCpfSearchResult;
import eu.archivesportaleurope.portal.search.ead.list.EadSearchResult;
import eu.archivesportaleurope.portal.search.eag.EagSearchResult;

public class SolrDocumentListHolder implements Iterable<SearchResult> {

	private SolrDocumentList list;
	private Map<String, Map<String, List<String>>> highlightingMap;
	private Class<? extends SearchResult> searchResultClass;
	private DatabaseCacher databaseCacher;
	
	public SolrDocumentListHolder(){
		this.list = null;
		this.highlightingMap = null;
	}
	public SolrDocumentListHolder(QueryResponse response, Class<? extends SearchResult> searchResultClass){
		this.list = response.getResults();
		this.highlightingMap = response.getHighlighting();
		this.searchResultClass = searchResultClass;
	}
	public SolrDocumentListHolder(QueryResponse response, Class<? extends SearchResult> searchResultClass, DatabaseCacher databaseCacher){
		this.list = response.getResults();
		this.highlightingMap = response.getHighlighting();
		this.searchResultClass = searchResultClass;
		this.databaseCacher = databaseCacher;
	}
	@Override
	public Iterator<SearchResult> iterator() {
		return new SolrDocumentListIterator(list, highlightingMap, searchResultClass,databaseCacher);
	}

	public static class SolrDocumentListIterator implements Iterator<SearchResult>{

		private SolrDocumentList list;
		private int currentPosition = 0;
		private Map<String, Map<String, List<String>>> highlightingMap;
		private DatabaseCacher databaseCacher;
		private Class<? extends SearchResult> searchResultClass;
		public SolrDocumentListIterator(SolrDocumentList list, Map<String, Map<String, List<String>>> highlightingMap, Class<? extends SearchResult> searchResultClass, DatabaseCacher databaseCacher){
			this.list = list;
			this.highlightingMap = highlightingMap;
			this.searchResultClass = searchResultClass;
			this.databaseCacher = databaseCacher;
		}		
		@Override
		public boolean hasNext() {
			if (list == null){
				return false;
			}
			return currentPosition < list.size();
		}

		@Override
		public SearchResult next() {
			if (list == null){
				return null;
			}
			SearchResult item = null;
			if (EadSearchResult.class.equals(searchResultClass)){
				item =  new EadSearchResult(list.get(currentPosition), highlightingMap, databaseCacher);
			}else if (EacCpfSearchResult.class.equals(searchResultClass)){
				item =  new EacCpfSearchResult(list.get(currentPosition), highlightingMap);
			}else if (EagSearchResult.class.equals(searchResultClass)){
				item =  new EagSearchResult(list.get(currentPosition), highlightingMap);
			}
			currentPosition++;
			return item;
		}

		@Override
		public void remove() {
			
		}
		
	}
}
