package eu.archivesportaleurope.portal.admin;

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
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

/**
 * 
 * 
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "adminController")
@RequestMapping(value = "VIEW")
public class AdminController {
	private final static Logger LOGGER = Logger.getLogger(AdminController.class);
	private ResourceBundleMessageSource messageSource;
	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@RenderMapping
    public ModelAndView chooseLayout(PortletRequest portletRequest) throws SystemException {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        return modelAndView;
    }

    @ActionMapping(params = "myaction=cleanServer")
	public void cleanServer(ActionRequest actionRequest, ActionResponse response) {
    	try {
        ThemeDisplay themeDisplay = (ThemeDisplay) actionRequest.getAttribute(WebKeys.THEME_DISPLAY);
        long groupId = themeDisplay.getLayout().getGroupId();
        List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
        for (Layout layout: layouts){
        	layout.setThemeId(null);
        	layout.getExpandoBridge().setAttribute("display-in-footer", null);
        	LayoutLocalServiceUtil.updateLayout(layout);
        }
    	}catch (Exception e){
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

}
