package eu.archivesportaleurope.portal.featuredexhibition;

import java.util.ArrayList;
import java.util.List;

import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;

@Controller(value = "featuredExhibitionController")
@RequestMapping(value = "VIEW")
public class FeaturedExhibitionController {
	private final static Logger LOGGER = Logger.getLogger(FeaturedExhibitionController.class);

	@RenderMapping
	public ModelAndView showFeaturedExhibition(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String templateId = request.getPreferences().getValue("webContentTemplateId", "");
		List<String> articlesContent = new ArrayList<String>();
		try {
			List<JournalArticle> articles = JournalArticleLocalServiceUtil.getTemplateArticles(
					themeDisplay.getScopeGroupId(), templateId);
			for (JournalArticle journalArticle : articles) {
				LOGGER.info(journalArticle.getArticleId() + ":" + journalArticle.getVersion() + ": " + journalArticle.getTitle(themeDisplay.getLanguageId()));
				if (JournalArticleLocalServiceUtil.isLatestVersion(themeDisplay.getScopeGroupId(),journalArticle.getArticleId(), journalArticle.getVersion())){
					JournalArticleDisplay article = JournalArticleLocalServiceUtil.getArticleDisplay(
							themeDisplay.getScopeGroupId(), journalArticle.getArticleId() , "view", themeDisplay.getLanguageId(), themeDisplay);
					articlesContent.add(article.getContent());
				}
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		modelAndView.addObject("articlesContent", articlesContent);
		return modelAndView;
	}

}
