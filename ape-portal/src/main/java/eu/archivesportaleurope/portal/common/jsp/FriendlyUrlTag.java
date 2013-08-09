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
	private String type;
	private String var;
	private final static Map<String, String> urls = new HashMap<String,String>();
	static {
		urls.put(EAD_DISPLAY_SEARCH, "/ead-display/-/ead/s");
		urls.put(EAD_DISPLAY_FRONTPAGE, "/ead-display/-/ead/fp"); 	
		urls.put(FEATURED_EXHIBITION, "/featured-exhibition/-/fed/pk/"); 
	}
	@Override
	public void doTag() throws JspException, IOException {
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		try {
			getJspContext().setAttribute(var, themeDisplay.getURLHome()+urls.get(type));
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
