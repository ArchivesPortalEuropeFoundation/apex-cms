package eu.archivesportaleurope.portal.search.common;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.TermsResponse;

public abstract class AbstractSearcher {
	public static final String OR = " OR ";
	protected static final String WHITESPACE = " ";
	protected final static SimpleDateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private final static Logger LOGGER = Logger.getLogger(AbstractSearcher.class);
	private HttpSolrServer solrServer;
	protected final HttpSolrServer getSolrServer(){
		if (solrServer == null){
			try {
				solrServer = new HttpSolrServer(getSolrSearchUrl(), null);
				LOGGER.info("Successfully instantiate the solr client: " + getSolrSearchUrl());
			} catch (Exception e) {
				LOGGER.error("Unable to instantiate the solr client: " + e.getMessage());
			}			
		}
		return solrServer;
	}
	protected abstract String getSolrSearchUrl();
	
	public TermsResponse getTerms(String term) throws SolrServerException{
		SolrQuery  query = new SolrQuery ();
		//query.setShowDebugInfo(true);
		query.setTermsPrefix(term.toLowerCase());
		query.setTermsLower(term.toLowerCase());
		query.setRequestHandler("/terms");
		if (LOGGER.isDebugEnabled()){
			LOGGER.debug("Query(autocompletion): " +getSolrSearchUrl() + "/select?"+ query.toString());
		}
	    return getSolrServer().query(query, METHOD.POST).getTermsResponse();
	}
	public static String convertToOrQuery(List<String> list) {
		String result = null;
		if (list != null && list.size() > 0) {
			
			if (list.size() == 1) {
				result = list.get(0);
			} else {
				result = "(";
				for (int i = 0; i < list.size(); i++) {
					if (i == list.size() - 1) {
						result+=   list.get(i)  + ")";
					} else {
						result +=  list.get(i)  + OR;
					}
				}
			}
		}
		return result;
	}
	public static String escapeSolrCharacters(String term){
		if (StringUtils.isNotBlank(term)){
			term = term.replaceAll(" - ", " \"-\" " );
			term = term.replaceAll(" \\+ ", " \"+\" " );
		}
		return term;
	}
}
