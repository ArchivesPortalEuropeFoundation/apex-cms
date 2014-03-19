package eu.archivesportaleurope.portal.eaccpf.search;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;
import eu.archivesportaleurope.portal.search.common.AbstractSearchController;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
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

	protected SolrQueryParameters handleSearchParameters(PortletRequest portletRequest, EacCpfSearch eacCpfSearch) {
		SolrQueryParameters solrQueryParameters = getSolrQueryParametersByForm(eacCpfSearch, portletRequest);
		AdvancedSearchUtil.setFromDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getFromdate(),
				eacCpfSearch.hasExactDateSearch());
		AdvancedSearchUtil.setToDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getTodate(),
				eacCpfSearch.hasExactDateSearch());

		AdvancedSearchUtil.addPublishedDates(eacCpfSearch.getPublishedFromDate(), eacCpfSearch.getPublishedToDate(), solrQueryParameters);	
		solrQueryParameters.setMatchAllWords(eacCpfSearch.matchAllWords());
		return solrQueryParameters;
	}
	protected ListResults performNewSearchForListView(PortletRequest request, SolrQueryParameters solrQueryParameters,
			EacCpfSearch eacCpfSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		if (solrQueryParameters != null){
			results.setPageSize(Integer.parseInt(eacCpfSearch.getResultsperpage()));
			List<ListFacetSettings> list = eacCpfSearch.getFacetSettingsList();
			list.clear();
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
