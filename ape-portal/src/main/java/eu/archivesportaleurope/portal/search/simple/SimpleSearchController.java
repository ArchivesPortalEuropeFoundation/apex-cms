package eu.archivesportaleurope.portal.search.simple;

import java.text.NumberFormat;

import javax.portlet.RenderRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.commons.utils.Cache;
import eu.apenet.commons.utils.CacheManager;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.ContentSearchOptions;
import eu.apenet.persistence.dao.EacCpfDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.EacCpf;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;

@Controller(value = "simpleSearchController")
@RequestMapping(value = "VIEW")
public class SimpleSearchController {
	private static final String EAD_RESULTS = "ead";
	private static final String INSTITUTIONS = "numberOfInstitutions";
	private static final String EAD_UNITS = "numberOfEadDescriptiveUnits";
	private static final String EAC_CPF_UNITS = "numberOfEacCpfs";

	private static final String PORTLET_TYPE_EMBEDDED = "embedded";
	private static final String PORTLET_TYPE_WIDGET = "widget";
	
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	private EacCpfDAO eacCpfDAO;
	private final static Cache<String, Long> CACHE = CacheManager.getInstance().<String, Long>initCache("SimpleSearchCache");
	
	
	@RenderMapping
	public ModelAndView showSimpleSearch(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		String portletType= request.getPreferences().getValue("portletType", "normal");
		if (PORTLET_TYPE_EMBEDDED.equalsIgnoreCase(portletType)){
			String results = request.getPreferences().getValue("resultsType", EAD_RESULTS);
			modelAndView.getModel().put("resultsType", results);
			modelAndView.setViewName("embedded");
		}else if (PORTLET_TYPE_WIDGET.equalsIgnoreCase(portletType)){
			String results = request.getPreferences().getValue("resultsType", EAD_RESULTS);
			String savedSearchId = request.getPreferences().getValue("savedSearchId", "");
			modelAndView.getModel().put("savedSearchId", savedSearchId);
			modelAndView.getModel().put("resultsType", results);
			modelAndView.setViewName("widget");
		}else {
			modelAndView.setViewName("home");
			Long institutions = CACHE.get(INSTITUTIONS);
			Long eadUnits = CACHE.get(EAD_UNITS);
			Long eacCpfUnits = CACHE.get(EAC_CPF_UNITS);			
			if (institutions == null) {
				institutions = archivalInstitutionDAO.countArchivalInstitutionsWithEag();
				CACHE.put(INSTITUTIONS, institutions);
			}	
			if (eadUnits == null) {
				eadUnits = eadDAO.getTotalCountOfUnits();
				CACHE.put(EAD_UNITS, eadUnits);
			}	
			if (eacCpfUnits == null) {
				ContentSearchOptions contentSearchOptions = new ContentSearchOptions();
				contentSearchOptions.setContentClass(EacCpf.class);
				contentSearchOptions.setPublished(true);
				eacCpfUnits = eacCpfDAO.countEacCpfs(contentSearchOptions);
				CACHE.put(EAC_CPF_UNITS, eacCpfUnits);
			}
			modelAndView.getModelMap().addAttribute(INSTITUTIONS, institutions);
			NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
			modelAndView.getModelMap().addAttribute(EAD_UNITS, numberFormat.format(eadUnits));
			modelAndView.getModelMap().addAttribute(EAC_CPF_UNITS, numberFormat.format(eacCpfUnits));

	
			PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.TITLE_HOME);
		}
		return modelAndView;
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

	public void setEacCpfDAO(EacCpfDAO eacCpfDAO) {
		this.eacCpfDAO = eacCpfDAO;
	}
	
}
