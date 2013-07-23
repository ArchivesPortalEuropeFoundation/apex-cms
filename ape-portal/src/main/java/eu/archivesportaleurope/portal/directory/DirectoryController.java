package eu.archivesportaleurope.portal.directory;

import java.io.IOException;

import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;

@Controller(value = "directoryController")
@RequestMapping(value = "VIEW")
public class DirectoryController {
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	
	private final static Logger LOGGER = Logger.getLogger(DirectoryController.class);

	// --maps the incoming portlet request to this method
	@RenderMapping
	public ModelAndView showDirectory(RenderRequest renderRequest) {
		String mapUrl = "https://maps.google.com/maps?ie=UTF8&t=m&hl=";
		mapUrl+=renderRequest.getLocale().getLanguage();
		String mapUrlCenterParameters = "&ll=54.5259614,15.255118700000025&spn=48.804369699999995,102.17279989999997";
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");		
		modelAndView.getModelMap().addAttribute("embeddedMapUrl",mapUrl+"&output=embed");
		modelAndView.getModelMap().addAttribute("mapUrl",mapUrl);
		modelAndView.getModelMap().addAttribute("mapUrlCenterParameters", mapUrlCenterParameters);
		return modelAndView;
	}
	@ResourceMapping(value = "aiDetails")
	public ModelAndView displayAiDetails(@RequestParam String id){
		try {
			if (StringUtils.isNotBlank(id)) {
				return fillAIDetails(new Long(id));
			}
		} catch (Exception e) {

		}

		return null;
	}
	@RenderMapping(params = "myaction=printEagDetails")
	public ModelAndView displayEagPrint(@RequestParam String id){
		try {
			if (StringUtils.isNotBlank(id)) {
				return fillPrint(new Long(id));
			}
		} catch (Exception e) {
			LOGGER.error("Exception on print process: "+e.getMessage());
		}
		return null;
	}
	
	private ModelAndView fillPrint(Long idLong) throws IOException {
		ModelAndView modelAndView = new ModelAndView("ape-pagelayout-directory ");
		modelAndView.setViewName("print");
		//Google Maps part
		String mapUrl = "https://maps.google.com/maps?ie=UTF8&t=m";
		String mapUrlCenterParameters = "&ll=54.5259614,15.255118700000025&spn=48.804369699999995,102.17279989999997";
		modelAndView.getModelMap().addAttribute("embeddedMapUrl",mapUrl+"&output=embed");
		modelAndView.getModelMap().addAttribute("mapUrl",mapUrl);
		modelAndView.getModelMap().addAttribute("mapUrlCenterParameters", mapUrlCenterParameters);
		//EAG part
		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(idLong.intValue());
		String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
		modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
		modelAndView.getModelMap().addAttribute("archivalInstitutionName",archivalInstitution.getAiname());
		modelAndView.getModelMap().addAttribute("country",archivalInstitution.getCountry().getIsoname());
		return modelAndView;
	}

	private ModelAndView fillAIDetails(Long idLong) throws IOException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("aidetails");
		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(idLong.intValue());
		String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
		modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
		modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
		return modelAndView;
	}
	public ArchivalInstitutionDAO getArchivalInstitutionDAO() {
		return archivalInstitutionDAO;
	}
	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}
}
