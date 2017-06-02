package eu.archivesportaleurope.portal.search.common;

import eu.apenet.commons.utils.Cache;
import eu.apenet.commons.utils.CacheManager;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;

public class DatabaseCacher {

	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private final static Cache<Integer, String> repositoryCodeCache = CacheManager.getInstance().<Integer, String>initCache("repositoryCodeCache");

	public String getRepositoryCode(Integer aiId){
		String repositoryCode = repositoryCodeCache.get(aiId);
		if (repositoryCode == null){
			ArchivalInstitution archivalInstitution = archivalInstitutionDAO.getArchivalInstitution(aiId);
			if (archivalInstitution != null){
				repositoryCode = archivalInstitution.getRepositorycode();
				repositoryCodeCache.put(aiId, repositoryCode);
			}
		}
		return repositoryCode;
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}
	
}
