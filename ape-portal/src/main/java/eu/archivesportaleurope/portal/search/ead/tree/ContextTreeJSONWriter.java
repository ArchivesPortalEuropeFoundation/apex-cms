package eu.archivesportaleurope.portal.search.ead.tree;

import eu.apenet.commons.solr.SearchUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.solr.SolrQueryParameters;
import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.solr.facet.FacetType;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;
import eu.archivesportaleurope.portal.search.common.AbstractSearchController;
import java.io.IOException;

/**
 * Generate JSON for context tab.
 * 
 * @author bastiaan
 * 
 */
@Controller(value = "contextTreeJSONWriter")
@RequestMapping(value = "VIEW")
public class ContextTreeJSONWriter extends AbstractJSONWriter {

	private static final String SEARCH_TYPE_AI = "ai";
	private static final String SEARCH_TYPE_FOND = "hgfa";
	private static final int MAX_NUMBER_OF_ITEMS = 10;
	private static final int NO_LIMIT = MAX_NUMBER_OF_ITEMS;



	@ResourceMapping(value = "contextTree")
	public ModelAndView getJSON(@ModelAttribute(value = "eadSearch") TreeEadSearch eadSearch,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		Locale locale = resourceRequest.getLocale();

		long startTime = System.currentTimeMillis();

		try {
			SolrQueryParameters solrQueryParameters = AbstractSearchController.getSolrQueryParametersByForm(eadSearch, resourceRequest);

			SearchUtil.addSelectedNodesToQuery(eadSearch.getSelectedNodesList(), solrQueryParameters);

			SearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.TYPE,
					eadSearch.getTypedocument());
			SearchUtil.setFromDate(solrQueryParameters.getAndParameters(), eadSearch.getFromdate(),
					eadSearch.hasExactDateSearch());
			SearchUtil.setToDate(solrQueryParameters.getAndParameters(), eadSearch.getTodate(),
					eadSearch.hasExactDateSearch());
			
			SearchUtil.setParameter(solrQueryParameters.getAndParameters(), FacetType.TOPIC.getName(), eadSearch.getSimpleSearchTopic());
			// Only refine on dao if selected
			if ("true".equals(eadSearch.getDao())) {
				SearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.DAO,
						eadSearch.getDao());
			}
			solrQueryParameters.setSolrFields(SolrField.getSolrFieldsByIdString(eadSearch.getElement()));
			if (eadSearch.getSearchType() == null) {
				log.error("No search type found");
			}
			SearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.COUNTRY_ID,
					eadSearch.getCountry());
			if (SEARCH_TYPE_AI.equals(eadSearch.getSearchType())) {
				writeToResponseAndClose(generateAiJSON(eadSearch, solrQueryParameters, locale), resourceResponse);
			} else if (SEARCH_TYPE_FOND.equals(eadSearch.getSearchType())) {
				writeToResponseAndClose(
						generateFindingAidsOrHoldingGuidesJSON(eadSearch, solrQueryParameters, locale),
						resourceResponse);
			} else {
				writeToResponseAndClose(generateCLevelJSON(eadSearch, solrQueryParameters, locale),
						resourceResponse);
			}

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.debug("Context search time: " + (System.currentTimeMillis() - startTime));

		return null;
	}

	private StringBuilder generateFindingAidsOrHoldingGuidesJSON(TreeEadSearch eadSearch,
			SolrQueryParameters solrQueryParameters, Locale locale) throws SolrServerException, IOException {
		SearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.AI_ID,
				eadSearch.getParentId());
		Integer startInt = 0;
		List<Count> counts = new ArrayList<Count>();
		if (eadSearch.getStart() != null) {
			startInt = new Integer(eadSearch.getStart());
		} else {

			FacetField holdingGuides = getEadSearcher().getFonds(solrQueryParameters, SolrFields.HG_DYNAMIC_NAME,
					startInt, NO_LIMIT);
			List<Count> hgCounts = holdingGuides.getValues();
			if (hgCounts != null) {
				counts.addAll(hgCounts);
			}
			FacetField sourceGuides = getEadSearcher().getFonds(solrQueryParameters, SolrFields.SG_DYNAMIC_NAME, startInt,
					NO_LIMIT);
			List<Count> sgCounts = sourceGuides.getValues();
			if (sgCounts != null) {
				counts.addAll(sgCounts);
			}
		}
		int faMaxCount = (MAX_NUMBER_OF_ITEMS + 1) - counts.size();
		int maxNumberOfItems = counts.size();
		if (faMaxCount <= 1) {
			faMaxCount = 2;
		}
		maxNumberOfItems += faMaxCount;
		FacetField findingAids = getEadSearcher().getFonds(solrQueryParameters, SolrFields.FA_DYNAMIC_NAME, startInt,
				faMaxCount);
		List<Count> faCounts = findingAids.getValues();
		if (faCounts != null) {
			counts.addAll(faCounts);
		}
		StringBuilder buffer = new StringBuilder();
		addStartArray(buffer);
		for (int i = 0; i < counts.size(); i++) {
			TreeFacetValue facetValue = new TreeFacetValue(counts.get(i), TreeFacetValue.Type.FOND, locale);
			buffer.append(START_ITEM);
			if (i == (maxNumberOfItems - 1)) {
				addMore(buffer, locale);
				buffer.append(COMMA);
				addStart(buffer, startInt + MAX_NUMBER_OF_ITEMS);
				buffer.append(COMMA);
				addParentId(buffer, eadSearch.getParentId());
				buffer.append(COMMA);
				addSearchType(buffer, SEARCH_TYPE_FOND);

			} else {
				addTitle(buffer, facetValue, locale);
				buffer.append(COMMA);
				addParentId(buffer, facetValue.getId());
				buffer.append(COMMA);
				if (facetValue.getId().startsWith(SolrValues.FA_PREFIX)) {
					addSearchType(buffer, SolrValues.FA_PREFIX);
				} else if (facetValue.getId().startsWith(SolrValues.HG_PREFIX)) {
					addSearchType(buffer, SolrValues.HG_PREFIX);
				} else {
					addSearchType(buffer, SolrValues.SG_PREFIX);
				}
				buffer.append(COMMA);
				addFondId(buffer, facetValue.getId());

			}
			buffer.append(COMMA);
			addLevel(buffer, 0);
			buffer.append(COMMA);
			buffer.append(FOLDER_LAZY);
			if (i < counts.size() - 1) {
				buffer.append("},\n");
			} else {
				buffer.append("}\n");
			}
		}
		addEndArray(buffer);
		return buffer;
	}

	private StringBuilder generateAiJSON(TreeEadSearch eadSearch, SolrQueryParameters solrQueryParameters,
			Locale locale) throws SolrServerException, IOException {
		int startInt = eadSearch.getStartInt();
		int levelInt = eadSearch.getLevelInt();
		FacetField aiFacetField = getEadSearcher().getAis(solrQueryParameters, eadSearch.getParentId(), levelInt,
				startInt, MAX_NUMBER_OF_ITEMS + 1);
		List<Count> archivalInstitutions = aiFacetField.getValues();
		boolean moreResultsAvailable = (archivalInstitutions != null && archivalInstitutions.size() > MAX_NUMBER_OF_ITEMS);
		StringBuilder buffer = new StringBuilder();
		addStartArray(buffer);
		if (archivalInstitutions != null) {
			int clevelAdded = 0;
			Iterator<Count> iterator = archivalInstitutions.iterator();
			while (iterator.hasNext() && clevelAdded < MAX_NUMBER_OF_ITEMS) {
				TreeFacetValue aiFacetValue = new TreeFacetValue(iterator.next(),
						TreeFacetValue.Type.ARCHIVAL_INSTITUTION, locale);
				buffer.append(START_ITEM);
				addLevel(buffer, levelInt + 1);
				buffer.append(COMMA);
				buffer.append(FOLDER_LAZY);
				buffer.append(COMMA);
				addTitle(buffer, aiFacetValue, locale);
				buffer.append(COMMA);
				if (aiFacetValue.isLeaf()) {
					addSearchType(buffer, SEARCH_TYPE_FOND);
					buffer.append(COMMA);
					addParentId(buffer, aiFacetValue.getId().substring(1));
				} else {
					addSearchType(buffer, SEARCH_TYPE_AI);
					buffer.append(COMMA);
					addParentId(buffer, aiFacetValue.getId());
				}

				buffer.append(END_ITEM);

				// add more item
				clevelAdded++;
				if (iterator.hasNext() && clevelAdded < MAX_NUMBER_OF_ITEMS) {
					buffer.append(COMMA);
				} else if (moreResultsAvailable) {
					buffer.append(COMMA);
					buffer.append(START_ITEM);
					addLevel(buffer, levelInt);
					buffer.append(COMMA);
					buffer.append(FOLDER_LAZY);
					buffer.append(COMMA);
					addParentId(buffer, eadSearch.getParentId());
					buffer.append(COMMA);
					addSearchType(buffer, SEARCH_TYPE_AI);
					buffer.append(COMMA);
					addMore(buffer, locale);
					buffer.append(COMMA);
					addStart(buffer, startInt + MAX_NUMBER_OF_ITEMS);
					buffer.append(END_ITEM);
				}
			}
		}
		addEndArray(buffer);
		return buffer;
	}

	private StringBuilder generateCLevelJSON(TreeEadSearch eadSearch,
			SolrQueryParameters solrQueryParameters, Locale locale) throws SolrServerException, IOException {
		int startInt = eadSearch.getStartInt();
		int levelInt = eadSearch.getLevelInt();
		FacetField fonds = getEadSearcher().getLevels(solrQueryParameters, eadSearch.getSearchType(),
				eadSearch.getParentId(), levelInt, startInt, MAX_NUMBER_OF_ITEMS + 1);
		List<Count> mapCounts = fonds.getValues();
		QueryResponse searchResultResponse = getEadSearcher().getResultsInLevels(solrQueryParameters,
				eadSearch.getParentId(), startInt, MAX_NUMBER_OF_ITEMS);
		SolrDocumentList results = searchResultResponse.getResults();
		Map<String, Map<String, List<String>>> highlightingMap = searchResultResponse.getHighlighting();
		boolean moreResultsAvailable = (mapCounts != null && mapCounts.size() > MAX_NUMBER_OF_ITEMS)
				|| (startInt + MAX_NUMBER_OF_ITEMS) < results.getNumFound();
		Map<String, CLevelInfo> clevelInfos = new TreeMap<String, CLevelInfo>();
		if (mapCounts != null) {
			for (Count count : mapCounts) {
				TreeFacetValue facetValue = new TreeFacetValue(count, TreeFacetValue.Type.CLEVEL, locale);
				CLevelInfo cLevelInfo = new CLevelInfo();
				cLevelInfo.setClevelWithCounts(facetValue);
				clevelInfos.put(facetValue.getId(), cLevelInfo);
			}
		}
		for (SolrDocument clevel : results) {
			String id = (String) clevel.getFieldValue(SolrFields.ID);
			CLevelInfo cLevelInfo = clevelInfos.get(id);
			if (cLevelInfo == null) {
				cLevelInfo = new CLevelInfo();
				clevelInfos.put(id, cLevelInfo);
			}
			cLevelInfo.setSearchResult(clevel);

		}
		StringBuilder buffer = new StringBuilder();
		addStartArray(buffer);
		int clevelAdded = 0;
		List<CLevelInfo> clevels = new ArrayList<CLevelInfo>();
		clevels.addAll(clevelInfos.values());
		Collections.sort(clevels);
		Iterator<CLevelInfo> iterator = clevels.iterator();
		while (iterator.hasNext() && clevelAdded < MAX_NUMBER_OF_ITEMS) {
			CLevelInfo clevelInfo = iterator.next();
			TreeFacetValue facetValue = clevelInfo.getClevelWithCounts();
			SolrDocument searchResult = clevelInfo.getSearchResult();
			buffer.append(START_ITEM);
			if (facetValue != null) {
				addLevel(buffer, levelInt + 1);
				buffer.append(COMMA);
				addSearchType(buffer, eadSearch.getSearchType());
				buffer.append(COMMA);
				buffer.append(FOLDER_LAZY);
				buffer.append(COMMA);
				addParentId(buffer, facetValue.getId());
				buffer.append(COMMA);
				// addFondId(buffer, fondId);
				// buffer.append(COMMA);
				if (searchResult == null) {
					addTitle(buffer, facetValue, locale);
				} else if (searchResult != null) {
					String id = (String) searchResult.getFieldValue(SolrFields.ID);
					String title = (String) searchResult.getFieldValue(SolrFields.TITLE);
					String highlightedTitle = SearchUtil.getHighlightedString(highlightingMap, id,
							SolrFields.TITLE, title);
					addTitleWithLinkAndCount(buffer, highlightedTitle, facetValue.getCount(), locale);
					buffer.append(COMMA);
					addSearchResult(buffer);
				}
			} else if (facetValue == null && searchResult != null) {
				String id = (String) searchResult.getFieldValue(SolrFields.ID);
				String title = (String) searchResult.getFieldValue(SolrFields.TITLE);
				String highlightedTitle = SearchUtil.getHighlightedString(highlightingMap, id,
						SolrFields.TITLE, title);
				addTitleWithLink(buffer, highlightedTitle, locale);
				buffer.append(COMMA);
				addParentId(buffer, id);
				buffer.append(COMMA);
				// addFondId(buffer, fondId);
				// buffer.append(COMMA);
				addSearchResult(buffer);
			}

			buffer.append(END_ITEM);
			clevelAdded++;
			if (iterator.hasNext() && clevelAdded < MAX_NUMBER_OF_ITEMS) {
				buffer.append(COMMA);
			} else if (moreResultsAvailable) {
				buffer.append(COMMA);
				buffer.append(START_ITEM);
				addLevel(buffer, levelInt);
				buffer.append(COMMA);
				buffer.append(FOLDER_LAZY);
				buffer.append(COMMA);
				addParentId(buffer, eadSearch.getParentId());
				buffer.append(COMMA);
				// addFondId(buffer, fondId);
				// buffer.append(COMMA);
				addSearchType(buffer, eadSearch.getSearchType());
				buffer.append(COMMA);
				addMore(buffer, locale);
				buffer.append(COMMA);
				addStart(buffer, startInt + MAX_NUMBER_OF_ITEMS);
				buffer.append(END_ITEM);
			}
		}
		addEndArray(buffer);
		return buffer;
	}

	private void addTitleWithLink(StringBuilder buffer, String title, Locale locale) {
		addTitle("contextTitleWithLink", buffer, title, null, locale);

	}

	private void addTitle(String styleClass, StringBuilder buffer, String title, String count, Locale locale) {
		addNoIcon(buffer);
		String convertedTitle = PortalDisplayUtil.replaceQuotesAndReturnsForTree(title);
		boolean hasTitle = convertedTitle != null && convertedTitle.length() > 0;
		if (!hasTitle) {
			if (styleClass == null) {
				styleClass = "notitle";
			} else {
				styleClass += " notitle";
			}
		}

		if (styleClass != null) {
			buffer.append("\"addClass\":");
			buffer.append(" \"" + styleClass + "\"");
			buffer.append(COMMA);
		}

		buffer.append("\"title\":\"");
		if (hasTitle) {
			buffer.append(convertedTitle);
		} else {
			buffer.append(this.getMessageSource().getMessage(ADVANCEDSEARCH_TEXT_NOTITLE, null, locale));
		}
		if (count != null) {
			buffer.append(" <span class='numberOfHits'>(" + count + ")</span>\"");
		} else {
			buffer.append("\"");
		}
	}

	private void addTitleWithLinkAndCount(StringBuilder buffer, String title, String count, Locale locale) {
		addTitle("contextTitleWithLink", buffer, title, count, locale);
	}

	private void addTitle(StringBuilder buffer, TreeFacetValue facetValue, Locale locale) {
		addTitle(null, buffer, facetValue.getName(), facetValue.getCount(), locale);

	}

	private static void addSearchType(StringBuilder buffer, String searchType) {
		buffer.append("\"searchType\":");
		buffer.append(" \"" + searchType + "\"");
	}

	private static void addFondId(StringBuilder buffer, String fondId) {
		buffer.append("\"fondId\":");
		buffer.append(" \"" + fondId + "\"");
	}

	private static void addLevel(StringBuilder buffer, int level) {
		buffer.append("\"level\":");
		buffer.append(" \"" + level + "\"");
	}

	private static void addParentId(StringBuilder buffer, String parentId) {
		buffer.append("\"parentId\":");
		buffer.append(" \"" + parentId + "\"");
	}

	private static void addSearchResult(StringBuilder buffer) {
		buffer.append("\"searchResult\":");
		buffer.append(" \"true\"");
	}

	private static class CLevelInfo implements Comparable<CLevelInfo> {
		private TreeFacetValue clevelWithCounts;
		private SolrDocument searchResult;
		private Long orderId;

		public TreeFacetValue getClevelWithCounts() {
			return clevelWithCounts;
		}

		public void setClevelWithCounts(TreeFacetValue clevelWithCounts) {
			this.clevelWithCounts = clevelWithCounts;
		}

		public SolrDocument getSearchResult() {
			return searchResult;
		}

		public void setSearchResult(SolrDocument searchResult) {
			this.searchResult = searchResult;
			Integer temp = (Integer) searchResult.getFieldValue(SolrFields.ORDER_ID);
			orderId = temp.longValue();
		}

		public Long getOrderId() {
			if (orderId == null) {
				if (clevelWithCounts != null) {
					return clevelWithCounts.getOrderId();
				}
			}
			return orderId;

		}

		@Override
		public int compareTo(CLevelInfo other) {
			Long otherOrderId = other.getOrderId();
			Long thisOrderId = this.getOrderId();
			if (otherOrderId != null && thisOrderId != null) {
				return thisOrderId.compareTo(otherOrderId);
			}
			return 0;
		}

	}

	static class FacetFieldInfo {
		public FacetFieldInfo() {

		}
	}

}
