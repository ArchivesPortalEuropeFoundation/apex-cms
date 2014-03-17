package eu.archivesportaleurope.portal.search.simple;

import java.text.NumberFormat;
import java.text.ParseException;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderRequest;
import javax.portlet.WindowState;
import javax.portlet.WindowStateException;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;
import eu.archivesportaleurope.portal.search.common.AbstractSearcher;
import eu.archivesportaleurope.portal.search.common.EacCpfSearcher;
import eu.archivesportaleurope.portal.search.common.EadSearcher;
import eu.archivesportaleurope.portal.search.common.ListResults;
import eu.archivesportaleurope.portal.search.common.SolrDocumentListHolder;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;

@Controller(value = "simpleSearchController")
@RequestMapping(value = "VIEW")
public class SimpleSearchController {
	//private final static Logger LOGGER = Logger.getLogger(SimpleSearchController.class);
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	private EadSearcher eadSearcher;
	private EacCpfSearcher eacCpfSearcher;
	private ResourceBundleMessageSource messageSource;

	
	
	public void setEadSearcher(EadSearcher eadSearcher) {
		this.eadSearcher = eadSearcher;
	}

	public void setEacCpfSearcher(EacCpfSearcher eacCpfSearcher) {
		this.eacCpfSearcher = eacCpfSearcher;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@RenderMapping
	public ModelAndView showSimpleSearch(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");

		modelAndView.getModelMap().addAttribute("institutions", archivalInstitutionDAO.countArchivalInstitutionsWithContentIndexed());
		NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
		modelAndView.getModelMap().addAttribute("units", numberFormat.format(eadDAO.getTotalCountOfUnits()));
		String numberOfDaoUnitsString= request.getPreferences().getValue("numberOfDaoUnits", "0");
		long numberOfDaoUnits = 0;
		if (StringUtils.isNotBlank(numberOfDaoUnitsString)){
			numberOfDaoUnits = Long.parseLong(numberOfDaoUnitsString);
			
		}
		modelAndView.getModelMap().addAttribute("numberOfDaoUnits", numberFormat.format(numberOfDaoUnits));
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_HOME);
		return modelAndView;
	}
	
	@RenderMapping(params = "myaction=displayResults")
	public ModelAndView displayResults(@ModelAttribute(value = "simpleSearch") SimpleSearch simpleSearch, RenderRequest request) throws SolrServerException, ParseException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("results");

		modelAndView.getModelMap().addAttribute("institutions", archivalInstitutionDAO.countArchivalInstitutionsWithContentIndexed());
		NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
		modelAndView.getModelMap().addAttribute("units", numberFormat.format(eadDAO.getTotalCountOfUnits()));
		String numberOfDaoUnitsString= request.getPreferences().getValue("numberOfDaoUnits", "0");
		long numberOfDaoUnits = 0;
		if (StringUtils.isNotBlank(numberOfDaoUnitsString)){
			numberOfDaoUnits = Long.parseLong(numberOfDaoUnitsString);
			
		}
		modelAndView.getModelMap().addAttribute("numberOfDaoUnits", numberFormat.format(numberOfDaoUnits));
		
		modelAndView.getModelMap().addAttribute("eadResults", performNewSearchForListView(eadSearcher,request,simpleSearch));
		modelAndView.getModelMap().addAttribute("eacCpfResults", performNewSearchForListView(eacCpfSearcher,request,simpleSearch));
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_HOME);
		return modelAndView;
	}
	@ActionMapping(params = "myaction=simpleSearch")
	public void showSavedSearch(ActionRequest request, ActionResponse response) throws WindowStateException {
		response.setWindowState(WindowState.MAXIMIZED);
		response.setRenderParameter("term", request.getParameter("term"));
		response.setRenderParameter("myaction", "displayResults");
	}
	@ModelAttribute("simpleSearch")
	public SimpleSearch getCommandObject() {
		return new SimpleSearch();
	}
	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}
	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}
	protected ListResults performNewSearchForListView(AbstractSearcher abstractSearcher, PortletRequest request, 
			SimpleSearch simpleSearch) throws SolrServerException, ParseException {
		ListResults results = new ListResults();
		SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
		results.setPageSize(Integer.parseInt(simpleSearch.getResultsperpage()));
		if (AdvancedSearch.SEARCH_ALL_STRING.equals(simpleSearch.getTerm())){
			solrQueryParameters.setTerm("");
		}else {
			solrQueryParameters.setTerm(simpleSearch.getTerm());
		}
		QueryResponse solrResponse = abstractSearcher.performNewSearchForListView(solrQueryParameters, results.getPageSize(),
				null);
		request.setAttribute("numberFormat", NumberFormat.getInstance(request.getLocale()));
		results.init(solrResponse, null, simpleSearch,
				new SpringResourceBundleSource(messageSource, request.getLocale()));
		updatePagination(results);
		if (results.getTotalNumberOfResults() > 0) {
			if (abstractSearcher instanceof EadSearcher){
			results.setItems(new SolrDocumentListHolder(solrResponse, true));
			}else {
				results.setItems(new SolrDocumentListHolder(solrResponse, false));
			}
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
	
}
