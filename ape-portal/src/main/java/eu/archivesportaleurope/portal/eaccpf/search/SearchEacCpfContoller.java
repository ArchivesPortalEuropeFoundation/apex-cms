package eu.archivesportaleurope.portal.eaccpf.search;

import java.text.ParseException;

import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

/**
 * 
 * This is display eac cpf controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "searchEacCpfContoller")
@RequestMapping(value = "VIEW")
public class SearchEacCpfContoller {
	private final static Logger LOGGER = Logger.getLogger(SearchEacCpfContoller.class);
	private MessageSource messageSource;
	
	public void setMessageSource(MessageSource messageSource) {
		this.messageSource = messageSource;
	}
	@RenderMapping
	public ModelAndView searchEacCpf() {
		LOGGER.info("no");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		return modelAndView;
	}
	@RenderMapping(params = "myaction=eacCpfSearch")
	public ModelAndView search(@ModelAttribute(value = "eacCpfSearch") EacCpfSearch eacCpfSearch,RenderRequest request) throws SolrServerException, ParseException {
		LOGGER.info("yes");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("index");
		modelAndView.getModelMap().addAttribute("test", "yes");
		return modelAndView;
	}

	@ModelAttribute("eacCpfSearch")
	public EacCpfSearch getCommandObject() {
		return new EacCpfSearch();
	}
}
