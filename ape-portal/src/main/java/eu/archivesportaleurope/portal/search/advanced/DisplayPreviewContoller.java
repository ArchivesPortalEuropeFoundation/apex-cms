package eu.archivesportaleurope.portal.search.advanced;

import java.io.IOException;

import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.AnalyzeLogger;
import eu.archivesportaleurope.portal.common.NotExistInDatabaseException;

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
	private final static Logger LOGGER = Logger.getLogger(DisplayPreviewContoller.class);

	private EadContentDAO eadContentDAO;
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private CLevelDAO clevelDAO;

	@ResourceMapping(value = "displayPreview")
	public ModelAndView displayPreview(ResourceRequest resourceRequest) {
		String id = resourceRequest.getParameter("id");
		try {
			if (StringUtils.isNotBlank(id)) {
				AnalyzeLogger.logPreview(id);
				if (id.startsWith(SolrValues.C_LEVEL_PREFIX)) {
					Long idLong = new Long(id.substring(1));
					if (id.startsWith(SolrValues.C_LEVEL_PREFIX))
						return fillCDetails(idLong);

				} else {
					String solrPrefix = id.substring(0, 1);
					XmlType xmlType = XmlType.getTypeBySolrPrefix(solrPrefix);
					if (xmlType != null) {
						Long idLong = Long.parseLong(id.substring(1));
						ModelAndView modelAndView = new ModelAndView();
						modelAndView.setViewName("preview/frontpage");
						modelAndView.getModelMap().addAttribute("xmlType", xmlType);
						EadContent eadContent = eadContentDAO.getEadContentByFileId(idLong.intValue(), xmlType.getEadClazz());
						if (eadContent == null) {
							throw new NotExistInDatabaseException(id);
						}
						modelAndView.getModelMap().addAttribute("eadContent", eadContent);
						Ead ead = eadContent.getEad();
						ArchivalInstitution archivalInstitution = ead.getArchivalInstitution();
						modelAndView.getModelMap().addAttribute("aiRepoCode", archivalInstitution.getRepositorycodeForUrl());
						modelAndView.getModelMap().addAttribute("eadid", ead.getEadid());
						modelAndView.getModelMap().addAttribute("xmlTypeName", xmlType.getResourceName());
						return modelAndView;
					}else {
						return fillAIDetails(Long.parseLong(id));
					}
				}

			}
		}catch (NotExistInDatabaseException e){
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
			modelAndView.setViewName("preview/indexError");
			return modelAndView;
		}catch (Exception e) {
			LOGGER.error(e.getMessage(),e);
		}

		return null;
	}

	private ModelAndView fillAIDetails(Long idLong) throws IOException, NotExistInDatabaseException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("preview/ai");
		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(idLong.intValue());
		if (archivalInstitution == null) {
			throw new NotExistInDatabaseException();
		}
		String eagPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + archivalInstitution.getEagPath();
		modelAndView.getModelMap().addAttribute("eagUrl", eagPath);
		return modelAndView;
	}

	private ModelAndView fillCDetails(Long idLong) throws NotExistInDatabaseException {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("preview/cdetails");
		CLevel currentCLevel = clevelDAO.findById(idLong);
		if (currentCLevel == null) {
			throw new NotExistInDatabaseException();
		}
		Ead ead = currentCLevel.getEadContent().getEad();
		ArchivalInstitution archivalInstitution = ead.getArchivalInstitution();
		XmlType xmlType = XmlType.getContentType(ead);
		modelAndView.getModelMap().addAttribute("xmlType", xmlType);
		modelAndView.getModelMap().addAttribute("c", currentCLevel);
		modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
		String repoCode = archivalInstitution.getRepositorycode().replace('/', '_');
		modelAndView.getModelMap().addAttribute("aiRepoCode", repoCode);
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
