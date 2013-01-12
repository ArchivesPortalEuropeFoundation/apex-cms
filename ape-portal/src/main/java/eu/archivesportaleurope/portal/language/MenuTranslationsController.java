package eu.archivesportaleurope.portal.language;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletRequest;
import javax.portlet.RenderResponse;

import eu.archivesportaleurope.portal.common.email.EmailSender;
import eu.archivesportaleurope.portal.contact.Contact;
import eu.archivesportaleurope.portal.contact.ContactValidator;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

        TranslationForm translationForm = new TranslationForm(translations);

		modelAndView.getModelMap().addAttribute("translationForm", translationForm);
		modelAndView.setViewName("index");
		return modelAndView;
	}

    @RenderMapping(params = "myaction=saved")
    public String showSuccess(RenderResponse response, Model model) {
        return "saved";
    }

    @RenderMapping(params = "myaction=error")
    public String showError(RenderResponse response, Model model) {
        return "error";
    }

    @ActionMapping(params = "myaction=save")
    public void showResult(ActionRequest request, ActionResponse response,
                           @RequestParam("index") int index,
                           @ModelAttribute("translationForm") TranslationForm translationForm, Model model, PortletRequest portletRequest) {
//    public void showResult(@ModelAttribute("translation") Translation translation, BindingResult result, ActionResponse response) {
        try {
            //save
//            LOGGER.info(translation.getName());
            Translation translation = translationForm.getTranslations().get(index);
            LOGGER.info(translation.getName());

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

            response.setRenderParameter("myaction", "saved");
        } catch (Exception e) {
            LOGGER.error("Error saving translations", e);
            response.setRenderParameter("myaction", "error");
        }
    }
}
