package eu.archivesportaleurope.portal.search.simple;

import java.util.regex.Pattern;

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
	private final static Pattern COLOR_PATTERN = Pattern.compile("[A-Fa-f0-9]{6}|[A-Fa-f0-9]{3}");
	@RenderMapping
	public ModelAndView showSimpleSearch(RenderRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		String savedSearchId = request.getParameter("savedSearchId");
		updateColors(modelAndView, request);
		if (StringUtils.isNotBlank(savedSearchId) && StringUtils.isNumeric(savedSearchId)) {
			modelAndView.getModel().put("savedSearchId", savedSearchId);
			modelAndView.setViewName("widget");
		} else {
			modelAndView.setViewName("widget-all");
		}

		return modelAndView;
	}
	
	private void updateColors(ModelAndView modelAndView, RenderRequest request){
		String backgroundColor = request.getParameter("backgroundColor");
		String buttonBackgroundColor = request.getParameter("buttonBackgroundColor");
		String buttonBorderColor = request.getParameter("buttonBorderColor");
		String buttonTextColor = request.getParameter("buttonTextColor");
		modelAndView.getModel().put("backgroundColor", getCss("background-color", backgroundColor));
		String buttonStyle = "";
		buttonStyle += getCss("background-color", buttonBackgroundColor);
		buttonStyle += getCss("color", buttonTextColor);	
		buttonStyle += getCss("border-color", buttonBorderColor);	
		modelAndView.getModel().put("buttonStyle", buttonStyle);
	}
	private String getCss(String name, String value){
		if (StringUtils.isNotBlank(value) && COLOR_PATTERN.matcher(value).matches()){
			return name+ ": #" +value +";";
		}else {
			return "";
		}		
	}

	@ModelAttribute("simpleSearch")
	public SimpleSearch getCommandObject() {
		return new SimpleSearch();
	}

}
