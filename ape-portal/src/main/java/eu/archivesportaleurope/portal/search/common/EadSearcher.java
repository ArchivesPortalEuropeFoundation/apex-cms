package eu.archivesportaleurope.portal.search.common;


import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.FacetParams;

import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.utils.APEnetUtilities;

public final class EadSearcher extends AbstractSearcher {

	//private static final String FACET_SORT_INDEX = "index";
	private static final String FACET_SORT_COUNT = "count";
	private static final String QUERY_TYPE_CONTEXT = "context";


	//private static final String WHITESPACE = " ";

	private final static Logger LOGGER = Logger.getLogger(EadSearcher.class);

	

	@Override
	protected String getSolrSearchUrl() {
		return APEnetUtilities.getApePortalConfig().getBaseSolrSearchUrl() + "/eads";
	}









	

	
	public QueryResponse performNewSearchForContextView(SolrQueryParameters solrQueryParameters) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.addFacetField(SolrFields.COUNTRY);
		query.setFacetSort(FACET_SORT_COUNT);
		query.setFacetLimit(-1);
		query.setFacetMinCount(1);
		query.setRows(0);
		query.setHighlight(false);
		return executeQuery(query, solrQueryParameters,  QUERY_TYPE_CONTEXT, true);
	}

	public FacetField getAis(SolrQueryParameters solrQueryParameters, String parentId,int level, int start, int maxNumber) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		if (parentId != null){
			query.addFilterQuery(SolrFields.AI_DYNAMIC_ID + level+SolrFields.DYNAMIC_STRING_SUFFIX + COLON +parentId);
		}
		query.addFacetField(SolrFields.AI_DYNAMIC + (level+1)+SolrFields.DYNAMIC_STRING_SUFFIX);
		query.setFacetSort(FacetParams.FACET_SORT_INDEX);
		query.setParam("facet.offset", start + "");
		query.setFacetSort(FACET_SORT_COUNT);
		query.setFacetMinCount(1);
		query.setFacetLimit(maxNumber);
		//query.setFacetLimit(-1);
		query.setRows(0);
		query.setHighlight(false);
		QueryResponse rsp = executeQuery(query, solrQueryParameters, QUERY_TYPE_CONTEXT, false);
		return rsp.getFacetFields().get(0);
	}
	public FacetField getFonds(SolrQueryParameters solrQueryParameters, String facetField, int start, int maxNumber) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.addFacetField(facetField);
		query.setFacetSort(FacetParams.FACET_SORT_COUNT);
		query.setParam("facet.offset", start + "");
		query.setFacetMinCount(1);
		query.setFacetLimit(maxNumber);
		//query.setFacetLimit(-1);
		query.setRows(0);
		query.setHighlight(false);
		QueryResponse rsp = executeQuery(query, solrQueryParameters, QUERY_TYPE_CONTEXT, false);
		return rsp.getFacetFields().get(0);	}
	
	public FacetField getLevels(SolrQueryParameters solrQueryParameters, String prefixFondType,String parentId,int level, int start, int maxNumber) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.addFilterQuery(prefixFondType+ SolrFields.DYNAMIC_ID + level+SolrFields.DYNAMIC_STRING_SUFFIX + COLON +parentId);
		query.addFacetField(prefixFondType + (level+1)+SolrFields.DYNAMIC_STRING_SUFFIX);
		query.setFacetSort(FacetParams.FACET_SORT_INDEX);
		query.setParam("facet.offset", start + "");
		query.setFacetMinCount(1);
		query.setFacetLimit(maxNumber);
		//query.setFacetLimit(-1);
		query.setRows(0);
		query.setHighlight(false);
		QueryResponse rsp = executeQuery(query, solrQueryParameters, QUERY_TYPE_CONTEXT,false);
		return rsp.getFacetFields().get(0);
	}
	public QueryResponse getResultsInLevels(SolrQueryParameters solrQueryParameters, String parentId, int start, int maxNumberOfRows) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		query.setHighlight(true);
		//query.setFields(SolrFields.TITLE, SolrFields.ID, "orderId_i");
		query.addFilterQuery(SolrFields.PARENT_ID + COLON + parentId );
		query.setRows(maxNumberOfRows);
		query.setStart(start);
		query.addSort(SolrFields.ORDER_ID, ORDER.asc);
		return executeQuery(query, solrQueryParameters, QUERY_TYPE_CONTEXT, false);
	}




	
}
