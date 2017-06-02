package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.apenet.commons.view.jsp.HrefObject;

public class RemoveParametersTag extends BodyTagSupport {
	  /**
	 * 
	 */
	private static final long serialVersionUID = -6095427587325955129L;
	private static final Logger LOGGER = Logger.getLogger(RemoveParametersTag.class);
	private String var;
	private String parameters;
	private String namespace;

	@Override
	public int doEndTag() throws JspException {
		String url = "";
	    if (bodyContent != null && bodyContent.getString() != null)
	    	url = bodyContent.getString().trim();
		try {
			HrefObject hrefObject = new HrefObject(url);
			for (String parameter : getParametersArray()) {
				hrefObject.removeParameter(namespace+parameter);
			}
			if (StringUtils.isBlank(var)){
				pageContext.getOut().print(hrefObject.toString());
			}else {
				pageContext.setAttribute(var, hrefObject.toString());
			}	
		} catch (IOException e) {
			LOGGER.error(e.getMessage(),e);
		}

	    return EVAL_PAGE;
	}
	@Override
	public int doStartTag() throws JspException {
		return super.doStartTag();
	}
	public String[] getParametersArray(){
		if (StringUtils.isNotBlank(parameters)){
			return parameters.split(",");
		}else {
			return new String[] {};
		}
	}
	public void setVar(String var) {
		this.var = var;
	}
	public void setParameters(String parameters) {
		this.parameters = parameters;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	
}
