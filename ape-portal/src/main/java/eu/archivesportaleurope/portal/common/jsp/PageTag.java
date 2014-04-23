package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;
import java.util.List;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.model.LayoutTypePortlet;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

import eu.apenet.commons.utils.Cache;
import eu.archivesportaleurope.util.ApeUtil;

public class PageTag extends SimpleTagSupport {
	private static final String SEPARATOR = ":";
	private static final String _WAR = "_WAR";
	private final static Logger LOGGER = Logger.getLogger(PageTag.class);
	private String portletName;
	private String friendlyUrl;
	private String varPlId;
	private String varPortletId; 
	private final static Cache<String, String> CACHE = new Cache<String, String>();
	@Override
	public void doTag() throws JspException, IOException {
		try {
			String key = friendlyUrl + SEPARATOR + portletName;
			String result = CACHE.get(key);
			if (result == null) {
				result = retrievePortletName(portletName, friendlyUrl);
				CACHE.put(key, result);
			}
			String[] splitted = result.split(SEPARATOR);
			getJspContext().setAttribute(varPlId, splitted[0]);
			getJspContext().setAttribute(varPortletId, splitted[1]);
			
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve portletId and plId:  ("+ friendlyUrl + " " + portletName+")" +ApeUtil.generateThrowableLog(e));
		}

		super.doTag();
	}

	public String retrievePortletName(String portletName, String friendlyUr){
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		long groupId = themeDisplay.getLayout().getGroupId();
		Layout otherPage;
		try {
			LOGGER.info(friendlyUrl + " " + portletName);
			otherPage = LayoutLocalServiceUtil.getFriendlyURLLayout(groupId, false, friendlyUrl);
			String result = otherPage.getPlid() + SEPARATOR;
			String newPortletId = portletName + _WAR;
			LayoutTypePortlet layoutTypePortlet = (LayoutTypePortlet)otherPage.getLayoutType();
			List<String> actualPortletList = layoutTypePortlet.getPortletIds();
			boolean found = false;
			for (int i=0; !found && i < actualPortletList.size();i++){
				String portletId = actualPortletList.get(i);
				if (portletId.startsWith(newPortletId)){
					found = true;
					result += portletId;
					
				}
			}
			return result;
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve portletId and plId: ("+ friendlyUrl + " " + portletName+")" +ApeUtil.generateThrowableLog(e));
		}
		return null;
	}
	public String getPortletName() {
		return portletName;
	}
	public void setPortletName(String portletName) {
		this.portletName = portletName;
	}
	public String getFriendlyUrl() {
		return friendlyUrl;
	}
	public void setFriendlyUrl(String friendlyUrl) {
		this.friendlyUrl = friendlyUrl;
	}
	public String getVarPlId() {
		return varPlId;
	}
	public void setVarPlId(String varPlId) {
		this.varPlId = varPlId;
	}
	public String getVarPortletId() {
		return varPortletId;
	}
	public void setVarPortletId(String varPortletId) {
		this.varPortletId = varPortletId;
	}


}
