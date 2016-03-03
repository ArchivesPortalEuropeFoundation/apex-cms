package eu.archivesportaleurope.portal.api;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.User;
import eu.apenet.persistence.dao.ApiKeyDAO;
import eu.apenet.persistence.hibernate.ApiKeyHibernateDAO;
import eu.apenet.persistence.vo.BaseEntity;
import eu.archivesportaleurope.portal.common.ApiKeyGenUtil;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import javax.portlet.RenderRequest;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author kaisar
 */
@Controller(value = "apiController")
@RequestMapping(value = "VIEW")
public class ApiController {

    private ApiKeyDAO apiKeyDAO;
    private final static Logger LOGGER = Logger.getLogger(ApiController.class);
    private ResourceBundleMessageSource messageSource;

    public void setApiKeyDAO(ApiKeyHibernateDAO apiKeyHibernateDAO) {
        this.apiKeyDAO = apiKeyHibernateDAO;
    }

    public void setMessageSource(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RenderMapping
    public ModelAndView chooseLayout(PortletRequest portletRequest) throws SystemException {
        ApiKey apiKey;
        Principal principal = portletRequest.getUserPrincipal();
        ModelAndView modelAndView = new ModelAndView();
        PortalDisplayUtil.setPageTitle(portletRequest, PortalDisplayUtil.TITLE_API_KEY);
        if (principal != null) {
            User user = (User) portletRequest.getAttribute(WebKeys.USER);
            Long liferayUserId = Long.parseLong(principal.toString());
            eu.apenet.persistence.vo.ApiKey persistantApiKey = apiKeyDAO.findByLiferayUserId(liferayUserId);
            if (persistantApiKey != null) {
                LOGGER.info("::: api key found in DB :::");
                apiKey = new ApiKey(persistantApiKey);
                LOGGER.info(persistantApiKey.toString());
            } else {
                LOGGER.info("::: api key not found in DB :::");
                apiKey = new ApiKey();
                apiKey.setFirstName(user.getFirstName());
                apiKey.setLastName(user.getLastName());
                apiKey.setEmail(user.getEmailAddress());
                apiKey.setLiferayUserId(liferayUserId);
                apiKey.setStatus(BaseEntity.STATUS_CREATED);
            }
            modelAndView.getModelMap().addAttribute("apiKey", apiKey);
            modelAndView.setViewName("home");
            LOGGER.info(apiKey.toString());
//            LOGGER.info(persistantApiKey.toString());
        }

        return modelAndView;
    }

    @ActionMapping(params = "myaction=getApiKey")
    public void saveApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException, NoSuchAlgorithmException {
        Principal principal = actionRequest.getUserPrincipal();
        if (principal != null) {
            User user = (User) actionRequest.getAttribute(WebKeys.USER);
            LOGGER.info("::: User is : " + user.getFullName() + " :::");
            Long liferayUserId = Long.parseLong(principal.toString());
            eu.apenet.persistence.vo.ApiKey persistantApiKey = apiKeyDAO.findByLiferayUserId(liferayUserId);
            if (persistantApiKey == null) {
                LOGGER.info("::: No api key found in DB :::");
                apiKey.setKey(ApiKeyGenUtil.generateApiKey(user.getEmailAddress()));
                LOGGER.info("::: Set api key :::");
                LOGGER.info("::::Object presen is " + apiKey.toString());
                apiKey.setStatus(BaseEntity.STATUS_CREATED);
                apiKeyDAO.store(apiKey.getPerApiKey(apiKey));
                LOGGER.info("::: api key sotred in DB :::");
                //apiKey = new ApiKey(apiKeyDAO.findByEmail(user.getEmailAddress()));
            }
            response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.API_KEY));
        } else {
            LOGGER.error(":::: No Principle found ::::");
        }
    }

    @ActionMapping(params = "myaction=changeApiKey")
    public void changeApiKeyView(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        Principal principal = actionRequest.getUserPrincipal();
        if (principal != null) {
            User user = (User) actionRequest.getAttribute(WebKeys.USER);
            Long liferayUserId = Long.parseLong(principal.toString());
            eu.apenet.persistence.vo.ApiKey persistantApiKey = apiKeyDAO.findByLiferayUserId(liferayUserId);
            if (persistantApiKey != null) {
                persistantApiKey.setStatus(BaseEntity.STATUS_DELETED);
                apiKeyDAO.update(persistantApiKey);
            }
            response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.API_KEY));
            LOGGER.error(":::: api key changed to null ::::");
        } else {
            LOGGER.error(":::: No Principle found ::::");
        }
    }

    @ActionMapping(params = "myaction=doChangeApiKey")
    public void changeApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        if (actionRequest.getUserPrincipal() != null) {
            apiKey.setKey("changed");
            response.sendRedirect("/api-key");
        }
    }
}
