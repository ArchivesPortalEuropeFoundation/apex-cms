package eu.archivesportaleurope.portal.common;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentType;

import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

public class PortalDisplayUtil {
    private static final Logger LOGGER = Logger.getLogger(PortalDisplayUtil.class);

	public static String replaceQuotesAndReturns(String string) {
		String result = string;
		if (result != null) {
			result = result.replaceAll("\"", "'");
			result = result.replaceAll("[\n\t\r]", "");
			result = result.trim();
		}
		return result;
	}
	public static boolean useNoJavascriptPages(PortletRequest portletRequest){
		HttpServletRequest request = PortalUtil. getHttpServletRequest(portletRequest);
		String header = request.getHeader("User-Agent");
		ReadableUserAgent agent = CachedUserAgentStringParser.getInstance().parse(header);
		LOGGER.info(agent.getType());
		return !UserAgentType.BROWSER.equals(agent.getType());
	}
}
