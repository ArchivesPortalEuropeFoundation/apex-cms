package eu.archivesportaleurope.portal.search.saved;

import java.util.Date;

import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;

public class SavedSearchService {
	private static final String TRUE = "true";
	private EadSavedSearchDAO eadSavedSearchDAO;
	
	
	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}
	public void saveSearch(Long liferayUserId, AdvancedSearch advancedSearch){
		EadSavedSearch eadSavedSearch = new EadSavedSearch();
		eadSavedSearch.setLiferayUserId(liferayUserId);
		eadSavedSearch.setModifiedDate(new Date());
		eadSavedSearch.setSearchTerm(advancedSearch.getTerm());
		eadSavedSearch.setHierarchy(AdvancedSearch.VIEW_HIERARCHY.equals(advancedSearch.getView()));
		eadSavedSearch.setMethodOptional(AdvancedSearch.METHOD_OPTIONAL.equals(advancedSearch.getMethod()));
		eadSavedSearch.setOnlyWithDaos(TRUE.equals(advancedSearch.getDao()));
		eadSavedSearchDAO.store(eadSavedSearch);
	}
	
	public EadSavedSearch getEadSavedSearch(Long liferayUserId, Long savedSearchId){
		return eadSavedSearchDAO.getEadSavedSearch(savedSearchId, liferayUserId);

	}
	public AdvancedSearch convert(EadSavedSearch eadSavedSearch){
		if (eadSavedSearch != null) {
			AdvancedSearch advancedSearch = new AdvancedSearch();
			advancedSearch.setTerm(eadSavedSearch.getSearchTerm());
			if (eadSavedSearch.isHierarchy()){
				advancedSearch.setView(AdvancedSearch.VIEW_HIERARCHY);
			}
			if (eadSavedSearch.isMethodOptional()){
				advancedSearch.setMethod(AdvancedSearch.METHOD_OPTIONAL);
			}
			if (eadSavedSearch.isOnlyWithDaos()){
				advancedSearch.setDao(TRUE);
			}
			return advancedSearch;
		}else {
			return null;
		}		
	}
}
