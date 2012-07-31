package eu.archivesportaleurope.portal.search.advanced;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.solr.SolrValues;
import eu.archivesportaleurope.portal.search.advanced.list.ListResults;
import eu.archivesportaleurope.portal.search.advanced.list.SolrDocumentListHolder;
import eu.archivesportaleurope.portal.search.advanced.tree.ContextResults;
import eu.archivesportaleurope.portal.search.advanced.tree.FacetValue;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
import eu.archivesportaleurope.portal.search.common.Facet;
import eu.archivesportaleurope.portal.search.common.Results;
import eu.archivesportaleurope.portal.search.common.SearchUtils;
import eu.archivesportaleurope.portal.search.common.Searcher;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;

@Controller(value = "advancedSearchController")
@RequestMapping(value = "VIEW")
public class AdvancedSearchController {
	private static final String HIERARCHY = "hierarchy";
	private final static Logger LOGGER = Logger.getLogger(AdvancedSearchController.class);
	public static final String MODE_NEW = "new";
	public static final String MODE_NEW_SEARCH = "new-search";
	public static final String MODE_UPDATE_SEARCH = "update-search";
	private Searcher searcher;

	public Searcher getSearcher() {
		return searcher;
	}

	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public String showAdvancedSearch() {
		return "home";
	}
	
	@RenderMapping(params = "myaction=simpleSearch")
	public ModelAndView search(@ModelAttribute(value = "advancedSearch") AdvancedSearch advancedSearch, RenderRequest request) {
		advancedSearch.setMode(MODE_NEW_SEARCH);
		Results results = performNewSearch(request, advancedSearch);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		modelAndView.getModelMap().addAttribute("advancedSearch", advancedSearch);
		modelAndView.getModelMap().addAttribute("results", results);
		return modelAndView;
	}
	

