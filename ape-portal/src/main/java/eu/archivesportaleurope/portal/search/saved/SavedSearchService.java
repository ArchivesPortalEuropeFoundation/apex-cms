package eu.archivesportaleurope.portal.search.saved;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ResourceBundleMessageSource;

import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.utils.DisplayUtils;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CountryDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.dao.EadSavedSearchDAO;
import eu.apenet.persistence.dao.ContentSearchOptions;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.Country;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.EadSavedSearch;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.TreeType;
import eu.archivesportaleurope.portal.search.common.DateGap;
import eu.archivesportaleurope.portal.search.common.FacetType;
import eu.archivesportaleurope.portal.search.common.FacetValue;
import eu.archivesportaleurope.portal.search.ead.EadSearch;
import eu.archivesportaleurope.portal.search.ead.DateRefinement;
import eu.archivesportaleurope.portal.search.ead.Refinement;

public class SavedSearchService {
	private final static Logger LOGGER = Logger.getLogger(SavedSearchService.class);
	private static final String TRUE = "true";
	private EadSavedSearchDAO eadSavedSearchDAO;
	private CountryDAO countryDAO;
	private ArchivalInstitutionDAO archivalInstitutionDAO;
	private EadDAO eadDAO;
	private ResourceBundleMessageSource messageSource;

