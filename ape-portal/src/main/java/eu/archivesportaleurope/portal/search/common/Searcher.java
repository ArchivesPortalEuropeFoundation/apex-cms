package eu.archivesportaleurope.portal.search.common;

import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.common.params.FacetParams;

import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;

public final class Searcher {
	private static final String WHITESPACE = " ";
	private static final String OR = " OR ";
	//private static final String FACET_SORT_INDEX = "index";
	private static final String FACET_SORT_COUNT = "count";
	private static final String QUERY_TYPE_CONTEXT = "context";
	private static final String QUERY_TYPE_LIST = "list";
	private static final String COLON = ":";
	//private static final String WHITESPACE = " ";
	private final static SimpleDateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private final static Logger LOGGER = Logger.getLogger(Searcher.class);
	private CommonsHttpSolrServer solrServer;

	public Searcher(){
		try {
			solrServer = new CommonsHttpSolrServer(APEnetUtilities.getApePortalConfig().getSolrSearchUrl(), null);
		} catch (MalformedURLException e) {
			LOGGER.error("Unable to instantiate the solr client: " + e.getMessage());
		}
	}
	
	public TermsResponse getTerms(String term) throws SolrServerException{
		SolrQuery  query = new SolrQuery ();
		//query.setShowDebugInfo(true);
		query.setTermsPrefix(term.toLowerCase());
		query.setTermsLower(term.toLowerCase());
		query.setQueryType("/terms");

	    return solrServer.query(query, METHOD.POST).getTermsResponse();
	}
	public QueryResponse performNewSearchForListView(SolrQueryParameters solrQueryParameters, int rows, List<ListFacetSettings> facetSettings) throws SolrServerException, ParseException{
		return getListViewResults(solrQueryParameters, 0, rows,facetSettings, null, null, null, true);
	}
	public QueryResponse updateListView(SolrQueryParameters solrQueryParameters, int start, int rows, List<ListFacetSettings> facetSettings, String orderByField, String startDate, String endDate) throws SolrServerException, ParseException{
		return getListViewResults(solrQueryParameters, start, rows, facetSettings, orderByField, startDate, endDate, false);
	}
	private QueryResponse getListViewResults(SolrQueryParameters solrQueryParameters, int start, int rows, List<ListFacetSettings> facetSettingsList, String orderByField, String startDate, String endDate, boolean needSuggestions) throws SolrServerException, ParseException {
		SolrQuery query = new SolrQuery();
		query.setHighlight(true);
		for (ListFacetSettings facetSettings: facetSettingsList){
			FacetType facetType = facetSettings.getFacetType();
			if (!facetType.isDate()){
				query.addFacetField(facetType.getNameWithLabel());
				query.setParam("f."+ facetType.getName() +".facet.limit", facetSettings.getLimit() +"");
			}
			
		}
		buildDateRefinement(query,startDate, endDate, true);
		query.setStart(start);
		query.setRows(rows);
		//query.setFacetLimit(ListFacetSettings.DEFAULT_FACET_VALUE_LIMIT);
		if (orderByField != null && orderByField.length() > 0 && !"relevancy".equals(orderByField)) {
			query.addSortField(orderByField, ORDER.asc);
			if(orderByField.equals("startdate")){
				query.addSortField("enddate", ORDER.asc);
			}
		}
		return executeQuery(query, solrQueryParameters,QUERY_TYPE_LIST, needSuggestions);
		
	}
	public QueryResponse getMoreFacetFieldValues(SolrQueryParameters solrQueryParameters, String facetField, int start, String startDate, String endDate) throws SolrServerException, ParseException {
		SolrQuery query = new SolrQuery();
		query.setHighlight(true);
		query.addFacetField(facetField);
		buildDateRefinement(query,startDate, endDate, false);
		query.setStart(0);
		query.setRows(0);
		query.setFacetLimit(ListFacetSettings.DEFAULT_FACET_VALUE_LIMIT);
		query.setFacetMinCount(1);
		query.setParam("facet.offset", start +"");
		return executeQuery(query, solrQueryParameters,QUERY_TYPE_LIST, false);
		
	}
	
