package eu.archivesportaleurope.portal.eaccpf.search;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.archivesportaleurope.portal.common.AnalyzeLogger;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;
import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;
import eu.archivesportaleurope.portal.search.advanced.tree.ContextResults;
import eu.archivesportaleurope.portal.search.common.AbstractSearchController;
import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.ListResults;
import eu.archivesportaleurope.portal.search.common.Results;
import eu.archivesportaleurope.portal.search.common.SolrDocumentListHolder;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;

/**
 * 
 * This is display eac cpf controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "searchEacCpfContoller")
@RequestMapping(value = "VIEW")
public class SearchEacCpfContoller extends AbstractSearchController{
	private final static Logger LOGGER = Logger.getLogger(SearchEacCpfContoller.class);
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	

	@RenderMapping
	public ModelAndView searchEacCpf() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		return modelAndView;
	}
	@RenderMapping(params = "myaction=simpleSearch")
	public ModelAndView searchSimple(@ModelAttribute(value = "eacCpfSearch")  EacCpfSearch eacCpfSearch,
			RenderRequest request) throws SolrServerException, ParseException {
		ModelAndView modelAndView = new ModelAndView();
		eacCpfSearch.setMode(EacCpfSearch.MODE_NEW_SEARCH);
		modelAndView.setViewName("index");
		modelAndView.getModelMap().addAttribute("eacCpfSearch", eacCpfSearch);
		if (StringUtils.isNotBlank(eacCpfSearch.getTerm())){
			SolrQueryParameters solrQueryParameters = handleSearchParameters(request, eacCpfSearch);
			ListResults results = performNewSearchForListView(request, solrQueryParameters, eacCpfSearch);
			modelAndView.getModelMap().addAttribute("results", results);
		}
		return modelAndView;
	}
	@RenderMapping(params = "myaction=eacCpfSearch")
	public ModelAndView search(@ModelAttribute(value = "eacCpfSearch") EacCpfSearch eacCpfSearch,RenderRequest request) throws SolrServerException, ParseException {
		ModelAndView modelAndView = new ModelAndView();
		if (StringUtils.isNotBlank(eacCpfSearch.getTerm())){
			SolrQueryParameters solrQueryParameters = handleSearchParameters(request, eacCpfSearch);
			ListResults results = performNewSearchForListView(request, solrQueryParameters, eacCpfSearch);
			modelAndView.getModelMap().addAttribute("results", results);
		}

		modelAndView.setViewName("index");
		
		return modelAndView;
	}

	@ResourceMapping(value = "eacCpfSearch")
	public ModelAndView searchAjax(@ModelAttribute(value = "eacCpfSearch") EacCpfSearch eacCpfSearch,
			BindingResult bindingResult, ResourceRequest request) throws SolrServerException, ParseException {
		Results results = null;
		if (AbstractSearchForm.MODE_NEW_SEARCH.equalsIgnoreCase(eacCpfSearch.getMode())) {
			LOGGER.info("NEW");
			results = performNewSearch(request, eacCpfSearch);
		} else if (AbstractSearchForm.MODE_UPDATE_SEARCH.equalsIgnoreCase(eacCpfSearch.getMode())) {
			LOGGER.info("update");
			results = updateCurrentSearch(request, eacCpfSearch);

		}else {
			LOGGER.info("other");
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("results");
		modelAndView.getModelMap().addAttribute("eacCpfSearch", eacCpfSearch);
		modelAndView.getModelMap().addAttribute("results", results);
		return modelAndView;
	}
	public Results performNewSearch(PortletRequest request, EacCpfSearch eacCpfSearch) {
		Results results = null;
		try {
			String error = validate(eacCpfSearch);
			if (error == null) {
				SolrQueryParameters solrQueryParameters = handleSearchParameters(request, eacCpfSearch);
				results = performNewSearchForListView(request, solrQueryParameters, eacCpfSearch);
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
				countOtherSearchResults(request, eacCpfSearch, results);
			} else {
				results = new ListResults();
				results.setErrorMessage(error);
			}
			// request.setAttribute("results", results);

		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the eac cpf search: Error: " + e.getMessage(), e);
		}
		return results;
	}
	public Results updateCurrentSearch(PortletRequest request, EacCpfSearch eacCpfSearch) {
		Results results = null;
		try {
			SolrQueryParameters solrQueryParameters = handleSearchParametersForListUpdate(request, eacCpfSearch);
			results = performUpdateSearchForListView(request, solrQueryParameters, eacCpfSearch);
		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the advanced search: Error: " + e.getMessage(), e);
		}
		return results;
	}
	protected SolrQueryParameters handleSearchParameters(PortletRequest portletRequest, EacCpfSearch eacCpfSearch) {
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(eacCpfSearch, portletRequest);
		AdvancedSearchUtil.setFromDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getFromdate(),
				eacCpfSearch.hasExactDateSearch());
		AdvancedSearchUtil.setToDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getTodate(),
				eacCpfSearch.hasExactDateSearch());

		AdvancedSearchUtil.addPublishedDates(eacCpfSearch.getPublishedFromDate(), eacCpfSearch.getPublishedToDate(), solrQueryParameters);	
		return solrQueryParameters;
	}
	protected ListResults performNewSearchForListView(PortletRequest request, SolrQueryParameters solrQueryParameters,
			EacCpfSearch eacCpfSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		if (solrQueryParameters != null){
			results.setPageSize(Integer.parseInt(eacCpfSearch.getResultsperpage()));
			List<ListFacetSettings> list = eacCpfSearch.getFacetSettingsList();
			QueryResponse solrResponse = getEacCpfSearcher().performNewSearchForListView(solrQueryParameters, results.getPageSize(),
					list);
			request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
			results.init(solrResponse, list, eacCpfSearch,
					new SpringResourceBundleSource(messageSource, request.getLocale()));
			updatePagination( results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, false));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}
			countOtherSearchResults(request, eacCpfSearch, results);
		}
		return results;
	}
	protected ListResults performUpdateSearchForListView(PortletRequest request,
			SolrQueryParameters solrQueryParameters, EacCpfSearch eacCpfSearch) throws SolrServerException,
			ParseException {
		ListResults results = new ListResults();
		if (solrQueryParameters != null){
			results.setPageSize(Integer.parseInt(eacCpfSearch.getResultsperpage()));
			Integer pageNumber = Integer.parseInt(eacCpfSearch.getPageNumber());
			QueryResponse solrResponse = getEacCpfSearcher().updateListView(solrQueryParameters, results.getPageSize()
					* (pageNumber - 1), results.getPageSize(), eacCpfSearch.getFacetSettingsList(),
					eacCpfSearch.getOrder(), eacCpfSearch.getStartdate(), eacCpfSearch.getEnddate());
			request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
			results.init(solrResponse, eacCpfSearch.getFacetSettingsList(), eacCpfSearch,
					new SpringResourceBundleSource(messageSource, request.getLocale()));
			updatePagination(results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, false));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}
		}
		return results;
	}
	protected SolrQueryParameters handleSearchParametersForListUpdate(PortletRequest portletRequest, EacCpfSearch eacCpfSearch) {
		SolrQueryParameters solrQueryParameters = handleSearchParameters(portletRequest, eacCpfSearch);
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.EAC_CPF_OCCUPATION, eacCpfSearch.getOccupationFacetList());
		AdvancedSearchUtil.addRefinement(solrQueryParameters, FacetType.EAC_CPF_PLACES, eacCpfSearch.getPlacesFacetList());
		return solrQueryParameters;
	}
	
	@ModelAttribute("eacCpfSearch")
	public EacCpfSearch getCommandObject() {
		return new EacCpfSearch();
	}
	protected void countOtherSearchResults(PortletRequest request, 
			EacCpfSearch eacCpfSearch, Results results) throws SolrServerException, ParseException{
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(eacCpfSearch, request);
		results.setEacCpfNumberOfResults(results.getTotalNumberOfResults());
		results.setEadNumberOfResults(getEadSearcher().getNumberOfResults(solrQueryParameters));
	}
}
