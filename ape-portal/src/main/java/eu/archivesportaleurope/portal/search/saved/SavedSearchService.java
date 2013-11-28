package eu.archivesportaleurope.portal.search.saved;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;

public class SavedSearchService {
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private EadSavedSearchDAO eadSavedSearchDAO;
	
	
	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}
	public void saveSearch(Long liferayUserId, AdvancedSearch advancedSearch){
		EadSavedSearch eadSavedSearch = new EadSavedSearch();
		eadSavedSearch.setLiferayUserId(liferayUserId);
		eadSavedSearch.setModifiedDate(new Date());
		/*
		 * simple search options
		 */
		eadSavedSearch.setSearchTerm(removeEmptyString(advancedSearch.getTerm()));
		eadSavedSearch.setHierarchy(AdvancedSearch.VIEW_HIERARCHY.equals(advancedSearch.getView()));
		eadSavedSearch.setMethodOptional(AdvancedSearch.METHOD_OPTIONAL.equals(advancedSearch.getMethod()));
		eadSavedSearch.setOnlyWithDaos(TRUE.equals(advancedSearch.getSimpleSearchDao()));
		/*
		 * advanced search options
		 */
		eadSavedSearch.setElement(removeEmptyString(advancedSearch.getElement()));
		eadSavedSearch.setTypedocument(removeEmptyString(advancedSearch.getTypedocument()));
		eadSavedSearch.setFromdate(removeEmptyString(advancedSearch.getFromdate()));
		eadSavedSearch.setTodate(removeEmptyString(advancedSearch.getTodate()));
		eadSavedSearch.setExactDateSearch(TRUE.equals(advancedSearch.getExactDateSearch()));
		
		eadSavedSearch.setPageNumber(Integer.parseInt(advancedSearch.getPageNumber()));
		eadSavedSearch.setSorting(removeEmptyString(advancedSearch.getOrder()));
		eadSavedSearch.setResultPerPage(Integer.parseInt(advancedSearch.getResultsperpage()));
		
		/*
		 * refinements
		 */
		eadSavedSearch.setRefinementCountry(removeEmptyString(advancedSearch.getCountry()));
		eadSavedSearch.setRefinementAi(removeEmptyString(advancedSearch.getAi()));
		eadSavedSearch.setRefinementFond(removeEmptyString(advancedSearch.getFond()));
		eadSavedSearch.setRefinementType(removeEmptyString(advancedSearch.getType()));
		eadSavedSearch.setRefinementLevel(removeEmptyString(advancedSearch.getLevel()));
		eadSavedSearch.setRefinementDao(removeEmptyString(advancedSearch.getDao()));
		eadSavedSearch.setRefinementRoledao(removeEmptyString(advancedSearch.getRoledao()));
		eadSavedSearch.setRefinementDateType(removeEmptyString(advancedSearch.getDateType()));
		eadSavedSearch.setRefinementStartdate(removeEmptyString(advancedSearch.getStartdate()));
		eadSavedSearch.setRefinementEnddate(removeEmptyString(advancedSearch.getEnddate()));
		eadSavedSearch.setRefinementFacetSettings(removeEmptyString(advancedSearch.getFacetSettings()));
		eadSavedSearchDAO.store(eadSavedSearch);
	}
	
	public EadSavedSearch getEadSavedSearch(Long liferayUserId, Long savedSearchId){
		return eadSavedSearchDAO.getEadSavedSearch(savedSearchId, liferayUserId);

	}
	public AdvancedSearch convert(EadSavedSearch eadSavedSearch){
		if (eadSavedSearch != null) {
			AdvancedSearch advancedSearch = new AdvancedSearch();
			/*
			 * simple search options
			 */
			advancedSearch.setTerm(eadSavedSearch.getSearchTerm());
			if (eadSavedSearch.isHierarchy()){
				advancedSearch.setView(AdvancedSearch.VIEW_HIERARCHY);
			}
			if (eadSavedSearch.isMethodOptional()){
				advancedSearch.setMethod(AdvancedSearch.METHOD_OPTIONAL);
			}
			if (eadSavedSearch.isOnlyWithDaos()){
				advancedSearch.setSimpleSearchDao(TRUE);
			}
			/*
			 * advanced search options
			 */
			advancedSearch.setElement(eadSavedSearch.getElement());
			advancedSearch.setTypedocument(eadSavedSearch.getTypedocument());
			advancedSearch.setFromdate(eadSavedSearch.getFromdate());
			advancedSearch.setTodate(eadSavedSearch.getTodate());
			if (eadSavedSearch.isExactDateSearch()){
				advancedSearch.setExactDateSearch(TRUE);
			}

			if (!eadSavedSearch.isTemplate()){
				if (eadSavedSearch.getPageNumber() != null){
					advancedSearch.setPageNumber(eadSavedSearch.getPageNumber() + "");
				}
				advancedSearch.setOrder(eadSavedSearch.getSorting());
				if (eadSavedSearch.getResultPerPage() != null){
					advancedSearch.setResultsperpage(eadSavedSearch.getResultPerPage() + "");
				}
				eadSavedSearch.setResultPerPage(Integer.parseInt(advancedSearch.getResultsperpage()));
				/*
				 * refinements
				 */
				advancedSearch.setCountry(eadSavedSearch.getRefinementCountry());
				advancedSearch.setAi(eadSavedSearch.getRefinementAi());
				advancedSearch.setFond(eadSavedSearch.getRefinementFond());
				advancedSearch.setType(eadSavedSearch.getRefinementType());
				advancedSearch.setLevel(eadSavedSearch.getRefinementLevel());
				advancedSearch.setDateType(removeEmptyString(eadSavedSearch.getRefinementDateType()));
				advancedSearch.setDao(eadSavedSearch.getRefinementDao());
				advancedSearch.setRoledao(eadSavedSearch.getRefinementRoledao());
				advancedSearch.setStartdate(eadSavedSearch.getRefinementStartdate());
				advancedSearch.setEnddate(eadSavedSearch.getRefinementEnddate());
				advancedSearch.setFacetSettings(eadSavedSearch.getRefinementFacetSettings());
			}
			return advancedSearch;
		}else {
			return null;
		}		
	}
	private static String removeEmptyString(String string){
		if (StringUtils.isBlank(string)){
			return null;
		}else {
			return string;
		}
	}
	private static Boolean convertToBoolean(String string){
		Boolean result = null;
		return result;
	}
}
