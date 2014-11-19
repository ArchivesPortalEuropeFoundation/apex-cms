package eu.archivesportaleurope.portal.directory;

import javax.portlet.RenderRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.RenderMapping;

import eu.apenet.commons.exceptions.APEnetException;
import eu.apenet.commons.types.XmlType;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.ContentSearchOptions;
import eu.apenet.persistence.dao.EacCpfDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.EacCpf;
import eu.archivesportaleurope.portal.common.PortalDisplayUtil;
import eu.archivesportaleurope.util.ApeUtil;

@Controller(value = "aiContentController")
@RequestMapping(value = "VIEW")
public class AiContentController {
	private final static Logger LOGGER = Logger.getLogger(AiContentController.class);
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	private EacCpfDAO eacCpfDAO;


	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}


	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}


	public void setEacCpfDAO(EacCpfDAO eacCpfDAO) {
		this.eacCpfDAO = eacCpfDAO;
	}


	@RenderMapping(params = "myaction=showAiContent")
	public ModelAndView showCountryDetails(@ModelAttribute(value = "aiContentParams") AiContentParams aiContentParams,
			RenderRequest renderRequest) throws APEnetException {
		ModelAndView modelAndView = new ModelAndView();
		try {
			modelAndView.setViewName("aicontent");
			if (PortalDisplayUtil.isNotDesktopBrowser(renderRequest)) {
				modelAndView.getModelMap().addAttribute("mobile", "mobile");
			} else {
				modelAndView.getModelMap().addAttribute("mobile", "");
			}
			ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitutionByRepositoryCode(aiContentParams.getRepoCode());
			ContentSearchOptions eadSearchOptions = new ContentSearchOptions();
			eadSearchOptions.setArchivalInstitionId(archivalInstitution.getAiId());
			eadSearchOptions.setContentClass(XmlType.getTypeByResourceName(aiContentParams.getXmlTypeName()).getClazz());
			eadSearchOptions.setPublished(true);
			eadSearchOptions.setPageSize(100);
			eadSearchOptions.setOrderByField("title");
			eadSearchOptions.setPageNumber(Integer.parseInt(aiContentParams.getPageNumber()));
			modelAndView.getModelMap().addAttribute("archivalInstitution", archivalInstitution);
			modelAndView.getModelMap().addAttribute("aiRepoCode", aiContentParams.getRepoCode());
			modelAndView.getModelMap().addAttribute("xmlTypeName", aiContentParams.getXmlTypeName());
			modelAndView.getModelMap().addAttribute("pageNumber", aiContentParams.getPageNumber());

			modelAndView.getModelMap().addAttribute("pageSize", eadSearchOptions.getPageSize());
			if (EacCpf.class.equals(eadSearchOptions.getContentClass())){
				modelAndView.getModelMap().addAttribute("eacCpfs", eacCpfDAO.getEacCpfs(eadSearchOptions));
				modelAndView.getModelMap().addAttribute("totalNumberOfResults", eacCpfDAO.countEacCpfs(eadSearchOptions));
			}else {
				modelAndView.getModelMap().addAttribute("totalNumberOfResults", eadDAO.countEads(eadSearchOptions));
				modelAndView.getModelMap().addAttribute("eads", eadDAO.getEads(eadSearchOptions));
			}
			PortalDisplayUtil.setPageTitle(renderRequest, PortalDisplayUtil.getArchivalInstitutionDisplayTitle(archivalInstitution));
		} catch (NullPointerException e) {
			modelAndView.setViewName("indexError");
		} catch (Exception e) {
			LOGGER.error(ApeUtil.generateThrowableLog(e));
			modelAndView.setViewName("indexError");
		}
		return modelAndView;
	}
}
