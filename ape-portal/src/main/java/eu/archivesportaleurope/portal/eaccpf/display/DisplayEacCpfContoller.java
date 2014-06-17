package eu.archivesportaleurope.portal.eaccpf.display;

import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.xslt.tags.AbstractEacTag;
import eu.apenet.persistence.dao.EacCpfDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.EacCpf;
import eu.archivesportaleurope.portal.common.NotExistInDatabaseException;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.portal.common.PropertiesKeys;
import eu.archivesportaleurope.portal.common.PropertiesUtil;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
/**
 * 
 * This is display eac cpf controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "displayEacCpfContoller")
@RequestMapping(value = "VIEW")
public class DisplayEacCpfContoller {
	private final static Logger LOGGER = Logger.getLogger(DisplayEacCpfContoller.class);
	private MessageSource messageSource;
	private EacCpfDAO eacCpfDAO;
	
	public MessageSource getMessageSource() {
		return messageSource;
	}
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public EacCpfDAO getEacCpfDAO() {
		return this.eacCpfDAO;
	}
	
	public void setEacCpfDAO(EacCpfDAO eacCpfDAO) {
		this.eacCpfDAO = eacCpfDAO;
	}

	@RenderMapping
	public ModelAndView displayEacCpf(RenderRequest renderRequest, @ModelAttribute(value = "eacParams") EacCpfParams eacParams) {
		ModelAndView modelAndView = null;

		try {
			modelAndView = displayDetails(renderRequest, eacParams);
		}catch (NotExistInDatabaseException e) {
			//LOGGER.error("SOLRID NOT IN DB:" + e.getId());
		}catch (Exception e) {
			LOGGER.error("Error in EAC-CPF display process:" + e.getMessage(),e);

		}
		if (modelAndView == null){
			modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}
	
	@RenderMapping(params = "myaction=printEacDetails")
	public ModelAndView printDetails(@ModelAttribute(value = "eacParams") EacCpfParams eacParams, RenderRequest renderRequest){
		ModelAndView modelAndView = null;
		try {
			modelAndView = displayDetails(renderRequest, eacParams);
			modelAndView.setViewName("printEacdetails");
		}catch (NotExistInDatabaseException e) {
		}catch (Exception e) {
			LOGGER.error("Error in EAC-CPF display process:" + e.getMessage(),e);
		}
		if (modelAndView == null){
			modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}

	public ModelAndView displayDetails(RenderRequest renderRequest, EacCpfParams eacParams) throws NotExistInDatabaseException{
		ModelAndView modelAndView = new ModelAndView();
		EacCpf eaccpf = null;
		
		// Recover the current apeEAC-CPF from identifier and repositoryCode.
		if (eacParams.getEaccpfIdentifier() != null
				&& eacParams.getRepositoryCode() != null){
			eaccpf = eacCpfDAO.getEacCpfByIdentifier(eacParams.getRepositoryCode(), eacParams.getEaccpfIdentifier());
		}
		
		if (eaccpf != null) {
			PortalDisplayUtil.setPageTitle(renderRequest,
					PortalDisplayUtil.getEacCpfDisplayTitle(eaccpf));
				return displayEacDetails(renderRequest, eacParams, modelAndView, eaccpf);

		}else {
			return null;
		}
	}
	
	public ModelAndView displayEacDetails(RenderRequest renderRequest, EacCpfParams eacParam, ModelAndView modelAndView,
			EacCpf eac) {
		ArchivalInstitution archivalInstitution = null;
		try {

			if (eac == null) {
				modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
				modelAndView.setViewName("indexError");
				return modelAndView;
			} else {
				if (eac.isPublished()) {
					archivalInstitution = eac.getArchivalInstitution();
				} else {
					// LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				XmlType xmlType = XmlType.getContentType(eac);
				modelAndView.getModelMap().addAttribute("type", AbstractEacTag.EACCPFDETAILS_XSLT);
				modelAndView.getModelMap().addAttribute("repositoryCode", eacParam.getRepositoryCode());
				modelAndView.getModelMap().addAttribute("eaccpfIdentifier", eacParam.getEaccpfIdentifier());
			//	modelAndView.getModelMap().addAttribute("id",xmlType.getSolrPrefix() + eac.getId());
				modelAndView.getModelMap().addAttribute("eac", eac);
				modelAndView.getModelMap().addAttribute("xmlTypeId", XmlType.getContentType(eac).getIdentifier());
				modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						renderRequest.getLocale());
			//	String localizedName = DisplayUtils.getLocalizedCountryName(source, archivalInstitution.getCountry());
			//	modelAndView.getModelMap().addAttribute("localizedCountryName", localizedName);
				modelAndView.setViewName("index");
				modelAndView.getModel().put("recaptchaAjaxUrl",  PropertiesUtil.get(PropertiesKeys.APE_RECAPTCHA_AJAX_URL));
				modelAndView.getModelMap().addAttribute("recaptchaPubKey",  PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY));
				String documentTitle = eac.getTitle();
				documentTitle = PortalDisplayUtil.getEacCpfDisplayTitle(eac);
				modelAndView.getModelMap().addAttribute("documentTitle",documentTitle);
				return modelAndView;
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process: " + e.getMessage());
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
			return modelAndView;
		}
	}
}
