package eu.archivesportaleurope.portal.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.PortletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
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


	@RenderMapping
	public ModelAndView displayLanguage(PortletRequest portletRequest) throws SystemException {

		ModelAndView modelAndView = new ModelAndView();
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getLayout().getGroupId();
		List<Layout> layouts = LayoutLocalServiceUtil.getLayouts(groupId, false);
		List<Translation> translations = new ArrayList<Translation>();
		for (Layout layout: layouts){
			
			Locale[] locales = LanguageUtil.getAvailableLocales();
			for (Locale locale: locales){
				translations.add(new Translation(locale.toString(), layout.getFriendlyURL(), layout.getName(locale)));
			}
		}
		
		modelAndView.getModelMap().addAttribute("menuTranslations",translations);
		modelAndView.setViewName("index");
		return modelAndView;

	}
}
