package eu.archivesportaleurope.portal.ead;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.portlet.RenderRequest;

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
import eu.apenet.persistence.dao.CouAlternativeNameDAO;
import eu.apenet.persistence.dao.EadContentDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Country;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.FileState;

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
	public ModelAndView displayEad(@ModelAttribute(value = "eadParams") EadParams eadParams, RenderRequest request) {
		try {
			ArchivalInstitution archivalInstitution = null;
			Ead ead = null;
			if (StringUtils.isNotBlank(eadParams.getId()) && eadParams.getId().startsWith(SolrValues.C_LEVEL_PREFIX)) {
				String subSolrId = eadParams.getId().substring(1);
				if (StringUtils.isNotBlank(subSolrId) && StringUtils.isNumeric(subSolrId)) {
					CLevel clevel = clevelDAO.findById(Long.parseLong(subSolrId));
					if (clevel != null) {
						ead = clevel.getEadContent().getEad();
					}
				}
			} else {
				XmlType xmlType = XmlType.getType(eadParams.getXmlTypeId());
				if (eadParams.getAiId() != null) {
					if (StringUtils.isNotBlank(eadParams.getEadid())) {
						ead = eadDAO
								.getEadByEadid(xmlType.getClazz(), eadParams.getAiId(), eadParams.getEadid());
					}

				} else if (StringUtils.isNotBlank(eadParams.getId()) && StringUtils.isNumeric(eadParams.getId())) {
					ead = eadDAO.findById(Integer.parseInt(eadParams.getId()), xmlType.getClazz());
				}

			}
			if (ead == null) {
				LOGGER.error("Could not found EAD in second display");
				request.setAttribute("errorMessage", "error.user.second.display.notexist");
				return new ModelAndView("indexError");
			} else {
				List<String> indexedStates = Arrays.asList(FileState.INDEXED_FILE_STATES);
				if (indexedStates.contains(ead.getFileState().getState())) {
					archivalInstitution = ead.getArchivalInstitution();
				} else {
					LOGGER.error("Found not indexed EAD in second display");
					request.setAttribute("errorMessage", "error.user.second.display.notindexed");
					return new ModelAndView("indexError");
				}
				request.setAttribute("ead", ead);
				request.setAttribute("xmlTypeId", XmlType.getEadType(ead).getIdentifier());
				request.setAttribute("archivalInstitution", archivalInstitution);
				Country country = archivalInstitution.getCountry();
				request.setAttribute("country", country);
			}
			return new ModelAndView("index");
		} catch (Exception e) {
			LOGGER.error("Error in second display process", e);
			request.setAttribute("errorMessage", "error.user.second.display.notexist");
			return new ModelAndView("indexError");
		}

	}

}
