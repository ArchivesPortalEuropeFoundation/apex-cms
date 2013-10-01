package eu.archivesportaleurope.portal.common;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import eu.apenet.persistence.vo.Ead;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentType;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

public class PortalDisplayUtil {
    private static final Logger LOGGER = Logger.getLogger(PortalDisplayUtil.class);
    public static final String TITLE_HOME = "HOME";
    public static final String TITLE_DIRECTORY = "DIRECTORY";
    public static final String TITLE_ADVANCED_SEARCH = "ADVANCED SEARCH";
    public static final String TITLE_SIMPLE_SEARCH = "SIMPLE SEARCH";
	public static String replaceQuotesAndReturns(String string) {
		String result = string;
		if (result != null) {
			result = result.replaceAll("\"", "'");
			result = result.replaceAll("[\n\t\r]", "");
			result = result.trim();
		}
		return result;
	}
	public static boolean isNotDesktopBrowser(PortletRequest portletRequest){
		HttpServletRequest request = PortalUtil.getHttpServletRequest(portletRequest);
		String header = request.getHeader("User-Agent");
		ReadableUserAgent agent = CachedUserAgentStringParser.getInstance().parse(header);
		return !UserAgentType.BROWSER.equals(agent.getType());
	}
	public static boolean isNotNormalBrowser(PortletRequest portletRequest){
		HttpServletRequest request = PortalUtil.getHttpServletRequest(portletRequest);
		String header = request.getHeader("User-Agent");
		ReadableUserAgent agent = CachedUserAgentStringParser.getInstance().parse(header);
		return !(UserAgentType.BROWSER.equals(agent.getType()) || UserAgentType.MOBILE_BROWSER.equals(agent.getType()));
	}
	public static void setPageTitle(PortletRequest portletRequest, String title){
		String documentTitle = PortalDisplayUtil.replaceQuotesAndReturns(title);
		HttpServletRequest request = PortalUtil. getHttpServletRequest(portletRequest);
		PortalUtil.setPageTitle(documentTitle, request);
	}
    public static String getEadDisplayTitle(Ead ead, String string) {
        return string;
    }
}
