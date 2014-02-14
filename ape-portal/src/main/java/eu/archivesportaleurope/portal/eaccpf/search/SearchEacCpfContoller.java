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
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;
import eu.archivesportaleurope.portal.search.advanced.list.ListFacetSettings;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
import eu.archivesportaleurope.portal.search.common.EacCpfSearcher;
import eu.archivesportaleurope.portal.search.common.ListResults;
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
public class SearchEacCpfContoller {
	private final static Logger LOGGER = Logger.getLogger(SearchEacCpfContoller.class);
	private MessageSource messageSource;
	private EacCpfSearcher searcher;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public void setSearcher(EacCpfSearcher searcher) {
		this.searcher = searcher;
	}

	@RenderMapping
	public ModelAndView searchEacCpf() {
		LOGGER.info("no");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		return modelAndView;
	}
	@RenderMapping(params = "myaction=eacCpfSearch")
	public ModelAndView search(@ModelAttribute(value = "eacCpfSearch") EacCpfSearch eacCpfSearch,RenderRequest request) throws SolrServerException, ParseException {
		ModelAndView modelAndView = new ModelAndView();
		if (StringUtils.isNotBlank(eacCpfSearch.getTerm())){
			SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
			handleSearchParameters(request, eacCpfSearch, solrQueryParameters);
			ListResults results = performNewSearchForListView(request, solrQueryParameters, eacCpfSearch);
			modelAndView.getModelMap().addAttribute("results", results);
		}

		modelAndView.setViewName("index");
		
		return modelAndView;
	}

	protected void handleSearchParameters(PortletRequest portletRequest, EacCpfSearch eacCpfSearch, SolrQueryParameters solrQueryParameters) {
		AdvancedSearchUtil.setFromDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getFromdate(),
				eacCpfSearch.hasExactDateSearch());
		AdvancedSearchUtil.setToDate(solrQueryParameters.getAndParameters(), eacCpfSearch.getTodate(),
				eacCpfSearch.hasExactDateSearch());

		AdvancedSearchUtil.addPublishedDates(eacCpfSearch.getPublishedFromDate(), eacCpfSearch.getPublishedToDate(), solrQueryParameters);
		if (AdvancedSearch.SEARCH_ALL_STRING.equals(eacCpfSearch.getTerm())){
			solrQueryParameters.setTerm("");
		}else {
			solrQueryParameters.setTerm(eacCpfSearch.getTerm());
		}
		
		solrQueryParameters.setMatchAllWords(eacCpfSearch.matchAllWords());
	}
	protected ListResults performNewSearchForListView(PortletRequest request, SolrQueryParameters solrQueryParameters,
			EacCpfSearch eacCpfSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		results.setPageSize(Integer.parseInt(eacCpfSearch.getResultsperpage()));
		List<ListFacetSettings> list = eacCpfSearch.getFacetSettingsList();
		list.clear();
		QueryResponse solrResponse = searcher.performNewSearchForListView(solrQueryParameters, results.getPageSize(),
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
		return results;
	}
	protected void updatePagination( ListResults results) {
		Long totalNumberOfPages = results.getTotalNumberOfResults() / results.getPageSize();
		Long rest = results.getTotalNumberOfResults() % results.getPageSize();
		if (rest > 0) {
			totalNumberOfPages++;
		}
		results.setTotalNumberOfPages(totalNumberOfPages);
	}
	@ModelAttribute("eacCpfSearch")
	public EacCpfSearch getCommandObject() {
		return new EacCpfSearch();
	}
}
