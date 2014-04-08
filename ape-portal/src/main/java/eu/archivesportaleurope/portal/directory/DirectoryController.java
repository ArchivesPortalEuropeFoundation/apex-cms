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
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.exceptions.APEnetException;
import eu.apenet.commons.infraestructure.ArchivalInstitutionUnit;
import eu.apenet.commons.infraestructure.CountryUnit;
import eu.apenet.commons.infraestructure.NavigationTree;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.commons.utils.DisplayUtils;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CountryDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.dao.ContentSearchOptions;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.Country;
import eu.apenet.persistence.vo.FindingAid;
import eu.apenet.persistence.vo.HoldingsGuide;
import eu.apenet.persistence.vo.SourceGuide;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.util.ApeUtil;

@Controller(value = "directoryController")
@RequestMapping(value = "VIEW")
public class DirectoryController {
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private CountryDAO countryDAO;
	private MessageSource messageSource;
	private EadDAO eadDAO;
	private final static Logger LOGGER = Logger.getLogger(DirectoryController.class);
	
	private String google_maps_license = PropertiesUtil.get(PropertiesKeys.APE_GOOGLEMAPS_KEY);		// License key
	private String google_maps_url = PropertiesUtil.get(PropertiesKeys.APE_GOOGLEMAPS_URL);				// used to insulate https://maps.googleapis.com/maps/api/js?key=
	private String google_maps_jsapi = PropertiesUtil.get(PropertiesKeys.APE_GOOGLEMAPS_JSAPI);			// used to insulate https://www.google.com/jsapi

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

	public String getGoogle_maps_license() {
		return google_maps_license;
	}

	public void setGoogle_maps_license(String google_maps_license) {
		this.google_maps_license = google_maps_license;
	}

	public String getGoogle_maps_url() {
		return google_maps_url;
	}

	public void setGoogle_maps_url(String google_maps_url) {
		this.google_maps_url = google_maps_url;
	}

	public String getGoogle_maps_jsapi() {
		return google_maps_jsapi;
	}

	public void setGoogle_maps_jsapi(String google_maps_jsapi) {
		this.google_maps_jsapi = google_maps_jsapi;
	}

