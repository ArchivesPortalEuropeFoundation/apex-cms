package eu.archivesportaleurope.portal.ead;

import java.util.List;

import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.xslt.tags.AbstractEadTag;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadContentDAO;
import eu.apenet.persistence.factory.DAOFactory;
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
	public ModelAndView displayDetails(@ModelAttribute(value = "eadDetailsParams") EadDetailsParams eadDetailsParams, ResourceRequest request){
		if (StringUtils.isNotBlank(eadDetailsParams.getId())){
			return displayCDetails(eadDetailsParams, request);
		}else {
			return displayEadDetails(eadDetailsParams, request);
		}

	}

	private ModelAndView displayCDetails(EadDetailsParams eadDetailsParams, ResourceRequest request){
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
		request.setAttribute("c", currentCLevel);
		request.setAttribute("totalNumberOfChildren", totalNumberOfChildren);
		request.setAttribute("pageNumber", pageNumberInt);
		request.setAttribute("pageSize", PAGE_SIZE);
		request.setAttribute("childXml", builder.toString());
		return new ModelAndView("cdetails");		
	}
	private ModelAndView displayEadDetails(EadDetailsParams eadDetailsParams, ResourceRequest request){
		if (eadDetailsParams.getEcId() != null){
	        EadContent eadContent = eadContentDAO.findById(eadDetailsParams.getEcId());
	        if (AbstractEadTag.INTRODUCTION_XSLT.equals(eadDetailsParams.getType()) || AbstractEadTag.DIDCONTENT_XSLT.equals(eadDetailsParams.getType())){
	        	request.setAttribute("type", eadDetailsParams.getType());
	        }else {
	        	request.setAttribute("type", AbstractEadTag.FRONTPAGE_XSLT);
	        }
	        request.setAttribute("eadContent", eadContent);
	        return new ModelAndView("eaddetails");
		}
		return new ModelAndView("eadDetailsError");
	}


}
