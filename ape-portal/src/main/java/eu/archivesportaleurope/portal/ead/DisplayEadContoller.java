package eu.archivesportaleurope.portal.ead;

import java.util.List;

import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.utils.DisplayUtils;
import eu.apenet.commons.xslt.tags.AbstractEadTag;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.AnalyzeLogger;
import eu.archivesportaleurope.portal.common.NotExistInDatabaseException;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;

/**
 *
 * This is display ead controller
 *
 * @author bverhoef
 *
 */
@Controller(value = "displayEadController")
@RequestMapping(value = "VIEW")
public class DisplayEadContoller {
	private static final int PAGE_SIZE = 10;
	private final static Logger LOGGER = Logger.getLogger(DisplayEadContoller.class);
	private CLevelDAO clevelDAO;
	private EadDAO eadDAO;
	private MessageSource messageSource;

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}

	public void setClevelDAO(CLevelDAO clevelDAO) {
		this.clevelDAO = clevelDAO;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@RenderMapping
	public ModelAndView displayEad(RenderRequest renderRequest, @ModelAttribute(value = "eadParams") EadParams eadParams) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = displayEadOrCLevelInternal(renderRequest, eadParams);
			
			modelAndView.getModel().put("recaptchaAjaxUrl",  PropertiesUtil.get(PropertiesKeys.APE_RECAPTCHA_AJAX_URL));
		}catch (NotExistInDatabaseException e) {
			//LOGGER.error("SOLRID NOT IN DB:" + e.getId());
		}catch (Exception e) {
			LOGGER.error("Error in ead display process:" + e.getMessage(),e);

		}
		if (modelAndView == null){
			modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}

	public ModelAndView displayEadOrCLevelInternal(RenderRequest renderRequest, EadParams eadParams) throws NotExistInDatabaseException {
		ModelAndView modelAndView = new ModelAndView();
		Ead ead = null;
		if (StringUtils.isNotBlank(eadParams.getEadDisplayId())) {
			AnalyzeLogger.logSecondDisplay(eadParams.getEadDisplayId());
			if (eadParams.getEadDisplayId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
				String subSolrId = eadParams.getEadDisplayId().substring(1);
				if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
					CLevel clevel = clevelDAO.findById(Long.parseLong(subSolrId));
					modelAndView.getModelMap().addAttribute("solrId", eadParams.getEadDisplayId());
					if (clevel != null) {
						ead = clevel.getEadContent().getEad();
						if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
							return displayCDetails(renderRequest, eadParams, modelAndView, ead, clevel);
						}
					}
				}
			} else {
				String solrPrefix = eadParams.getEadDisplayId().substring(0, 1);
				XmlType xmlType = XmlType.getTypeBySolrPrefix(solrPrefix);
				String subId = eadParams.getEadDisplayId().substring(1);
				if (xmlType != null) {
					ead = eadDAO.findById(Integer.parseInt(subId), xmlType.getClazz());
				} else if (eadParams.getAiId() != null) {
					xmlType = XmlType.getType(eadParams.getXmlTypeId());
					if (StringUtils.isNotBlank(eadParams.getEadid())) {
						ead = eadDAO.getEadByEadid(xmlType.getEadClazz(), eadParams.getAiId(), eadParams.getEadid());
					}

				}
				if (ead == null){

				}
			}

		} else if (eadParams.getAiId() != null) {
			XmlType xmlType = XmlType.getType(eadParams.getXmlTypeId());
			if (StringUtils.isNotBlank(eadParams.getEadid())) {
				ead = eadDAO.getEadByEadid(xmlType.getEadClazz(), eadParams.getAiId(), eadParams.getEadid());
			}
		} else if (eadParams.getRepoCode() != null) {
			XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
			if (StringUtils.isNotBlank(eadParams.getEadid())) {
				ead = eadDAO.getEadByEadid(xmlType.getEadClazz(), eadParams.getRepoCode(), eadParams.getEadid());
			}
		}

		if (ead != null) {
			EadContent eadContent = ead.getEadContent();
			PortalDisplayUtil.setPageTitle(renderRequest,
					PortalDisplayUtil.getEadDisplayTitle(ead, eadContent.getUnittitle()));
			if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
				return displayEadDetails(renderRequest, eadParams, modelAndView, ead);

			} else {
				return displayEadIndex(renderRequest, eadParams, modelAndView, ead);
			}
		}else {
			return null;
		}

	}

	public ModelAndView displayEadIndex(RenderRequest renderRequest, EadParams eadParam, ModelAndView modelAndView,
			Ead ead) {
		ArchivalInstitution archivalInstitution = null;
		try {

			if (ead == null) {
				modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
				modelAndView.setViewName("indexError");
				return modelAndView;
			} else {
				if (ead.isPublished()) {
					archivalInstitution = ead.getArchivalInstitution();
				} else {
					// LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				modelAndView.getModelMap().addAttribute("ead", ead);
				modelAndView.getModelMap().addAttribute("xmlTypeId", XmlType.getContentType(ead).getIdentifier());
				modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
				String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
				modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
				modelAndView.setViewName("index");
				return modelAndView;
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process: " + e.getMessage());
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
			return modelAndView;
		}

	}

	public ModelAndView displayEadDetails(RenderRequest renderRequest, EadParams eadParams, ModelAndView modelAndView,
			Ead ead) {
		ArchivalInstitution archivalInstitution = null;
		try {

			if (ead == null) {
				modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
				modelAndView.setViewName("indexError");
				return modelAndView;
			} else {
				if (ead.isPublished()) {
					archivalInstitution = ead.getArchivalInstitution();
				} else {
					// LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				EadContent eadContent = ead.getEadContent();
				PortalDisplayUtil.setPageTitle(renderRequest,
						PortalDisplayUtil.getEadDisplayTitle(ead, eadContent.getUnittitle()));
				XmlType xmlType = XmlType.getContentType(ead);
				modelAndView.getModelMap().addAttribute("eadContent", eadContent);
				modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
				modelAndView.getModelMap().addAttribute("eadid", ead.getEncodedIdentifier());
				modelAndView.getModelMap().addAttribute("xmlTypeName", xmlType.getResourceName());
				modelAndView.getModelMap().addAttribute("id",xmlType.getSolrPrefix() + ead.getId());
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
				String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
				modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
				Integer pageNumberInt = 1;
				if (eadParams.getPageNumber() != null) {
					pageNumberInt = eadParams.getPageNumber();
				}
				int orderId = (pageNumberInt - 1) * PAGE_SIZE;
				Long totalNumberOfChildren = clevelDAO.countTopCLevels(eadContent.getEcId());
				List<CLevel> children =  clevelDAO.findTopCLevels(eadContent.getEcId(), orderId, PAGE_SIZE);
				modelAndView.getModelMap().addAttribute("totalNumberOfChildren", totalNumberOfChildren);
				modelAndView.getModelMap().addAttribute("pageNumber", pageNumberInt);
				modelAndView.getModelMap().addAttribute("pageSize", PAGE_SIZE);
				modelAndView.getModelMap().addAttribute("children", children);
				modelAndView.setViewName("eaddetails-noscript");
				return modelAndView;
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process: " + e.getMessage());
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
			return modelAndView;
		}

	}

	private ModelAndView displayCDetails(RenderRequest renderRequest, EadParams eadParams, ModelAndView modelAndView,
			Ead ead, CLevel currentCLevel) {
		modelAndView.getModelMap().addAttribute("type", AbstractEadTag.CDETAILS_XSLT);
		Integer pageNumberInt = 1;
		if (eadParams.getPageNumber() != null) {
			pageNumberInt = eadParams.getPageNumber();
		}
		int orderId = (pageNumberInt - 1) * PAGE_SIZE;
		List<CLevel> children = clevelDAO.findChildCLevels(currentCLevel.getClId(), orderId, PAGE_SIZE);
		Long totalNumberOfChildren = clevelDAO.countChildCLevels(currentCLevel.getClId());
		ArchivalInstitution archivalInstitution = ead.getArchivalInstitution();
		modelAndView.getModelMap().addAttribute("c", currentCLevel);
		modelAndView.getModelMap().addAttribute("totalNumberOfChildren", totalNumberOfChildren);
		modelAndView.getModelMap().addAttribute("pageNumber", pageNumberInt);
		modelAndView.getModelMap().addAttribute("pageSize", PAGE_SIZE);
		modelAndView.getModelMap().addAttribute("children", children);
		SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
				renderRequest.getLocale());
		String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
		modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
		String documentTitle = currentCLevel.getUnittitle();
		PortalDisplayUtil.setPageTitle(renderRequest,PortalDisplayUtil.getEadDisplayTitle(ead, documentTitle));
		modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);

		modelAndView.setViewName("eaddetails-noscript");
		return modelAndView;
	}
}
