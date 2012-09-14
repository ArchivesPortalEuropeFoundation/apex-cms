package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Layout;
import com.liferay.portal.service.LayoutLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;

public class PageTag extends SimpleTagSupport {
	private static final String _WAR = "_WAR";
	private final static Logger LOGGER = Logger.getLogger(PageTag.class);
	private String portletName;
	private String friendlyUrl;
	private String varPlId;
	private String varPortletId; 
	@Override
	public void doTag() throws JspException, IOException {
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		ThemeDisplay themeDisplay = (ThemeDisplay) portletRequest.getAttribute(WebKeys.THEME_DISPLAY);
		String portletId = (String) portletRequest.getAttribute(WebKeys.PORTLET_ID);
		int index = portletId.indexOf(_WAR);
		String newPortletId = portletName + portletId.substring(index);
		getJspContext().setAttribute(varPortletId, newPortletId);
		long groupId = themeDisplay.getLayout().getGroupId();
		Layout otherPage;
		try {
			otherPage = LayoutLocalServiceUtil.getFriendlyURLLayout(groupId, false, friendlyUrl);
			getJspContext().setAttribute(varPlId, otherPage.getPlid());
		} catch (Exception e) {
			LOGGER.error("Unable to retrieve portletId and plId: " +e.getMessage(), e);
		}

		super.doTag();
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
