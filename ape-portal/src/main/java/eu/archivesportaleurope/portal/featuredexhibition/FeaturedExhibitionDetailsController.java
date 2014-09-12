package eu.archivesportaleurope.portal.featuredexhibition;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portlet.journal.NoSuchArticleException;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.util.comparator.ArticleDisplayDateComparator;

import eu.archivesportaleurope.portal.common.PortalDisplayUtil;

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
		String languageId = themeDisplay.getLanguageId();
		String articleContent = null;
		String documentTitle = null;
		try {
			if (StringUtils.isNotBlank(classPK) && StringUtils.isNumeric(classPK)){
				long classPkLong = Long.parseLong(classPK);
				JournalArticle journalArticle = JournalArticleLocalServiceUtil.getLatestArticle(classPkLong);
				JournalArticleDisplay articleDisplay = JournalArticleLocalServiceUtil.getArticleDisplay(
						themeDisplay.getScopeGroupId(), journalArticle.getArticleId() , "view",languageId , themeDisplay);
				articleContent = articleDisplay.getContent();
				documentTitle = articleDisplay.getTitle();
			}else if (StringUtils.isNotBlank(articleId)){
				JournalArticleDisplay article = JournalArticleLocalServiceUtil.getArticleDisplay(themeDisplay.getScopeGroupId(), articleId,"view", languageId, themeDisplay);
				if (article == null){
					articleContent = "Article does not exists";
				}else {
					articleContent = article.getContent();
					documentTitle =  article.getTitle();			
				}
			}

		} catch (NoSuchArticleException e){
			LOGGER.warn(e.getMessage());
			articleContent = "Article does not exists";			
		}catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
			articleContent = "Article does not exists";
		}
		modelAndView.addObject("articleDetails", articleContent);
		PortalDisplayUtil.setPageTitle(request, PortalDisplayUtil.getFeaturedExhibitionTitle(documentTitle));

		List<FeaturedExhibitionSummary> featuredExhibitionSummaries = new ArrayList<FeaturedExhibitionSummary>();
		try {
			List<JournalArticle> articles = JournalArticleLocalServiceUtil.search(themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId(), 0, null, null, null, null, templateId, null, null, 0, null, 0, 100,
					new ArticleDisplayDateComparator());
			for (JournalArticle article: articles){
				FeaturedExhibitionSummary featuredExhibitionSummary = new FeaturedExhibitionSummary();
				featuredExhibitionSummary.setClassPk(article.getResourcePrimKey());
				featuredExhibitionSummary.setTitle(article.getTitle(languageId));
				featuredExhibitionSummary.setDate(SIMPLE_DATE_FORMAT.format(article.getDisplayDate()));
				featuredExhibitionSummaries.add(featuredExhibitionSummary);
			}
		}catch (Exception e){
			
		}
		modelAndView.addObject("featuredExhibitionSummaries", featuredExhibitionSummaries);
		return modelAndView;
	}

}
