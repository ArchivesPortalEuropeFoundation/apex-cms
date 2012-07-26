package eu.archivesportaleurope.portal.search.advanced;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadContentDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.EadContent;

/**
 * 
 * This is preview ead controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "displayPreviewController")
@RequestMapping(value = "VIEW")
public class DisplayPreviewContoller {

	/**
	 * 
	 */
	private final static Logger LOG = Logger.getLogger(DisplayPreviewContoller.class);

	private EadContentDAO eadContentDAO;
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private CLevelDAO clevelDAO;

	@ResourceMapping(value = "displayPreview")
	public ModelAndView displayPreview(@RequestParam String id) {
		try {
			if (StringUtils.isNotBlank(id)) {
				if (id.startsWith(SolrValues.C_LEVEL_PREFIX)) {
					Long idLong = new Long(id.substring(1));
					if (id.startsWith(SolrValues.C_LEVEL_PREFIX))
						return fillCDetails(idLong);

				} else {
					String solrPrefix = id.substring(0, 1);
					XmlType xmlType = XmlType.getTypeBySolrPrefix(solrPrefix);
					if (xmlType != null) {
						Long idLong = new Long(id.substring(1));
						ModelAndView modelAndView = new ModelAndView();
						modelAndView.setViewName("preview/frontpage");
						modelAndView.getModelMap().addAttribute("xmlType", xmlType);
						EadContent eadContent = eadContentDAO.getEadContentByFileId(idLong.intValue(), xmlType.getClazz());
						modelAndView.getModelMap().addAttribute("eadContent", eadContent);
						modelAndView.getModelMap().addAttribute("id", id);
						return modelAndView;
					}else {
						return fillAIDetails(new Long(id));
					}
				}

			} 
		} catch (Exception e) {

		}

		return null;
	}

	private ModelAndView fillAIDetails(Long idLong) throws IOException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("preview/ai");
		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(idLong.intValue());
		String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
		modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
		return modelAndView;
	}

	private ModelAndView fillCDetails(Long idLong) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("preview/cdetails");
		CLevel currentCLevel = clevelDAO.findById(idLong);
		XmlType xmlType = XmlType.getEadType(currentCLevel.getEadContent().getEad());
		modelAndView.getModelMap().addAttribute("xmlType", xmlType);
		modelAndView.getModelMap().addAttribute("c", currentCLevel);
		return modelAndView;
	}

	public void setEadContentDAO(EadContentDAO eadContentDAO) {
		this.eadContentDAO = eadContentDAO;
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	public void setClevelDAO(CLevelDAO clevelDAO) {
		this.clevelDAO = clevelDAO;
	}

}
