package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;
import java.net.URLEncoder;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

public class EncodeUrlTag extends SimpleTagSupport {

	
	private String value;
	private String var;
	private String liferayFriendlyUrl;
	
	@Override
	public void doTag() throws JspException, IOException {
		if ("true".equalsIgnoreCase(liferayFriendlyUrl)){
			if (StringUtils.isNotBlank(value)){
				value = value.replaceAll("/", "_");
			}
			
		}
		getJspContext().setAttribute(var, URLEncoder.encode(value, "utf-8"));
		super.doTag();
	}

	public void setValue(String value) {
		this.value = value;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setLiferayFriendlyUrl(String liferayFriendlyUrl) {
		this.liferayFriendlyUrl = liferayFriendlyUrl;
	}
	
}
