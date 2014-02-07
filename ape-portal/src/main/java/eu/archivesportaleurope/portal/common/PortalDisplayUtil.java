package eu.archivesportaleurope.portal.common;

import javax.portlet.PortletRequest;
import javax.servlet.http.HttpServletRequest;

import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.liferay.portal.util.PortalUtil;

import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.Country;
import eu.apenet.persistence.vo.Ead;

public class PortalDisplayUtil {
    private static final String END_CHARACTER = ")";
	private static final String START_CHARACTER = " (";
	private static final Logger LOGGER = Logger.getLogger(PortalDisplayUtil.class);
    public static final String TITLE_HOME = "HOME";
    public static final String TITLE_DIRECTORY = "DIRECTORY";
    public static final String TITLE_ADVANCED_SEARCH = "ADVANCED SEARCH";
    public static final String TITLE_ADVANCED_SEARCH_SAVED = "ADVANCED SEARCH (SAVED)";
    public static final String TITLE_SIMPLE_SEARCH = "ADVANCED SEARCH";
    public static final String TITLE_FEATURED_DOCUMENT = "FEATURED DOCUMENTS";
    public static final String TITLE_SAVED_SEARCH = "SAVED SEARCHES";
    public static void main(String args[]){
    	System.out.println(replaceQuotesAndReturns("taba\t \\%\\; algo   por \\p algo2 \n go \r con \r\n más \\ con /"));
    }
	public static String replaceQuotesAndReturns(String string) {
		String result = string;
		if (result != null) {
			result = result.replaceAll("\"", "'");
			result = result.replaceAll("[\n\t\r\\\\/%;]", "");
			result = result.trim();
		}
		return result;
	}
	public static ReadableUserAgent getUserAgent(PortletRequest portletRequest){
		HttpServletRequest request = PortalUtil.getHttpServletRequest(portletRequest);
		String header = request.getHeader("User-Agent");
		return CachedUserAgentStringParser.getInstance().parse(header);
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
	public static String getFeaturedExhibitionTitle(String title){
		if (StringUtils.isBlank(title)){
			return TITLE_FEATURED_DOCUMENT;
		}else {
			return PortalDisplayUtil.replaceQuotesAndReturns( title + START_CHARACTER + TITLE_FEATURED_DOCUMENT + END_CHARACTER);
		}
	}
	public static String getEadDisplayTitle(Ead ead, String title){
		return PortalDisplayUtil.replaceQuotesAndReturns( title + START_CHARACTER + ead.getArchivalInstitution().getRepositorycode() + " - " + ead.getEadid() + END_CHARACTER);
	}
	public static String getArchivalInstitutionDisplayTitle(ArchivalInstitution institution){
		if (institution.isGroup()){
			return PortalDisplayUtil.replaceQuotesAndReturns(institution.getAiname() + START_CHARACTER + institution.getCountry().getIsoname() + END_CHARACTER);
		}else {
			return PortalDisplayUtil.replaceQuotesAndReturns(institution.getAiname() + START_CHARACTER + institution.getRepositorycode() + END_CHARACTER);
		}
		
	}
	public static String getCountryDisplayTitle(Country country){
		return PortalDisplayUtil.replaceQuotesAndReturns(country.getCname() + START_CHARACTER + country.getIsoname() + END_CHARACTER);
	}
}
