package eu.archivesportaleurope.portal.search.eaccpf;

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

import eu.apenet.commons.solr.SolrField;
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

/**
 * 
 * This is display eac cpf controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "searchEacCpfContoller")
@RequestMapping(value = "VIEW")
public class EacCpfSearchContoller extends AbstractSearchController{
	private final static Logger LOGGER = Logger.getLogger(EacCpfSearchContoller.class);
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	

	@RenderMapping
	public ModelAndView searchEacCpf(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAC_CPF_SEARCH);
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
			ListResults results = performNewSearch(request,  eacCpfSearch);
			modelAndView.getModelMap().addAttribute("results", results);
		}
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAC_CPF_SEARCH);
		return modelAndView;
	}
	@RenderMapping(params = "myaction=eacCpfSearch")
	public ModelAndView search(@ModelAttribute(value = "eacCpfSearch") EacCpfSearch eacCpfSearch,RenderRequest request) throws SolrServerException, ParseException {
		ModelAndView modelAndView = new ModelAndView();
		if (StringUtils.isNotBlank(eacCpfSearch.getTerm())){
			ListResults results = performNewSearch(request, eacCpfSearch);
			modelAndView.getModelMap().addAttribute("results", results);
		}
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_EAC_CPF_SEARCH);
		modelAndView.setViewName("index");
		
		return modelAndView;
	}

	@ResourceMapping(value = "eacCpfSearch")
	public ModelAndView searchAjax(@ModelAttribute(value = "eacCpfSearch") EacCpfSearch eacCpfSearch,
			BindingResult bindingResult, ResourceRequest request) throws SolrServerException, ParseException {
		Results results = null;
		if (AbstractSearchForm.MODE_NEW_SEARCH.equalsIgnoreCase(eacCpfSearch.getMode())) {
			results = performNewSearch(request, eacCpfSearch);
		} else if (AbstractSearchForm.MODE_UPDATE_SEARCH.equalsIgnoreCase(eacCpfSearch.getMode())) {
			results = updateCurrentSearch(request, eacCpfSearch);

		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("results");
		modelAndView.getModelMap().addAttribute("eacCpfSearch", eacCpfSearch);
		modelAndView.getModelMap().addAttribute("results", results);
		return modelAndView;
	}
	public ListResults performNewSearch(PortletRequest request, EacCpfSearch eacCpfSearch) {
		ListResults results = null;
		try {
			String error = validate(eacCpfSearch, request);
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
			LOGGER.error(e.getMessage());
		}
		return results;
	}
	public Results updateCurrentSearch(PortletRequest request, EacCpfSearch eacCpfSearch) {
		Results results = null;
		try {
			SolrQueryParameters solrQueryParameters = handleSearchParametersForListUpdate(request, eacCpfSearch);
			results = performUpdateSearchForListView(request, solrQueryParameters, eacCpfSearch);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return results;
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
			SpringResourceBundleSource springResourceBundleSource = new SpringResourceBundleSource(messageSource, request.getLocale());
			results.init(solrResponse, list, eacCpfSearch,springResourceBundleSource);
			updatePagination( results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, EacCpfSearchResult.class,springResourceBundleSource));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}

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
			SpringResourceBundleSource springResourceBundleSource = new SpringResourceBundleSource(messageSource, request.getLocale());
			results.init(solrResponse, eacCpfSearch.getFacetSettingsList(), eacCpfSearch,springResourceBundleSource);
			updatePagination(results);
			if (results.getTotalNumberOfResults() > 0) {
				results.setItems(new SolrDocumentListHolder(solrResponse, EacCpfSearchResult.class,springResourceBundleSource));
			} else {
				results.setItems(new SolrDocumentListHolder());
			}
		}
		return results;
	}
	
	protected SolrQueryParameters handleSearchParameters(PortletRequest portletRequest, EacCpfSearch eacCpfSearch) {
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(eacCpfSearch, portletRequest);
		if (solrQueryParameters != null){
			SearchUtil.setFromDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getFromdate(),
					eacCpfSearch.hasExactDateSearch());
			SearchUtil.setToDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getTodate(),
					eacCpfSearch.hasExactDateSearch());
			SearchUtil.setParameter(solrQueryParameters.getAndParameters(), SolrFields.EAC_CPF_FACET_ENTITY_TYPE,
					eacCpfSearch.getEntityType());
			solrQueryParameters.setSolrField(SolrField.getSolrField(eacCpfSearch.getElement()));
	
			SearchUtil.addPublishedDates(eacCpfSearch.getPublishedFromDate(), eacCpfSearch.getPublishedToDate(), solrQueryParameters);	
		}
		return solrQueryParameters;
	}
	
	protected SolrQueryParameters handleSearchParametersForListUpdate(PortletRequest portletRequest, EacCpfSearch eacCpfSearch) {
		SolrQueryParameters solrQueryParameters = handleSearchParameters(portletRequest, eacCpfSearch);
		if (solrQueryParameters != null){
			SearchUtil.addRefinement(solrQueryParameters, FacetType.COUNTRY, eacCpfSearch.getCountryList());
			SearchUtil.addRefinement(solrQueryParameters, FacetType.AI, eacCpfSearch.getAiList());
			SearchUtil.addRefinement(solrQueryParameters, FacetType.EAC_CPF_ENTITY_TYPE, eacCpfSearch.getEntityTypeFacetList());
			SearchUtil.addRefinement(solrQueryParameters, FacetType.LANGUAGE, eacCpfSearch.getLanguageList());
			SearchUtil.addRefinement(solrQueryParameters, FacetType.DATE_TYPE, eacCpfSearch.getDateTypeList());
			SearchUtil.addTextRefinement(solrQueryParameters, FacetType.EAC_CPF_OCCUPATION, eacCpfSearch.getOccupationsFacetList());
			SearchUtil.addTextRefinement(solrQueryParameters, FacetType.EAC_CPF_PLACES, eacCpfSearch.getPlacesFacetList());
			SearchUtil.addTextRefinement(solrQueryParameters, FacetType.EAC_CPF_MANDATE, eacCpfSearch.getMandatesFacetList());
			SearchUtil.addTextRefinement(solrQueryParameters, FacetType.EAC_CPF_FUNCTION, eacCpfSearch.getFunctionsFacetList());
		}
		return solrQueryParameters;
	}
	
	@ModelAttribute("eacCpfSearch")
	public EacCpfSearch getCommandObject(PortletRequest portletRequest) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource,
				portletRequest.getLocale());
		EacCpfSearch eacCpfSearch = new EacCpfSearch();
		eacCpfSearch.getElementValues().put(EacCpfSearch.NOSELECTION, source.getString("advancedsearch.text.noselection"));
		eacCpfSearch.getElementValues().put(SolrFields.EAC_CPF_NAMES, source.getString("advancedsearch.eaccpf.element.name"));
		eacCpfSearch.getElementValues().put(SolrFields.EAC_CPF_ENTITY_ID, source.getString("advancedsearch.eaccpf.element.id"));	
		eacCpfSearch.getElementValues().put(SolrFields.EAC_CPF_PLACES, source.getString("advancedsearch.facet.title.placesfacet"));			
		eacCpfSearch.getElementValues().put(SolrFields.EAC_CPF_OCCUPATION, source.getString("advancedsearch.facet.title.occupationsfacet"));	
		eacCpfSearch.getElementValues().put(SolrFields.EAC_CPF_MANDATE, source.getString("advancedsearch.facet.title.mandatesfacet"));
		eacCpfSearch.getElementValues().put(SolrFields.EAC_CPF_FUNCTION, source.getString("advancedsearch.facet.title.functionsfacet"));			
		eacCpfSearch.getEntityTypeValues().put("", source.getString("advancedsearch.text.noselection"));		
		eacCpfSearch.getEntityTypeValues().put(SolrValues.EAC_CPF_FACET_ENTITY_TYPE_PERSON, source.getString("advancedsearch.facet.value.eaccpf.entitytype.person"));
		eacCpfSearch.getEntityTypeValues().put(SolrValues.EAC_CPF_FACET_ENTITY_TYPE_FAMILY, source.getString("advancedsearch.facet.value.eaccpf.entitytype.family"));		
		eacCpfSearch.getEntityTypeValues().put(SolrValues.EAC_CPF_FACET_ENTITY_TYPE_CORPORATE_BODY, source.getString("advancedsearch.facet.value.eaccpf.entitytype.corporatebody"));			
        return eacCpfSearch;
    }
	protected void countOtherSearchResults(PortletRequest request, 
			EacCpfSearch eacCpfSearch, Results results) throws SolrServerException, ParseException{
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(eacCpfSearch, request);
		if (solrQueryParameters != null){ 
			results.setEacCpfNumberOfResults(results.getTotalNumberOfResults());
			results.setEadNumberOfResults(getEadSearcher().getNumberOfResults(solrQueryParameters));
			results.setEagNumberOfResults(getEagSearcher().getNumberOfResults(solrQueryParameters));
		}
	}
}
