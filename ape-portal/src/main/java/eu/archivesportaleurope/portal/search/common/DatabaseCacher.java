package eu.archivesportaleurope.portal.search.common;

import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;

public class DatabaseCacher {

	private ArchivalInstitutionDAO archivalInstitutionDAO;
	
	public String getRepositoryCode(Integer aiId){
		ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitution(aiId);
		return archivalInstitution.getRepositorycode();
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}
	
}
