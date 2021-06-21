package eu.archivesportaleurope.portal.search.ead;

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
import eu.apenet.persistence.dao.Ead3DAO;
import eu.apenet.persistence.dao.EadContentDAO;
import eu.apenet.persistence.vo.AbstractContent;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.Ead3;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.NotExistInDatabaseException;
import eu.archivesportaleurope.util.ApeUtil;

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
    private Ead3DAO ead3DAO;

    @ResourceMapping(value = "displayPreview")
    public ModelAndView displayPreview(ResourceRequest resourceRequest) {
        String id = resourceRequest.getParameter("id");
        String term = ApeUtil.decodeSpecialCharacters(resourceRequest.getParameter("term"));
        try {
            if (StringUtils.isNotBlank(id)) {
                if (id.startsWith(SolrValues.C_LEVEL_PREFIX)) {
                    Long idLong = new Long(id.substring(1));
                    if (id.startsWith(SolrValues.C_LEVEL_PREFIX)) {
                        return fillCDetails(idLong, term);
                    }

                } else {
                    String solrPrefix = id.substring(0, 1);
                    XmlType xmlType = XmlType.getTypeBySolrPrefix(solrPrefix);
                    if (xmlType != null) {
                        Long idLong = Long.parseLong(id.substring(1));
                        ModelAndView modelAndView = new ModelAndView();

                        modelAndView.getModelMap().addAttribute("xmlType", xmlType);
                        modelAndView.getModelMap().addAttribute("term", term);
                        EadContent eadContent = null;
                        if (xmlType.equals(XmlType.EAD_3)) {
                            Ead3 ead3 = ead3DAO.findById(idLong.intValue());
                            eadContent = ead3.getEadContent();
                        } else {
                            eadContent = eadContentDAO.getEadContentByFileId(idLong.intValue(), xmlType.getEadClazz());
                        }
                        if (eadContent == null) {
                            throw new NotExistInDatabaseException(id);
                        }
                        modelAndView.getModelMap().addAttribute("eadContent", eadContent);
                        if (xmlType.equals(XmlType.EAD_3)) {
                            modelAndView.setViewName("preview/frontpage-ead3");
                            Ead3 ead3 = eadContent.getEad3();
                            ArchivalInstitution archivalInstitution = ead3.getArchivalInstitution();
                            modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
                            modelAndView.getModelMap().addAttribute("aiRepoCode", archivalInstitution.getEncodedRepositorycode());
                            modelAndView.getModelMap().addAttribute("eadid", ead3.getEncodedIdentifier());
                        } else {
                            modelAndView.setViewName("preview/frontpage");
                            Ead ead = eadContent.getEad();
                            ArchivalInstitution archivalInstitution = ead.getArchivalInstitution();
                            modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
                            modelAndView.getModelMap().addAttribute("aiRepoCode", archivalInstitution.getEncodedRepositorycode());
                            modelAndView.getModelMap().addAttribute("eadid", ead.getEncodedIdentifier());
                        }
                        modelAndView.getModelMap().addAttribute("xmlTypeName", xmlType.getResourceName());
                        return modelAndView;
                    } else {
                        return fillAIDetails(Long.parseLong(id));
                    }
                }

            }
        } catch (NotExistInDatabaseException e) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.getModelMap().addAttribute("errorMessage", "error.user.second.display.notexist");
            modelAndView.setViewName("preview/indexError");
            return modelAndView;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
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

    private ModelAndView fillCDetails(Long idLong, String term) throws NotExistInDatabaseException {
        ModelAndView modelAndView = new ModelAndView();

        CLevel currentCLevel = clevelDAO.findById(idLong);
        if (currentCLevel == null) {
            throw new NotExistInDatabaseException();
        }
        AbstractContent ead = null;
        if (currentCLevel.getEad3() != null) {
            modelAndView.setViewName("preview/ead3-cdetails");
            ead = currentCLevel.getEad3();
        } else {
            modelAndView.setViewName("preview/cdetails");
            ead = currentCLevel.getEadContent().getEad();
        }
        if (currentCLevel.isDuplicateUnitid()) {
            currentCLevel.setUnitid(null);
        }
        ArchivalInstitution archivalInstitution = ead.getArchivalInstitution();
        XmlType xmlType = XmlType.getContentType(ead);
        LOGGER.debug("fillCDetails: term-" + term + ", xmlType-" + xmlType + ", c-" + currentCLevel + ", aiId-" + archivalInstitution.getAiId() + ", aiRepoCode-" + archivalInstitution.getEncodedRepositorycode() + ", eadid-" + ead.getEncodedIdentifier());
        modelAndView.getModelMap().addAttribute("term", term);
        modelAndView.getModelMap().addAttribute("xmlType", xmlType);
        modelAndView.getModelMap().addAttribute("c", currentCLevel);
        modelAndView.getModelMap().addAttribute("aiId", archivalInstitution.getAiId());
        modelAndView.getModelMap().addAttribute("aiRepoCode", archivalInstitution.getEncodedRepositorycode());
        modelAndView.getModelMap().addAttribute("eadid", ead.getEncodedIdentifier());
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

    public Ead3DAO getEad3DAO() {
        return ead3DAO;
    }

    public void setEad3DAO(Ead3DAO ead3DAO) {
        this.ead3DAO = ead3DAO;
    }

}
