package eu.archivesportaleurope.portal.search.advanced;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.solr.SolrFields;
import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.common.AnalyzeLogger;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.advanced.list.ListResults;
import eu.archivesportaleurope.portal.search.advanced.list.SolrDocumentListHolder;
import eu.archivesportaleurope.portal.search.advanced.tree.ContextResults;
import eu.archivesportaleurope.portal.search.advanced.tree.TreeFacetValue;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.Results;
import eu.archivesportaleurope.portal.search.common.Searcher;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;
import eu.archivesportaleurope.portal.search.saved.SavedSearchService;

@Controller(value = "advancedSearchController")
@RequestMapping(value = "VIEW")
public class AdvancedSearchController {

	private static final String SEARCH_ALL_STRING = "*:*";
	private final static Logger LOGGER = Logger.getLogger(AdvancedSearchController.class);
	public static final String MODE_NEW = "new";
	public static final String MODE_NEW_SEARCH = "new-search";
	public static final String MODE_UPDATE_SEARCH = "update-search";
	private Searcher searcher;
	private ResourceBundleMessageSource messageSource;
	private SavedSearchService savedSearchService;

	public void setSavedSearchService(SavedSearchService savedSearchService) {
		this.savedSearchService = savedSearchService;
	}

	public Searcher getSearcher() {
		return searcher;
	}

	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public String showAdvancedSearch(RenderRequest request) {
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_ADVANCED_SEARCH);
		return "home";
	}

	@RenderMapping(params = "myaction=showSavedSearch")
	public ModelAndView showSavedSearch(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SIMPLE_SEARCH);
		try {
			String id = request.getParameter("id");
			Long savedSearchId = Long.parseLong(id);
			Long liferayUserId = null;
			if (request.getUserPrincipal() != null) {
				liferayUserId = Long.parseLong(request.getUserPrincipal().toString());
			}
			EadSavedSearch eadSavedSearch = savedSearchService.getEadSavedSearch(liferayUserId, savedSearchId);
			if (eadSavedSearch != null) {
				AdvancedSearch advancedSearch = savedSearchService.convert(eadSavedSearch);
				
				if (eadSavedSearch.isTemplate()){
					advancedSearch.setMode(MODE_NEW);
				}else{
					Results results = performNewSearch(request, advancedSearch);
					advancedSearch.setMode(MODE_NEW_SEARCH);
					modelAndView.getModelMap().addAttribute("results", results);
				}
				modelAndView.getModelMap().addAttribute("advancedSearch", advancedSearch);
				
				modelAndView.setViewName("home");
				return modelAndView;
			}
		} catch (Exception e) {

		}
		modelAndView.setViewName("nosaved-search");
		return modelAndView;
	}

	@RenderMapping(params = "myaction=simpleSearch")
	public ModelAndView search(@ModelAttribute(value = "advancedSearch") AdvancedSearch advancedSearch,
			RenderRequest request) {
		advancedSearch.setMode(MODE_NEW_SEARCH);
		AnalyzeLogger.logSimpleSearch(advancedSearch);
		Results results = performNewSearch(request, advancedSearch);
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");
		modelAndView.getModelMap().addAttribute("advancedSearch", advancedSearch);
		modelAndView.getModelMap().addAttribute("results", results);
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_SIMPLE_SEARCH);
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
			if (error == null && StringUtils.isNotBlank(advancedSearch.getTerm())) {
					SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
					handleSearchParameters(request, advancedSearch, solrQueryParameters);
					AnalyzeLogger.logAdvancedSearch(advancedSearch, solrQueryParameters);
					if (AdvancedSearch.VIEW_HIERARCHY.equals(advancedSearch.getView())) {
						results = performNewSearchForContextView(request, solrQueryParameters, advancedSearch);
					} else {
						results = performNewSearchForListView(request, solrQueryParameters, advancedSearch);
					}
					boolean showSuggestions = false;
					if (results.getSpellCheckResponse() != null) {
						List<Collation> suggestions = results.getSpellCheckResponse().getCollatedResults();
						if (suggestions != null) {
							for (Collation collation : suggestions) {
								showSuggestions = showSuggestions
										|| (collation.getNumberOfHits() > results.getTotalNumberOfResults());
							}
						}
					}
					results.setShowSuggestions(showSuggestions);
			} else {
				if (AdvancedSearch.VIEW_HIERARCHY.equals(advancedSearch.getView())) {
					results = new ContextResults();
				} else {
					results = new ListResults();
				}
				results.setErrorMessage(error);
			}

			// request.setAttribute("results", results);

		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the advanced search: Error: " + e.getMessage(), e);
		}
		return results;
	}

	public Results updateCurrentSearch(PortletRequest request, AdvancedSearch advancedSearch) {
		Results results = null;
		try {
			if (StringUtils.isNotBlank(advancedSearch.getTerm())){
				SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
				if (AdvancedSearch.VIEW_HIERARCHY.equals(advancedSearch.getView())) {
					handleSearchParametersForContextUpdate(request, advancedSearch, solrQueryParameters);
					AnalyzeLogger.logAdvancedSearch(advancedSearch, solrQueryParameters);
					results = performNewSearchForContextView(request, solrQueryParameters, advancedSearch);
				} else {
					handleSearchParametersForListUpdate(request, advancedSearch, solrQueryParameters);
					AnalyzeLogger.logUpdateAdvancedSearchList(advancedSearch, solrQueryParameters);
					results = performUpdateSearchForListView(request, solrQueryParameters, advancedSearch);
				}
			}else {
				if (AdvancedSearch.VIEW_HIERARCHY.equals(advancedSearch.getView())) {
					results = new ContextResults();
				} else {
					results = new ListResults();
				}				
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
		QueryResponse solrResponse = searcher.updateListView(solrQueryParameters, results.getPageSize()
				* (pageNumber - 1), results.getPageSize(), advancedSearch.getFacetSettingsList(),
				advancedSearch.getOrder(), advancedSearch.getStartdate(), advancedSearch.getEnddate());
		request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
		results.init(solrResponse, advancedSearch.getFacetSettingsList(), advancedSearch,
				new SpringResourceBundleSource(messageSource, request.getLocale()));
		updatePagination(advancedSearch, results);
		if (results.getTotalNumberOfResults() > 0) {
			results.setItems(new SolrDocumentListHolder(solrResponse));
		} else {
			results.setItems(new SolrDocumentListHolder());
		}
		return results;
	}

	protected ListResults performNewSearchForListView(PortletRequest request, SolrQueryParameters solrQueryParameters,
			AdvancedSearch advancedSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		results.setPageSize(Integer.parseInt(advancedSearch.getResultsperpage()));
		QueryResponse solrResponse = searcher.performNewSearchForListView(solrQueryParameters, results.getPageSize(),
				advancedSearch.getFacetSettingsList());
		request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
		results.init(solrResponse, advancedSearch.getFacetSettingsList(), advancedSearch,
				new SpringResourceBundleSource(messageSource, request.getLocale()));
		updatePagination(advancedSearch, results);
		if (results.getTotalNumberOfResults() > 0) {
			results.setItems(new SolrDocumentListHolder(solrResponse));
		} else {
			results.setItems(new SolrDocumentListHolder());
		}
		return results;
	}

	protected ContextResults performNewSearchForContextView(PortletRequest request,
			SolrQueryParameters solrQueryParameters, AdvancedSearch advancedSearch) throws SolrServerException {
		ContextResults results = new ContextResults();
		QueryResponse solrResponse = searcher.performNewSearchForContextView(solrQueryParameters);
		NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
		results.init(solrResponse, numberFormat);
		List<Count> countries = solrResponse.getFacetField(FacetType.COUNTRY.getName()).getValues();
		if (countries != null) {
			for (Count country : countries) {
				results.getCountries().add(
						new TreeFacetValue(country, TreeFacetValue.Type.COUNTRY, request.getLocale()));
			}
		}
		return results;

	}

	protected void handleSearchParameters(PortletRequest portletRequest, AdvancedSearch advancedSearch, SolrQueryParameters solrQueryParameters) {
		AdvancedSearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.TYPE,
				advancedSearch.getTypedocument());
		AdvancedSearchUtil.setFromDate(solrQueryParameters.getAndParameters(), advancedSearch.getFromdate(),
				advancedSearch.hasExactDateSearch());
		AdvancedSearchUtil.setToDate(solrQueryParameters.getAndParameters(), advancedSearch.getTodate(),
				advancedSearch.hasExactDateSearch());

		AdvancedSearchUtil.addSelectedNodesToQuery(advancedSearch.getSelectedNodesList(), solrQueryParameters);

		solrQueryParameters.setSolrFields(SolrField.getSolrFieldsByIdString(advancedSearch.getElement()));
		if (SEARCH_ALL_STRING.equals(advancedSearch.getTerm()) && portletRequest.getUserPrincipal() != null){
			solrQueryParameters.setTerm("");
		}else {
			solrQueryParameters.setTerm(advancedSearch.getTerm());
		}
		
		solrQueryParameters.setMatchAllWords(advancedSearch.matchAllWords());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.DAO, advancedSearch.getDaoList());
	}

	protected void handleSearchParametersForListUpdate(PortletRequest portletRequest, AdvancedSearch advancedSearch,
			SolrQueryParameters solrQueryParameters) {
		handleSearchParameters(portletRequest, advancedSearch, solrQueryParameters);
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.COUNTRY, advancedSearch.getCountryList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.AI, advancedSearch.getAiList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.TYPE, advancedSearch.getTypeList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.DATE_TYPE, advancedSearch.getDateTypeList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.ROLEDAO, advancedSearch.getRoledaoList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.FOND, advancedSearch.getFondList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.LEVEL, advancedSearch.getLevelList());
	}

	protected void updatePagination(AdvancedSearch advancedSearch, ListResults results) {
		Long totalNumberOfPages = results.getTotalNumberOfResults() / results.getPageSize();
		Long rest = results.getTotalNumberOfResults() % results.getPageSize();
		if (rest > 0) {
			totalNumberOfPages++;
		}
		results.setTotalNumberOfPages(totalNumberOfPages);
	}

	protected void handleSearchParametersForContextUpdate(PortletRequest portletRequest, AdvancedSearch advancedSearch,
			SolrQueryParameters solrQueryParameters) {
		handleSearchParameters(portletRequest, advancedSearch, solrQueryParameters);

	}

	public String validate(AdvancedSearch advancedSearch) {
		if (StringUtils.isNotBlank(advancedSearch.getFromdate())
				&& !AdvancedSearchUtil.isValidDate(advancedSearch.getFromdate())) {
			return "search.message.IncorrectDateTyped";

		}
		if (StringUtils.isNotBlank(advancedSearch.getTodate())
				&& !AdvancedSearchUtil.isValidDate(advancedSearch.getTodate())) {
			return "search.message.IncorrectDateTyped";
		}
		return null;
	}
}
