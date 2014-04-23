package eu.archivesportaleurope.portal.search.common;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;

import eu.archivesportaleurope.portal.search.eaccpf.EacCpfSearchResult;
import eu.archivesportaleurope.portal.search.ead.list.EadSearchResult;

public class SolrDocumentListHolder implements Iterable<SearchResult> {

	private SolrDocumentList list;
	private Map<String, Map<String, List<String>>> highlightingMap;
	private boolean isEad;
	
	public SolrDocumentListHolder(){
		this.list = null;
		this.highlightingMap = null;
	}
	public SolrDocumentListHolder(QueryResponse response, boolean isEad){
		this.list = response.getResults();
		this.highlightingMap = response.getHighlighting();
		this.isEad = isEad;
	}
	@Override
	public Iterator<SearchResult> iterator() {
		return new SolrDocumentListIterator(list, highlightingMap, isEad);
	}

	public static class SolrDocumentListIterator implements Iterator<SearchResult>{

		private SolrDocumentList list;
		private int currentPosition = 0;
		private Map<String, Map<String, List<String>>> highlightingMap;
		private boolean isEad;
		public SolrDocumentListIterator(SolrDocumentList list, Map<String, Map<String, List<String>>> highlightingMap, boolean isEad){
			this.list = list;
			this.highlightingMap = highlightingMap;
			this.isEad = isEad;
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
			if (isEad){
				item =  new EadSearchResult(list.get(currentPosition), highlightingMap);
			}else {
				item =  new EacCpfSearchResult(list.get(currentPosition), highlightingMap);
			}
			currentPosition++;
			return item;
		}

		@Override
		public void remove() {
			
		}
		
	}
}
