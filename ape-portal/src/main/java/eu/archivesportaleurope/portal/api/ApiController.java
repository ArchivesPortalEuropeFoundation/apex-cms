package eu.archivesportaleurope.portal.api;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.User;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import java.io.IOException;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 *
 *
 * @author bverhoef
 *
 */
@Controller(value = "apiController")
@RequestMapping(value = "VIEW")
public class ApiController {

    private final static Logger LOGGER = Logger.getLogger(ApiController.class);
    private ResourceBundleMessageSource messageSource;
    private ApiKey apiKey = null;

    public void setMessageSource(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @RenderMapping
    public ModelAndView chooseLayout(PortletRequest portletRequest) throws SystemException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        User user = (User) portletRequest.getAttribute(WebKeys.USER);
//        if (this.apiKey == null || !(this.apiKey.getEmail().equals(user.getEmailAddress()))) {
        if (this.apiKey == null) {
            this.apiKey = new ApiKey();
        }
        this.apiKey.setFirstName(user.getFirstName());
        this.apiKey.setLastName(user.getLastName());
        this.apiKey.setEmail(user.getEmailAddress());
//        }
        modelAndView.getModelMap().addAttribute("apiKey", this.apiKey);
        return modelAndView;
    }

    @ActionMapping(params = "myaction=getApiKey")
    public void saveApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        System.out.println(":::" + apiKey.getKey() + ":" + this.apiKey.getKey() + ":::");
        if ("renew".equals(apiKey.getKey())) {
            System.out.println(":::Change Request:::");
        } else {
            System.out.println(":::New Request:::");
        }
        apiKey.setKey("changed");
        this.apiKey = apiKey;
        System.out.println(":::" + apiKey.getKey() + ":" + this.apiKey.getKey() + ":::");
        response.sendRedirect("/api-key", null);
    }

    @ActionMapping(params = "myaction=changeApiKey")
    public void changeApiKeyView(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        apiKey.setKey("renew");
        this.apiKey = apiKey;
    }

    @ActionMapping(params = "myaction=doChangeApiKey")
    public void changeApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        apiKey.setKey("changed");
        this.apiKey = apiKey;
        response.sendRedirect("/api-key");
    }
}
