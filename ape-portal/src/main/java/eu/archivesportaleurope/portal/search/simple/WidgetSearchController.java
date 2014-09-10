package eu.archivesportaleurope.portal.search.simple;

import javax.portlet.RenderRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

@Controller(value = "widgetSearchController")
@RequestMapping(value = "VIEW")
public class WidgetSearchController {

	@RenderMapping
	public ModelAndView showSimpleSearch(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		String savedSearchId = request.getParameter("savedSearchId");
		if (StringUtils.isNotBlank(savedSearchId) && StringUtils.isNumeric(savedSearchId)){
			modelAndView.getModel().put("savedSearchId", savedSearchId);
			modelAndView.setViewName("widget");
		}else {
			modelAndView.setViewName("widget-all");
		}
		

		return modelAndView;
	}

	@ModelAttribute("simpleSearch")
	public SimpleSearch getCommandObject() {
		return new SimpleSearch();
	}

}
