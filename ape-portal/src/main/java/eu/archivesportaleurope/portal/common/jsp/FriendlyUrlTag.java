package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;

public class FriendlyUrlTag extends AbstractFriendlyUrlTag {

	
	private String type;
	private String var;

	@Override
	public void doTag() throws JspException, IOException {
		getJspContext().setAttribute(var, getUrl(type));
		super.doTag();
	}
	public void setType(String type) {
		this.type = type;
	}
	public void setVar(String var) {
		this.var = var;
	}
}
