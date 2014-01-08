package eu.archivesportaleurope.portal.search.saved;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CountryDAO;
import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;
import eu.archivesportaleurope.portal.search.advanced.Refinement;
import eu.archivesportaleurope.portal.search.common.FacetType;

public class SavedSearchService {
	private final static Logger LOGGER = Logger.getLogger(SavedSearchService.class);
	private static final String TRUE = "true";
	private static final String FALSE = "false";
	private EadSavedSearchDAO eadSavedSearchDAO;
	private CountryDAO countryDAO;
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private ResourceBundleMessageSource messageSource;

	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	public void saveSearch(Long liferayUserId, AdvancedSearch advancedSearch) {
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
		if (!"0".equals(advancedSearch.getElement())) {
			eadSavedSearch.setElement(removeEmptyString(advancedSearch.getElement()));
		}
		eadSavedSearch.setTypedocument(removeEmptyString(advancedSearch.getTypedocument()));
		eadSavedSearch.setFromdate(removeEmptyString(advancedSearch.getFromdate()));
		eadSavedSearch.setTodate(removeEmptyString(advancedSearch.getTodate()));
		eadSavedSearch.setExactDateSearch(TRUE.equals(advancedSearch.getExactDateSearch()));

		eadSavedSearch.setPageNumber(Integer.parseInt(advancedSearch.getPageNumber()));
		eadSavedSearch.setSorting(removeEmptyString(advancedSearch.getOrder()));
		eadSavedSearch.setResultPerPage(Integer.parseInt(advancedSearch.getResultsperpage()));

		/*
		 * al search options
		 */
		eadSavedSearch.setAlTreeSelectedNodes(removeEmptyString(advancedSearch.getSelectedNodes()));
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

	public EadSavedSearch getEadSavedSearch(Long liferayUserId, Long savedSearchId) {
		return eadSavedSearchDAO.getEadSavedSearch(savedSearchId, liferayUserId);

	}

	public AdvancedSearch convert(EadSavedSearch eadSavedSearch) {
		if (eadSavedSearch != null) {
			AdvancedSearch advancedSearch = new AdvancedSearch();
			/*
			 * simple search options
			 */
			advancedSearch.setTerm(eadSavedSearch.getSearchTerm());
			if (eadSavedSearch.isHierarchy()) {
				advancedSearch.setView(AdvancedSearch.VIEW_HIERARCHY);
			}
			if (eadSavedSearch.isMethodOptional()) {
				advancedSearch.setMethod(AdvancedSearch.METHOD_OPTIONAL);
			}
			if (eadSavedSearch.isOnlyWithDaos()) {
				advancedSearch.setSimpleSearchDao(TRUE);
			}
			/*
			 * advanced search options
			 */
			advancedSearch.setElement(eadSavedSearch.getElement());
			if (!"0".equals(eadSavedSearch.getElement())) {
				advancedSearch.setElement(removeEmptyString(eadSavedSearch.getElement()));
			}
			advancedSearch.setTypedocument(eadSavedSearch.getTypedocument());
			advancedSearch.setFromdate(eadSavedSearch.getFromdate());
			advancedSearch.setTodate(eadSavedSearch.getTodate());
			if (eadSavedSearch.isExactDateSearch()) {
				advancedSearch.setExactDateSearch(TRUE);
			}
			advancedSearch.setSelectedNodes(eadSavedSearch.getAlTreeSelectedNodes());
			if (!eadSavedSearch.isTemplate()) {
				if (eadSavedSearch.getPageNumber() != null) {
					advancedSearch.setPageNumber(eadSavedSearch.getPageNumber() + "");
				}
				advancedSearch.setOrder(eadSavedSearch.getSorting());
				if (eadSavedSearch.getResultPerPage() != null) {
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
			advancedSearch.setAdvanced(eadSavedSearch.isContainsAlSearchOptions());
			return advancedSearch;
		} else {
			return null;
		}
	}

	public List<Refinement> convertToRefinements(PortletRequest request, AdvancedSearch advancedSearch,
			EadSavedSearch eadSavedSearch) {
		List<Refinement> refinements = new ArrayList<Refinement>();
		try {
			List<Integer> aiIds = convertToIntegers(advancedSearch.getAiList());
			List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO.getArchivalInstitutions(aiIds);
			for (ArchivalInstitution archivalInstitution : archivalInstitutions) {
				refinements.add(new Refinement(FacetType.AI.getName(), archivalInstitution.getAiId(),
						archivalInstitution.getAiname()));
				removeInteger(aiIds, archivalInstitution.getAiId());
			}
			for (Integer aiId : aiIds) {
				String aiName = messageSource.getMessage("advancedsearch.text.savesearch.removedai", null,
						request.getLocale());
				refinements.add(new Refinement(FacetType.AI.getName(), aiId, aiName));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return refinements;

	}

	private static List<Integer> convertToIntegers(List<String> strings) {
		List<Integer> integers = new ArrayList<Integer>();
		for (String temp : strings) {
			if (StringUtils.isNumeric(temp)) {
				integers.add(Integer.parseInt(temp));
			}
		}
		return integers;
	}

	private static void removeInteger(List<Integer> list, Integer value){
		boolean found = false;
		for (int i=0;!found &&  i < list.size();i++){
			if (value.equals(list.get(i))){
				list.remove(i);
				found = true;
			}
		}
	}
	private static String removeEmptyString(String string) {
		if (StringUtils.isBlank(string)) {
			return null;
		} else {
			return string;
		}
	}
}
