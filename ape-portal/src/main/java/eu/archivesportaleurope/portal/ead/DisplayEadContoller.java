package eu.archivesportaleurope.portal.ead;

import javax.portlet.RenderRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.util.PortalUtil;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.utils.DisplayUtils;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.AnalyzeLogger;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
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
		ModelAndView modelAndView = new ModelAndView();
		try {
		
		Ead ead = retrieveEad(renderRequest,eadParams, modelAndView);
		EadContent eadContent = ead.getEadContent();
		String documentTitle = eadContent.getUnittitle();
		documentTitle = PortalDisplayUtil.replaceQuotesAndReturns(ead.getArchivalInstitution().getRepositorycode() + ": " + documentTitle);
		HttpServletRequest request = PortalUtil. getHttpServletRequest(renderRequest);
		PortalUtil.setPageTitle(documentTitle, request);
		if (PortalDisplayUtil.isNotNormalBrowser(renderRequest)){
			return displayEadDetails(renderRequest,eadParams, modelAndView,ead);
		}else {
			return displayEadIndex(renderRequest,eadParams, modelAndView,ead);
		}
		}catch (Exception e){
			LOGGER.error("Error in second display process", e);
	
		}
		modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
		modelAndView.setViewName("indexError");
		return modelAndView;	
	}
	public Ead retrieveEad(RenderRequest renderRequest,EadParams eadParams,ModelAndView modelAndView){
		Ead ead = null;
		if (StringUtils.isNotBlank(eadParams.getEadDisplayId())) {
			AnalyzeLogger.logSecondDisplay(eadParams.getEadDisplayId());
			if (eadParams.getEadDisplayId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
				String subSolrId = eadParams.getEadDisplayId().substring(1);
				if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
					CLevel clevel = clevelDAO.findById(Long.parseLong(subSolrId));
					if (clevel != null) {
						ead = clevel.getEadContent().getEad();
					}
					// this is a search result
					modelAndView.getModelMap().addAttribute("solrId", eadParams.getEadDisplayId());
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
						ead = eadDAO.getEadByEadid(xmlType.getClazz(), eadParams.getAiId(), eadParams.getEadid());
					}

				}
			}

		} else if (eadParams.getAiId() != null) {
			XmlType xmlType = XmlType.getType(eadParams.getXmlTypeId());
			if (StringUtils.isNotBlank(eadParams.getEadid())) {
				ead = eadDAO.getEadByEadid(xmlType.getClazz(), eadParams.getAiId(), eadParams.getEadid());
			}
		} else if (eadParams.getRepoCode() != null) {
			String repoCode = eadParams.getRepoCode().replace('_', '/');
			XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
			if (StringUtils.isNotBlank(eadParams.getEadid())) {
				ead = eadDAO.getEadByEadid(xmlType.getClazz(), repoCode, eadParams.getEadid());
			}
		}
		return ead;
	
	}
	public ModelAndView displayEadIndex(RenderRequest renderRequest,EadParams eadParam, ModelAndView modelAndView, Ead ead) {
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
					//LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				modelAndView.getModelMap().addAttribute("ead", ead);
				modelAndView.getModelMap().addAttribute("xmlTypeId", XmlType.getEadType(ead).getIdentifier());
				modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
				String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
				modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
				modelAndView.setViewName("index");
				return modelAndView;
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process", e);
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
			return modelAndView;
		}

	}
	public ModelAndView displayEadDetails(RenderRequest renderRequest,EadParams eadParam, ModelAndView modelAndView, Ead ead) {
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
					//LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				EadContent eadContent = ead.getEadContent();
				String documentTitle = eadContent.getUnittitle();
				documentTitle = PortalDisplayUtil.replaceQuotesAndReturns(archivalInstitution.getRepositorycode() + documentTitle);
				HttpServletRequest request = PortalUtil. getHttpServletRequest(renderRequest);
				PortalUtil.setPageTitle(documentTitle, request);
				modelAndView.getModelMap().addAttribute("eadContent",eadContent );
				modelAndView.getModelMap().addAttribute("xmlTypeId", XmlType.getEadType(ead).getIdentifier());
				modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
				String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
				modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
				modelAndView.setViewName("eaddetails-noscript");
				return modelAndView;
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process", e);
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
			return modelAndView;
		}

	}
}

