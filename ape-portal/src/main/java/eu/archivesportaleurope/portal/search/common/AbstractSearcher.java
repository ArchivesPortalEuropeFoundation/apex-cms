package eu.archivesportaleurope.portal.search.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.TermsResponse;

import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.email.EmailSender;
import eu.archivesportaleurope.portal.search.ead.list.ListFacetSettings;

public abstract class AbstractSearcher {
	public static final String OR = " OR ";
	protected static final String WHITESPACE = " ";
	// private static final String ESCAPING_CHARACTER= "\\\\";
	private static final Pattern NORMAL_TERM_PATTERN = Pattern.compile("[\\p{L}\\p{Digit}\\s]+");
	private static final String QUERY_TYPE_LIST = "list";
	protected static final String COLON = ":";
	protected final static SimpleDateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private final static Logger LOGGER = Logger.getLogger(AbstractSearcher.class);
	private final static Integer TIME_ALLOWED = PropertiesUtil.getInt(PropertiesKeys.APE_MAX_SOLR_QUERY_TIME);
	private final static Integer TIME_ALLOWED_TREE = PropertiesUtil.getInt(PropertiesKeys.APE_MAX_SOLR_QUERY_TREE_TIME);
	private final static Integer HTTP_TIMEOUT = PropertiesUtil.getInt(PropertiesKeys.APE_SOLR_HTTP_TIMEOUT);
	private final static Long RESEND_EMAIL_TIME = PropertiesUtil
			.getLong(PropertiesKeys.APE_SOLR_RESEND_EMAIL_TIME_WAIT);
	private static Long lastTimeEmailSend = 0l;
	private HttpSolrServer solrServer;

	protected final HttpSolrServer getSolrServer() {
		if (solrServer == null) {
			try {
				solrServer = new HttpSolrServer(getSolrSearchUrl(), null);
				solrServer.setConnectionTimeout(HTTP_TIMEOUT);
				solrServer.setSoTimeout(HTTP_TIMEOUT);
				LOGGER.info("Successfully instantiate the solr client: " + getSolrSearchUrl());
			} catch (Exception e) {
				LOGGER.error("Unable to instantiate the solr client: " + e.getMessage());
			}
		}
		return solrServer;
	}

	protected final String getSolrSearchUrl() {
		return APEnetUtilities.getApePortalConfig().getBaseSolrSearchUrl() + "/" + getCore();
	}

	protected abstract String getCore();

	public TermsResponse getTerms(String term) throws SolrServerException {
		SolrQuery query = new SolrQuery();
		// query.setShowDebugInfo(true);
		query.setTermsPrefix(term.toLowerCase());
		query.setTermsLower(term.toLowerCase());
		query.setRequestHandler("/terms");
		String resultLog = "Query;terms;" + getCore() + ";'" + term + "';";
		long startTime = System.currentTimeMillis();
		try {
			TermsResponse response = query(query).getTermsResponse();
			if (LOGGER.isDebugEnabled()) {
				resultLog += (System.currentTimeMillis() - startTime) + "ms;;;;;;;;";
				LOGGER.debug(resultLog);
			}
			return response;
		} catch (SolrServerException e) {
			resultLog += (System.currentTimeMillis() - startTime) + "ms;;;;;;;;" + e.getMessage();
			LOGGER.error(resultLog);
			throw e;
		}

	}

	public long getNumberOfResults(SolrQueryParameters solrQueryParameters) throws SolrServerException, ParseException {
		QueryResponse queryResponse = getListViewResults(solrQueryParameters, 0, 0, null, null, null, null, false,
				false);
		return queryResponse.getResults().getNumFound();
	}

	public QueryResponse performNewSearchForListView(SolrQueryParameters solrQueryParameters, int rows,
			List<ListFacetSettings> facetSettings) throws SolrServerException, ParseException {
		return getListViewResults(solrQueryParameters, 0, rows, facetSettings, null, null, null, true, true);
	}

	public QueryResponse updateListView(SolrQueryParameters solrQueryParameters, int start, int rows,
			List<ListFacetSettings> facetSettings, String orderByField, String startDate, String endDate)
			throws SolrServerException, ParseException {
		return getListViewResults(solrQueryParameters, start, rows, facetSettings, orderByField, startDate, endDate,
				false, true);
	}

