package eu.archivesportaleurope.portal.common;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

import eu.archivesportaleurope.portal.common.urls.EacCpfPersistentUrl;
import eu.archivesportaleurope.portal.common.urls.EadPersistentUrl;
import eu.archivesportaleurope.portal.common.urls.SitemapUrl;

public final class FriendlyUrlUtil {
	private final static Logger LOGGER = Logger.getLogger(FriendlyUrlUtil.class);
	public static final String EAD_DISPLAY_PERSISTENT = "eaddisplay-persistent-archdesc";	
	public static final String EAD_DISPLAY_PERSISTENT_PAGING = "eaddisplay-persistent-paging";		
	public static final String FEATURED_EXHIBITION = "featured-exhibition-details";
	public static final String FEATURED_EXHIBITION_ARTICLE = "featured-exhibition-details-article";
	public static final String DIRECTORY_COUNTRY = "directory-country";
	public static final String DIRECTORY_INSTITUTION_ID = "directory-institution-id";
	public static final String DIRECTORY_INSTITUTION_CODE = "directory-institution-code";
	public static final String DIRECTORY_CONTENT = "directory-content";
	public static final String DIRECTORY_SITEMAP = "directory-sitemap";
	public static final String SEARCH = "ead-search";
	public static final String WIDGET_EAD_SEARCH = "widget-ead-search";
	public static final String WIDGET_SAVED_SEARCH = "widget-saved-search";
	public static final String SAVED_SEARCH = "saved-search";
	public static final String SAVED_SEARCH_OVERVIEW = "saved-search-overview";
	public static final String SAVED_BOOKMARKS = "saved-bookmarks";
	public static final String SAVED_BOOKMARKS_OVERVIEW = "saved-bookmarks-overview";
	public static final String SAVED_COLLECTION_OVERVIEW = "saved-collection-overview";
	public static final String SEPARATOR = "/";
	public static final String EAC_CPF_DISPLAY = "eac-display";
	

	
	private final static Map<String, String> urls = new HashMap<String, String>();
	static {
		urls.put(EAD_DISPLAY_PERSISTENT, "/ead-display/-/ead/pl");
		urls.put(FEATURED_EXHIBITION, "/featured-document/-/fed/pk");
		urls.put(FEATURED_EXHIBITION_ARTICLE, "/featured-document/-/fed/a");
		urls.put(DIRECTORY_COUNTRY, "/directory/-/dir/co");
		urls.put(DIRECTORY_INSTITUTION_ID, "/directory/-/dir/ai/id");
		urls.put(DIRECTORY_INSTITUTION_CODE, "/directory/-/dir/ai/code");
		urls.put(DIRECTORY_CONTENT, "/directory/-/dir/content");
		urls.put(DIRECTORY_SITEMAP, "/directory/-/dir/sitemap");
		urls.put(SEARCH, "/search");
		urls.put(WIDGET_EAD_SEARCH, "/search/-/s/widget/n");
		urls.put(WIDGET_SAVED_SEARCH, "/search/-/s/widget/d");		
		urls.put(SAVED_SEARCH, "/search/-/s/d");
		urls.put(SAVED_SEARCH_OVERVIEW, "/saved-searches/-/sv");
		urls.put(SAVED_BOOKMARKS, "/bookmarks/-/s/d");
		urls.put(SAVED_BOOKMARKS_OVERVIEW, "/savedbookmarks/-/sb");
		urls.put(EAC_CPF_DISPLAY, "/eac-display/-/eac/pl");
		urls.put(SAVED_COLLECTION_OVERVIEW, "/saved-collections/-/cs");
	}

	public static String getUrl(PortletRequest portletRequest, String type) {
		return getUrl(portletRequest, type, false);
	}
	public static String getUrl(PortletRequest portletRequest, String type, boolean noHttps) {
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			String urlHome = themeDisplay.getURLHome();
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
	public static String getUrlWithoutLocalization(PortletRequest portletRequest, String type, boolean noHttps) {
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			String urlHome = themeDisplay.getPortalURL();
			if (noHttps){
				urlHome = urlHome.replaceFirst("https://", "http://");
			}
			return urlHome + urls.get(type);
		} catch (Exception e) {
			LOGGER.error("Unable to generate url: " + e.getMessage());
		}
		return null;

	}
	public static String getRelativeUrl(PortletRequest portletRequest, String type) {
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			String urlHome = "";
			if (themeDisplay.isI18n() && StringUtils.isNotBlank(themeDisplay.getI18nPath())) {
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
	public static String getRelativeEadPersistentUrl(PortletRequest portletRequest, EadPersistentUrl eadPerstistentUrl){
		String baseUrl = FriendlyUrlUtil.getRelativeUrl(portletRequest, FriendlyUrlUtil.EAD_DISPLAY_PERSISTENT) ;
		return baseUrl + eadPerstistentUrl.toString() ;	
	}
	public static String getEacCpfPersistentUrl(PortletRequest portletRequest, EacCpfPersistentUrl eacCpfPerstistentUrl, boolean noHttps){
		String baseUrl = FriendlyUrlUtil.getUrl(portletRequest, FriendlyUrlUtil.EAC_CPF_DISPLAY, noHttps) ;
		return baseUrl + eacCpfPerstistentUrl.toString() ;	
	}
	public static String getEadPersistentUrl(PortletRequest portletRequest, EadPersistentUrl eadPerstistentUrl, boolean noHttps){
		String baseUrl = FriendlyUrlUtil.getUrl(portletRequest, FriendlyUrlUtil.EAD_DISPLAY_PERSISTENT, noHttps) ;
		return baseUrl + eadPerstistentUrl.toString() ;	
	}
	public static String getEadPersistentUrlForSitemap(PortletRequest portletRequest, EadPersistentUrl eadPerstistentUrl){
		String baseUrl = FriendlyUrlUtil.getUrlWithoutLocalization(portletRequest, FriendlyUrlUtil.EAD_DISPLAY_PERSISTENT, true) ;
		return baseUrl + eadPerstistentUrl.toString() ;	
	}
	public static String getSitemapUrl(PortletRequest portletRequest, SitemapUrl sitemapUrl){
		String baseUrl = FriendlyUrlUtil.getUrlWithoutLocalization(portletRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP, true) ;
		return baseUrl + sitemapUrl.toString() ;	
	}
	public static String getSitemapUrl(PortletRequest portletRequest, EadPersistentUrl eadPerstistentUrl){
		String baseUrl = FriendlyUrlUtil.getUrlWithoutLocalization(portletRequest, FriendlyUrlUtil.DIRECTORY_SITEMAP, true) ;
		return baseUrl + eadPerstistentUrl.toString() ;	
	}
	
}
