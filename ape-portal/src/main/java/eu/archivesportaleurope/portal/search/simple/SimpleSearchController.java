package eu.archivesportaleurope.portal.search.simple;

import java.text.NumberFormat;

import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.EadDAO;

@Controller(value = "simpleSearchController")
@RequestMapping(value = "VIEW")
public class SimpleSearchController {
	//private final static Logger LOGGER = Logger.getLogger(SimpleSearchController.class);
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	// --maps the incoming portlet request to this method
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
		return modelAndView;
	}
	@ModelAttribute("simpleSearch")
	public SimpleSearch getCommandObject() {
		return new SimpleSearch();
	}
	@ActionMapping(params = "myaction=simpleSearch")
	public void addBook( @ModelAttribute(value = "simpleSearch") SimpleSearch simhpleSearc,
			BindingResult bindingResult, ActionResponse response) {

	}
	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}
	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}

	
}
