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
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.asset.AssetRendererFactoryRegistryUtil;
import com.liferay.portlet.asset.model.AssetEntry;
import com.liferay.portlet.asset.model.AssetLink;
import com.liferay.portlet.asset.model.AssetRenderer;
import com.liferay.portlet.asset.model.AssetRendererFactory;
import com.liferay.portlet.asset.service.AssetEntryLocalServiceUtil;
import com.liferay.portlet.asset.service.AssetEntryServiceUtil;
import com.liferay.portlet.asset.service.AssetLinkLocalServiceUtil;
import com.liferay.portlet.journal.model.JournalArticle;
import com.liferay.portlet.journal.model.JournalArticleDisplay;
import com.liferay.portlet.journal.service.JournalArticleLocalServiceUtil;
import com.liferay.portlet.journal.util.comparator.ArticleDisplayDateComparator;

@Controller(value = "featuredExhibitionFrontpageController")
@RequestMapping(value = "VIEW")
public class FeaturedExhibitionFrontpageController {
	private final static Logger LOGGER = Logger.getLogger(FeaturedExhibitionFrontpageController.class);

	@RenderMapping
	public ModelAndView showFeaturedExhibition(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("frontpage-index");
		ThemeDisplay themeDisplay = (ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY);
		String templateId = request.getPreferences().getValue("webContentTemplateId", "");
		List<FeaturedExhibition> featuredExhibitions = new ArrayList<FeaturedExhibition>();
		try {
			List<JournalArticle> articles = JournalArticleLocalServiceUtil.search(themeDisplay.getCompanyId(),
					themeDisplay.getScopeGroupId(), 0, null, null, null, null, templateId, null, null, 0, null, 0, 20,
					new ArticleDisplayDateComparator());
			for (JournalArticle journalArticle : articles) {
				AssetEntry assetEntry = null;

				if (JournalArticleLocalServiceUtil.isLatestVersion(themeDisplay.getScopeGroupId(),
						journalArticle.getArticleId(), journalArticle.getVersion())) {
					JournalArticleDisplay articleDisplay = JournalArticleLocalServiceUtil.getArticleDisplay(
							themeDisplay.getScopeGroupId(), journalArticle.getArticleId(), "view",
							themeDisplay.getLanguageId(), themeDisplay);
					FeaturedExhibition featuredExhibition = new FeaturedExhibition();
					featuredExhibition.setContent(articleDisplay.getContent());
					try {
						assetEntry = AssetEntryLocalServiceUtil.getEntry(JournalArticle.class.getName(),
								articleDisplay.getResourcePrimKey());
						long assetEntryId = assetEntry.getEntryId();
						List<AssetLink> assetLinks = AssetLinkLocalServiceUtil.getDirectLinks(assetEntryId);
						for (AssetLink assetLink : assetLinks) {
							AssetEntry assetLinkEntry = null;

							if (assetLink.getEntryId1() == assetEntryId) {
								assetLinkEntry = AssetEntryServiceUtil.getEntry(assetLink.getEntryId2());
							} else {
								assetLinkEntry = AssetEntryServiceUtil.getEntry(assetLink.getEntryId1());
							}

							if (!assetLinkEntry.isVisible()) {
								continue;
							}
							assetLinkEntry = assetLinkEntry.toEscapedModel();

							AssetRendererFactory assetRendererFactory = AssetRendererFactoryRegistryUtil
									.getAssetRendererFactoryByClassName(PortalUtil.getClassName(assetLinkEntry
											.getClassNameId()));

							AssetRenderer assetRenderer = assetRendererFactory.getAssetRenderer(assetLinkEntry
									.getClassPK());

							if (assetRenderer.hasViewPermission(themeDisplay.getPermissionChecker())) {
								featuredExhibition.setDetailsClassPk(assetLinkEntry.getClassPK());
							}
						}
					} catch (Exception e) {
					}
					featuredExhibitions.add(featuredExhibition);

				}

				if (assetEntry != null) {

				}
			}

		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		modelAndView.addObject("featuredExhibitions", featuredExhibitions);
		return modelAndView;
	}

}