	@ResourceMapping(value = "advancedSearch")
	public ModelAndView searchAjax(@ModelAttribute(value = "advancedSearch") AdvancedSearch advancedSearch,
			BindingResult bindingResult, ResourceRequest request) throws SolrServerException, ParseException {
		Results results = null;
		if (MODE_NEW_SEARCH.equalsIgnoreCase(advancedSearch.getMode())) {
			results = performNewSearch(request, advancedSearch);
		} else if (MODE_UPDATE_SEARCH.equalsIgnoreCase(advancedSearch.getMode())) {
			results = updateCurrentSearch(request, advancedSearch);

		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("results");
		modelAndView.getModelMap().addAttribute("advancedSearch", advancedSearch);
		modelAndView.getModelMap().addAttribute("results", results);
		return modelAndView;
	}

	@ModelAttribute("advancedSearch")
	public AdvancedSearch getCommandObject() {
		return new AdvancedSearch();
	}

	public Results performNewSearch(PortletRequest request, AdvancedSearch advancedSearch) {
		Results results = null;
		try {
			String error = validate(advancedSearch);
			if (error == null) {
				SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
				handleSearchParameters(advancedSearch, solrQueryParameters);
				if (HIERARCHY.equals(advancedSearch.getView())) {
					results = performNewSearchForContextView(request, solrQueryParameters);
				} else {
					results = performNewSearchForListView(request, solrQueryParameters, advancedSearch);
				}
				boolean showSuggestions = false;
				if (results.getSolrResponse().getSpellCheckResponse() != null) {
					List<Collation> suggestions = results.getSolrResponse().getSpellCheckResponse()
							.getCollatedResults();
					if (suggestions != null) {
						for (Collation collation : suggestions) {
							showSuggestions = showSuggestions
									|| (collation.getNumberOfHits() > results.getTotalNumberOfResults());
						}
					}
				}
				results.setShowSuggestions(showSuggestions);
			} else {
				if (HIERARCHY.equals(advancedSearch.getView())) {
					results = new ContextResults();
				} else {
					results = new ListResults();
				}
				results.setErrorMessage(error);
			}

			//request.setAttribute("results", results);

		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the advanced search: Error: " + e.getMessage(), e);
		}
		return results;
	}

	public Results updateCurrentSearch(PortletRequest request, AdvancedSearch advancedSearch) {
		Results results = null;
		try {

			SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
			if (HIERARCHY.equals(advancedSearch.getView())) {
				handleSearchParametersForContextUpdate(advancedSearch, solrQueryParameters);
				results = performNewSearchForContextView(request, solrQueryParameters);
			} else {
				handleSearchParametersForListUpdate(advancedSearch, solrQueryParameters);
				results = performUpdateSearchForListView(request, solrQueryParameters, advancedSearch);
			}
		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the advanced search: Error: " + e.getMessage(), e);
		}
		return results;
	}

	protected ListResults performUpdateSearchForListView(PortletRequest request,
			SolrQueryParameters solrQueryParameters, AdvancedSearch advancedSearch) throws SolrServerException,
			ParseException {
		ListResults results = new ListResults();
		results.setPageSize(Integer.parseInt(advancedSearch.getResultsperpage()));
		Integer pageNumber = Integer.parseInt(advancedSearch.getPageNumber());
		results.setSolrResponse(searcher.updateListView(solrQueryParameters, results.getPageSize() * (pageNumber - 1),
				results.getPageSize(), advancedSearch.getOrder(), advancedSearch.getStartdate(),
				advancedSearch.getEnddate()));
		updatePagination(advancedSearch, results);
		if (results.getTotalNumberOfResults() > 0) {
			results.setItems(new SolrDocumentListHolder(results.getSolrResponse()));
		} else {
			results.setItems(new SolrDocumentListHolder());
		}
		return results;
	}

	protected ListResults performNewSearchForListView(PortletRequest request, SolrQueryParameters solrQueryParameters,
			AdvancedSearch advancedSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		results.setPageSize(Integer.parseInt(advancedSearch.getResultsperpage()));
		results.setSolrResponse(searcher.performNewSearchForListView(solrQueryParameters, results.getPageSize()));
		updatePagination(advancedSearch, results);
		if (results.getTotalNumberOfResults() > 0) {
			results.setItems(new SolrDocumentListHolder(results.getSolrResponse()));
		} else {
			results.setItems(new SolrDocumentListHolder());
		}
		return results;
	}

	protected ContextResults performNewSearchForContextView(PortletRequest request,
			SolrQueryParameters solrQueryParameters) throws SolrServerException {
		ContextResults results = new ContextResults();
		results.setSolrResponse(searcher.performNewSearchForContextView(solrQueryParameters));
		List<Count> countries = results.getSolrResponse().getFacetFields().get(0).getValues();
		if (countries != null) {
			for (Count country : countries) {
				results.getCountries().add(new FacetValue(country, FacetValue.Type.COUNTRY));
			}
		}
		return results;

	}

	protected void handleSearchParameters(AdvancedSearch advancedSearch, SolrQueryParameters solrQueryParameters) {
		if (advancedSearch.getNavigationTreeNodesSelected() != null) {
			String tmpResult = "";
			List<String> tmpList = advancedSearch.getNavigationTreeNodesSelected();
			if (tmpList != null) {
				for (int i = 0; i < tmpList.size(); i++) {
					tmpResult = tmpResult + tmpList.get(i);
					if (i < tmpList.size() - 1) {
						tmpResult = tmpResult + ",";
					}
				}
			}
			advancedSearch.setNavigationTreeNodesSelectedSerialized(tmpResult);
		} 
//		else {
//			if (session.get(NAVIGATION_TREE_NODES_SELECTED_KEY) != null) {
//				String navigationTreeNodesSelectedStringSession = (String) session
//						.get(NAVIGATION_TREE_NODES_SELECTED_KEY);
//				List<String> navigationTreeNodesSelected = null;
//				String[] tmpAuxList = null;
//				tmpAuxList = navigationTreeNodesSelectedStringSession.split(",");
//				navigationTreeNodesSelected = Arrays.asList(tmpAuxList);
//				setNavigationTreeNodesSelected(navigationTreeNodesSelected);
//				session.remove(NAVIGATION_TREE_NODES_SELECTED_KEY);
//			}
//		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("navigationTreeNodesSelected: " + advancedSearch.getNavigationTreeNodesSelected());
		}
		if (advancedSearch.getExpandedNodes() != null) {
			String tmpResult = "";
			List<String> tmpList = advancedSearch.getExpandedNodes();
			if (tmpList != null) {
				for (int i = 0; i < tmpList.size(); i++) {
					tmpResult = tmpResult + tmpList.get(i);
					if (i < tmpList.size() - 1) {
						tmpResult = tmpResult + ",";
					}
				}
			}
			advancedSearch.setExpandedNodesSerialized(tmpResult);
		} 
//		else {
//			if (session.get(EXPANDED_NODES_SELECTED_KEY) != null) {
//				String expandedNodesStringSession = (String) session.get(EXPANDED_NODES_SELECTED_KEY);
//				List<String> expandedNodes = null;
//				String[] tmpAuxList = null;
//				tmpAuxList = expandedNodesStringSession.split(",");
//				expandedNodes = Arrays.asList(tmpAuxList);
//				setExpandedNodes(expandedNodes);
//				session.remove(EXPANDED_NODES_SELECTED_KEY);
//			}
//		}
		List<String> countriesSelectedForSearchId = new ArrayList<String>();
		List<String> archivalInstitutionsSelectedForSearchId = new ArrayList<String>();
		List<String> holdingsGuideSelectedForSearchId = new ArrayList<String>();
		List<String> findingAidsSelectedForSearchId = new ArrayList<String>();
		AdvancedSearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.TYPE,
				AdvancedSearchUtil.convertToType(advancedSearch.getTypedocument()));
		String startDate = AdvancedSearchUtil.obtainDate(advancedSearch.getFromdate(), true);
		String endDate = AdvancedSearchUtil.obtainDate(advancedSearch.getTodate(), false);
		AdvancedSearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.START_DATE,
				AdvancedSearchUtil.convertToSolrStartDate(startDate, endDate));
		AdvancedSearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.END_DATE,
				AdvancedSearchUtil.convertToSolrEndDate(startDate, endDate));

		if (advancedSearch.getNavigationTreeNodesSelected() != null) {

			SearchUtils.fillSelectedForSearchIdLists(advancedSearch.getNavigationTreeNodesSelected(), countriesSelectedForSearchId,
					archivalInstitutionsSelectedForSearchId, holdingsGuideSelectedForSearchId,
					findingAidsSelectedForSearchId);

			// Adding the ids of the Finding Aid selected for searching
			List<String> faHgIdsSelected = new ArrayList<String>();
			if (findingAidsSelectedForSearchId != null) {
				for (int i = 0; i < findingAidsSelectedForSearchId.size(); i++) {
					faHgIdsSelected.add(SolrValues.FA_PREFIX + findingAidsSelectedForSearchId.get(i));
				}
			}

			// Adding the ids of the Holdings Guide selected for searching
			if (holdingsGuideSelectedForSearchId != null) {
				for (int i = 0; i < holdingsGuideSelectedForSearchId.size(); i++) {
					faHgIdsSelected.add(SolrValues.HG_PREFIX + holdingsGuideSelectedForSearchId.get(i));
				}
			}

			List<String> archivalInstitutionsIdsSelected = new ArrayList<String>();
			List<String> countriesSelected = new ArrayList<String>();
			// Adding the ids of the Countries selected for searching
			if (countriesSelectedForSearchId != null) {
				for (int i = 0; i < countriesSelectedForSearchId.size(); i++) {
					countriesSelected.add(countriesSelectedForSearchId.get(i));
				}
			}

			// Adding the ids of the Archival Institutions selected for
			// searching
			if (archivalInstitutionsSelectedForSearchId != null) {
				for (int i = 0; i < archivalInstitutionsSelectedForSearchId.size(); i++) {
					if (!archivalInstitutionsIdsSelected.contains(Integer
							.parseInt(archivalInstitutionsSelectedForSearchId.get(i)))) {
						archivalInstitutionsIdsSelected.add(archivalInstitutionsSelectedForSearchId.get(i));
					}
				}
			}

			if (countriesSelected.size() > 0) {
				AdvancedSearchUtil.setParameter(solrQueryParameters.getOrParameters(), SolrFields.COUNTRY_ID,
						countriesSelected);
			}
			if (archivalInstitutionsIdsSelected.size() > 0) {
				AdvancedSearchUtil.setParameter(solrQueryParameters.getOrParameters(), SolrFields.AI_ID,
						archivalInstitutionsIdsSelected);
			}

			AdvancedSearchUtil.setParameter(solrQueryParameters.getOrParameters(), SolrFields.FOND_ID, faHgIdsSelected);
		}
		solrQueryParameters.setSolrFields(SolrField.getSolrFieldsByIdString(advancedSearch.getElement()));

		solrQueryParameters.setTerm(advancedSearch.getTerm());
		solrQueryParameters.setMatchAllWords(advancedSearch.matchAllWords());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, Facet.DAO, advancedSearch.getDaoList());
	}

	protected void handleSearchParametersForListUpdate(AdvancedSearch advancedSearch,
			SolrQueryParameters solrQueryParameters) {
		handleSearchParameters(advancedSearch, solrQueryParameters);
		AdvancedSearchUtil.addRefinement(solrQueryParameters, Facet.COUNTRY, advancedSearch.getCountryList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, Facet.AI, advancedSearch.getAiList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, Facet.TYPE, advancedSearch.getTypeList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, Facet.DATE_TYPE, advancedSearch.getDateTypeList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, Facet.ROLEDAO, advancedSearch.getRoledaoList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, Facet.FOND, advancedSearch.getFondList());
	}

	protected void updatePagination(AdvancedSearch advancedSearch, ListResults results) {
		Long totalNumberOfPages = results.getTotalNumberOfResults() / results.getPageSize();
		Long rest = results.getTotalNumberOfResults() % results.getPageSize();
		if (rest > 0) {
			totalNumberOfPages++;
		}
		results.setTotalNumberOfPages(totalNumberOfPages);
	}

	protected void handleSearchParametersForContextUpdate(AdvancedSearch advancedSearch,
			SolrQueryParameters solrQueryParameters) {
		handleSearchParameters(advancedSearch, solrQueryParameters);

	}

	public String validate(AdvancedSearch advancedSearch) {
//		String startDate = AdvancedSearchUtil.obtainDate(advancedSearch.getFromdate(), true);
//		if (StringUtils.isBlank(advancedSearch.getFromdate()) && StringUtils.isBlank(advancedSearch.getTodate())
//				&& StringUtils.isBlank(advancedSearch.getTerm()) && advancedSearch.getNavigationTreeNodesSelected() == null) {
//			return "search.message.noDatesTyped";
//		}
//		if (StringUtils.isNotBlank(advancedSearch.getFromdate()) && startDate == null) {
//			return "search.message.IncorrectDateTyped";
//
//		}
//		String endDate = AdvancedSearchUtil.obtainDate(advancedSearch.getTodate(), false);
//		if (StringUtils.isNotBlank(advancedSearch.getTodate()) && endDate == null) {
//			return "search.message.IncorrectDateTyped";
//		}
		return null;
	}
}
