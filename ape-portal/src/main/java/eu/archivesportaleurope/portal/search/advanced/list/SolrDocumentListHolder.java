package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import eu.archivesportaleurope.portal.search.common.DatabaseCacher;

public class SolrDocumentListHolder implements Iterable<SearchResult> {

	private SolrDocumentList list;
	private Map<String, Map<String, List<String>>> highlightingMap;
	private DatabaseCacher databaseCacher;
	public SolrDocumentListHolder(){
		this.list = null;
		this.highlightingMap = null;
	}
	public SolrDocumentListHolder(QueryResponse response, DatabaseCacher databaseCacher){
		this.list = response.getResults();
		this.highlightingMap = response.getHighlighting();
		this.databaseCacher = databaseCacher;
	}
	@Override
	public Iterator<SearchResult> iterator() {
		return new SolrDocumentListIterator(list, highlightingMap, databaseCacher);
	}

	public static class SolrDocumentListIterator implements Iterator<SearchResult>{

		private SolrDocumentList list;
		private int currentPosition = 0;
		private Map<String, Map<String, List<String>>> highlightingMap;
		private DatabaseCacher databaseCacher;
		
		public SolrDocumentListIterator(SolrDocumentList list, Map<String, Map<String, List<String>>> highlightingMap, DatabaseCacher databaseCacher){
			this.list = list;
			this.highlightingMap = highlightingMap;
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
			SearchResult item =  new SearchResult(list.get(currentPosition), highlightingMap, databaseCacher);
			currentPosition++;
			return item;
		}

		@Override
		public void remove() {
			
		}
		
	}
}
