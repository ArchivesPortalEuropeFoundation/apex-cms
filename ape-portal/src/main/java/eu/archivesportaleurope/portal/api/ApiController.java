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
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
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
                this.apiKey = new ApiKey(persistantApiKey);
            } else {
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
    public void saveApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        if (actionRequest.getUserPrincipal() != null) {
            User user = (User) actionRequest.getAttribute(WebKeys.USER);
            eu.apenet.persistence.vo.ApiKey perApiKey = apiKeyDAO.findByEmail(user.getEmailAddress());
            if (perApiKey == null) {
                apiKey.setKey(new Date().toString());
                apiKeyDAO.store(apiKey.getPerApiKey(apiKey));
                this.apiKey = new ApiKey(apiKeyDAO.findByEmail(user.getEmailAddress()));
            } else {
                perApiKey.setApiKey(new Date().toString());
                apiKeyDAO.update(perApiKey);
            }
        }
    }

    @ActionMapping(params = "myaction=changeApiKey")
    public void changeApiKeyView(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        if (actionRequest.getUserPrincipal() != null) {
            User user = (User) actionRequest.getAttribute(WebKeys.USER);
            eu.apenet.persistence.vo.ApiKey perApiKey = apiKeyDAO.findByEmail(user.getEmailAddress());
            perApiKey.setApiKey(null);
            apiKeyDAO.update(perApiKey);
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
