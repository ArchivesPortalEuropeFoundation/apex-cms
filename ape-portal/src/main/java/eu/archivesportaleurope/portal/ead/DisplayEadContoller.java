package eu.archivesportaleurope.portal.ead;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.types.XmlType;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Country;
import eu.apenet.persistence.vo.Ead;
import eu.archivesportaleurope.portal.common.AnalyzeLogger;

/**
 * 
 * This is display ead controller
 * 
 * @author bverhoef
 * 
 */
@Controller(value = "displayEadController")
@RequestMapping(value = "VIEW")
public class DisplayEadContoller {
	private final static Logger LOGGER = Logger.getLogger(DisplayEadContoller.class);
	private CLevelDAO clevelDAO;
	private EadDAO eadDAO;

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}

	public void setClevelDAO(CLevelDAO clevelDAO) {
		this.clevelDAO = clevelDAO;
	}

	@RenderMapping
	public ModelAndView displayEad(@ModelAttribute(value = "eadParams") EadParams eadParams) {
		ModelAndView modelAndView = new ModelAndView();
		try {
			String parameters = "";
			ArchivalInstitution archivalInstitution = null;
			Ead ead = null;
			if (StringUtils.isNotBlank(eadParams.getEadDisplayId())) {
				parameters+="id=" + eadParams.getEadDisplayId();
				AnalyzeLogger.logSecondDisplay(eadParams.getEadDisplayId());
				if (eadParams.getEadDisplayId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
					String subSolrId = eadParams.getEadDisplayId().substring(1);
					if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
						CLevel clevel = clevelDAO.findById(Long.parseLong(subSolrId));
						if (clevel != null) {
							ead = clevel.getEadContent().getEad();
						}
						// this is a search result
						modelAndView.getModelMap().addAttribute("solrId", eadParams.getEadDisplayId());
					}
				} else {
					String solrPrefix = eadParams.getEadDisplayId().substring(0, 1);
					XmlType xmlType = XmlType.getTypeBySolrPrefix(solrPrefix);
					String subId = eadParams.getEadDisplayId().substring(1);
					if (xmlType != null) {
						ead = eadDAO.findById(Integer.parseInt(subId), xmlType.getClazz());
					} else if (eadParams.getAiId() != null) {
						xmlType = XmlType.getType(eadParams.getXmlTypeId());
						if (StringUtils.isNotBlank(eadParams.getEadid())) {
							ead = eadDAO.getEadByEadid(xmlType.getClazz(), eadParams.getAiId(), eadParams.getEadid());
						}

					}
				}

			} else if (eadParams.getAiId() != null) {
				parameters+=" aiId=" +eadParams.getAiId();
				XmlType xmlType = XmlType.getType(eadParams.getXmlTypeId());
				if (StringUtils.isNotBlank(eadParams.getEadid())) {
					ead = eadDAO.getEadByEadid(xmlType.getClazz(), eadParams.getAiId(), eadParams.getEadid());
				}
			} else if (eadParams.getRepoCode() != null) {
				String repoCode = eadParams.getRepoCode().replace('_', '/');
				parameters+=" repoCode=" +repoCode;
				XmlType xmlType = XmlType.getTypeByResourceName(eadParams.getXmlTypeName());
				if (StringUtils.isNotBlank(eadParams.getEadid())) {
					ead = eadDAO.getEadByEadid(xmlType.getClazz(), repoCode, eadParams.getEadid());
				}
			}
			if (ead == null) {
				parameters+=" eadid=" +eadParams.getEadid();
				LOGGER.info("Could not found EAD in second display "+ parameters);
				modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
				modelAndView.setViewName("indexError");
				return modelAndView;
			} else {
				if (ead.isPublished()) {
					archivalInstitution = ead.getArchivalInstitution();
				} else {
					//LOGGER.info("Found not indexed EAD in second display");
					modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notindexed");
					modelAndView.setViewName("indexError");
					return modelAndView;
				}
				modelAndView.getModelMap().addAttribute("ead", ead);
				modelAndView.getModelMap().addAttribute("xmlTypeId", XmlType.getEadType(ead).getIdentifier());
				modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
				Country country = archivalInstitution.getCountry();
				modelAndView.getModelMap().addAttribute("country", country);
				modelAndView.setViewName("index");
				return modelAndView;
			}

		} catch (Exception e) {
			LOGGER.error("Error in second display process", e);
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("indexError");
			return modelAndView;
		}

	}

}
