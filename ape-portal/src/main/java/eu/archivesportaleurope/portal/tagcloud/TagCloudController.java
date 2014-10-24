package eu.archivesportaleurope.portal.tagcloud;

import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.archivesportaleurope.portal.search.ead.EadSearcher;

@Controller(value = "tagCloudController")
@RequestMapping(value = "VIEW")
public class TagCloudController {

	private final static Logger LOGGER = Logger.getLogger(TagCloudController.class);


	private ResourceBundleMessageSource messageSource;
	private EadSearcher eadSearcher;

	

	public void setEadSearcher(EadSearcher eadSearcher) {
		this.eadSearcher = eadSearcher;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	// --maps the incoming portlet request to this method
	@RenderMapping
	public String showTagCloud(RenderRequest request) {
		return "index";
	}

}
