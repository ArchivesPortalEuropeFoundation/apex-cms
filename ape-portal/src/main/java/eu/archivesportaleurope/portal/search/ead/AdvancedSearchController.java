package eu.archivesportaleurope.portal.search.ead;

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
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.common.AnalyzeLogger;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.common.AbstractSearchController;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.ListResults;
import eu.archivesportaleurope.portal.search.common.Results;
import eu.archivesportaleurope.portal.search.common.SolrDocumentListHolder;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;
import eu.archivesportaleurope.portal.search.ead.tree.ContextResults;
import eu.archivesportaleurope.portal.search.ead.tree.TreeFacetValue;
import eu.archivesportaleurope.portal.search.saved.SavedSearchService;
import eu.archivesportaleurope.util.ApeUtil;

@Controller(value = "advancedSearchController")
@RequestMapping(value = "VIEW")
public class AdvancedSearchController extends AbstractSearchController{


	private final static Logger LOGGER = Logger.getLogger(AdvancedSearchController.class);


	private ResourceBundleMessageSource messageSource;
	private SavedSearchService savedSearchService;

	public void setSavedSearchService(SavedSearchService savedSearchService) {
		this.savedSearchService = savedSearchService;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public String showAdvancedSearch(RenderRequest request) {
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAD_SEARCH);
		return "home";
	}

