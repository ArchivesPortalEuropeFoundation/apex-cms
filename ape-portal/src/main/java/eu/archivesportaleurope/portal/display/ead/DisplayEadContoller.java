package eu.archivesportaleurope.portal.display.ead;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
public class DisplayEadContoller extends AbstractEadController {
	private static final String ERROR_USER_SECOND_DISPLAY_NOTINDEXED = "error.user.second.display.notindexed";
	private static final String UNKNOWN = "UNKNOWN";
	private final static Logger LOGGER = Logger.getLogger(DisplayEadContoller.class);
	private static final Pattern POSITION_REGEX = Pattern.compile("c\\d+(-c\\d+){0,9}");

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

	public ModelAndView displayDefaultPage() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.getModelMap().addAttribute("errorMessage", ERROR_USER_SECOND_DISPLAY_NOTEXIST);
		modelAndView.setViewName("indexError");
		return modelAndView;
	}

	@RenderMapping(params = "myaction=displayArchdescAction")
	public ModelAndView displayArchdesc(RenderRequest renderRequest, EadParams eadParams) {
		return displayArchdescInternal(renderRequest, eadParams, new ModelAndView());
	}

	public ModelAndView displayArchdescInternal(RenderRequest renderRequest, EadParams eadParams,
			ModelAndView modelAndView) {
		XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
		if (xmlType != null) {
			boolean published = !eadParams.isPreview();
			modelAndView.getModelMap().addAttribute("previewDetails", eadParams.isPreview());
			Ead ead = eadDAO.getEadByEadid(xmlType.getEadClazz(), eadParams.getRepoCode(), eadParams.getEadid(),published);
			if (ead != null) {
				EadContent eadContent = ead.getEadContent();
				PortalDisplayUtil.setPageTitle(renderRequest,
						PortalDisplayUtil.getEadDisplayTitle(ead, eadContent.getUnittitle()));
				return displayEadDetails(renderRequest, eadParams, modelAndView, ead);
			}
		}
		return displayDefaultPage();
	}

	@RenderMapping(params = "myaction=displayUnitidAction")
	public ModelAndView displayUnitid(RenderRequest renderRequest, EadParams eadParams) {
		ModelAndView modelAndView = new ModelAndView();
		XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
		if (xmlType != null) {
			List<CLevel> clevels = getClevelDAO().getCLevel(eadParams.getRepoCode(), xmlType.getEadClazz(),
					eadParams.getEadid(), eadParams.getUnitid());
			int size = clevels.size();

			if (size > 0) {
				if (size > 1) {
					modelAndView.getModelMap().addAttribute("errorMessage", DISPLAY_EAD_CLEVEL_UNITID_NOTUNIQUE);
				}
				CLevel clevel = clevels.get(0);
				Ead ead = clevel.getEadContent().getEad();
				return displayCDetails(renderRequest, eadParams, modelAndView, ead, clevel);

			} else {
				modelAndView.getModelMap().addAttribute("errorMessage", DISPLAY_EAD_CLEVEL_NOTFOUND);
				return displayArchdescInternal(renderRequest, eadParams, modelAndView);
			}
		}
		return displayDefaultPage();
	}

	@RenderMapping(params = "myaction=displayDatabaseIdAction")
	public ModelAndView displayDatabaseId(RenderRequest renderRequest, EadParams eadParams) {
		ModelAndView modelAndView = new ModelAndView();
		XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
		if (xmlType != null) {
			if (eadParams.getDatabaseId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
				String subSolrId = eadParams.getDatabaseId().substring(1);
				if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
					CLevel clevel = getClevelDAO().getCLevel(eadParams.getRepoCode(), xmlType.getEadClazz(),
							eadParams.getEadid(), Long.parseLong(subSolrId));
					if (clevel != null) {
						Ead ead = clevel.getEadContent().getEad();
						return displayCDetails(renderRequest, eadParams, modelAndView, ead, clevel);
					}
				}
			}
			modelAndView.getModelMap().addAttribute("errorMessage", DISPLAY_EAD_CLEVEL_NOTFOUND);
			return displayArchdescInternal(renderRequest, eadParams, modelAndView);
		}
		return displayDefaultPage();
	}

	@RenderMapping(params = "myaction=displayCidAction")
	public ModelAndView displayCid(RenderRequest renderRequest, EadParams eadParams) {
		ModelAndView modelAndView = new ModelAndView();
		XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
		if (xmlType != null) {
			CLevel clevel = getClevelDAO().getCLevelByCid(eadParams.getRepoCode(), xmlType.getEadClazz(),
					eadParams.getEadid(), eadParams.getCid());
			if (clevel != null) {
				Ead ead = clevel.getEadContent().getEad();
				return displayCDetails(renderRequest, eadParams, modelAndView, ead, clevel);
			}
			modelAndView.getModelMap().addAttribute("errorMessage", DISPLAY_EAD_CLEVEL_NOTFOUND);
			return displayArchdescInternal(renderRequest, eadParams, modelAndView);
		}
		return displayDefaultPage();
	}

	@ActionMapping(params = "myaction=redirectAction")
	public void redirect(ActionRequest actionRequest, ActionResponse actionResponse,
			@ModelAttribute(value = "eadParams") EadParams eadParams) {
		try {
			EadPersistentUrl eadPersistentUrl = generateRedirectUrl(actionRequest, eadParams);
			if (eadPersistentUrl != null) {
				String redirectUrl = FriendlyUrlUtil.getRelativeEadPersistentUrl(actionRequest, eadPersistentUrl);
				actionResponse.sendRedirect(redirectUrl);
			}
		} catch (NotExistInDatabaseException e) {
			// LOGGER.error("SOLRID NOT IN DB:" + e.getId());
		} catch (Exception e) {
			LOGGER.error("Error in ead display process:" + ApeUtil.generateThrowableLog(e));

		}

	}

	@ActionMapping(params = "myaction=redirectPositionAction")
	public void redirectPosition(ActionRequest actionRequest, ActionResponse actionResponse,
			@ModelAttribute(value = "eadParams") EadParams eadParams) {
		try {
			if (StringUtils.isNotBlank(eadParams.getRepoCode()) && StringUtils.isNotBlank(eadParams.getEadid())
					&& StringUtils.isNotBlank(eadParams.getPosition())) {
				XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
				Long cLevelId = getCLevelId(eadParams.getRepoCode(), xmlType.getEadClazz(), eadParams.getEadid(),
						eadParams.getPosition());
				EadPersistentUrl eadPersistentUrl = new EadPersistentUrl(eadParams.getRepoCode(),
						xmlType.getResourceName(), eadParams.getEadid());
				if (cLevelId == null) {
					eadPersistentUrl.setSearchId(SolrValues.C_LEVEL_PREFIX + UNKNOWN);
				} else {
					eadPersistentUrl.setSearchIdAsLong(cLevelId);
				}
				String redirectUrl = FriendlyUrlUtil.getRelativeEadPersistentUrl(actionRequest, eadPersistentUrl);
				actionResponse.sendRedirect(redirectUrl);
			}
		} catch (Exception e) {
			LOGGER.error("Error in ead display process:" + e.getMessage());

		}

	}

	private Long getCLevelId(String repositoryCode, Class<? extends Ead> clazz, String identifier, String position) {
		Matcher m = POSITION_REGEX.matcher(position);
		boolean matches = m.matches();
		Long cLevelId = null;
		if (matches) {
			String[] positions = position.split("-");
			Long parentId = null;

			for (int i = 0; i < positions.length; i++) {
				Integer orderId = Integer.parseInt(positions[i].substring(1));

				if (i == 0) {
					cLevelId = getClevelDAO().getTopCLevelId(repositoryCode, clazz, identifier, orderId);
				} else {
					cLevelId = getClevelDAO().getChildCLevelId(parentId, orderId);
				}
				parentId = cLevelId;
			}

		}
		return cLevelId;
	}

	private EadPersistentUrl generateRedirectUrl(ActionRequest request, EadParams eadParams)
			throws NotExistInDatabaseException {
		Ead ead = null;
		CLevel clevel = null;
		if (StringUtils.isNotBlank(eadParams.getDatabaseId())) {
			if (eadParams.getDatabaseId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
				String subSolrId = eadParams.getDatabaseId().substring(1);
				if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
					clevel = getClevelDAO().findById(Long.parseLong(subSolrId));
					if (clevel != null) {
						ead = clevel.getEadContent().getEad();
					}
				}
			} else {
				String solrPrefix = eadParams.getDatabaseId().substring(0, 1);
				XmlType xmlType = XmlType.getTypeBySolrPrefix(solrPrefix);
				String subId = eadParams.getDatabaseId().substring(1);
				if (xmlType != null) {
					ead = eadDAO.findById(Integer.parseInt(subId), xmlType.getClazz());
				}
			}

		} else if (eadParams.getRepoCode() != null) {
			XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
			if (StringUtils.isNotBlank(eadParams.getEadid())) {
				ead = eadDAO.getEadByEadid(xmlType.getEadClazz(), eadParams.getRepoCode(),
						eadParams.getEadid(), true);
			}
		}

		if (ead != null) {
			EadContent eadContent = ead.getEadContent();
			PortalDisplayUtil.setPageTitle(request,
					PortalDisplayUtil.getEadDisplayTitle(ead, eadContent.getUnittitle()));
			XmlType xmlType = XmlType.getContentType(ead);
			EadPersistentUrl eadPersistentUrl = new EadPersistentUrl(ead.getArchivalInstitution().getRepositorycode(),
					xmlType.getResourceName(), ead.getEadid());
			eadPersistentUrl.setClevel(clevel);
			eadPersistentUrl.setPageNumberAsInt(eadParams.getPageNumber());
			eadPersistentUrl.setSearchFieldsSelectionId(eadParams.getElement());
			eadPersistentUrl.setSearchTerms(eadParams.getTerm());
			return eadPersistentUrl;
		} else {
			return null;
		}

	}

	private ModelAndView displayEadDetails(RenderRequest renderRequest, EadParams eadParams, ModelAndView modelAndView,
			Ead ead) {
		try {
			fillCommon(modelAndView, eadParams);
			if (ead == null) {
				modelAndView.getModelMap().addAttribute("errorMessage", ERROR_USER_SECOND_DISPLAY_NOTEXIST);
				modelAndView.setViewName("indexError");
				return modelAndView;
			} else {
				if (!eadParams.isPreview() && !ead.isPublished()) {
					// LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", ERROR_USER_SECOND_DISPLAY_NOTINDEXED);
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				EadContent eadContent = ead.getEadContent();
				if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
					fillEadDetails(eadContent, renderRequest, eadParams.getPageNumber(), modelAndView, true);
					modelAndView.setViewName("eaddetails-noscript");
				} else {
					fillEadDetails(eadContent, renderRequest, null, modelAndView, false);
					modelAndView.setViewName("index");
				}
				return modelAndView;
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process: " + e.getMessage());
			modelAndView.getModelMap().addAttribute("errorMessage", ERROR_USER_SECOND_DISPLAY_NOTEXIST);
			modelAndView.setViewName("indexError");
			return modelAndView;
		}

	}

	private ModelAndView displayCDetails(RenderRequest renderRequest, EadParams eadParams, ModelAndView modelAndView,
			Ead ead, CLevel currentCLevel) {
		fillCommon(modelAndView, eadParams);
		modelAndView.getModelMap().addAttribute("solrId", SolrValues.C_LEVEL_PREFIX + currentCLevel.getId());
		if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
			fillCDetails(currentCLevel, renderRequest, eadParams.getPageNumber(), modelAndView, true);
			modelAndView.setViewName("eaddetails-noscript");
		} else {
			fillCDetails(currentCLevel, renderRequest, eadParams.getPageNumber(), modelAndView, false);
			modelAndView.setViewName("index");
		}
		return modelAndView;
	}

	private void fillCommon(ModelAndView modelAndView, EadParams eadParams) {
		modelAndView.getModelMap().addAttribute("element", eadParams.getElement());
		modelAndView.getModelMap().addAttribute("term", ApeUtil.decodeSpecialCharacters(eadParams.getTerm()));
		modelAndView.getModel().put("recaptchaAjaxUrl", PropertiesUtil.get(PropertiesKeys.APE_RECAPTCHA_AJAX_URL));
		modelAndView.getModelMap().addAttribute("recaptchaPubKey",
				PropertiesUtil.get(PropertiesKeys.LIFERAY_RECAPTCHA_PUB_KEY));
	}

}