package eu.archivesportaleurope.portal.common.jsp;

import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

import eu.apenet.commons.DefaultResourceBundleSource;
import eu.apenet.commons.ResourceBundleSource;
import eu.apenet.commons.xslt.tags.AbstractEagTag;

public class EagTag extends AbstractEagTag {
	private Logger logger = Logger.getLogger(this.getClass());
	@Override
	protected ResourceBundleSource getResourceBundleSource() {
		LocalizationContext locCtxt = BundleSupport.getLocalizationContext((PageContext) this.getJspContext());
		if (locCtxt != null) {
			return new DefaultResourceBundleSource(locCtxt.getResourceBundle());
		} else {
			logger.error("Unable to find the localizationContext");
			return new DefaultResourceBundleSource(null);
		}
	}

}