	private QueryResponse getListViewResults(SolrQueryParameters solrQueryParameters, int start, int rows,
			List<ListFacetSettings> facetSettingsList, String orderByField, String startDate, String endDate,
			boolean needSuggestions, boolean highlight) throws SolrServerException, ParseException {
		boolean withStartdateAndEnddate = false;
		SolrQuery query = new SolrQuery();
		query.setHighlight(highlight);
		if (facetSettingsList != null) {
			query.setFacetMinCount(1);
			for (ListFacetSettings facetSettings : facetSettingsList) {
				FacetType facetType = facetSettings.getFacetType();
				if (facetType.isDate()) {
					withStartdateAndEnddate = true;
				} else {
					query.addFacetField(facetType.getNameWithLabel());
					query.setParam("f." + facetType.getName() + ".facet.limit", facetSettings.getLimit() + "");
				}

			}
			if (withStartdateAndEnddate) {
				buildDateRefinement(query, startDate, endDate, true);
			}
		}
		query.setStart(start);
		if (rows > 50) {
			rows = 10;
		}
		query.setRows(rows);
		// query.setFacetLimit(ListFacetSettings.DEFAULT_FACET_VALUE_LIMIT);
		if (orderByField != null && orderByField.length() > 0 && !"relevancy".equals(orderByField)) {
			query.addSort(orderByField, ORDER.asc);
			if (withStartdateAndEnddate && orderByField.equals("startdate")) {
				query.addSort("enddate", ORDER.asc);
			}
		}
		return executeQuery(query, solrQueryParameters, QUERY_TYPE_LIST, needSuggestions);

	}

	private void buildDateRefinement(SolrQuery query, String startDate, String endDate, boolean searchResults)
			throws SolrServerException, ParseException {
		boolean facetStartDate = true;
		boolean facetEndDate = true;
		if (StringUtils.isNotBlank(startDate)) {
			String[] splittedStartDate = startDate.split("_");
			String startDateString = splittedStartDate[0];
			String gapString = splittedStartDate[1];
			DateGap dateGap = DateGap.getGapById(gapString);
			if (dateGap != null) {
				Date beginDate = SOLR_DATE_FORMAT.parse(startDateString);
				Calendar endDateCalendar = Calendar.getInstance();
				endDateCalendar.setTime(beginDate);
				endDateCalendar.add(dateGap.getType(), dateGap.getSolrTimespan());
				String finalStartDateString = startDateString + "T00:00:00Z";
				String finalEndDateString = finalStartDateString + "+" + dateGap.previous().getName();
				query.addFilterQuery("startdate:[" + finalStartDateString + " TO " + finalEndDateString + "]");
				if (searchResults && dateGap.next() != null) {
					query.setParam("f.startdate.facet.date.start", finalStartDateString);
					query.setParam("f.startdate.facet.date.end", finalEndDateString);
					query.set("f.startdate.facet.date.gap", "+" + dateGap.getName());
				} else {
					facetStartDate = false;
				}
			} else {
				facetStartDate = false;
			}
		} else if (searchResults) {
			query.setParam("f.startdate.facet.date.start", "0000-01-01T00:00:00Z");
			query.setParam("f.startdate.facet.date.end", "NOW");
			query.set("f.startdate.facet.date.gap", "+200YEARS");
		} else {
			facetStartDate = false;
		}
		if (StringUtils.isNotBlank(endDate)) {
			String[] splittedStartDate = endDate.split("_");
			String startDateString = splittedStartDate[0];
			String gapString = splittedStartDate[1];
			DateGap dateGap = DateGap.getGapById(gapString);
			if (dateGap != null) {
				Date beginDate = SOLR_DATE_FORMAT.parse(startDateString);
				Calendar endDateCalendar = Calendar.getInstance();
				endDateCalendar.setTime(beginDate);
				endDateCalendar.add(dateGap.getType(), dateGap.getSolrTimespan());
				String finalStartDateString = startDateString + "T00:00:00Z";
				String finalEndDateString = finalStartDateString + "+" + dateGap.previous().getName();
				query.addFilterQuery("enddate:[" + finalStartDateString + " TO " + finalEndDateString + "]");
				if (searchResults && dateGap.next() != null) {
					query.setParam("f.enddate.facet.date.start", finalStartDateString);
					query.setParam("f.enddate.facet.date.end", finalEndDateString);
					query.set("f.enddate.facet.date.gap", "+" + dateGap.getName());
				} else {
					facetEndDate = false;
				}
			} else {
				facetEndDate = false;
			}
		} else if (searchResults) {
			query.setParam("f.enddate.facet.date.start", "0000-01-01T00:00:00Z");
			query.setParam("f.enddate.facet.date.end", "NOW");
			query.set("f.enddate.facet.date.gap", "+200YEARS");
		} else {
			facetEndDate = false;
		}
		if (facetStartDate && facetEndDate) {
			query.setParam("facet.date", "startdate", "enddate");
		} else if (facetStartDate) {
			query.setParam("facet.date", "startdate");
		} else if (facetEndDate) {
			query.setParam("facet.date", "enddate");
		}
		if (facetStartDate || facetEndDate) {
			query.setParam("facet.date.include", "lower");
		}

	}

