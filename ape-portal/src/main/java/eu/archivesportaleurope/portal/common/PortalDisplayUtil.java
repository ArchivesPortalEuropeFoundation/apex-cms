package eu.archivesportaleurope.portal.common;

public class PortalDisplayUtil {
	public static String replaceQuotesAndReturns(String string) {
		String result = string;
		if (result != null) {
			result = result.replaceAll("\"", "'");
			result = result.replaceAll("[\n\t\r]", "");
			result = result.trim();
		}
		return result;
	}
}
