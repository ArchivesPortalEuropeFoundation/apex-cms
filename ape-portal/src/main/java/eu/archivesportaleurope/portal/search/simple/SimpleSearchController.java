package eu.archivesportaleurope.portal.search.simple;

import java.text.NumberFormat;

import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.commons.utils.Cache;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;

@Controller(value = "simpleSearchController")
@RequestMapping(value = "VIEW")
public class SimpleSearchController {
	private static final String INSTITUTIONS = "institutions";
	private static final String UNITS = "units";
	private static final String TRUE = "TRUE";
	//private final static Logger LOGGER = Logger.getLogger(SimpleSearchController.class);
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;

	private final static Cache<String, Long> CACHE = new Cache<String, Long>();
	
	
	@RenderMapping
	public ModelAndView showSimpleSearch(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		String embedded= request.getPreferences().getValue("embedded", "false");
		if (TRUE.equalsIgnoreCase(embedded)){
			modelAndView.setViewName("embedded");
		}else {
			modelAndView.setViewName("home");
			Long institutions = CACHE.get(INSTITUTIONS);
			Long units = CACHE.get(UNITS);
			if (institutions == null) {
				institutions = archivalInstitutionDAO.countArchivalInstitutionsWithContentIndexed();
				CACHE.put(INSTITUTIONS, institutions);
			}	
			if (units == null) {
				units = eadDAO.getTotalCountOfUnits();
				CACHE.put(UNITS, units);
			}	
			modelAndView.getModelMap().addAttribute(INSTITUTIONS, institutions);
			NumberFormat numberFormat = NumberFormat.getInstance(request.getLocale());
			modelAndView.getModelMap().addAttribute(UNITS, numberFormat.format(units));
			String numberOfDaoUnitsString= request.getPreferences().getValue("numberOfDaoUnits", "0");
			long numberOfDaoUnits = 0;
			if (StringUtils.isNotBlank(numberOfDaoUnitsString)){
				numberOfDaoUnits = Long.parseLong(numberOfDaoUnitsString);
				
			}
			modelAndView.getModelMap().addAttribute("numberOfDaoUnits", numberFormat.format(numberOfDaoUnits));
	
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
	
}
