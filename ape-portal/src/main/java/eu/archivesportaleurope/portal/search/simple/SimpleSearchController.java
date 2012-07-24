package eu.archivesportaleurope.portal.search.simple;

import javax.portlet.ActionResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.factory.DAOFactory;

@Controller(value = "simpleSearchController")
@RequestMapping(value = "VIEW")
public class SimpleSearchController {
	private final static Logger LOGGER = Logger.getLogger(SimpleSearchController.class);
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	// --maps the incoming portlet request to this method
	@RenderMapping
	public ModelAndView showSimpleSearch() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("home");

		modelAndView.getModelMap().addAttribute("institutions", archivalInstitutionDAO.countArchivalInstitutionsWithContentIndexed());
		modelAndView.getModelMap().addAttribute("units", eadDAO.getTotalCountOfUnits());
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
