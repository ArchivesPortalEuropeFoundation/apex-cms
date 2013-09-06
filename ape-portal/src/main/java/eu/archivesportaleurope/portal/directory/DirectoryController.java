package eu.archivesportaleurope.portal.directory;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import javax.portlet.RenderRequest;
import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.exceptions.APEnetException;
import eu.apenet.commons.infraestructure.ArchivalInstitutionUnit;
import eu.apenet.commons.infraestructure.CountryUnit;
import eu.apenet.commons.infraestructure.NavigationTree;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CountryDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.dao.EadSearchOptions;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.Country;
import eu.apenet.persistence.vo.FindingAid;
import eu.apenet.persistence.vo.HoldingsGuide;
import eu.apenet.persistence.vo.SourceGuide;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;

@Controller(value = "directoryController")
@RequestMapping(value = "VIEW")
public class DirectoryController {
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private CountryDAO countryDAO;
	private MessageSource messageSource;
	private EadDAO eadDAO;
	private final static Logger LOGGER = Logger.getLogger(DirectoryController.class);

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}

	@RenderMapping(params = "myaction=showCountryDetails")
	public ModelAndView showCountryDetails(RenderRequest renderRequest) throws APEnetException {
		ModelAndView modelAndView = new ModelAndView();
		String countryCode = renderRequest.getParameter("countryCode");
		try {
			List<Country> countries = countryDAO.getCountries(countryCode);
			Country country = countries.get(0);

			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					renderRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);
			List<ArchivalInstitutionUnit> archivalInstitutionList = navigationTree
					.getArchivalInstitutionsByParentAiId("country_" + country.getId());
			archivalInstitutionList = navigationTree.filterArchivalInstitutionsWithEAG(archivalInstitutionList);
			Collections.sort(archivalInstitutionList);
			CountryUnit countryUnit = navigationTree.getCountryUnit(country);
			modelAndView.getModelMap().addAttribute("countryCode", countryCode);
			modelAndView.getModelMap().addAttribute("parent", countryUnit.getLocalizedName());
			modelAndView.getModelMap().addAttribute("archivalInstitutionUnits", archivalInstitutionList);
			modelAndView.setViewName("institutions-noscript");

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			modelAndView.setViewName("indexError");
		}

		return modelAndView;
	}

	@RenderMapping(params = "myaction=showAiGroup")
	public ModelAndView showAiGroup(RenderRequest renderRequest) throws APEnetException {
		ModelAndView modelAndView = new ModelAndView();
		String aiIdString = renderRequest.getParameter("aiId");
		try {
			if (StringUtils.isNotBlank(aiIdString)) {
				ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(Integer.parseInt(aiIdString));
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
				NavigationTree navigationTree = new NavigationTree(source);
				List<ArchivalInstitutionUnit> archivalInstitutionList = navigationTree
						.getArchivalInstitutionsByParentAiId("aigroup_" + aiIdString);
				archivalInstitutionList = navigationTree.filterArchivalInstitutionsWithEAG(archivalInstitutionList);
				Collections.sort(archivalInstitutionList);
				modelAndView.getModelMap().addAttribute("aiId", aiIdString);
				modelAndView.getModelMap().addAttribute("parent", archivalInstitution.getAiname());
				modelAndView.getModelMap().addAttribute("archivalInstitutionUnits", archivalInstitutionList);
				modelAndView.setViewName("institutions-noscript");
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			modelAndView.setViewName("indexError");
		}

		return modelAndView;
	}

	@RenderMapping(params = "myaction=showAiDetails")
	public ModelAndView showAiDetails(RenderRequest renderRequest) throws IOException {
		ModelAndView modelAndView;
		try {
			String repositoryCode = renderRequest.getParameter("repoCode");
			List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO
					.getArchivalInstitutionsByRepositorycode(repositoryCode);
			ArchivalInstitution archivalInstitution = archivalInstitutions.get(0);
			modelAndView = fillAIDetails(archivalInstitution);
			if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
				modelAndView.getModelMap().addAttribute("mobile", "mobile");
			} else {
				modelAndView.getModelMap().addAttribute("mobile", "");
			}
			modelAndView.setViewName("aidetails-direct");
		} catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			modelAndView = new ModelAndView();
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}

	@RenderMapping
	public ModelAndView showDirectory(RenderRequest renderRequest) throws APEnetException {
		ModelAndView modelAndView = new ModelAndView();
		if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
			modelAndView.setViewName("index-noscript");
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					renderRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);
			List<CountryUnit> countryList = navigationTree.getALCountriesWithArchivalInstitutionsWithEAG();
			Collections.sort(countryList);
			modelAndView.getModelMap().addAttribute("countries", countryList);
			return modelAndView;
		} else {
			String mapUrl = "https://maps.google.com/maps?ie=UTF8&t=m&hl=";
			mapUrl += renderRequest.getLocale().getLanguage();
			String mapUrlCenterParameters = "&ll=54.5259614,15.255118700000025&spn=48.804369699999995,102.17279989999997";

			modelAndView.setViewName("index");
			modelAndView.getModelMap().addAttribute("embeddedMapUrl", mapUrl + "&output=embed");
			modelAndView.getModelMap().addAttribute("mapUrl", mapUrl);
			modelAndView.getModelMap().addAttribute("mapUrlCenterParameters", mapUrlCenterParameters);

		}
		return modelAndView;
	}

	@ResourceMapping(value = "aiDetails")
	public ModelAndView displayAiDetails(ResourceRequest resourceRequest) {
		String idString = resourceRequest.getParameter("id");
		try {
			if (StringUtils.isNotBlank(idString)) {
				Integer id = Integer.parseInt(idString);
				ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(id);
				ModelAndView modelAndView = fillAIDetails(archivalInstitution);
				modelAndView.setViewName("aidetails");
				return modelAndView;
			}
		} catch (Exception e) {

		}
		return new ModelAndView("indexError");
	}

	@RenderMapping(params = "myaction=printEagDetails")
	public ModelAndView displayEagPrint(@RequestParam String id) {
		try {
			if (StringUtils.isNotBlank(id)) {
				return fillPrint(new Long(id));
			}
		} catch (Exception e) {
			LOGGER.error("Exception on print process: " + e.getMessage());
		}
		return new ModelAndView("indexError");
	}

	private ModelAndView fillPrint(Long idLong) throws IOException {
		ModelAndView modelAndView = new ModelAndView("ape-pagelayout-directory ");
		modelAndView.setViewName("print");
		// Google Maps part
		String mapUrl = "https://maps.google.com/maps?ie=UTF8&t=m";
		String mapUrlCenterParameters = "&ll=54.5259614,15.255118700000025&spn=48.804369699999995,102.17279989999997";
		modelAndView.getModelMap().addAttribute("embeddedMapUrl", mapUrl + "&output=embed");
		modelAndView.getModelMap().addAttribute("mapUrl", mapUrl);
		modelAndView.getModelMap().addAttribute("mapUrlCenterParameters", mapUrlCenterParameters);
		// EAG part
		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(idLong.intValue());
		String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
		modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
		modelAndView.getModelMap().addAttribute("archivalInstitutionName", archivalInstitution.getAiname());
		modelAndView.getModelMap().addAttribute("country", archivalInstitution.getCountry().getIsoname());
		return modelAndView;
	}

	private ModelAndView fillAIDetails(ArchivalInstitution archivalInstitution) throws IOException {
		ModelAndView modelAndView = new ModelAndView();
		String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
		modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
		modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
		EadSearchOptions eadSearchOptions = new EadSearchOptions();
		eadSearchOptions.setArchivalInstitionId(archivalInstitution.getAiId());
		eadSearchOptions.setPublished(true);
		eadSearchOptions.setEadClass(HoldingsGuide.class);
		modelAndView.getModelMap().addAttribute("hasHoldingsGuides",eadDAO.existEads(eadSearchOptions));
		eadSearchOptions.setEadClass(FindingAid.class);
		modelAndView.getModelMap().addAttribute("hasFindingAids",eadDAO.existEads(eadSearchOptions));
		eadSearchOptions.setEadClass(SourceGuide.class);
		modelAndView.getModelMap().addAttribute("hasSourceGuides",eadDAO.existEads(eadSearchOptions));
		modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
		return modelAndView;
	}

	public ArchivalInstitutionDAO getArchivalInstitutionDAO() {
		return archivalInstitutionDAO;
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}
}