	private void buildDateRefinement(SolrQuery query, String startDate, String endDate, boolean searchResults) throws SolrServerException, ParseException {
		boolean facetStartDate = true;
		boolean facetEndDate = true;
		if (StringUtils.isNotBlank(startDate)){
			String[] splittedStartDate = startDate.split("_");
			String startDateString = splittedStartDate[0];
			String gapString = splittedStartDate[1];
			DateGap dateGap = DateGap.getGapById(gapString);
			if (dateGap != null){
				Date beginDate = SOLR_DATE_FORMAT.parse(startDateString);
				Calendar endDateCalendar = Calendar.getInstance();	
				endDateCalendar.setTime(beginDate);
				endDateCalendar.add(dateGap.getType(), dateGap.getSolrTimespan());
				String finalStartDateString = startDateString + "T00:00:00Z";
				String finalEndDateString = finalStartDateString + "+" + dateGap.previous().getName();
				query.addFilterQuery("startdate:[" + finalStartDateString+   " TO " + finalEndDateString + "]");
				if (searchResults && dateGap.next() != null){
					query.setParam("f.startdate.facet.date.start", finalStartDateString);
					query.setParam("f.startdate.facet.date.end", finalEndDateString);
					query.set("f.startdate.facet.date.gap", "+" + dateGap.getName());
				}else {
					facetStartDate = false;
				}
			}else {
				facetStartDate = false;
			}
		}else if (searchResults){
			query.setParam("f.startdate.facet.date.start", "0000-01-01T00:00:00Z");
			query.setParam("f.startdate.facet.date.end", "NOW");
			query.set("f.startdate.facet.date.gap", "+200YEARS");
		}else {
			facetStartDate = false;
		}
		if (StringUtils.isNotBlank(endDate)){
			String[] splittedStartDate = endDate.split("_");
			String startDateString = splittedStartDate[0];
			String gapString = splittedStartDate[1];
			DateGap dateGap = DateGap.getGapById(gapString);
			if (dateGap != null){
				Date beginDate = SOLR_DATE_FORMAT.parse(startDateString);
				Calendar endDateCalendar = Calendar.getInstance();	
				endDateCalendar.setTime(beginDate);
				endDateCalendar.add(dateGap.getType(), dateGap.getSolrTimespan());
				String finalStartDateString = startDateString + "T00:00:00Z";
				String finalEndDateString = finalStartDateString + "+" + dateGap.previous().getName();
				query.addFilterQuery("enddate:[" + finalStartDateString+   " TO " + finalEndDateString + "]");
				if (searchResults && dateGap.next() != null){
					query.setParam("f.enddate.facet.date.start", finalStartDateString);
					query.setParam("f.enddate.facet.date.end", finalEndDateString);
					query.set("f.enddate.facet.date.gap", "+" + dateGap.getName());
				}else {
					facetEndDate = false;
				}
			}else {
				facetEndDate = false;
			}
		}else  if (searchResults){
			query.setParam("f.enddate.facet.date.start", "0000-01-01T00:00:00Z");
			query.setParam("f.enddate.facet.date.end", "NOW");
			query.set("f.enddate.facet.date.gap", "+200YEARS");
		}else {
			facetEndDate = false;
		}
		if (facetStartDate && facetEndDate){
			query.setParam("facet.date", "startdate", "enddate");
		}else if (facetStartDate){
			query.setParam("facet.date", "startdate");
		}else if (facetEndDate){
			query.setParam("facet.date", "enddate");
		}
		if (facetStartDate || facetEndDate){
			query.setParam("facet.date.include", "lower");
		}
		
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
	private QueryResponse executeQuery(SolrQuery query, SolrQueryParameters solrQueryParameters, String queryType, boolean needSuggestions)
			throws SolrServerException {
		query.setQuery(escapeSolrCharacters(solrQueryParameters.getTerm()));
		
		if (solrQueryParameters.getAndParameters() != null) {
			for (Map.Entry<String, List<String>> criteria : solrQueryParameters.getAndParameters().entrySet()) {
				if (criteria.getValue() != null){	
					query.addFilterQuery(criteria.getKey() + COLON + convertToOrQuery(criteria.getValue()));
					
				}
			}
		}
		if (solrQueryParameters.getOrParameters() != null) {
			String orQuery = null;
			for (Map.Entry<String, List<String>> criteria : solrQueryParameters.getOrParameters().entrySet()) {
				if (criteria.getValue() != null){	
					if (orQuery == null){
						orQuery = criteria.getKey() + COLON + convertToOrQuery(criteria.getValue());
					}else {
						orQuery += OR + criteria.getKey() + COLON + convertToOrQuery(criteria.getValue());
					}
					
					
				}
			}
			if (orQuery != null){
				query.addFilterQuery(orQuery);
			}
		}
		String searchableField = null;
		for (SolrField field: solrQueryParameters.getSolrFields()){
			if (searchableField == null){
				searchableField = field.toString();
			}else {
				searchableField += WHITESPACE  + field.toString();
			}
		}
	
		if (searchableField != null){
			query.set("qf", searchableField);
			query.set("hl.fl", searchableField);
		}
		if (!solrQueryParameters.isMatchAllWords()){
			query.set("mm", "0%");
		}
		if (queryType != null){
			query.setQueryType(queryType);
		}
		if (needSuggestions && !(solrQueryParameters.getSolrFields().contains(SolrField.UNITID) || solrQueryParameters.getSolrFields().contains(SolrField.OTHERUNITID)) && StringUtils.isNotBlank(solrQueryParameters.getTerm())){
			query.set("spellcheck", "on");
		}
		return solrServer.query(query, METHOD.POST);
	}
	
	private static String escapeSolrCharacters(String term){
		if (StringUtils.isNotBlank(term)){
			term = term.replaceAll(" - ", " \"-\" " );
			term = term.replaceAll(" \\+ ", " \"+\" " );
		}
		return term;
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
		query.addSortField(SolrFields.ORDER_ID, ORDER.asc);
		return executeQuery(query, solrQueryParameters, QUERY_TYPE_CONTEXT, false);
	}




	
}
