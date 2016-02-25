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
        if (this.apiKey == null || !(this.apiKey.getEmail().equals(user.getEmailAddress()))) {
            System.out.println("Initializing ......");
            this.apiKey = new ApiKey();
            this.apiKey.setFirstName(user.getFirstName());
            this.apiKey.setLastName(user.getLastName());
            this.apiKey.setEmail(user.getEmailAddress());
        }
        modelAndView.getModelMap().addAttribute("apiKey", this.apiKey);
        return modelAndView;
    }

    @ActionMapping(params = "myaction=getApiKey")
    public void saveApiKey(@ModelAttribute("apiKey") ApiKey apiKey, ActionRequest actionRequest, ActionResponse response) throws IOException {
        System.out.println(apiKey.getDomain() + "**************************");
        apiKey.setKey("changed");
        this.apiKey = apiKey;
        response.sendRedirect("/api-key");
    }

    @ActionMapping(params = "myaction=cleanServer")
    public void cleanServer(ActionRequest actionRequest, ActionResponse response) {
        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
            long groupId = themeDisplay.getLayout().getGroupId();
            List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
            for (Layout layout : layouts) {
                layout.setThemeId(null);
                //layout.getExpandoBridge().setAttribute("display-in-footer", null);
                LayoutLocalServiceUtil.updateLayout(layout);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @ActionMapping(params = "myaction=cleanTranslations")
    public void cleanTranslations(ActionRequest actionRequest, ActionResponse response) {
        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
            long groupId = themeDisplay.getLayout().getGroupId();
            List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
            Locale defaultLocale = themeDisplay.getCompany().getLocale();
            LOGGER.info("Default locale: " + defaultLocale);
            for (Layout layout : layouts) {
                String url = layout.getFriendlyURL().substring(1);
                Map<Locale, String> nameMap = layout.getNameMap();
                for (Locale locale : nameMap.keySet()) {
                    if (!locale.equals(defaultLocale)) {
                        LOGGER.info("Page " + url + " remove translation " + locale);
                        layout.setName(null, locale);
                        layout.setTitle(null, locale);

                    }
                }
                LayoutLocalServiceUtil.updateLayout(layout);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @ActionMapping(params = "myaction=updateTranslations")
    public void updateTranslations(ActionRequest actionRequest, ActionResponse response) {
        try {
            ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
            long groupId = themeDisplay.getLayout().getGroupId();
            List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
            Locale defaultLocale = themeDisplay.getCompany().getLocale();
            Locale.setDefault(defaultLocale);
            LOGGER.info("Default locale: " + defaultLocale);
            for (Layout layout : layouts) {
                String url = layout.getFriendlyURL().substring(1);
                String resourcePropertyMenuName = "menu." + url;
                String resourcePropertyMenuTitle = resourcePropertyMenuName + ".title";
                Locale[] locales = LanguageUtil.getAvailableLocales();
                String defaultLocaleMenuName = "";

                try {
                    defaultLocaleMenuName = messageSource.getMessage(resourcePropertyMenuName, null, defaultLocale);
                    LOGGER.info("Page " + url + " add default " + defaultLocale + " " + defaultLocaleMenuName);
                    layout.setName(defaultLocaleMenuName, defaultLocale);
                } catch (NoSuchMessageException e) {
                    LOGGER.error("No translation " + defaultLocale + ": " + resourcePropertyMenuName);
                }
                String defaultLocaleMenuTitle = "";
                try {
                    defaultLocaleMenuTitle = messageSource.getMessage(resourcePropertyMenuTitle, null, defaultLocale);
                    LOGGER.info("Page " + url + " add default " + defaultLocale + " " + defaultLocaleMenuTitle);
                    layout.setTitle(defaultLocaleMenuTitle, defaultLocale);
                } catch (NoSuchMessageException e) {
                    LOGGER.error("No translation " + defaultLocale + ": " + resourcePropertyMenuTitle);
                }
                for (Locale locale : locales) {
                    try {
                        String localizedMenuName = messageSource.getMessage(resourcePropertyMenuName, null, locale);
                        if (StringUtils.isNotBlank(localizedMenuName) && !localizedMenuName.equals(defaultLocaleMenuName)) {
                            LOGGER.info("Page " + url + " add translation " + locale + " " + localizedMenuName);
                            layout.setName(localizedMenuName, locale);
                        }
                    } catch (NoSuchMessageException e) {
                    }

                    try {
                        String localizedMenuTitle = messageSource.getMessage(resourcePropertyMenuTitle, null, locale);
                        if (StringUtils.isNotBlank(localizedMenuTitle) && !localizedMenuTitle.equals(defaultLocaleMenuName)) {
                            LOGGER.info("Page " + url + " add translation " + locale + " " + localizedMenuTitle);
                            layout.setTitle(localizedMenuTitle, locale);
                        }
                    } catch (NoSuchMessageException e) {
                    }
                }
                LayoutLocalServiceUtil.updateLayout(layout);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
