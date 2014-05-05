package eu.archivesportaleurope.portal.common;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import eu.archivesportaleurope.portal.common.urls.EadPersistentUrl;
import eu.archivesportaleurope.portal.common.urls.SitemapUrl;

public final class FriendlyUrlUtil {
	private final static Logger LOGGER = Logger.getLogger(FriendlyUrlUtil.class);
	public static final String OLD_EAD_DISPLAY_SEARCH = "eaddisplay-search";
	public static final String OLD_EAD_DISPLAY_FRONTPAGE = "eaddisplay-frontpage";
	public static final String EAD_DISPLAY_PERSISTENT = "eaddisplay-persistent-archdesc";	
	public static final String EAD_DISPLAY_PERSISTENT_PAGING = "eaddisplay-persistent-paging";		
	public static final String OLD_EAD_DISPLAY_SEARCH_PAGING = "eaddisplay-search-paging";
	public static final String OLD_EAD_DISPLAY_FRONTPAGE_PAGING = "eaddisplay-frontpage-paging";
	public static final String FEATURED_EXHIBITION = "featured-exhibition-details";
	public static final String FEATURED_EXHIBITION_ARTICLE = "featured-exhibition-details-article";
	public static final String DIRECTORY_COUNTRY = "directory-country";
	public static final String DIRECTORY_INSTITUTION_ID = "directory-institution-id";
	public static final String DIRECTORY_INSTITUTION_CODE = "directory-institution-code";
	public static final String DIRECTORY_CONTENT = "directory-content";
	public static final String DIRECTORY_SITEMAP = "directory-sitemap";
	public static final String SEARCH = "ead-search";
	public static final String SAVED_SEARCH = "saved-search";
	public static final String SAVED_SEARCH_OVERVIEW = "saved-search-overview";
	public static final String SEPARATOR = "/";
	public static final String EAC_CPF_DISPLAY = "eac-display";
	public static final String EAC_CPF_RELATION_DISPLAY = "eac-relation-display";
	

	
	private final static Map<String, String> urls = new HashMap<String, String>();
	static {
		urls.put(OLD_EAD_DISPLAY_SEARCH, "/ead-display/-/ead/s");
		urls.put(OLD_EAD_DISPLAY_FRONTPAGE, "/ead-display/-/ead/fp");
		urls.put(EAD_DISPLAY_PERSISTENT, "/ead-display/-/ead/pl");
		urls.put(FEATURED_EXHIBITION, "/featured-document/-/fed/pk");
		urls.put(FEATURED_EXHIBITION_ARTICLE, "/featured-document/-/fed/a");
		urls.put(DIRECTORY_COUNTRY, "/directory/-/dir/co");
		urls.put(DIRECTORY_INSTITUTION_ID, "/directory/-/dir/ai/id");
		urls.put(DIRECTORY_INSTITUTION_CODE, "/directory/-/dir/ai/code");
		urls.put(DIRECTORY_CONTENT, "/directory/-/dir/content");
		urls.put(OLD_EAD_DISPLAY_SEARCH_PAGING, "/ead-display/-/ead/s-p");
		urls.put(OLD_EAD_DISPLAY_FRONTPAGE_PAGING, "/ead-display/-/ead/fp-p");
		urls.put(DIRECTORY_SITEMAP, "/directory/-/dir/sitemap");
		urls.put(SEARCH, "/search");
		urls.put(SAVED_SEARCH, "/search/-/s/d");
		urls.put(SAVED_SEARCH_OVERVIEW, "/saved-searches/-/sv");
		urls.put(EAC_CPF_DISPLAY, "/eac-display/-/eac/fp");
		urls.put(EAC_CPF_RELATION_DISPLAY, "/eac-display/-/eac/rel");
		
	}

	public static String getUrl(PortletRequest portletRequest, String type) {
		return getUrl(portletRequest, type, false);
	}
	public static String getUrl(PortletRequest portletRequest, String type, boolean noHttps) {
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			String urlHome = themeDisplay.getPortalURL();
			if (noHttps){
				urlHome = urlHome.replaceFirst("https://", "http://");
			}
			if (themeDisplay.isI18n() && StringUtils.isNotBlank(themeDisplay.getI18nPath())
					&& !urlHome.contains(themeDisplay.getI18nPath())) {
				// only desktop users have extra multilanguage urls. This is to prevent search engines to have everything multiplied
				if (!PortalDisplayUtil.isNotDesktopBrowser(portletRequest)){
					urlHome += themeDisplay.getI18nPath();
				}
			}
			return urlHome + urls.get(type);
		} catch (Exception e) {
			LOGGER.error("Unable to generate url: " + e.getMessage());
		}
		return null;

	}
	public static String getRelativeUrl(String type) {
			return urls.get(type);

	}
	public static String getEadPersistentUrl(PortletRequest portletRequest, EadPersistentUrl eadPerstistentUrl, boolean noHttps){
		String baseUrl = FriendlyUrlUtil.getUrl(portletRequest, FriendlyUrlUtil.EAD_DISPLAY_PERSISTENT, noHttps) ;
		return baseUrl + eadPerstistentUrl.toString() ;	
	}
	public static String getSitemapUrl(PortletRequest portletRequest, SitemapUrl sitemapUrl, boolean noHttps){
		String baseUrl = FriendlyUrlUtil.getUrl(portletRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP, noHttps) ;
		return baseUrl + sitemapUrl.toString() ;	
	}
	public static String getSitemapUrl(PortletRequest portletRequest, EadPersistentUrl eadPerstistentUrl, boolean noHttps){
		String baseUrl = FriendlyUrlUtil.getUrl(portletRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP, noHttps) ;
		return baseUrl + eadPerstistentUrl.toString() ;	
	}
	
}