	public void setEadSavedSearchDAO(EadSavedSearchDAO eadSavedSearchDAO) {
		this.eadSavedSearchDAO = eadSavedSearchDAO;
	}

	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}

	public void setMessageSource(ResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	public void saveSearch(Long liferayUserId, EadSearch eadSearch) {
		EadSavedSearch eadSavedSearch = new EadSavedSearch();
		eadSavedSearch.setLiferayUserId(liferayUserId);
		eadSavedSearch.setModifiedDate(new Date());
		/*
		 * simple search options
		 */
		eadSavedSearch.setSearchTerm(removeEmptyString(eadSearch.getTerm()));
		eadSavedSearch.setHierarchy(EadSearch.VIEW_HIERARCHY.equals(eadSearch.getView()));
		eadSavedSearch.setMethodOptional(EadSearch.METHOD_OPTIONAL.equals(eadSearch.getMethod()));
		eadSavedSearch.setOnlyWithDaos(TRUE.equals(eadSearch.getSimpleSearchDao()));
		/*
		 * advanced search options
		 */
		if (!"0".equals(eadSearch.getElement())) {
			eadSavedSearch.setElement(removeEmptyString(eadSearch.getElement()));
		}
		eadSavedSearch.setTypedocument(removeEmptyString(eadSearch.getTypedocument()));
		eadSavedSearch.setFromdate(removeEmptyString(eadSearch.getFromdate()));
		eadSavedSearch.setTodate(removeEmptyString(eadSearch.getTodate()));
		eadSavedSearch.setExactDateSearch(TRUE.equals(eadSearch.getExactDateSearch()));

		eadSavedSearch.setPageNumber(Integer.parseInt(eadSearch.getPageNumber()));
		eadSavedSearch.setSorting(removeEmptyString(eadSearch.getOrder()));
		eadSavedSearch.setResultPerPage(Integer.parseInt(eadSearch.getResultsperpage()));

		/*
		 * al search options
		 */
		eadSavedSearch.setAlTreeSelectedNodes(removeDepthFromSelectedNodes(eadSearch.getSelectedNodes()));
		/*
		 * refinements
		 */
		if (StringUtils.isBlank(eadSearch.getFond())) {
			eadSavedSearch.setRefinementCountry(removeEmptyString(eadSearch.getCountry()));
			eadSavedSearch.setRefinementAi(removeEmptyString(eadSearch.getAi()));
		} else {
			eadSavedSearch.setRefinementFond(removeEmptyString(eadSearch.getFond()));
		}
		eadSavedSearch.setRefinementType(removeEmptyString(eadSearch.getType()));
		eadSavedSearch.setRefinementLevel(removeEmptyString(eadSearch.getLevel()));
		eadSavedSearch.setRefinementDao(removeEmptyString(eadSearch.getDao()));
		eadSavedSearch.setRefinementRoledao(removeEmptyString(eadSearch.getRoledao()));
		eadSavedSearch.setRefinementDateType(removeEmptyString(eadSearch.getDateType()));
		eadSavedSearch.setRefinementStartdate(removeEmptyString(eadSearch.getStartdate()));
		eadSavedSearch.setRefinementEnddate(removeEmptyString(eadSearch.getEnddate()));
		eadSavedSearch.setRefinementFacetSettings(removeEmptyString(eadSearch.getFacetSettings()));
		eadSavedSearchDAO.store(eadSavedSearch);
	}

	public EadSavedSearch getEadSavedSearch(Long liferayUserId, Long savedSearchId) {
		return eadSavedSearchDAO.getEadSavedSearch(savedSearchId, liferayUserId);

	}

	public EadSearch convert(EadSavedSearch eadSavedSearch, EadSearch eadSearch) {
		if (eadSavedSearch != null) {
			//EadSearch eadSearch = new EadSearch();
			/*
			 * simple search options
			 */
			eadSearch.setTerm(eadSavedSearch.getSearchTerm());
			if (eadSavedSearch.isHierarchy()) {
				eadSearch.setView(EadSearch.VIEW_HIERARCHY);
			}
			if (eadSavedSearch.isMethodOptional()) {
				eadSearch.setMethod(EadSearch.METHOD_OPTIONAL);
			}
			if (eadSavedSearch.isOnlyWithDaos()) {
				eadSearch.setSimpleSearchDao(TRUE);
			}
			/*
			 * advanced search options
			 */
			eadSearch.setElement(eadSavedSearch.getElement());
			if (!"0".equals(eadSavedSearch.getElement())) {
				eadSearch.setElement(removeEmptyString(eadSavedSearch.getElement()));
			}
			eadSearch.setTypedocument(eadSavedSearch.getTypedocument());
			eadSearch.setFromdate(eadSavedSearch.getFromdate());
			eadSearch.setTodate(eadSavedSearch.getTodate());
			if (eadSavedSearch.isExactDateSearch()) {
				eadSearch.setExactDateSearch(TRUE);
			}
			eadSearch.setSelectedNodes(addDepthToSelectedNodes(eadSavedSearch.getAlTreeSelectedNodes()));
			if (!eadSavedSearch.isTemplate()) {
				if (eadSavedSearch.getPageNumber() != null) {
					eadSearch.setPageNumber(eadSavedSearch.getPageNumber() + "");
				}
				eadSearch.setOrder(eadSavedSearch.getSorting());
				if (eadSavedSearch.getResultPerPage() != null) {
					eadSearch.setResultsperpage(eadSavedSearch.getResultPerPage() + "");
				}
				eadSavedSearch.setResultPerPage(Integer.parseInt(eadSearch.getResultsperpage()));
				/*
				 * refinements
				 */
				eadSearch.setCountry(eadSavedSearch.getRefinementCountry());
				eadSearch.setAi(eadSavedSearch.getRefinementAi());
				eadSearch.setFond(eadSavedSearch.getRefinementFond());
				eadSearch.setType(eadSavedSearch.getRefinementType());
				eadSearch.setLevel(eadSavedSearch.getRefinementLevel());
				eadSearch.setDateType(removeEmptyString(eadSavedSearch.getRefinementDateType()));
				eadSearch.setDao(eadSavedSearch.getRefinementDao());
				eadSearch.setRoledao(eadSavedSearch.getRefinementRoledao());
				eadSearch.setStartdate(eadSavedSearch.getRefinementStartdate());
				eadSearch.setEnddate(eadSavedSearch.getRefinementEnddate());
				eadSearch.setFacetSettings(eadSavedSearch.getRefinementFacetSettings());
			}
			eadSearch.setAdvanced(eadSavedSearch.isContainsAlSearchOptions());
			return eadSearch;
		} else {
			return null;
		}
	}

	public List<Refinement> convertToRefinements(PortletRequest request, EadSearch eadSearch,
			EadSavedSearch eadSavedSearch) {
		SpringResourceBundleSource source = new SpringResourceBundleSource(messageSource, request.getLocale());
		List<Refinement> refinements = new ArrayList<Refinement>();
		try {
			List<Integer> countryIds = convertToIntegers(eadSearch.getCountryList());
			List<Country> countries = countryDAO.getCountries(countryIds);
			for (Country country : countries) {
				String localizedName = DisplayUtils.getLocalizedCountryName(source, country);
				refinements.add(new Refinement(FacetType.COUNTRY.getName(), country.getId(), localizedName));
				removeInteger(countryIds, country.getId());
			}
			for (Integer countryId : countryIds) {
				String countryName = source.getString("advancedsearch.text.savesearch.removedcountry");
				refinements.add(new Refinement(FacetType.COUNTRY.getName(), countryId, countryName, true));
			}
			List<Integer> aiIds = convertToIntegers(eadSearch.getAiList());
			List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO.getArchivalInstitutions(aiIds);
			for (ArchivalInstitution archivalInstitution : archivalInstitutions) {
				refinements.add(new Refinement(FacetType.AI.getName(), archivalInstitution.getAiId(),
						archivalInstitution.getAiname()));
				removeInteger(aiIds, archivalInstitution.getAiId());
			}
			for (Integer aiId : aiIds) {
				String aiName = source.getString("advancedsearch.text.savesearch.removedai");
				refinements.add(new Refinement(FacetType.AI.getName(), aiId, aiName, true));
			}
			if (StringUtils.isNotBlank(eadSearch.getFond()) && eadSearch.getFond().length() > 1) {
				XmlType xmlType = XmlType.getTypeBySolrPrefix(eadSearch.getFond().substring(0, 1));
				String identifier = eadSearch.getFond().substring(1);
				Integer id = Integer.parseInt(identifier);
				ContentSearchOptions eadSearchOptions = new ContentSearchOptions();
				eadSearchOptions.setContentClass(xmlType.getClazz());
				eadSearchOptions.setId(id);
				List<Ead> eads = eadDAO.getEads(eadSearchOptions);
				if (eads.size() > 0) {
					Ead ead = eads.get(0);
					refinements.add(new Refinement(FacetType.FOND.getName(), eadSearch.getFond(), ead.getTitle()));
				} else {
					String fondName = source.getString("advancedsearch.text.savesearch.removedfond");
					refinements.add(new Refinement(FacetType.FOND.getName(), eadSearch.getFond(), fondName, true));
				}

			}
			List<String> types = eadSearch.getTypeList();
			if (types != null) {
				for (String type : types) {
					refinements.add(new Refinement(FacetType.TYPE.getName(), type, source.getString(FacetType.TYPE
							.getPrefix() + type)));
				}
			}
			List<String> levels = eadSearch.getLevelList();
			if (levels != null) {
				for (String level : levels) {
					refinements.add(new Refinement(FacetType.LEVEL.getName(), level, source.getString(FacetType.LEVEL
							.getPrefix() + level)));
				}
			}
			List<String> daos = eadSearch.getDaoList();
			if (daos != null) {
				for (String dao : daos) {
					refinements.add(new Refinement(FacetType.DAO.getName(), dao, source.getString(FacetType.DAO
							.getPrefix() + dao)));
				}
			}
			List<String> roledaos = eadSearch.getRoledaoList();
			if (roledaos != null) {
				for (String roledao : roledaos) {
					refinements.add(new Refinement(FacetType.ROLEDAO.getName(), roledao, source
							.getString(FacetType.ROLEDAO.getPrefix() + roledao.toLowerCase())));
				}
			}
			List<String> dateTypes = eadSearch.getDateTypeList();
			if (dateTypes != null) {
				for (String dateType : dateTypes) {
					refinements.add(new Refinement(FacetType.DATE_TYPE.getName(), dateType, source
							.getString(FacetType.DATE_TYPE.getPrefix() + dateType)));
				}
			}
			if (StringUtils.isNotBlank(eadSearch.getStartdate())) {
				String dateSpan = getDateSpan(eadSearch.getStartdate());
				String description = source.getString("advancedsearch.facet.title."
						+ FacetType.START_DATE.getName().toLowerCase())
						+ " " + dateSpan;
				refinements.add(new DateRefinement(FacetType.START_DATE.getName(), eadSearch.getStartdate(),
						description));

			}
			if (StringUtils.isNotBlank(eadSearch.getEnddate())) {
				String dateSpan = getDateSpan(eadSearch.getEnddate());
				String description = source.getString("advancedsearch.facet.title."
						+ FacetType.END_DATE.getName().toLowerCase())
						+ " " + dateSpan;
				refinements.add(new DateRefinement(FacetType.END_DATE.getName(), eadSearch.getStartdate(),
						description));
			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		return refinements;

	}

	private String getDateSpan(String date) {
		String[] splittedStartDate = date.split("_");
		String startDateString = splittedStartDate[0];
		String gapString = splittedStartDate[1];
		DateGap dateGap = DateGap.getGapById(gapString);
		if (dateGap != null) {
			dateGap = dateGap.previous();
			try {
				return FacetValue.getDateSpan(dateGap, startDateString);
			} catch (Exception e) {
				LOGGER.error("Unable to parse: " + date + " " + gapString);
			}

		}
		return null;
	}

	private static List<Integer> convertToIntegers(List<String> strings) {
		List<Integer> integers = new ArrayList<Integer>();
		if (strings != null) {
			for (String temp : strings) {
				if (StringUtils.isNumeric(temp)) {
					integers.add(Integer.parseInt(temp));
				}
			}
		}
		return integers;
	}

	private static void removeInteger(List<Integer> list, Integer value) {
		boolean found = false;
		for (int i = 0; !found && i < list.size(); i++) {
			if (value.equals(list.get(i))) {
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
	private static String removeDepthFromSelectedNodes(String selectedNodes){
		String result =null;
		if (StringUtils.isNotBlank(selectedNodes)) {
			List<String> tempList = Arrays.asList(selectedNodes.split(AlType.LIST_SEPARATOR));
			for (int i = 0; i < tempList.size(); i++){
				String selectedNode = tempList.get(i);
				AlType alType = AlType.getAlType(selectedNode);
				Long id = AlType.getId(selectedNode);
				TreeType treeType = AlType.getTreeType(selectedNode);
				String key = AlType.getKey(alType, id, treeType);
				if (i == 0){
					result = key;
				}else {
					result += AlType.LIST_SEPARATOR + key;
				}
			}
		}
		return result;
	}
	private String addDepthToSelectedNodes(String selectedNodes){
		String result =null;
		if (StringUtils.isNotBlank(selectedNodes)) {
			List<String> tempList = Arrays.asList(selectedNodes.split(AlType.LIST_SEPARATOR));
			for (int i = 0; i < tempList.size(); i++){
				String selectedNode = tempList.get(i);
				AlType alType = AlType.getAlType(selectedNode);
				Long id = AlType.getId(selectedNode);
				TreeType treeType = AlType.getTreeType(selectedNode);
				String key = null;
				if (AlType.ARCHIVAL_INSTITUTION.equals(alType) && TreeType.GROUP.equals(treeType)){
					int depth = 0;
					ArchivalInstitution archivalInstitution = archivalInstitutionDAO.findById(id.intValue());
					ArchivalInstitution parent  = archivalInstitution.getParent();
					while (parent != null){
						depth++;
						parent = parent.getParent();
					}
					key = AlType.getKeyWithDepth(alType, id, treeType, depth);
				}else {
					key = AlType.getKey(alType, id, treeType);
				}
				if (i == 0){
					result = key;
				}else {
					result += AlType.LIST_SEPARATOR + key;
				}
			}
		}
		return result;
	}
}
