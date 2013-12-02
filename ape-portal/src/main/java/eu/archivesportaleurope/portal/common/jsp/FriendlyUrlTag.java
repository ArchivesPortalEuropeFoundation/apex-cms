package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;

public class FriendlyUrlTag extends SimpleTagSupport {

	
	private String type;
	private String var;
	private String noHttps;

	@Override
	public void doTag() throws JspException, IOException {
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		boolean noHttpsBoolean = "true".equalsIgnoreCase(noHttps);
		getJspContext().setAttribute(var, FriendlyUrlUtil.getUrl(portletRequest, type,noHttpsBoolean));
		super.doTag();
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setVar(String var) {
		this.var = var;
	}
	public void setNoHttps(String noHttps) {
		this.noHttps = noHttps;
	}
	
}
