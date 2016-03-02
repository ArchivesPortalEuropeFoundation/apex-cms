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
import eu.apenet.persistence.hibernate.ApiKeyRepo;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import javax.portlet.RenderRequest;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ApiKey apiKey = null;

    public void setApiKeyDAO(ApiKeyHibernateDAO apiKeyHibernateDAO) {
        this.apiKeyDAO = apiKeyHibernateDAO;
    }

    public void setMessageSource(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RenderMapping
    public ModelAndView chooseLayout(PortletRequest portletRequest) throws SystemException {
        Principal principal = portletRequest.getUserPrincipal();
        ModelAndView modelAndView = new ModelAndView();
        if (principal != null) {
            modelAndView.setViewName("index");
            User user = (User) portletRequest.getAttribute(WebKeys.USER);
            eu.apenet.persistence.vo.ApiKey persistantApiKey = apiKeyDAO.findByEmail(user.getEmailAddress());
            if (persistantApiKey != null) {
                LOGGER.info("::: api key found in DB :::");
                this.apiKey = new ApiKey(persistantApiKey);
            } else {
                LOGGER.info("::: api key not found in DB :::");
                this.apiKey = new ApiKey();
                this.apiKey.setFirstName(user.getFirstName());
                this.apiKey.setLastName(user.getLastName());
                this.apiKey.setEmail(user.getEmailAddress());
            }
            modelAndView.getModelMap().addAttribute("apiKey", this.apiKey);
        }
        return modelAndView;
    }

    @ActionMapping(params = "myaction=getApiKey")
    public void saveApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) {
        if (actionRequest.getUserPrincipal() != null) {
            User user = (User) actionRequest.getAttribute(WebKeys.USER);
            LOGGER.info("::: User is : " + user.getFullName() + " :::");
            eu.apenet.persistence.vo.ApiKey perApiKey = apiKeyDAO.findByEmail(user.getEmailAddress());
            if (perApiKey == null) {
                LOGGER.info("::: No api key found in DB :::");
                apiKey.setKey(new Date().toString());
                LOGGER.info("::: Set api key :::");
                apiKeyDAO.store(apiKey.getPerApiKey(apiKey));
                LOGGER.info("::: api key sotred in DB :::");
                this.apiKey = new ApiKey(apiKeyDAO.findByEmail(user.getEmailAddress()));
            } else {
                LOGGER.info("::: api key found in DB :::");
                perApiKey.setApiKey(new Date().toString());
                apiKeyDAO.update(perApiKey);
                LOGGER.info("::: api key updated in DB :::");
            }
//            System.out.println(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.API_KEY));
//            response.sendRedirect(FriendlyUrlUtil.getRelativeUrl(FriendlyUrlUtil.API_KEY),null);
        } else {
            LOGGER.error(":::: No Principle found ::::");
        }
    }

    @RenderMapping(params = "myaction=editApiKey")
    public String edit(RenderRequest renderRequest) {
        eu.apenet.persistence.vo.ApiKey perApiKey = apiKeyDAO.findByEmail(((User) renderRequest.getAttribute(WebKeys.USER)).getEmailAddress());
        perApiKey.setApiKey(null);
        apiKeyDAO.update(perApiKey);
        return "index";
    }

    @ActionMapping(params = "myaction=changeApiKey")
    public void changeApiKeyView(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        if (actionRequest.getUserPrincipal() != null) {
            User user = (User) actionRequest.getAttribute(WebKeys.USER);
            eu.apenet.persistence.vo.ApiKey perApiKey = apiKeyDAO.findByEmail(user.getEmailAddress());
            LOGGER.error(":::: deleting api key ::::");
            perApiKey.setApiKey(null);
            apiKeyDAO.update(perApiKey);
            LOGGER.error(":::: api key changed to null ::::");
        }
        else {
            LOGGER.error(":::: No Principle found ::::");
        }
    }

    @ActionMapping(params = "myaction=doChangeApiKey")
    public void changeApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        if (actionRequest.getUserPrincipal() != null) {
            apiKey.setKey("changed");
            this.apiKey = apiKey;
            response.sendRedirect("/api-key");
        }
    }
}