	@RenderMapping(params = "myaction=showCountryDetails")
	public ModelAndView showCountryDetails(RenderRequest renderRequest) throws APEnetException {
		ModelAndView modelAndView = new ModelAndView();
		String countryCode = renderRequest.getParameter("countryCode");
		try {
			List<Country> countries = countryDAO.getCountries(countryCode);
			if (countries.size() > 0){
				Country country = countries.get(0);
	
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
				NavigationTree navigationTree = new NavigationTree(source);
				List<ArchivalInstitutionUnit> archivalInstitutionList = navigationTree
						.getArchivalInstitutionsByParentAiId("country_" + country.getId());
				archivalInstitutionList = navigationTree.filterArchivalInstitutionsWithEAG(archivalInstitutionList);
				Collections.sort(archivalInstitutionList);
				String localizedName = DisplayUtils.getLocalizedCountryName(source, country);
				modelAndView.getModelMap().addAttribute("countryCode", countryCode);
				modelAndView.getModelMap().addAttribute("parent", localizedName);
				modelAndView.getModelMap().addAttribute("archivalInstitutionUnits", archivalInstitutionList);
				modelAndView.setViewName("institutions-noscript");
				PortalDisplayUtil.setPageTitle(renderRequest,PortalDisplayUtil.getCountryDisplayTitle(country));
				return modelAndView;
			}
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
		modelAndView = new ModelAndView();
		modelAndView.setViewName("indexError");
		return modelAndView;
	}

	@RenderMapping(params = "myaction=showAiGroup")
	public ModelAndView showAiGroup(RenderRequest renderRequest) throws APEnetException {
		ModelAndView modelAndView = new ModelAndView();
		String aiIdString = renderRequest.getParameter("aiId");
		try {
			if (StringUtils.isNotBlank(aiIdString)) {
				ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(Integer.parseInt(aiIdString));
				if (archivalInstitution != null){
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
					PortalDisplayUtil.setPageTitle(renderRequest,PortalDisplayUtil.getArchivalInstitutionDisplayTitle(archivalInstitution));
					return modelAndView;
				}
			}
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
		modelAndView = new ModelAndView();
		modelAndView.setViewName("indexError");
		return modelAndView;
	}

	@RenderMapping(params = "myaction=showAiDetails")
	public ModelAndView showAiDetails(RenderRequest renderRequest) throws IOException {
		ModelAndView modelAndView;
		try {
			String repositoryCode = renderRequest.getParameter("repoCode");
			List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO
					.getArchivalInstitutionsByRepositorycode(repositoryCode);
			if (archivalInstitutions.size() > 0){
				ArchivalInstitution archivalInstitution = archivalInstitutions.get(0);
				modelAndView = fillAIDetails(archivalInstitution);
				if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
					modelAndView.getModelMap().addAttribute("mobile", "mobile");
				} else {
					modelAndView.getModelMap().addAttribute("mobile", "");
				}
				String documentTitle = PortalDisplayUtil.getArchivalInstitutionDisplayTitle(archivalInstitution);
				modelAndView.getModelMap().addAttribute("documentTitle",documentTitle);
				PortalDisplayUtil.setPageTitle(renderRequest, documentTitle);
				modelAndView.setViewName("aidetails-direct");
				return modelAndView;
			}
				
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
		}
		modelAndView = new ModelAndView();
		modelAndView.setViewName("indexError");
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
		} else {
			String mapUrl = "https://maps.google.com/maps?ie=UTF8&t=m&hl=";
			mapUrl += renderRequest.getLocale().getLanguage();
			String mapUrlCenterParameters = "&ll=54.5259614,15.255118700000025&spn=48.804369699999995,102.17279989999997";

			modelAndView.setViewName("index");
			modelAndView.getModelMap().addAttribute("embeddedMapUrl", mapUrl + "&output=embed");
			modelAndView.getModelMap().addAttribute("mapUrl", mapUrl);
			modelAndView.getModelMap().addAttribute("mapUrlCenterParameters", mapUrlCenterParameters);

		}
		PortalDisplayUtil.setPageTitle(renderRequest, PortalDisplayUtil.TITLE_DIRECTORY);
		return this.addGoogleInfo(modelAndView);
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
				modelAndView.getModelMap().addAttribute("documentTitle",PortalDisplayUtil.getArchivalInstitutionDisplayTitle(archivalInstitution));
				return this.addGoogleInfo(modelAndView);
			}
		} catch (Exception e) {

		}
		return new ModelAndView("indexError");
	}

	@RenderMapping(params = "myaction=printEagDetails")
	public ModelAndView displayEagPrint(RenderRequest renderRequest) {
		String id = renderRequest.getParameter("id");
		try {
			if (StringUtils.isNotBlank(id)) {
				ModelAndView modelAndView = new ModelAndView("print");
				// Google Maps part
				String mapUrl = "https://maps.google.com/maps?ie=UTF8&t=m";
				String mapUrlCenterParameters = "&ll=54.5259614,15.255118700000025&spn=48.804369699999995,102.17279989999997";
				modelAndView.getModelMap().addAttribute("embeddedMapUrl", mapUrl + "&output=embed");
				modelAndView.getModelMap().addAttribute("mapUrl", mapUrl);
				modelAndView.getModelMap().addAttribute("mapUrlCenterParameters", mapUrlCenterParameters);
				// EAG part
				ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(Integer.parseInt(id));
				String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
				modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
				modelAndView.getModelMap().addAttribute("archivalInstitutionName", archivalInstitution.getAiname());
				modelAndView.getModelMap().addAttribute("countryCode", archivalInstitution.getCountry().getIsoname());
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
				modelAndView.getModelMap().addAttribute("countryName", DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry()));
				modelAndView.getModelMap().addAttribute("selectedAiId", id);
				PortalDisplayUtil.setPageTitle(renderRequest, PortalDisplayUtil.getArchivalInstitutionDisplayTitle(archivalInstitution));
				return this.addGoogleInfo(modelAndView);
			}
		} catch (Exception e) {
			LOGGER.error("Exception on print process: " + e.getMessage());
		}
		return new ModelAndView("indexError");
	}

	private ModelAndView fillAIDetails(ArchivalInstitution archivalInstitution) throws IOException {
		ModelAndView modelAndView = new ModelAndView();
		String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
		modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
		modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
		ContentSearchOptions eadSearchOptions = new ContentSearchOptions();
		eadSearchOptions.setArchivalInstitionId(archivalInstitution.getAiId());
		eadSearchOptions.setPublished(true);
		eadSearchOptions.setContentClass(HoldingsGuide.class);
		modelAndView.getModelMap().addAttribute("hasHoldingsGuides",eadDAO.existEads(eadSearchOptions));
		eadSearchOptions.setContentClass(FindingAid.class);
		modelAndView.getModelMap().addAttribute("hasFindingAids",eadDAO.existEads(eadSearchOptions));
		eadSearchOptions.setContentClass(SourceGuide.class);
		modelAndView.getModelMap().addAttribute("hasSourceGuides",eadDAO.existEads(eadSearchOptions));
		modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
		return this.addGoogleInfo(modelAndView);
	}

	public ArchivalInstitutionDAO getArchivalInstitutionDAO() {
		return archivalInstitutionDAO;
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	private ModelAndView addGoogleInfo(ModelAndView modelAndView) {
		ModelAndView model = modelAndView;
		modelAndView.getModelMap().addAttribute("google_maps_jsapi", this.getGoogle_maps_jsapi());
		modelAndView.getModelMap().addAttribute("google_maps_license", this.getGoogle_maps_license());
		modelAndView.getModelMap().addAttribute("google_maps_url", this.getGoogle_maps_url());
		return model;
	}
}
