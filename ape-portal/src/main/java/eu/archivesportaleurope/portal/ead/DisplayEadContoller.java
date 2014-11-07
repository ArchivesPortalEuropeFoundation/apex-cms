package eu.archivesportaleurope.portal.ead;

import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.types.XmlType;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.NotExistInDatabaseException;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.urls.EadPersistentUrl;
import eu.archivesportaleurope.util.ApeUtil;

/**
 *
 * This is display ead controller
 *
 * @author bverhoef
 *
 */
@Controller(value = "displayEadController")
@RequestMapping(value = "VIEW")
public class DisplayEadContoller extends AbstractEadController{
	private final static Logger LOGGER = Logger.getLogger(DisplayEadContoller.class);
	private EadDAO eadDAO;
	private MessageSource messageSource;

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}


	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@RenderMapping
	public ModelAndView displayPersistentEad(RenderRequest renderRequest, @ModelAttribute(value = "eadParams") EadParams eadParams) {
		ModelAndView modelAndView = null;
		try {
			modelAndView = displayPersistentEadOrCLevelInternal(renderRequest, eadParams);
			if (modelAndView != null){
				modelAndView.getModelMap().addAttribute("element", eadParams.getElement());
				modelAndView.getModelMap().addAttribute("term", ApeUtil.decodeSpecialCharacters(eadParams.getTerm()));
				modelAndView.getModel().put("recaptchaAjaxUrl", PropertiesUtil.get(PropertiesKeys.APE_RECAPTCHA_AJAX_URL));
				modelAndView.getModelMap().addAttribute("recaptchaPubKey", PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY));
			}
		}catch (NotExistInDatabaseException e) {
			//LOGGER.error("SOLRID NOT IN DB:" + e.getId());
		}catch (Exception e) {
			LOGGER.error("Error in ead display process:" +ApeUtil.generateThrowableLog(e));

		}
		if (modelAndView == null){
			modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}	
	public ModelAndView displayPersistentEadOrCLevelInternal(RenderRequest renderRequest, EadParams eadParams) throws NotExistInDatabaseException {
		ModelAndView modelAndView = new ModelAndView();
		Ead ead = null;
		if (StringUtils.isNotBlank(eadParams.getRepoCode()) && StringUtils.isNotBlank(eadParams.getEadid()) && StringUtils.isNotBlank(eadParams.getXmlTypeName())){
			XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
			if (xmlType != null){
				if (StringUtils.isNotBlank(eadParams.getUnitid())){
					List<CLevel> clevels = getClevelDAO().getCLevel(eadParams.getRepoCode(), xmlType.getEadClazz(), eadParams.getEadid(), eadParams.getUnitid());
					int size = clevels.size();
					
					if (size > 0) {
						if (size > 1){
							modelAndView.getModelMap().addAttribute("errorMessage", "display.ead.clevel.unitid.notunique");
						}
						CLevel clevel = clevels.get(0);
						modelAndView.getModelMap().addAttribute("solrId", SolrValues.C_LEVEL_PREFIX + clevel.getClId());
						ead = clevel.getEadContent().getEad();
						return displayCDetails(renderRequest, eadParams, modelAndView, ead, clevel);
						
					}else {
						modelAndView.getModelMap().addAttribute("errorMessage", "display.ead.clevel.notfound");
					}
				}else if (StringUtils.isNotBlank(eadParams.getEadDisplayId()) && eadParams.getEadDisplayId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
					String subSolrId = eadParams.getEadDisplayId().substring(1);
					if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
						CLevel clevel = getClevelDAO().getCLevel(eadParams.getRepoCode(), xmlType.getEadClazz(), eadParams.getEadid(), Long.parseLong(subSolrId));
						if (clevel != null) {
							modelAndView.getModelMap().addAttribute("solrId", eadParams.getEadDisplayId());
							ead = clevel.getEadContent().getEad();
							return displayCDetails(renderRequest, eadParams, modelAndView, ead, clevel);
						}else {
							modelAndView.getModelMap().addAttribute("errorMessage", "display.ead.clevel.notfound");
						}
					}						
				}
				/*
					 * link to archdesc
					 */
					if (ead == null && StringUtils.isNotBlank(eadParams.getEadid())) {
						ead = eadDAO.getPublishedEadByEadid(xmlType.getEadClazz(), eadParams.getRepoCode(), eadParams.getEadid());
					}
			}
		}
		if (ead != null) {
			EadContent eadContent = ead.getEadContent();
			PortalDisplayUtil.setPageTitle(renderRequest,
					PortalDisplayUtil.getEadDisplayTitle(ead, eadContent.getUnittitle()));
			return displayEadDetails(renderRequest, eadParams, modelAndView, ead);
		}else {
			return null;
		}

	}	

	@ActionMapping(params ="myaction=redirectAction")
	public void redirect(ActionRequest actionRequest, ActionResponse actionResponse, @ModelAttribute(value = "eadParams") EadParams eadParams) {
		try {
			EadPersistentUrl eadPersistentUrl = generateRedirectUrl(actionRequest, eadParams);
			if (eadPersistentUrl != null){
				String redirectUrl =  FriendlyUrlUtil.getRelativeEadPersistentUrl(actionRequest, eadPersistentUrl);
				actionResponse.sendRedirect(redirectUrl);
			}
		}catch (NotExistInDatabaseException e) {
			//LOGGER.error("SOLRID NOT IN DB:" + e.getId());
		}catch (Exception e) {
			LOGGER.error("Error in ead display process:" + ApeUtil.generateThrowableLog(e));

		}

	}
	

	public EadPersistentUrl generateRedirectUrl(ActionRequest request, EadParams eadParams) throws NotExistInDatabaseException {
		Ead ead = null;
		CLevel clevel = null;
		if (StringUtils.isNotBlank(eadParams.getEadDisplayId())) {
			if (eadParams.getEadDisplayId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
				String subSolrId = eadParams.getEadDisplayId().substring(1);
				if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
					clevel = getClevelDAO().findById(Long.parseLong(subSolrId));
					if (clevel != null) {
						ead = clevel.getEadContent().getEad();
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
			}

		} else if (eadParams.getAiId() != null) {
			XmlType xmlType = XmlType.getType(eadParams.getXmlTypeId());
			if (StringUtils.isNotBlank(eadParams.getEadid())) {
				ead = eadDAO.getEadByEadid(xmlType.getEadClazz(), eadParams.getAiId(), eadParams.getEadid());
			}
		} else if (eadParams.getRepoCode() != null) {
			XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
			if (StringUtils.isNotBlank(eadParams.getEadid())) {
				ead = eadDAO.getPublishedEadByEadid(xmlType.getEadClazz(), eadParams.getRepoCode(), eadParams.getEadid());
			}
		}

		if (ead != null) {
			EadContent eadContent = ead.getEadContent();
			PortalDisplayUtil.setPageTitle(request,
					PortalDisplayUtil.getEadDisplayTitle(ead, eadContent.getUnittitle()));
			XmlType xmlType = XmlType.getContentType(ead);
			EadPersistentUrl eadPersistentUrl = new EadPersistentUrl(ead.getArchivalInstitution().getRepositorycode(), xmlType.getResourceName(), ead.getEadid());
			eadPersistentUrl.setClevel(clevel);
			eadPersistentUrl.setPageNumberAsInt(eadParams.getPageNumber());
			eadPersistentUrl.setSearchFieldsSelectionId(eadParams.getElement());
			eadPersistentUrl.setSearchTerms(eadParams.getTerm());
			return eadPersistentUrl;
		}else {
			return null;
		}

	}


	public ModelAndView displayEadDetails(RenderRequest renderRequest, EadParams eadParams, ModelAndView modelAndView,
			Ead ead) {
		try {

			if (ead == null) {
				modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
				modelAndView.setViewName("indexError");
				return modelAndView;
			} else {
				if (!ead.isPublished()) {
					// LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				EadContent eadContent = ead.getEadContent();
				if(PortalDisplayUtil.isNotDesktopBrowser(renderRequest)){
					fillEadDetails(eadContent, renderRequest, eadParams.getPageNumber(), modelAndView, true);
					modelAndView.setViewName("eaddetails-noscript");
				}else {
					fillEadDetails(eadContent, renderRequest,null, modelAndView, false);
					modelAndView.setViewName("index");
				}
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

		if(PortalDisplayUtil.isNotDesktopBrowser(renderRequest)){
			fillCDetails(currentCLevel, renderRequest, eadParams.getPageNumber(), modelAndView,true);
			modelAndView.setViewName("eaddetails-noscript");
		}else {
			fillCDetails(currentCLevel, renderRequest, eadParams.getPageNumber(), modelAndView,false);
			modelAndView.setViewName("index");
		}
		return modelAndView;
	}
}
