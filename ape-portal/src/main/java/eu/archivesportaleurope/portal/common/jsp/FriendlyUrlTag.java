package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplay;

public class FriendlyUrlTag extends SimpleTagSupport {
	private final static Logger LOGGER = Logger.getLogger(FriendlyUrlTag.class);
	public static final String EAD_DISPLAY_SEARCH = "eaddisplay-search";
	public static final String EAD_DISPLAY_FRONTPAGE = "eaddisplay-frontpage";
	public static final String FEATURED_EXHIBITION = "featured-exhibition-details";
	public static final String FEATURED_EXHIBITION_ARTICLE = "featured-exhibition-details-article";
	public static final String DIRECTORY_COUNTRY = "directory-country";
	public static final String DIRECTORY_INSTITUTION_ID = "directory-institution-id";
	public static final String DIRECTORY_INSTITUTION_CODE = "directory-institution-code";
	public static final String DIRECTORY_CONTENT = "directory-content";
	
	
	private String type;
	private String var;
	private final static Map<String, String> urls = new HashMap<String,String>();
	static {
		urls.put(EAD_DISPLAY_SEARCH, "/ead-display/-/ead/s");
		urls.put(EAD_DISPLAY_FRONTPAGE, "/ead-display/-/ead/fp"); 	
		urls.put(FEATURED_EXHIBITION, "/featured-document/-/fed/pk"); 
		urls.put(FEATURED_EXHIBITION_ARTICLE, "/featured-document/-/fed/a");
		urls.put(DIRECTORY_COUNTRY, "/directory/-/dir/co");
		urls.put(DIRECTORY_INSTITUTION_ID, "/directory/-/dir/ai/id");
		urls.put(DIRECTORY_INSTITUTION_CODE, "/directory/-/dir/ai/code");
		urls.put(DIRECTORY_CONTENT, "/directory/-/dir/content");
		
	}
	@Override
	public void doTag() throws JspException, IOException {
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			String urlHome = themeDisplay.getURLHome();
			if (themeDisplay.isI18n()
					&& themeDisplay.getI18nPath() != null && !themeDisplay.getI18nPath().isEmpty()
					&& !urlHome.contains(themeDisplay.getI18nPath())) {
				// Try to change the URLHome from "http://www.archivesportaleurope.net/web/guest" to
				// "http://www.archivesportaleurope.net/lg/web/guest". (NOTE: <lg> is the selected
				// translation.
				int correctPosition = 3;
				int position = -1;
				int count = 0;
				for (int i = 0; i < urlHome.length(); i++) {
					String currentChar = urlHome.substring(i, (i + 1));
					if (currentChar.equalsIgnoreCase("/")) {
						position = i;
						count ++;
					}
					if (count == correctPosition) {
						break;
					}
				}

				if (position != -1) {
					String startString = urlHome.substring(0, position);
					String endString = urlHome.substring(position);
					urlHome = startString + themeDisplay.getI18nPath() + endString;
				}
			}

			getJspContext().setAttribute(var, urlHome +urls.get(type));
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve portletId and plId: " +e.getMessage(), e);
		}

		super.doTag();
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setVar(String var) {
		this.var = var;
	}
}
