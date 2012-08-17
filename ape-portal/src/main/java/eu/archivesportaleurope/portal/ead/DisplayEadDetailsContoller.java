package eu.archivesportaleurope.portal.ead;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.xslt.tags.AbstractEadTag;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadContentDAO;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.EadContent;

/**
 * 
 * This is display ead controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "displayEadDetailsController")
@RequestMapping(value = "VIEW")
public class DisplayEadDetailsContoller{
	private static final int PAGE_SIZE = 10;
	private final static Logger LOGGER = Logger.getLogger(DisplayEadDetailsContoller.class);
	private CLevelDAO clevelDAO;
	private EadContentDAO eadContentDAO;	


	public void setEadContentDAO(EadContentDAO eadContentDAO) {
		this.eadContentDAO = eadContentDAO;
	}

	public void setClevelDAO(CLevelDAO clevelDAO) {
		this.clevelDAO = clevelDAO;
	}
	
	@ResourceMapping(value = "displayEadDetails")
	public ModelAndView displayDetails(@ModelAttribute(value = "eadDetailsParams") EadDetailsParams eadDetailsParams){
		if (StringUtils.isNotBlank(eadDetailsParams.getId())){
			return displayCDetails(eadDetailsParams);
		}else {
			return displayEadDetails(eadDetailsParams);
		}

	}
	@RenderMapping(params = "myaction=printEadDetails")
	public ModelAndView printDetails(@ModelAttribute(value = "eadDetailsParams") EadDetailsParams eadDetailsParams){
		ModelAndView modelAndView = null;
		if (StringUtils.isNotBlank(eadDetailsParams.getId())){
			modelAndView = displayCDetails(eadDetailsParams);
		}else {
			modelAndView = displayEadDetails(eadDetailsParams);
		}
		modelAndView.setViewName("printEaddetails");
		return modelAndView;

	}
	private ModelAndView displayCDetails(EadDetailsParams eadDetailsParams){
		ModelAndView modelAndView = new ModelAndView();
		Long id = null;
		if (eadDetailsParams.getId().startsWith(SolrValues.C_LEVEL_PREFIX)){
			id = Long.parseLong(eadDetailsParams.getId().substring(1));
		}else {
			id = Long.parseLong(eadDetailsParams.getId());
		}

		CLevel currentCLevel = clevelDAO.findById(id);
		Integer pageNumberInt = 1;
		if (eadDetailsParams.getPageNumber() != null){
			pageNumberInt = eadDetailsParams.getPageNumber();
		}
		int orderId = (pageNumberInt -1) *PAGE_SIZE;
		List<CLevel> children = clevelDAO.findChildCLevels(currentCLevel.getClId(), orderId, PAGE_SIZE);
		Long totalNumberOfChildren = clevelDAO.countChildCLevels(id);
		StringBuilder builder = new StringBuilder();
		builder.append("<c xmlns=\"urn:isbn:1-931666-22-9\">");
		for (CLevel child: children){
			builder.append(child.getXml());
		}
		builder.append("</c>");
		modelAndView.getModelMap().addAttribute("country", currentCLevel.getEadContent().getEad().getArchivalInstitution().getCountry());
		modelAndView.getModelMap().addAttribute("c", currentCLevel);
		modelAndView.getModelMap().addAttribute("totalNumberOfChildren", totalNumberOfChildren);
		modelAndView.getModelMap().addAttribute("pageNumber", pageNumberInt);
		modelAndView.getModelMap().addAttribute("pageSize", PAGE_SIZE);
		modelAndView.getModelMap().addAttribute("childXml", builder.toString());
		String documentTitle = currentCLevel.getUnittitle();
		if (StringUtils.isNotBlank(currentCLevel.getUnitid())){
			documentTitle = currentCLevel.getUnitid() + " " + documentTitle;
		}
		modelAndView.getModelMap().addAttribute("documentTitle",documentTitle);
		modelAndView.setViewName("eaddetails");
		return modelAndView;		
	}
	private ModelAndView displayEadDetails(EadDetailsParams eadDetailsParams){
		ModelAndView modelAndView = new ModelAndView();
		if (eadDetailsParams.getEcId() != null){
	        EadContent eadContent = eadContentDAO.findById(eadDetailsParams.getEcId());
	        modelAndView.getModelMap().addAttribute("country", eadContent.getEad().getArchivalInstitution().getCountry());
	        if (AbstractEadTag.INTRODUCTION_XSLT.equals(eadDetailsParams.getType()) || AbstractEadTag.DIDCONTENT_XSLT.equals(eadDetailsParams.getType())){
	        	modelAndView.getModelMap().addAttribute("type", eadDetailsParams.getType());        	
	        }else {
	        	modelAndView.getModelMap().addAttribute("type", AbstractEadTag.FRONTPAGE_XSLT);
	        }
        	if (AbstractEadTag.DIDCONTENT_XSLT.equals(eadDetailsParams.getType())){
        		modelAndView.getModelMap().addAttribute("documentTitle", eadContent.getUnittitle());
        	}else {
        		modelAndView.getModelMap().addAttribute("documentTitle", eadContent.getTitleproper());
        	}
	        modelAndView.getModelMap().addAttribute("eadContent", eadContent);
	        modelAndView.setViewName("eaddetails");
		}
		else {
			modelAndView.setViewName("eadDetailsError");
		}
		return modelAndView;
	}


}
