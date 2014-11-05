package eu.archivesportaleurope.portal.ead;

import java.util.List;

import javax.portlet.PortletRequest;

import org.springframework.context.MessageSource;
import org.springframework.web.portlet.ModelAndView;

import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.utils.DisplayUtils;
import eu.apenet.commons.xslt.tags.AbstractEadTag;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;

public class AbstractEadController {
	private static final int PAGE_SIZE = 10;
	private CLevelDAO clevelDAO;
	private MessageSource messageSource;


	public void setClevelDAO(CLevelDAO clevelDAO) {
		this.clevelDAO = clevelDAO;
	}

	public MessageSource getMessageSource() {
		return messageSource;
	}

	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	protected CLevelDAO getClevelDAO() {
		return clevelDAO;
	}

	protected ModelAndView fillCDetails(CLevel currentCLevel, PortletRequest portletRequest, Integer pageNumber, ModelAndView modelAndView) {
		Integer pageNumberInt = 1;
		if (pageNumber != null) {
			pageNumberInt = pageNumber;
		}
		modelAndView.getModelMap().addAttribute("type", AbstractEadTag.CDETAILS_XSLT);
		int orderId = (pageNumberInt - 1) * PAGE_SIZE;
		List<CLevel> children = clevelDAO.findChildCLevels(currentCLevel.getClId(), orderId, PAGE_SIZE);
		Long totalNumberOfChildren = clevelDAO.countChildCLevels(currentCLevel.getClId());
		StringBuilder builder = new StringBuilder();
		builder.append("<c xmlns=\"urn:isbn:1-931666-22-9\">");
		for (CLevel child : children) {
			builder.append(child.getXml());
		}
		builder.append("</c>");
		ArchivalInstitution archivalInstitution = currentCLevel.getEadContent().getEad().getArchivalInstitution();
		modelAndView.getModelMap().addAttribute("c", currentCLevel);
		modelAndView.getModelMap().addAttribute("totalNumberOfChildren", totalNumberOfChildren);
		modelAndView.getModelMap().addAttribute("pageNumber", pageNumberInt);
		modelAndView.getModelMap().addAttribute("pageSize", PAGE_SIZE);
		modelAndView.getModelMap().addAttribute("childXml", builder.toString());
		SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
				portletRequest.getLocale());
		String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
		modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
		String documentTitle = currentCLevel.getUnittitle();
		EadContent eadContent = currentCLevel.getEadContent();
		documentTitle = PortalDisplayUtil.getEadDisplayTitle(eadContent.getEad(), documentTitle);
		modelAndView.getModelMap().addAttribute("documentTitle", documentTitle);
		modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
		modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
		modelAndView.getModelMap().addAttribute("eadContent", eadContent);
		XmlType xmlType = XmlType.getContentType(eadContent.getEad());
		modelAndView.getModelMap().addAttribute("xmlTypeName", xmlType.getResourceName());
		modelAndView.getModel().put("recaptchaAjaxUrl", PropertiesUtil.get(PropertiesKeys.APE_RECAPTCHA_AJAX_URL));
		modelAndView.getModelMap().addAttribute("recaptchaPubKey",
				PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY));
		return modelAndView;
	}

	protected ModelAndView fillEadDetails(EadContent eadContent, PortletRequest portletRequest,ModelAndView modelAndView) {
		Ead ead = eadContent.getEad();
		modelAndView.getModelMap().addAttribute("type", AbstractEadTag.FRONTPAGE_XSLT);
		SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
				portletRequest.getLocale());
		ArchivalInstitution archivalInstitution = ead.getArchivalInstitution();
		String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
		modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
		String documentTitle = eadContent.getUnittitle();
		documentTitle = PortalDisplayUtil.getEadDisplayTitle(ead, documentTitle);
		modelAndView.getModelMap().addAttribute("documentTitle", documentTitle);
		modelAndView.getModelMap().addAttribute("eadContent", eadContent);
		XmlType xmlType = XmlType.getContentType(ead);
		modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
		modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
		modelAndView.getModelMap().addAttribute("xmlTypeName", xmlType.getResourceName());
		modelAndView.getModel().put("recaptchaAjaxUrl", PropertiesUtil.get(PropertiesKeys.APE_RECAPTCHA_AJAX_URL));
		modelAndView.getModelMap().addAttribute("recaptchaPubKey",
				PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY));

		return modelAndView;
	}
}
