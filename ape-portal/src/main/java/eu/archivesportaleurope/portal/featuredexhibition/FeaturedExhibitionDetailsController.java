package eu.archivesportaleurope.portal.featuredexhibition;

import java.text.SimpleDateFormat;

import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
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

@Controller(value = "featuredExhibitionDetailsController")
@RequestMapping(value = "VIEW")
public class FeaturedExhibitionDetailsController {
	private final static SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private final static Logger LOGGER = Logger.getLogger(FeaturedExhibitionDetailsController.class);

	@RenderMapping
	public ModelAndView showFeaturedExhibition(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("details-index");
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String articleId = request.getParameter("articleId");
		String classPK = request.getParameter("classPK");
		String templateId = request.getPreferences().getValue("webContentTemplateId", "");
		
		String articleContent = null;
		try {
			if (StringUtils.isNumeric(classPK)){
				long classPkLong = Long.parseLong(classPK);
				JournalArticle journalArticle = JournalArticleLocalServiceUtil.getLatestArticle(classPkLong);
				JournalArticleDisplay articleDisplay = JournalArticleLocalServiceUtil.getArticleDisplay(
						themeDisplay.getScopeGroupId(), journalArticle.getArticleId() , "view", themeDisplay.getLanguageId(), themeDisplay);
				articleContent = articleDisplay.getContent();
			}else if (StringUtils.isNotBlank(articleId)){
				JournalArticleDisplay article = JournalArticleLocalServiceUtil.getArticleDisplay(themeDisplay.getScopeGroupId(), articleId,"view", themeDisplay.getLanguageId(), themeDisplay);
				if (article == null){
					articleContent = "Article does not exists";
				}else {
					articleContent = article.getContent();
				}
			}

		} catch (Exception e) {
			articleContent = "Article does not exists";
		}
		modelAndView.addObject("articleDetails", articleContent);
		return modelAndView;
	}

}