	public static String convertToOrQuery(List<String> list) {
		String result = null;
		if (list != null && list.size() > 0) {

			if (list.size() == 1) {
				result = escapeSolrCharactersInRefinements(list.get(0));
			} else {
				result = "(";
				for (int i = 0; i < list.size(); i++) {
					if (i == list.size() - 1) {
						result += escapeSolrCharactersInRefinements(list.get(i)) + ")";
					} else {
						result += escapeSolrCharactersInRefinements(list.get(i)) + OR;
					}
				}
			}
		}
		return result;
	}

	public static String escapeSolrCharacters(String term) {
		if (StringUtils.isNotBlank(term)) {
			term = term.replaceAll(" - ", " \"-\" ");
			term = term.replaceAll(" \\+ ", " \"+\" ");
		}
		// term = escapeSolrCharactersInRefinements(term);
		return term;
	}

	public static String escapeSolrCharactersInRefinements(String refinement) {
		return escapeSolrCharacters(refinement);
		// if (StringUtils.isNotBlank(refinement)){
		// refinement = refinement.replaceAll(ESCAPING_CHARACTER,
		// ESCAPING_CHARACTER + ESCAPING_CHARACTER);
		// refinement = refinement.replaceAll("-", ESCAPING_CHARACTER + "-");
		// refinement = refinement.replaceAll("\\+", ESCAPING_CHARACTER + "+");
		// refinement = refinement.replaceAll("&&", ESCAPING_CHARACTER + "&&");
		// refinement = refinement.replaceAll("\\|\\|", ESCAPING_CHARACTER +
		// "||");
		// refinement = refinement.replaceAll("!", ESCAPING_CHARACTER + "!");
		// refinement = refinement.replaceAll("\\(", ESCAPING_CHARACTER + "(");
		// refinement = refinement.replaceAll("\\)", ESCAPING_CHARACTER + ")");
		// refinement = refinement.replaceAll("\\{", ESCAPING_CHARACTER + "{");
		// refinement = refinement.replaceAll("\\}", ESCAPING_CHARACTER + "}");
		// refinement = refinement.replaceAll("\\[", ESCAPING_CHARACTER + "[");
		// refinement = refinement.replaceAll("\\]", ESCAPING_CHARACTER + "]");
		// refinement = refinement.replaceAll("\\^", ESCAPING_CHARACTER + "^");
		// refinement = refinement.replaceAll("\"", ESCAPING_CHARACTER + "\"");
		// refinement = refinement.replaceAll("~", ESCAPING_CHARACTER + "~");
		// refinement = refinement.replaceAll("\\*", ESCAPING_CHARACTER + "*");
		// refinement = refinement.replaceAll("\\?", ESCAPING_CHARACTER + "?");
		// refinement = refinement.replaceAll(":", ESCAPING_CHARACTER + ":");
		//
		// }
		// return refinement;
	}

