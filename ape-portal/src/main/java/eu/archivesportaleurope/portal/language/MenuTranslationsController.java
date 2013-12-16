package eu.archivesportaleurope.portal.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ActionMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * 
 * This is display ead controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "menuTranslationsController")
@RequestMapping(value = "VIEW")
public class MenuTranslationsController {
	private final static Logger LOGGER = Logger.getLogger(MenuTranslationsController.class);
	private ResourceBundleMessageSource messageSource;
	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@RenderMapping
    public ModelAndView chooseLayout(PortletRequest portletRequest) throws SystemException {
        ModelAndView modelAndView = new ModelAndView();
        ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        long groupId = themeDisplay.getLayout().getGroupId();
        List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
        List<String> layoutPages = new ArrayList<String>();
        for (Layout layout: layouts){
            layoutPages.add(layout.getFriendlyURL());
        }
        modelAndView.getModelMap().addAttribute("layouts", layoutPages);
        modelAndView.setViewName("first");
        return modelAndView;
    }

    @RenderMapping(params = "myaction=display")
    public ModelAndView displayAllLanguages(PortletRequest portletRequest, @RequestParam(value="page", required = false) String page) throws SystemException{
        LOGGER.info("page: " + page);

        ModelAndView modelAndView = new ModelAndView();
        ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
        long groupId = themeDisplay.getLayout().getGroupId();
        List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
        List<Translation> translations = new ArrayList<Translation>();
        for (Layout layout1: layouts){
            if(layout1.getFriendlyURL().equals(page)) {
                Locale[] locales = LanguageUtil.getAvailableLocales();
                for (Locale locale: locales){
                    translations.add(new Translation(locale.toString(), layout1.getFriendlyURL(), layout1.getName(locale)));
                }
            }
        }
        TranslationForm translationForm = new TranslationForm(translations);
        modelAndView.getModelMap().addAttribute("translationForm", translationForm);
        modelAndView.setViewName("index");
        return modelAndView;
    }
    
    @ActionMapping(params = "myaction=cleanTranslations")
	public void cleanTranslations(ActionRequest actionRequest, ActionResponse response) {
    	try {
        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        long groupId = themeDisplay.getLayout().getGroupId();
        List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
        Locale defaultLocale = themeDisplay.getCompany().getLocale();
        LOGGER.info("Default locale: " + defaultLocale);
        for (Layout layout: layouts){
        	String url = layout.getFriendlyURL().substring(1);
        	Map<Locale, String> nameMap = layout.getNameMap();
        	for (Locale locale: nameMap.keySet()){
        		if (!locale.equals(defaultLocale)){
        			LOGGER.info ("Page " + url + " remove translation " + locale);
        			layout.setName(null, locale);

        		}
        	}
        	LayoutLocalServiceUtil.updateLayout(layout);
        }
    	}catch (Exception e){
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
        for (Layout layout: layouts){
        	String url = layout.getFriendlyURL().substring(1);
    		String resourcePropertyName = "menu." + url;
        	Locale[] locales = LanguageUtil.getAvailableLocales();
        	String defaultLocaleMenuName = "";
        	try{
        		defaultLocaleMenuName = messageSource.getMessage(resourcePropertyName, null, defaultLocale);
			}catch (NoSuchMessageException e){
				LOGGER.error("No translation "+ defaultLocale + ": " +resourcePropertyName);
			}
        	for (Locale locale: locales){

        		try {
					String localizedMenuName = messageSource.getMessage(resourcePropertyName, null, locale);
	        		if (StringUtils.isNotBlank(localizedMenuName) && !localizedMenuName.equals(defaultLocaleMenuName)){
	        			LOGGER.info ("Page " + url + " add translation " + locale);
	        			layout.setName(localizedMenuName, locale);       			
	        		}
        		}catch (NoSuchMessageException e){
        		}
        	}
        	LayoutLocalServiceUtil.updateLayout(layout);
        }
    	}catch (Exception e){
    		LOGGER.error(e.getMessage(), e);
    	}
	}
    @ActionMapping(params = "myaction=display")
	public void displayLanguage(PortletRequest portletRequest, ActionResponse response, @RequestParam(value="layout", required = false) String layout) throws SystemException {
        response.setRenderParameter("myaction", "display");
        response.setRenderParameter("page", layout);
	}

    @RenderMapping(params = "myaction=error")
    public String showError(RenderResponse response, Model model) {
        return "error";
    }

    @ActionMapping(params = "myaction=save")
    public void showResult(ActionRequest request, ActionResponse response, @RequestParam("index") int index, @ModelAttribute("translationForm") TranslationForm translationForm, Model model, PortletRequest portletRequest) {
        try {
            Translation translation = translationForm.getTranslations().get(index);
            ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
            long groupId = themeDisplay.getLayout().getGroupId();
            List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
            LOGGER.info("Looking for layout: " + translation.getFriendlyUrl());
            for (Layout layout: layouts) {
                if(layout.getFriendlyURL().equals(translation.getFriendlyUrl())) {
                    LOGGER.info("Found our layout");
                    LOGGER.info("Looking for locale: " + translation.getLanguageId());
                    Locale[] locales = LanguageUtil.getAvailableLocales();
                    for (Locale locale: locales) {
                        if(locale.toString().equals(translation.getLanguageId())) {
                            LOGGER.info("Found our locale");
                            layout.setName(translation.getName(), locale);
                            LayoutLocalServiceUtil.updateLayout(layout);
                            LOGGER.info("Updated our layout!");
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Error saving translations", e);
            response.setRenderParameter("myaction", "error");
        }
    }
}
