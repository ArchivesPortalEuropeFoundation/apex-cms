package eu.archivesportaleurope.portal.eaccpf.search;

import java.io.IOException;

import javax.portlet.ResourceRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.utils.APEnetUtilities;
import eu.apenet.persistence.dao.EacCpfDAO;
import eu.apenet.persistence.vo.EacCpf;
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

	private EacCpfDAO eacCpfDAO;

	@ResourceMapping(value = "displayPreview")
	public ModelAndView displayPreview(ResourceRequest resourceRequest) {
		String eacCpfIdentifier = resourceRequest.getParameter("identifier");
		String repositoryCode = resourceRequest.getParameter("repositoryCode");
		try {
			if (StringUtils.isNotBlank(eacCpfIdentifier) && StringUtils.isNotBlank(repositoryCode)) {
				return fillEacCpfDetails(repositoryCode, eacCpfIdentifier);
			}else {
				throw new NotExistInDatabaseException();
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

	private ModelAndView fillEacCpfDetails(String repositoryCode, String eacCpfIdentifier) throws IOException, NotExistInDatabaseException {
		ModelAndView modelAndView = new ModelAndView();
		
		modelAndView.setViewName("preview/eaccpf");
		EacCpf eacCpf = eacCpfDAO.getEacCpfByIdentifier(repositoryCode, eacCpfIdentifier);
		if (eacCpf == null) {
			throw new NotExistInDatabaseException();
		}
		String eacCpfPath = APEnetUtilities.getApePortalConfig().getRepoDirPath() + eacCpf.getPath();
		modelAndView.getModelMap().addAttribute("eacCpf", eacCpf);
		modelAndView.getModelMap().addAttribute("eacCpfUrl", eacCpfPath);
		return modelAndView;
	}

	public void setEacCpfDAO(EacCpfDAO eacCpfDAO) {
		this.eacCpfDAO = eacCpfDAO;
	}



}