	protected QueryResponse executeQuery(SolrQuery query, SolrQueryParameters solrQueryParameters, String queryType,
			boolean needSuggestions) throws SolrServerException {
		query.setQuery(escapeSolrCharacters(solrQueryParameters.getTerm()));

		if (solrQueryParameters.getAndParameters() != null) {
			for (Map.Entry<String, List<String>> criteria : solrQueryParameters.getAndParameters().entrySet()) {
				if (criteria.getValue() != null) {
					query.addFilterQuery(criteria.getKey() + COLON + convertToOrQuery(criteria.getValue()));

				}
			}
		}
		if (solrQueryParameters.getOrParameters() != null) {
			String orQuery = null;
			for (Map.Entry<String, List<String>> criteria : solrQueryParameters.getOrParameters().entrySet()) {
				if (criteria.getValue() != null) {
					if (orQuery == null) {
						orQuery = criteria.getKey() + COLON + convertToOrQuery(criteria.getValue());
					} else {
						orQuery += OR + criteria.getKey() + COLON + convertToOrQuery(criteria.getValue());
					}

				}
			}
			if (orQuery != null) {
				query.addFilterQuery(orQuery);
			}
		}
		String searchableField = null;
		for (SolrField field : solrQueryParameters.getSolrFields()) {
			if (searchableField == null) {
				searchableField = field.toString();
			} else {
				searchableField += WHITESPACE + field.toString();
			}
		}

		if (searchableField != null) {
			query.set("qf", searchableField);
			query.set("hl.fl", searchableField);
		}
		if (!solrQueryParameters.isMatchAllWords()) {
			query.set("mm", "0%");
		}
		if (queryType != null) {
			query.setRequestHandler(queryType);
		}
		if (needSuggestions) {
			needSuggestions = isSimpleSearchTerms(solrQueryParameters.getTerm());
		}
		if (needSuggestions
				&& !(solrQueryParameters.getSolrFields().contains(SolrField.UNITID)
						|| solrQueryParameters.getSolrFields().contains(SolrField.OTHERUNITID) || solrQueryParameters
						.getSolrFields().contains(SolrField.EAC_CPF_ENTITY_ID))
				&& StringUtils.isNotBlank(solrQueryParameters.getTerm())) {
			query.set("spellcheck", "on");
		}
		Integer timeAllowed = TIME_ALLOWED_TREE;
		if (QUERY_TYPE_LIST.equals(queryType)) {
			timeAllowed = TIME_ALLOWED;
		}
		query.setTimeAllowed(timeAllowed);
		String resultLog = "Query;";
		if (query.getRows() == 0) {
			resultLog += "count;";
		} else {
			resultLog += queryType + ";";
		}
		resultLog +=  getCore() + ";'" + solrQueryParameters.getTerm() + "';";
		String resultLogSuffix = "";
		if (solrQueryParameters.isMatchAllWords()) {
			resultLogSuffix += "AND;";
		} else {
			resultLogSuffix +="OR;";
		}
		if (query.getRows() == 0) {
			resultLogSuffix += ";";
		} else {
			resultLogSuffix += query.getRows() + "rows;";
		}
		if (solrQueryParameters.getAndParameters() != null) {
			resultLogSuffix += solrQueryParameters.getAndParameters().size() +";";
		} else {
			resultLogSuffix +=";";
		}
		if (solrQueryParameters.getOrParameters() != null) {
			resultLogSuffix += solrQueryParameters.getOrParameters().size() +";";
		} else {
			resultLogSuffix +=";";
		}
		if (searchableField == null) {
			resultLogSuffix += ";";
		} else {
			resultLogSuffix += searchableField + ";";
		}
		if (query.getSorts().size() > 0) {
			resultLogSuffix += query.getSorts().get(0).getItem() + ";";
		} else {
			resultLogSuffix += ";";
		}


		long startTime = System.currentTimeMillis();
		try {

			QueryResponse result = query(query);
			resultLog += (System.currentTimeMillis() - startTime) + "ms;" + result.getResults().getNumFound()
					+ " hits;" + resultLogSuffix;
			if (result.getHeader().get("partialResults") != null) {
				resultLog += "exceed query time(" + timeAllowed + "ms)";
			}
			LOGGER.debug(resultLog);
			return result;
		} catch (SolrServerException e) {
			resultLog += (System.currentTimeMillis() - startTime) + "ms;;" +resultLogSuffix  + e.getMessage();
			LOGGER.error(resultLog);
			throw e;
		}
	}

	private static boolean isSimpleSearchTerms(String term) {
		Matcher matcher = NORMAL_TERM_PATTERN.matcher(term);
		return matcher.matches();
	}

	protected QueryResponse query(SolrQuery query) throws SolrServerException {
		try {
			return getSolrServer().query(query, METHOD.POST);
		} catch (SolrServerException sse) {
			long currentTime = System.currentTimeMillis();
			long lastTimeSend = currentTime - RESEND_EMAIL_TIME;
			boolean send = false;
			synchronized (lastTimeEmailSend) {
				if (lastTimeSend > lastTimeEmailSend) {
					lastTimeEmailSend = currentTime;
					send = true;

				}
			}
			if (send) {
				EmailSender.sendExceptionToAdmin("Solr search engine has problems", sse);
			}
			throw sse;
		}
	}
}
