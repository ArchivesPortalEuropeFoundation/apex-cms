package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;
import org.apache.taglibs.standard.tag.common.fmt.MessageSupport;

/**
 * Abstract Portlet Tag with localization support.
 * 
 * @author bastiaan
 * 
 */
public abstract class AbstractPortletTag extends SimpleTagSupport implements DynamicAttributes {
	private Logger logger = Logger.getLogger(this.getClass());
	private Map<String, Object> tagAttributes = new HashMap<String, Object>();
	private ResourceBundle bundle;

	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		tagAttributes.put(localName, value);
	}

	public final void doTag() throws JspException, IOException {
		LocalizationContext locCtxt = BundleSupport.getLocalizationContext((PageContext) this.getJspContext());
		if (locCtxt != null) {
			bundle = locCtxt.getResourceBundle();
		} else {
			logger.error("Unable to find the localizationContext");
		}
		doTagInternal();
	}

	protected abstract void doTagInternal() throws JspException, IOException;

	/**
	 * Retrieve resource message on a standardized way.
	 * 
	 * @param key
	 * @return
	 */
	protected final String getValueFromResourceBundle(String key) {
		String message = MessageSupport.UNDEFINED_KEY + key + MessageSupport.UNDEFINED_KEY;
		if (bundle != null) {
			try {
				message = bundle.getString(key);
			} catch (MissingResourceException mre) {

			}
		}

		return message;

	}

	public ResourceBundle getBundle() {
		return bundle;
	}

}
