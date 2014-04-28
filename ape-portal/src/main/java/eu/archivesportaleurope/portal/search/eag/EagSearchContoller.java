package eu.archivesportaleurope.portal.search.eag;

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

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.solr.SolrValues;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.common.AbstractSearchController;
import eu.archivesportaleurope.portal.search.common.AbstractSearchForm;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.ListResults;
import eu.archivesportaleurope.portal.search.common.Results;
import eu.archivesportaleurope.portal.search.common.SearchUtil;
import eu.archivesportaleurope.portal.search.common.SolrDocumentListHolder;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;
import eu.archivesportaleurope.portal.search.ead.list.ListFacetSettings;
import eu.archivesportaleurope.util.ApeUtil;

/**
 * 
 * This is display eag controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "searchEagContoller")
@RequestMapping(value = "VIEW")
public class EagSearchContoller extends AbstractSearchController{
	private final static Logger LOGGER = Logger.getLogger(EagSearchContoller.class);
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	

	@RenderMapping
	public ModelAndView searchEag(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAG_SEARCH);
		modelAndView.setViewName("index");
		return modelAndView;
	}
	@RenderMapping(params = "myaction=simpleSearch")
	public ModelAndView searchSimple(@ModelAttribute(value = "eacCpfSearch")  EagSearch eagSearch,
			RenderRequest request) throws SolrServerException, ParseException {
		ModelAndView modelAndView = new ModelAndView();
		eagSearch.setMode(EagSearch.MODE_NEW_SEARCH);
		modelAndView.setViewName("index");
		modelAndView.getModelMap().addAttribute("eagSearch", eagSearch);
		if (StringUtils.isNotBlank(eagSearch.getTerm())){
			ListResults results = performNewSearch(request,  eagSearch);
			modelAndView.getModelMap().addAttribute("results", results);
		}
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAG_SEARCH);
		return modelAndView;
	}
	@RenderMapping(params = "myaction=eagSearch")
	public ModelAndView search(@ModelAttribute(value = "eagSearch") EagSearch eagSearch,RenderRequest request) throws SolrServerException, ParseException {
		ModelAndView modelAndView = new ModelAndView();
		if (StringUtils.isNotBlank(eagSearch.getTerm())){
			ListResults results = performNewSearch(request, eagSearch);
			modelAndView.getModelMap().addAttribute("results", results);
		}
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAG_SEARCH);
		modelAndView.setViewName("index");
		
		return modelAndView;
	}

	@ResourceMapping(value = "eagSearch")
	public ModelAndView searchAjax(@ModelAttribute(value = "eagSearch") EagSearch eagSearch,
			BindingResult bindingResult, ResourceRequest request) throws SolrServerException, ParseException {
		Results results = null;
		if (AbstractSearchForm.MODE_NEW_SEARCH.equalsIgnoreCase(eagSearch.getMode())) {
			results = performNewSearch(request, eagSearch);
		} else if (AbstractSearchForm.MODE_UPDATE_SEARCH.equalsIgnoreCase(eagSearch.getMode())) {
			results = updateCurrentSearch(request, eagSearch);

		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("results");
		modelAndView.getModelMap().addAttribute("eagSearch", eagSearch);
		modelAndView.getModelMap().addAttribute("results", results);
		return modelAndView;
	}
	public ListResults performNewSearch(PortletRequest request, EagSearch eagSearch) {
		ListResults results = null;
		try {
			String error = validate(eagSearch);
			if (error == null) {
				SolrQueryParameters solrQueryParameters = handleSearchParameters(request, eagSearch);
				results = performNewSearchForListView(request, solrQueryParameters, eagSearch);
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
				countOtherSearchResults(request, eagSearch, results);
			} else {
				results = new ListResults();
				results.setErrorMessage(error);
			}
			// request.setAttribute("results", results);

		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the eag search: Error: " + ApeUtil.generateThrowableLog(e));
		}
		return results;
	}
	public Results updateCurrentSearch(PortletRequest request, EagSearch eagSearch) {
		Results results = null;
		try {
			SolrQueryParameters solrQueryParameters = handleSearchParametersForListUpdate(request, eagSearch);
			results = performUpdateSearchForListView(request, solrQueryParameters, eagSearch);
		} catch (Exception e) {
			LOGGER.error("There was an error during the execution of the advanced search: Error: " + ApeUtil.generateThrowableLog(e));
		}
		return results;
	}

	protected ListResults performNewSearchForListView(PortletRequest request, SolrQueryParameters solrQueryParameters,
			EagSearch eagSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		if (solrQueryParameters != null){
			results.setPageSize(Integer.parseInt(eagSearch.getResultsperpage()));
			List<ListFacetSettings> list = eagSearch.getFacetSettingsList();
			QueryResponse solrResponse = getEagSearcher().performNewSearchForListView(solrQueryParameters, results.getPageSize(),
					list);
			request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
			results.init(solrResponse, list, eagSearch,
					new SpringResourceBundleSource(messageSource, request.getLocale()));
			updatePagination( results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, EagSearchResult.class));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}

		}
		return results;
	}
	protected ListResults performUpdateSearchForListView(PortletRequest request,
			SolrQueryParameters solrQueryParameters, EagSearch eagSearch) throws SolrServerException,
			ParseException {
		ListResults results = new ListResults();
		if (solrQueryParameters != null){
			results.setPageSize(Integer.parseInt(eagSearch.getResultsperpage()));
			Integer pageNumber = Integer.parseInt(eagSearch.getPageNumber());
			QueryResponse solrResponse = getEagSearcher().updateListView(solrQueryParameters, results.getPageSize()
					* (pageNumber - 1), results.getPageSize(), eagSearch.getFacetSettingsList(),
					eagSearch.getOrder(), eagSearch.getStartdate(), eagSearch.getEnddate());
			request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
			results.init(solrResponse, eagSearch.getFacetSettingsList(), eagSearch,
					new SpringResourceBundleSource(messageSource, request.getLocale()));
			updatePagination(results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, EagSearchResult.class));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}
		}
		return results;
	}
	
	protected SolrQueryParameters handleSearchParameters(PortletRequest portletRequest, EagSearch eagSearch) {
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(eagSearch, portletRequest);
		if (solrQueryParameters != null){
//			SearchUtil.setFromDate(solrQueryParameters.getAndParameters(), eagSearch.getFromdate(),
//					eagSearch.hasExactDateSearch());
//			SearchUtil.setToDate(solrQueryParameters.getAndParameters(), eagSearch.getTodate(),
//					eagSearch.hasExactDateSearch());
//			SearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.EAC_CPF_FACET_ENTITY_TYPE,
//					eagSearch.getEntityType());
//			solrQueryParameters.setSolrField(SolrField.getSolrField(eagSearch.getElement()));
//	
//			SearchUtil.addPublishedDates(eagSearch.getPublishedFromDate(), eagSearch.getPublishedToDate(), solrQueryParameters);	
		}
		return solrQueryParameters;
	}
	
	protected SolrQueryParameters handleSearchParametersForListUpdate(PortletRequest portletRequest, EagSearch eagSearch) {
		SolrQueryParameters solrQueryParameters = handleSearchParameters(portletRequest, eagSearch);
		if (solrQueryParameters != null){
			SearchUtil.addRefinement(solrQueryParameters, FacetType.COUNTRY, eagSearch.getCountryList());
			SearchUtil.addRefinement(solrQueryParameters, FacetType.LANGUAGE, eagSearch.getLanguageList());
		}
		return solrQueryParameters;
	}
	
	@ModelAttribute("eacCpfSearch")
	public EagSearch getCommandObject(PortletRequest portletRequest) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource,
				portletRequest.getLocale());
		EagSearch eagSearch = new EagSearch();
		eagSearch.getElementValues().put(EagSearch.NOSELECTION, source.getString("advancedsearch.text.noselection"));
		eagSearch.getElementValues().put(SolrFields.EAC_CPF_NAMES, source.getString("advancedsearch.eaccpf.element.name"));
		eagSearch.getElementValues().put(SolrFields.EAC_CPF_ENTITY_ID, source.getString("advancedsearch.eaccpf.element.id"));	
		eagSearch.getElementValues().put(SolrFields.EAC_CPF_PLACES, source.getString("advancedsearch.facet.title.placesfacet"));			
		eagSearch.getElementValues().put(SolrFields.EAC_CPF_OCCUPATION, source.getString("advancedsearch.facet.title.occupationsfacet"));	
		eagSearch.getElementValues().put(SolrFields.EAC_CPF_FUNCTION, source.getString("advancedsearch.facet.title.functionsfacet"));			
		eagSearch.getElementValues().put(SolrFields.EAC_CPF_MANDATE, source.getString("advancedsearch.facet.title.mandatesfacet"));		
        return eagSearch;
    }
	protected void countOtherSearchResults(PortletRequest request, 
			EagSearch eagSearch, Results results) throws SolrServerException, ParseException{
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(eagSearch, request);
		if (solrQueryParameters != null){ 
			results.setEacCpfNumberOfResults(results.getTotalNumberOfResults());
			results.setEadNumberOfResults(getEadSearcher().getNumberOfResults(solrQueryParameters));
			results.setEagNumberOfResults(getEagSearcher().getNumberOfResults(solrQueryParameters));
		}
	}
}