	@RenderMapping(params = "myaction=showSavedSearch")
	public ModelAndView showSavedSearch(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAD_SEARCH_SAVED);
		String errorMessage = null;
		try {
			String id = request.getParameter("savedSearchId");
			String publishedFromDate = request.getParameter("publishedFromDate");
			String publishedToDate = request.getParameter("publishedToDate");
			String showOnlyNew = request.getParameter("showOnlyNew");
			Long savedSearchId = Long.parseLong(id);
			Long liferayUserId = null;
			if (request.getUserPrincipal() != null) {
				liferayUserId = Long.parseLong(request.getUserPrincipal().toString());
			}
			EadSavedSearch eadSavedSearch = savedSearchService.getEadSavedSearch(liferayUserId, savedSearchId);
			if (eadSavedSearch.isPublicSearch()){
				PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAD_SEARCH_PUBLIC_SAVED);
			}else {
				PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAD_SEARCH_MY_SAVED);
			}
			if (eadSavedSearch != null) {
				AdvancedSearch advancedSearch = savedSearchService.convert(eadSavedSearch);
				if (StringUtils.isNotEmpty(publishedFromDate)
						&& !AdvancedSearchUtil.isValidPublishedDate(publishedFromDate)) {
					errorMessage= "savedsearch.publisheddates.wrong";					

				}
				if (StringUtils.isNotEmpty(publishedToDate)
						&& !AdvancedSearchUtil.isValidPublishedDate(publishedToDate)) {
					errorMessage= "savedsearch.publisheddates.wrong";					

				}
				if (errorMessage == null){
					if ("true".equals(showOnlyNew)){
						advancedSearch.setPublishedFromDate(AdvancedSearchUtil.getFullDateTimePublishedDate(eadSavedSearch.getModifiedDate()));
					}else {
						advancedSearch.setPublishedFromDate(publishedFromDate);
						advancedSearch.setPublishedToDate(publishedToDate);
					}
					if (eadSavedSearch.isTemplate()){
						advancedSearch.setMode(AdvancedSearch.MODE_NEW);
					}else{
						Results results = updateCurrentSearch(request, advancedSearch);
						advancedSearch.setMode(AdvancedSearch.MODE_NEW_SEARCH);
						modelAndView.getModelMap().addAttribute("selectedRefinements", savedSearchService.convertToRefinements(request, advancedSearch, eadSavedSearch));
						modelAndView.getModelMap().addAttribute("results", results);
					}
					modelAndView.getModelMap().addAttribute("advancedSearch", advancedSearch);
					
					modelAndView.setViewName("home");
					return modelAndView;
				}
			}
		} catch (Exception e) {

		}
		if (errorMessage == null){
			errorMessage = "savedsearch.notexist";
		}
		modelAndView.getModelMap().addAttribute("errorMessage", errorMessage);
		modelAndView.setViewName("savedsearch-error");
		return modelAndView;
	}

	@RenderMapping(params = "myaction=simpleSearch")
	public ModelAndView search(@ModelAttribute(value = "advancedSearch") AdvancedSearch advancedSearch,
			RenderRequest request) {
		advancedSearch.setMode(AdvancedSearch.MODE_NEW_SEARCH);
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
		if (AdvancedSearch.MODE_NEW_SEARCH.equalsIgnoreCase(advancedSearch.getMode())) {
			results = performNewSearch(request, advancedSearch);
		} else if (AdvancedSearch.MODE_UPDATE_SEARCH.equalsIgnoreCase(advancedSearch.getMode())) {
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
				SolrQueryParameters solrQueryParameters = handleSearchParameters(request, advancedSearch);
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
				countOtherSearchResults(request, advancedSearch, results);
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
			LOGGER.error("There was an error during the execution of the advanced search: Error: " + ApeUtil.generateThrowableLog(e));
		}
		return results;
	}

	public Results updateCurrentSearch(PortletRequest request, AdvancedSearch advancedSearch) {
		Results results = null;
		try {
				if (AdvancedSearch.VIEW_HIERARCHY.equals(advancedSearch.getView())) {
					SolrQueryParameters solrQueryParameters = handleSearchParametersForContextUpdate(request, advancedSearch);
					AnalyzeLogger.logAdvancedSearch(advancedSearch, solrQueryParameters);
					results = performNewSearchForContextView(request, solrQueryParameters, advancedSearch);
				} else {
					SolrQueryParameters solrQueryParameters = handleSearchParametersForListUpdate(request, advancedSearch);
					AnalyzeLogger.logUpdateAdvancedSearchList(advancedSearch, solrQueryParameters);
					results = performUpdateSearchForListView(request, solrQueryParameters, advancedSearch);
				}
		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the advanced search: Error: " + ApeUtil.generateThrowableLog(e));
		}
		return results;
	}

	protected ListResults performUpdateSearchForListView(PortletRequest request,
			SolrQueryParameters solrQueryParameters, AdvancedSearch advancedSearch) throws SolrServerException,
			ParseException {
		ListResults results = new ListResults();
		if (solrQueryParameters != null){
			results.setPageSize(Integer.parseInt(advancedSearch.getResultsperpage()));
			Integer pageNumber = Integer.parseInt(advancedSearch.getPageNumber());
			QueryResponse solrResponse = getEadSearcher().updateListView(solrQueryParameters, results.getPageSize()
					* (pageNumber - 1), results.getPageSize(), advancedSearch.getFacetSettingsList(),
					advancedSearch.getOrder(), advancedSearch.getStartdate(), advancedSearch.getEnddate());
			request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
			results.init(solrResponse, advancedSearch.getFacetSettingsList(), advancedSearch,
					new SpringResourceBundleSource(messageSource, request.getLocale()));
			updatePagination(results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, true));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}
		}
		return results;
	}

	protected ListResults performNewSearchForListView(PortletRequest request, SolrQueryParameters solrQueryParameters,
			AdvancedSearch advancedSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		if (solrQueryParameters != null){
			results.setPageSize(Integer.parseInt(advancedSearch.getResultsperpage()));
			QueryResponse solrResponse = getEadSearcher().performNewSearchForListView(solrQueryParameters, results.getPageSize(),
					advancedSearch.getFacetSettingsList());
			request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
			results.init(solrResponse, advancedSearch.getFacetSettingsList(), advancedSearch,
					new SpringResourceBundleSource(messageSource, request.getLocale()));
			updatePagination(results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, true));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}
		}
		return results;
	}

	protected ContextResults performNewSearchForContextView(PortletRequest request,
			SolrQueryParameters solrQueryParameters, AdvancedSearch advancedSearch) throws SolrServerException {
		ContextResults results = new ContextResults();
		if (solrQueryParameters != null){
			QueryResponse solrResponse = getEadSearcher().performNewSearchForContextView(solrQueryParameters);
			NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
			results.init(solrResponse, numberFormat);
			List<Count> countries = solrResponse.getFacetField(FacetType.COUNTRY.getName()).getValues();
			if (countries != null) {
				for (Count country : countries) {
					results.getCountries().add(
							new TreeFacetValue(country, TreeFacetValue.Type.COUNTRY, request.getLocale()));
				}
			}
		}
		return results;

	}

	protected SolrQueryParameters handleSearchParameters(PortletRequest portletRequest, AdvancedSearch advancedSearch) {
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(advancedSearch, portletRequest);
		if (solrQueryParameters != null){
			AdvancedSearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.TYPE,
					advancedSearch.getTypedocument());
			AdvancedSearchUtil.setFromDate(solrQueryParameters.getAndParameters(), advancedSearch.getFromdate(),
					advancedSearch.hasExactDateSearch());
			AdvancedSearchUtil.setToDate(solrQueryParameters.getAndParameters(), advancedSearch.getTodate(),
					advancedSearch.hasExactDateSearch());
	
			AdvancedSearchUtil.addSelectedNodesToQuery(advancedSearch.getSelectedNodesList(), solrQueryParameters);
			AdvancedSearchUtil.addPublishedDates(advancedSearch.getPublishedFromDate(), advancedSearch.getPublishedToDate(), solrQueryParameters);
			solrQueryParameters.setSolrFields(SolrField.getSolrFieldsByIdString(advancedSearch.getElement()));
			AdvancedSearchUtil.setParameter(solrQueryParameters.getAndParameters(), FacetType.DAO.getName(), advancedSearch.getSimpleSearchDao());
		}
		return solrQueryParameters;
	}

	protected SolrQueryParameters handleSearchParametersForListUpdate(PortletRequest portletRequest, AdvancedSearch advancedSearch) {
		SolrQueryParameters solrQueryParameters = handleSearchParameters(portletRequest, advancedSearch);
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.COUNTRY, advancedSearch.getCountryList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.AI, advancedSearch.getAiList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.TYPE, advancedSearch.getTypeList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.DATE_TYPE, advancedSearch.getDateTypeList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.DAO, advancedSearch.getDaoList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.ROLEDAO, advancedSearch.getRoledaoList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.FOND, advancedSearch.getFondList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.LEVEL, advancedSearch.getLevelList());
		return solrQueryParameters;
	}


	protected SolrQueryParameters handleSearchParametersForContextUpdate(PortletRequest portletRequest, AdvancedSearch advancedSearch) {
		return handleSearchParameters(portletRequest, advancedSearch);

	}


	protected void countOtherSearchResults(PortletRequest request, 
			AdvancedSearch advancedSearch, Results results) throws SolrServerException, ParseException{
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(advancedSearch, request);
		if (solrQueryParameters != null){ 
			results.setEacCpfNumberOfResults(getEacCpfSearcher().getNumberOfResults(solrQueryParameters));
			results.setEadNumberOfResults(results.getTotalNumberOfResults());
			results.setEagNumberOfResults(getEagSearcher().getNumberOfResults(solrQueryParameters));
		}
	}
}
