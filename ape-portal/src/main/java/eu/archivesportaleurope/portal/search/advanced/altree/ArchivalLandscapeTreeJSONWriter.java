package eu.archivesportaleurope.portal.search.advanced.altree;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.exceptions.APEnetException;
import eu.apenet.commons.infraestructure.ArchivalInstitutionUnit;
import eu.apenet.commons.infraestructure.CLevelUnit;
import eu.apenet.commons.infraestructure.FindingAidUnit;
import eu.apenet.commons.infraestructure.HoldingsGuideUnit;
import eu.apenet.commons.infraestructure.NavigationTree;
import eu.apenet.commons.types.XmlType;
import eu.apenet.persistence.dao.ArchivalInstitutionDAO;
import eu.apenet.persistence.dao.CLevelDAO;
import eu.apenet.persistence.dao.CountryDAO;
import eu.apenet.persistence.dao.EadDAO;
import eu.apenet.persistence.dao.FindingAidDAO;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.CLevel;
import eu.apenet.persistence.vo.Ead;
import eu.apenet.persistence.vo.FindingAid;
import eu.apenet.persistence.vo.HoldingsGuide;
import eu.apenet.persistence.vo.SourceGuide;
import eu.archivesportaleurope.portal.common.AbstractJSONWriter;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.ArchivalLandscapeUtil;
import eu.archivesportaleurope.portal.common.al.CountryUnit;
import eu.archivesportaleurope.portal.common.al.TreeType;

/**
 * JSON Writer for the navigated tree
 * 
 * @author bastiaan
 * 
 */
@Controller(value = "archivalLandscapeTreeJSONWriter")
@RequestMapping(value = "VIEW")
public class ArchivalLandscapeTreeJSONWriter extends AbstractJSONWriter {
	private static final String FOLDER_LAZY = "\"isFolder\": true, \"isLazy\": true";
	private static final String FOLDER_NOT_LAZY = "\"isFolder\": true";
	private static final String EXPANDED = "\"expand\": true";
	private static final String NOT_SELECTABLE = "\"unselectable\": true";
	private static final String NOT_CHECKBOX = "\"hideCheckbox\": true";
	private static final String SELECTED = "\"select\": true";
	private static final String CHILDREN = "\"children\" :";
	private static final String NO_LINK = "\"noLink\": true";

	private static final Integer MAX_NUMBER_OF_CLEVELS = 20;
	private CountryDAO countryDAO;

	private ArchivalInstitutionDAO archivalInstitutionDAO;

	private EadDAO eadDAO;
	private FindingAidDAO findingAidDAO;
	private CLevelDAO cLevelDAO;
	public void setCountryDAO(CountryDAO countryDAO) {
		this.countryDAO = countryDAO;
	}

	public void setArchivalInstitutionDAO(ArchivalInstitutionDAO archivalInstitutionDAO) {
		this.archivalInstitutionDAO = archivalInstitutionDAO;
	}

	public void setEadDAO(EadDAO eadDAO) {
		this.eadDAO = eadDAO;
	}

	public void setFindingAidDAO(FindingAidDAO findingAidDAO) {
		this.findingAidDAO = findingAidDAO;
	}

	public void setcLevelDAO(CLevelDAO cLevelDAO) {
		this.cLevelDAO = cLevelDAO;
	}

	@ResourceMapping(value = "archivalLandscapeTree")
	public void writeJSON(@ModelAttribute(value = "alTreeParams") AlTreeParams alTreeParams,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					resourceRequest.getLocale());
			ArchivalLandscapeUtil archivalLandscapeUtil = new ArchivalLandscapeUtil(source);
			if (StringUtils.isBlank(alTreeParams.getParentId())) {
				writeToResponseAndClose(generateCountriesTreeJSON(alTreeParams, archivalLandscapeUtil),
						resourceResponse);
			} else {
				AlType parentType = AlType.getType(alTreeParams.getParentId());
				Integer countryId = null;
				Integer parentAiId = null;
				boolean displayAis = false;
				boolean displayEADfolders = false;
				if (AlType.COUNTRY.equals(parentType)) {
					countryId = AlType.getId(alTreeParams.getParentId());
					displayAis = true;
				} else if (AlType.ARCHIVAL_INSTITUTION.equals(parentType)) {
					parentAiId = AlType.getId(alTreeParams.getParentId());
					if (TreeType.GROUP.equals(TreeType.getType(alTreeParams.getType()))) {
						displayAis = true;
					} else {
						displayEADfolders = true;
					}
				} else if (AlType.SOURCE_GUIDE.equals(parentType) || AlType.HOLDINGS_GUIDE.equals(parentType)) {
					if (TreeType.GROUP.equals(TreeType.getType(alTreeParams.getType()))) {
						writeToResponseAndClose(
								generateEadFolderTreeJSON(alTreeParams, parentType, archivalLandscapeUtil, null, false),
								resourceResponse);
					} else {
						Integer parentId = AlType.getId(alTreeParams.getParentId());
						List<CLevel> topClevels = cLevelDAO.getTopClevelsByFileId(parentId, XmlType.getTypeBySolrPrefix(parentType.toString()).getClazz(), alTreeParams.getStart(), MAX_NUMBER_OF_CLEVELS+1);
						writeToResponseAndClose(
								generateCLevelsJSON(alTreeParams, parentType, archivalLandscapeUtil, topClevels, alTreeParams.getParentId(), alTreeParams.getType(), alTreeParams.getAiId(), alTreeParams.getStart(), false, false),
								resourceResponse);
					}
				}else if (AlType.C_LEVEL.equals(parentType)){
					Long parentId = AlType.getLongId(alTreeParams.getParentId());
					List<CLevel> clevels = cLevelDAO.findChildCLevels(parentId, alTreeParams.getStart(), MAX_NUMBER_OF_CLEVELS+1);
					writeToResponseAndClose(
							generateCLevelsJSON(alTreeParams, parentType, archivalLandscapeUtil, clevels, alTreeParams.getParentId(), alTreeParams.getType(), alTreeParams.getAiId(), alTreeParams.getStart(), false, false),
							resourceResponse);					
				}

				if (displayAis) {
					List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO
							.getArchivalInstitutionsWithSearchableItems(countryId, parentAiId);
					writeToResponseAndClose(
							generateArchivalInstitutionsTreeJSON(alTreeParams, archivalLandscapeUtil,
									archivalInstitutions, false), resourceResponse);
				} else if (displayEADfolders) {
					writeToResponseAndClose(
							generateEADFoldersTreeJSON(alTreeParams, archivalLandscapeUtil, null, false),
							resourceResponse);
				}
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.info("writeJSON: " + (System.currentTimeMillis() - startTime));
	}

	private StringBuilder generateCountriesTreeJSON(AlTreeParams alTreeParams,
			ArchivalLandscapeUtil archivalLandscapeUtil) throws APEnetException {
		List<CountryUnit> countryList = archivalLandscapeUtil.localizeCountries(countryDAO
				.getCountriesWithSearchableItems());
		StringBuilder buffer = new StringBuilder();
		buffer.append(START_ARRAY);
		for (int i = 0; i < countryList.size(); i++) {
			boolean parentSelected = false;
			// It is necessary to build a JSON response to display all the
			// countries in Navigated Search Tree
			CountryUnit countryUnit = countryList.get(i);
			buffer.append(START_ITEM);
			addTitle(buffer, countryUnit.getLocalizedName(), archivalLandscapeUtil.getLocale());
			buffer.append(COMMA);
			if (alTreeParams.existInSelectedNodes(AlType.COUNTRY, countryUnit.getCountry().getId(), TreeType.GROUP)) {
				buffer.append(SELECTED);
				buffer.append(COMMA);
				parentSelected = true;
			}
			if (alTreeParams.existInExpandedNodes(AlType.COUNTRY, countryUnit.getCountry().getId(), TreeType.GROUP)) {
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(EXPANDED);
				buffer.append(COMMA);

				List<ArchivalInstitution> archivalInstitutions = archivalInstitutionDAO
						.getArchivalInstitutionsWithSearchableItems(countryUnit.getCountry().getId(), null);
				StringBuilder tempBuffer = generateArchivalInstitutionsTreeJSON(alTreeParams, archivalLandscapeUtil,
						archivalInstitutions, parentSelected);
				buffer.append(CHILDREN);
				buffer.append(tempBuffer);
			} else {
				buffer.append(FOLDER_LAZY);

			}
			buffer.append(COMMA);
			addKey(buffer, AlType.COUNTRY, countryUnit.getCountry().getId(), TreeType.GROUP);
			buffer.append(END_ITEM);
			if (i != countryList.size() - 1) {
				buffer.append(COMMA);
			}
		}

		buffer.append(END_ARRAY);
		return buffer;

	}

	private StringBuilder generateArchivalInstitutionsTreeJSON(AlTreeParams alTreeParams,
			ArchivalLandscapeUtil archivalLandscapeUtil, List<ArchivalInstitution> archivalInstitutions,
			boolean parentSelected) throws APEnetException {
		StringBuilder buffer = new StringBuilder();
		buffer.append(START_ARRAY);
		for (int i = 0; i < archivalInstitutions.size(); i++) {
			boolean parentSelectedTemp = false;
			// It is necessary to build a JSON response to display all the
			// archival institutions in Navigated Tree Search
			ArchivalInstitution archivalInstitution = archivalInstitutions.get(i);
			if (archivalInstitution.isGroup()) {
				// The Archival Institution is a group and it has archival
				// institutions within it
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitution.getAiname(), archivalLandscapeUtil.getLocale());
				buffer.append(COMMA);
				buffer.append(NOT_CHECKBOX);
				buffer.append(COMMA);
				if (alTreeParams.existInExpandedNodes(AlType.ARCHIVAL_INSTITUTION, archivalInstitution.getAiId(),
						TreeType.GROUP)) {
					buffer.append(FOLDER_NOT_LAZY);
					buffer.append(COMMA);
					buffer.append(EXPANDED);
					buffer.append(COMMA);
					List<ArchivalInstitution> childArchivalInstitutions = archivalInstitutionDAO
							.getArchivalInstitutionsWithSearchableItems(null, archivalInstitution.getAiId());
					buffer.append(CHILDREN);
					buffer.append(generateArchivalInstitutionsTreeJSON(alTreeParams, archivalLandscapeUtil,
							childArchivalInstitutions, parentSelected));
				} else {
					buffer.append(FOLDER_LAZY);
				}
				buffer.append(COMMA);
				addKey(buffer, AlType.ARCHIVAL_INSTITUTION, archivalInstitution.getAiId(), TreeType.GROUP);
				buffer.append(END_ITEM);
			} else {
				// The Archival Institution is a leaf
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitution.getAiname(), archivalLandscapeUtil.getLocale());
				buffer.append(COMMA);
				if (parentSelected
						|| alTreeParams.existInSelectedNodes(AlType.ARCHIVAL_INSTITUTION,
								archivalInstitution.getAiId(), TreeType.GROUP)) {
					buffer.append(SELECTED);
					buffer.append(COMMA);
					parentSelectedTemp = true;
				}
				if (alTreeParams.existInExpandedNodes(AlType.ARCHIVAL_INSTITUTION, archivalInstitution.getAiId(),
						TreeType.GROUP)) {
					buffer.append(FOLDER_NOT_LAZY);
					buffer.append(COMMA);
					buffer.append(EXPANDED);
					buffer.append(COMMA);
					buffer.append(CHILDREN);
					buffer.append(generateEADFoldersTreeJSON(alTreeParams, archivalLandscapeUtil,
							archivalInstitution.getAiId(), parentSelectedTemp));
				} else {
					buffer.append(FOLDER_LAZY);
				}
				buffer.append(COMMA);
				addKey(buffer, AlType.ARCHIVAL_INSTITUTION, archivalInstitution.getAiId(), TreeType.LEAF);
				buffer.append(END_ITEM);
			}
			if (i != archivalInstitutions.size() - 1) {
				buffer.append(COMMA);
			}
		}

		buffer.append(END_ARRAY);
		return buffer;

	}

	private StringBuilder generateEADFoldersTreeJSON(AlTreeParams alTreeParams,
			ArchivalLandscapeUtil archivalLandscapeUtil, Integer parentAiId, boolean parentSelected) {
		StringBuilder buffer = new StringBuilder();
		Integer aiId = parentAiId;
		if (aiId == null) {
			aiId = AlType.getId(alTreeParams.getParentId());
		}
		buffer.append(START_ARRAY);
		boolean parentSelectedTemp = false;
		/*
		 * check if Holdings Guide exist
		 */
		HoldingsGuide holdingsGuideExample = new HoldingsGuide();
		holdingsGuideExample.setAiId(aiId);
		holdingsGuideExample.setSearchable(true);
		boolean holdingsGuideExists = eadDAO.existEads(holdingsGuideExample);
		/*
		 * check if Source Guide exist
		 */
		SourceGuide sourceGuideExample = new SourceGuide();
		sourceGuideExample.setAiId(aiId);
		sourceGuideExample.setSearchable(true);
		boolean sourceGuideExists = eadDAO.existEads(sourceGuideExample);
		boolean otherFindingAidExists = findingAidDAO.existFindingAidsNotLinkedByArchivalInstitution(aiId);
		if (holdingsGuideExists) {
			buffer.append(START_ITEM);
			addTitleFromKey(buffer, "text.holdings.guide.folder", archivalLandscapeUtil.getLocale());
			buffer.append(COMMA);
			if (parentSelected || alTreeParams.existInSelectedNodes(AlType.HOLDINGS_GUIDE, aiId, TreeType.GROUP)) {
				buffer.append(SELECTED);
				buffer.append(COMMA);
				parentSelectedTemp = true;
			}
			buffer.append(NOT_CHECKBOX);
			buffer.append(COMMA);
			addKey(buffer, AlType.HOLDINGS_GUIDE, aiId, TreeType.GROUP);
			buffer.append(COMMA);
			if (alTreeParams.existInExpandedNodes(AlType.HOLDINGS_GUIDE, aiId, TreeType.GROUP)) {
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(EXPANDED);
				buffer.append(COMMA);
				buffer.append(CHILDREN);
				buffer.append(generateEadFolderTreeJSON(alTreeParams, AlType.HOLDINGS_GUIDE, archivalLandscapeUtil,
						aiId, parentSelectedTemp));
			} else {
				buffer.append(FOLDER_LAZY);
			}
			buffer.append(END_ITEM);
			if (otherFindingAidExists || sourceGuideExists) {
				buffer.append(COMMA);
			}

		}
		if (sourceGuideExists) {
			buffer.append(START_ITEM);
			addTitleFromKey(buffer, "text.source.guide.folder", archivalLandscapeUtil.getLocale());
			buffer.append(COMMA);
			if (parentSelected || alTreeParams.existInSelectedNodes(AlType.SOURCE_GUIDE, aiId, TreeType.GROUP)) {
				buffer.append(SELECTED);
				buffer.append(COMMA);
				parentSelectedTemp = true;
			}
			buffer.append(NOT_CHECKBOX);
			buffer.append(COMMA);
			addKey(buffer, AlType.SOURCE_GUIDE, aiId, TreeType.GROUP);
			buffer.append(COMMA);
			if (alTreeParams.existInExpandedNodes(AlType.SOURCE_GUIDE, aiId, TreeType.GROUP)) {
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(EXPANDED);
				buffer.append(COMMA);
				buffer.append(CHILDREN);
				buffer.append(generateEadFolderTreeJSON(alTreeParams, AlType.SOURCE_GUIDE, archivalLandscapeUtil, aiId,
						parentSelectedTemp));
			} else {
				buffer.append(FOLDER_LAZY);
			}
			buffer.append(END_ITEM);
			if (otherFindingAidExists) {
				buffer.append(COMMA);
			}

		}
		// Adding the Other Finding Aid folder and children
		if (otherFindingAidExists) {
			buffer.append(START_ITEM);
			addTitleFromKey(buffer, "text.other.finding.aid.folder", archivalLandscapeUtil.getLocale());
			buffer.append(COMMA);
			if (parentSelected || alTreeParams.existInSelectedNodes(AlType.FINDING_AID, aiId, TreeType.GROUP)) {
				buffer.append(SELECTED);
				buffer.append(COMMA);
				parentSelectedTemp = true;
			}
			buffer.append(NOT_CHECKBOX);
			buffer.append(COMMA);
			addKey(buffer, AlType.FINDING_AID, aiId, TreeType.GROUP);
			buffer.append(COMMA);
			if (alTreeParams.existInExpandedNodes(AlType.FINDING_AID, aiId, TreeType.GROUP)) {
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(EXPANDED);
				buffer.append(COMMA);
				buffer.append(CHILDREN);
				// buffer.append(generateArchivalInstitutionsFAContentFolderTreeJSON(navigationTree,
				// ArchivalInstitutionUnit.getFindingAidsNotLinkedBySegment(key,
				// 0, MAX_NUMBER_OF_CLEVELS + 1),
				// key, MAX_NUMBER_OF_CLEVELS, selectedNodes, expandedNodes,
				// false, parentSelectedTemp));
			} else {
				buffer.append(FOLDER_LAZY);
			}
			buffer.append(END_ITEM);

		}
		buffer.append(END_ARRAY);
		return buffer;
	}

	// This method shows all the HG within Holdings Guide folder and the same
	// for Source guide

	private StringBuilder generateEadFolderTreeJSON(AlTreeParams alTreeParams, AlType alType,
			ArchivalLandscapeUtil archivalLandscapeUtil, Integer parentAiId, boolean parentSelected) {
		Locale locale = archivalLandscapeUtil.getLocale();
		Integer aiId = parentAiId;
		if (aiId == null) {
			aiId = AlType.getId(alTreeParams.getParentId());
		}

		Ead eadExample = null;
		if (AlType.HOLDINGS_GUIDE.equals(alType)) {
			eadExample = new HoldingsGuide();
		} else if (AlType.SOURCE_GUIDE.equals(alType)) {
			eadExample = new SourceGuide();
		}
		eadExample.setAiId(aiId);
		eadExample.setSearchable(true);
		List<Ead> eads = eadDAO.getEads(eadExample, 0, MAX_NUMBER_OF_CLEVELS);
		StringBuilder buffer = new StringBuilder();

		buffer.append(START_ARRAY);
		for (int i = 0; i < eads.size(); i++) {
			Ead ead = eads.get(i);
			buffer.append(START_ITEM);
			addTitle(buffer, ead.getTitle(), locale);
			buffer.append(COMMA);
			addAiId(buffer, aiId);
			buffer.append(COMMA);
			addKey(buffer, alType, ead.getId(), TreeType.LEAF);
			if (parentSelected || alTreeParams.existInSelectedNodes(alType, ead.getId(), TreeType.LEAF)) {
				buffer.append(COMMA);
				buffer.append(SELECTED);
			}
			buffer.append(COMMA);
			addPreviewId(buffer, ead.getId(), XmlType.getEadType(ead));
			if (alTreeParams.existInExpandedNodes(alType, ead.getId(), TreeType.LEAF)) {
				buffer.append(COMMA);
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(EXPANDED);
				buffer.append(COMMA);
				buffer.append(CHILDREN);
				// buffer.append(generateArchivalInstitutionsHGContentTreeJSON(navigationTree,
				// HoldingsGuideUnit
				// .getTopCLevels(holdingsGuideList.get(i).getHgId(), 0,
				// MAX_NUMBER_OF_CLEVELS + 1),
				// holdingsGuideList.get(i).getHgId(), MAX_NUMBER_OF_CLEVELS,
				// "hg", expandedNodes,
				// selectedNodes, false, parentSelected));
			} else {
				buffer.append(COMMA);
				buffer.append(FOLDER_LAZY);
			}
			buffer.append(END_ITEM);
			if (i < eads.size() - 1) {
				buffer.append(COMMA);
			}
		}

		buffer.append(END_ARRAY);
		return buffer;
	}

	private StringBuilder generateCLevelsJSON(AlTreeParams alTreeParams, AlType alType,
			ArchivalLandscapeUtil archivalLandscapeUtil, List<CLevel> clevels, String parentKey, String parentTreeType,
			Integer aiId, Integer start, boolean sibling, boolean parentSelected) {
		StringBuilder buffer = new StringBuilder();
		Integer numberOfCLevelsToShow = null;
		AlType parentAlType = AlType.getType(parentKey);
		Integer parentId = AlType.getId(parentKey);
		int i;
		if (!sibling) {
			buffer.append(START_ARRAY);
		}
		// Adding the top c-level children to Holdings Guide
		// We are going to use pagination, so only a maximum number of c_levels
		// is going to be displayed
		if (clevels.size() > 0) {

			if (clevels.size() > MAX_NUMBER_OF_CLEVELS) {
				numberOfCLevelsToShow = MAX_NUMBER_OF_CLEVELS;
			} else {
				numberOfCLevelsToShow = clevels.size();
			}

			for (i = 0; i <= numberOfCLevelsToShow - 1; i++) {
				CLevel clevel = clevels.get(i);
				buffer.append(START_ITEM);
				addTitle(buffer, clevel.getUnittitle(), archivalLandscapeUtil.getLocale());
				buffer.append(COMMA);
				if (clevel.isLeaf()) {
					// The node is a Finding Aid
					
					Integer faId = eadDAO.isEadidIndexed(clevel.getHrefEadid(), aiId, FindingAid.class);
					if (faId == null) {
						// The Finding Aid is not indexed, so it can not be
						// selectable
						buffer.append(NOT_CHECKBOX);
						buffer.append(COMMA);
						addPreviewCId(buffer, clevel.getClId());
					} else {
						addKey(buffer, AlType.FINDING_AID, faId, TreeType.LEAF);
						buffer.append(COMMA);
						addPreviewCId(buffer, clevel.getClId());
						if (parentSelected
								|| alTreeParams.existInSelectedNodes(AlType.C_LEVEL, clevel.getClId(), TreeType.LEAF)) {
							buffer.append(COMMA);
							buffer.append(SELECTED);
						}
					}

				} else {
					// The node is a group (series, subseries) within the
					// Holdings Guide
					addKey(buffer, AlType.C_LEVEL, clevel.getClId(), TreeType.GROUP);
					buffer.append(COMMA);
					addAiId(buffer, aiId);
					if (parentSelected
							|| alTreeParams.existInSelectedNodes(AlType.C_LEVEL, clevel.getClId(), TreeType.GROUP)) {
						buffer.append(COMMA);
						buffer.append(FOLDER_NOT_LAZY);
						buffer.append(COMMA);
						buffer.append(EXPANDED);
						buffer.append(COMMA);
						buffer.append(CHILDREN);
						// buffer.append(generateArchivalInstitutionsHGContentTreeJSON(navigationTree,
						// HoldingsGuideUnit
						// .getCLevels(clevels.get(i).getcLevelId(), 0,
						// MAX_NUMBER_OF_CLEVELS + 1), clevels
						// .get(i).getcLevelId().intValue(),
						// MAX_NUMBER_OF_CLEVELS, "hggroup", expandedNodes,
						// selectedNodes, false, parentSelected));
					} else {
						buffer.append(COMMA);
						buffer.append(FOLDER_LAZY);
					}
					buffer.append(COMMA);
					buffer.append(NOT_CHECKBOX);
					// buffer.append(COMMA);
					// buffer.append(NO_LINK);
				}
				buffer.append(END_ITEM);
				if (i < clevels.size() - 1) {
					buffer.append(COMMA);
				}
			}

			if (clevels.size() > MAX_NUMBER_OF_CLEVELS) {
				// if (expandedNodes != null
				// && expandedNodes.length > 0
				// && ArrayUtils.contains(expandedNodes,
				// "hggroupmore_" + parentId.toString() + "_" +
				// from.toString())) {
				if (alTreeParams.existInExpandedNodes(parentAlType, parentId, TreeType.GROUP, start)) {
					// buffer.append(generateArchivalInstitutionsHGContentTreeJSON(navigationTree,
					// HoldingsGuideUnit
					// .getCLevels(Long.parseLong(parentId.toString()), from,
					// MAX_NUMBER_OF_CLEVELS + 1),
					// parentId, from + MAX_NUMBER_OF_CLEVELS, "hggroupmore",
					// expandedNodes, selectedNodes, true,
					// parentSelected));
				} else {
					// There are more c_levels to show, so it is necessary to
					// display a More after... node
					buffer.append(START_ITEM);
					addMore(buffer, archivalLandscapeUtil.getLocale());
					buffer.append(COMMA);
					addKeyAndType(buffer, parentKey, parentTreeType);
					buffer.append(COMMA);
					addStart(buffer, start + MAX_NUMBER_OF_CLEVELS);
					buffer.append(COMMA);
					buffer.append(FOLDER_LAZY);
					buffer.append(COMMA);
					addAiId(buffer, aiId);
					buffer.append(COMMA);
					buffer.append(NOT_CHECKBOX);
					// buffer.append(COMMA);
					// buffer.append(NO_LINK);
					buffer.append(END_ITEM);
				}
			}

		}
		if (!sibling) {
			buffer.append(END_ARRAY);
		}
		return buffer;
	}

	// This method shows all the FA not linked within Other Finding Aid folder
	private StringBuilder generateArchivalInstitutionsFAContentFolderTreeJSON(NavigationTree navigationTree,
			List<FindingAidUnit> findingAidList, Integer parentId, Integer from, String[] selectedNodes,
			String[] expandedNodes, boolean sibling, boolean parentSelected) {
		Locale locale = navigationTree.getResourceBundleSource().getLocale();
		StringBuilder buffer = new StringBuilder();
		Integer numberOfCLevelsToShow = null;
		int i;
		if (!sibling) {
			buffer.append(START_ARRAY);
		}
		// Adding the Other Finding Aid children
		if (findingAidList.size() > 0) {

			if (findingAidList.size() > MAX_NUMBER_OF_CLEVELS) {
				numberOfCLevelsToShow = MAX_NUMBER_OF_CLEVELS;
			} else {
				numberOfCLevelsToShow = findingAidList.size();
			}

			for (i = 0; i <= numberOfCLevelsToShow - 1; i++) {
				buffer.append(START_ITEM);
				addTitle(buffer, findingAidList.get(i).getFaTitle(), locale);
				buffer.append(COMMA);
				addKey(buffer, findingAidList.get(i).getFaId(), "finding_aid");
				buffer.append(COMMA);
				if ((selectedNodes != null && selectedNodes.length > 0 && ArrayUtils.contains(selectedNodes, "fa_"
						+ findingAidList.get(i).getFaId()))
						|| parentSelected) {
					buffer.append(SELECTED);
					buffer.append(COMMA);
				}
				addPreviewId(buffer, findingAidList.get(i).getFaId(), XmlType.EAD_FA);
				buffer.append(END_ITEM);
				if (i < findingAidList.size() - 1) {
					buffer.append(COMMA);
				}
			}

			if (findingAidList.size() > MAX_NUMBER_OF_CLEVELS) {
				if (expandedNodes != null
						&& expandedNodes.length > 0
						&& ArrayUtils.contains(expandedNodes,
								"fafoldermore_" + parentId.toString() + "_" + from.toString())) {
					// buffer.append(COMMA);
					buffer.append(generateArchivalInstitutionsFAContentFolderTreeJSON(navigationTree,
							ArchivalInstitutionUnit.getFindingAidsNotLinkedBySegment(parentId, from,
									MAX_NUMBER_OF_CLEVELS + 1), parentId, from + MAX_NUMBER_OF_CLEVELS, selectedNodes,
							expandedNodes, true, parentSelected));
				} else {
					// There are more finding aids to show, so it is necessary
					// to display a More after... node
					buffer.append(START_ITEM);
					addMore(buffer, locale);
					buffer.append(COMMA);
					addKeyMore(buffer, parentId, from, "fa_folder_more");
					buffer.append(COMMA);
					buffer.append(FOLDER_LAZY);
					buffer.append(COMMA);
					buffer.append(NOT_CHECKBOX);
					// buffer.append(COMMA);
					// buffer.append(NO_LINK);
					buffer.append(END_ITEM);
				}
			}

		}
		if (!sibling) {
			buffer.append(END_ARRAY);
		}
		return buffer;
	}

	// This method shows all the HG within Holdings Guide folder
	private StringBuilder generateArchivalInstitutionsHGContentFolderTreeJSON(NavigationTree navigationTree,
			List<HoldingsGuideUnit> holdingsGuideList, String[] expandedNodes, String[] selectedNodes,
			boolean parentSelected) {
		Locale locale = navigationTree.getResourceBundleSource().getLocale();
		StringBuilder buffer = new StringBuilder();

		buffer.append(START_ARRAY);
		// Adding the first Holdings Guide children
		if (holdingsGuideList.size() > 0) {
			for (int i = 0; i < holdingsGuideList.size(); i++) {
				buffer.append(START_ITEM);
				addTitle(buffer, holdingsGuideList.get(i).getHgTitle(), locale);
				buffer.append(COMMA);
				addKey(buffer, holdingsGuideList.get(i).getHgId(), "holdings_guide");
				if ((selectedNodes != null && selectedNodes.length > 0 && ArrayUtils.contains(selectedNodes, "hg_"
						+ holdingsGuideList.get(i).getHgId()))
						|| parentSelected) {
					buffer.append(COMMA);
					buffer.append(SELECTED);
				}
				buffer.append(COMMA);
				addPreviewId(buffer, holdingsGuideList.get(i).getHgId(), XmlType.EAD_HG);
				if (expandedNodes != null && expandedNodes.length > 0
						&& ArrayUtils.contains(expandedNodes, "hg_" + holdingsGuideList.get(i).getHgId())) {
					buffer.append(COMMA);
					buffer.append(FOLDER_NOT_LAZY);
					buffer.append(COMMA);
					buffer.append(EXPANDED);
					buffer.append(COMMA);
					buffer.append(CHILDREN);
					buffer.append(generateArchivalInstitutionsHGContentTreeJSON(navigationTree, HoldingsGuideUnit
							.getTopCLevels(holdingsGuideList.get(i).getHgId(), 0, MAX_NUMBER_OF_CLEVELS + 1),
							holdingsGuideList.get(i).getHgId(), MAX_NUMBER_OF_CLEVELS, "hg", expandedNodes,
							selectedNodes, false, parentSelected));
				} else {
					buffer.append(COMMA);
					buffer.append(FOLDER_LAZY);
				}
				buffer.append(END_ITEM);
				if (i < holdingsGuideList.size() - 1) {
					buffer.append(COMMA);
				}
			}
		}

		buffer.append(END_ARRAY);
		return buffer;
	}

	private StringBuilder generateArchivalInstitutionsHGContentTreeJSON(NavigationTree navigationTree,
			List<CLevelUnit> topCLevels, Integer parentId, Integer from, String parentNodeType, String[] expandedNodes,
			String[] selectedNodes, boolean sibling, boolean parentSelected) {
		Locale locale = navigationTree.getResourceBundleSource().getLocale();
		StringBuilder buffer = new StringBuilder();
		Integer numberOfCLevelsToShow = null;
		int i;
		if (!sibling) {
			buffer.append(START_ARRAY);
		}
		// Adding the top c-level children to Holdings Guide
		// We are going to use pagination, so only a maximum number of c_levels
		// is going to be displayed
		if (topCLevels.size() > 0) {

			if (topCLevels.size() > MAX_NUMBER_OF_CLEVELS) {
				numberOfCLevelsToShow = MAX_NUMBER_OF_CLEVELS;
			} else {
				numberOfCLevelsToShow = topCLevels.size();
			}

			for (i = 0; i <= numberOfCLevelsToShow - 1; i++) {
				buffer.append(START_ITEM);
				addTitle(buffer, topCLevels.get(i).getUnittitle(), locale);
				buffer.append(COMMA);
				if (topCLevels.get(i).getIsLeaf()) {
					// The node is a Finding Aid
					addKey(buffer, topCLevels.get(i).getFaId(), "finding_aid");
					buffer.append(COMMA);
					if (!topCLevels.get(i).getIsFindingAidIndexed()) {
						// The Finding Aid is not indexed, so it can not be
						// selectable
						buffer.append(NOT_CHECKBOX);
						buffer.append(COMMA);
						addPreviewCId(buffer, topCLevels.get(i).getcLevelId());
					} else {
						addPreviewCId(buffer, topCLevels.get(i).getcLevelId());
						// addPreviewId(buffer, topCLevels.get(i).getFaId(),
						// XmlType.EAD_FA);
						if ((selectedNodes != null && selectedNodes.length > 0 && ArrayUtils.contains(selectedNodes,
								"fa_" + topCLevels.get(i).getFaId())) || parentSelected) {
							buffer.append(COMMA);
							buffer.append(SELECTED);
						}
					}

				} else {
					// The node is a group (series, subseries) within the
					// Holdings Guide
					addKey(buffer, topCLevels.get(i).getcLevelId(), "c_level_group");
					if (expandedNodes != null && expandedNodes.length > 0
							&& ArrayUtils.contains(expandedNodes, "hggroup_" + topCLevels.get(i).getcLevelId())) {
						buffer.append(COMMA);
						buffer.append(FOLDER_NOT_LAZY);
						buffer.append(COMMA);
						buffer.append(EXPANDED);
						buffer.append(COMMA);
						buffer.append(CHILDREN);
						buffer.append(generateArchivalInstitutionsHGContentTreeJSON(navigationTree, HoldingsGuideUnit
								.getCLevels(topCLevels.get(i).getcLevelId(), 0, MAX_NUMBER_OF_CLEVELS + 1), topCLevels
								.get(i).getcLevelId().intValue(), MAX_NUMBER_OF_CLEVELS, "hggroup", expandedNodes,
								selectedNodes, false, parentSelected));
					} else {
						buffer.append(COMMA);
						buffer.append(FOLDER_LAZY);
					}
					buffer.append(COMMA);
					buffer.append(NOT_CHECKBOX);
					// buffer.append(COMMA);
					// buffer.append(NO_LINK);
				}
				buffer.append(END_ITEM);
				if (i < topCLevels.size() - 1) {
					buffer.append(COMMA);
				}
			}

			if (topCLevels.size() > MAX_NUMBER_OF_CLEVELS) {
				if (expandedNodes != null
						&& expandedNodes.length > 0
						&& ArrayUtils.contains(expandedNodes,
								"hggroupmore_" + parentId.toString() + "_" + from.toString())) {
					buffer.append(generateArchivalInstitutionsHGContentTreeJSON(navigationTree, HoldingsGuideUnit
							.getCLevels(Long.parseLong(parentId.toString()), from, MAX_NUMBER_OF_CLEVELS + 1),
							parentId, from + MAX_NUMBER_OF_CLEVELS, "hggroupmore", expandedNodes, selectedNodes, true,
							parentSelected));
				} else {
					// There are more c_levels to show, so it is necessary to
					// display a More after... node
					buffer.append(START_ITEM);
					addMore(buffer, locale);
					buffer.append(COMMA);
					addStart(buffer, from + MAX_NUMBER_OF_CLEVELS);
					buffer.append(COMMA);
					buffer.append(FOLDER_LAZY);
					buffer.append(COMMA);
					buffer.append(NOT_CHECKBOX);
					// buffer.append(COMMA);
					// buffer.append(NO_LINK);
					buffer.append(END_ITEM);
				}
			}

		}
		if (!sibling) {
			buffer.append(END_ARRAY);
		}
		return buffer;
	}

	public void writeAiContentJSON(@RequestParam String nodeId, ResourceRequest resourceRequest,
			ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					resourceRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);
			String keyNode = this.getKeyNode(nodeId);

			writeToResponseAndClose(buildResponse(navigationTree, nodeId, keyNode), resourceResponse);

		} catch (Exception e) {
			log.error(e.getMessage(), e);

		}

		log.info("writeAiContentJSON: " + (System.currentTimeMillis() - startTime));
	}

	private StringBuilder buildResponse(NavigationTree navigationTree, String nodeId, String keyNode)
			throws NumberFormatException, IOException {
		StringBuilder tempString = new StringBuilder();
		if (nodeId.startsWith("fafolder_")) {
			// The node expanded is the Other Finding Aids folder (those FA
			// which don't have a link to any HG)
			tempString = generateArchivalInstitutionsFAContentFolderTreeJSON(navigationTree,
					ArchivalInstitutionUnit.getFindingAidsNotLinkedBySegment(Integer.parseInt(keyNode), 0,
							MAX_NUMBER_OF_CLEVELS + 1), Integer.parseInt(keyNode), MAX_NUMBER_OF_CLEVELS, null, null,
					false, false);
		} else if (nodeId.startsWith("hgfolder_")) {
			// The node expanded is the Holdings Guide folder
			tempString = generateArchivalInstitutionsHGContentFolderTreeJSON(navigationTree,
					ArchivalInstitutionUnit.getHoldingsGuide(Integer.parseInt(keyNode)), null, null, false);
		} else if (nodeId.startsWith("hg_")) {
			// The node expanded is a Holdings Guide
			tempString = generateArchivalInstitutionsHGContentTreeJSON(navigationTree,
					HoldingsGuideUnit.getTopCLevels(Integer.parseInt(keyNode), 0, MAX_NUMBER_OF_CLEVELS + 1),
					Integer.parseInt(keyNode), MAX_NUMBER_OF_CLEVELS, "hg", null, null, false, false);
		} else if (nodeId.startsWith("hggroup_")) {
			// The node expanded is group (series, subseries) within the
			// Holdings Guide
			tempString = generateArchivalInstitutionsHGContentTreeJSON(navigationTree,
					HoldingsGuideUnit.getCLevels(Long.parseLong(keyNode), 0, MAX_NUMBER_OF_CLEVELS + 1),
					Integer.parseInt(keyNode), MAX_NUMBER_OF_CLEVELS, "hggroup", null, null, false, false);
		} else if (nodeId.startsWith("hgmore_")) {
			// The node expanded is a Holdings Guide and it is needed to expand
			// the next results
			tempString = generateArchivalInstitutionsHGContentTreeJSON(
					navigationTree,
					HoldingsGuideUnit.getTopCLevels(Integer.parseInt(this.getParentId(nodeId)),
							Integer.parseInt(this.getFrom(nodeId)), MAX_NUMBER_OF_CLEVELS + 1),
					Integer.parseInt(this.getParentId(nodeId)), Integer.parseInt(this.getFrom(nodeId))
							+ MAX_NUMBER_OF_CLEVELS, "hgmore", null, null, false, false);
		} else if (nodeId.startsWith("hggroupmore_")) {
			// The node expanded is group (series, subseries) within the
			// Holdings Guide and it is needed to expand the next results
			tempString = generateArchivalInstitutionsHGContentTreeJSON(
					navigationTree,
					HoldingsGuideUnit.getCLevels(Long.parseLong(this.getParentId(nodeId)),
							Integer.parseInt(this.getFrom(nodeId)), MAX_NUMBER_OF_CLEVELS + 1),
					Integer.parseInt(this.getParentId(nodeId)), Integer.parseInt(this.getFrom(nodeId))
							+ MAX_NUMBER_OF_CLEVELS, "hggroupmore", null, null, false, false);
		} else if (nodeId.startsWith("fafoldermore_")) {
			// The node expanded is the Other Finding Aids folder (those FA
			// which don't have a link to any HG) and it is needed to expand the
			// next results
			tempString = generateArchivalInstitutionsFAContentFolderTreeJSON(navigationTree,
					ArchivalInstitutionUnit.getFindingAidsNotLinkedBySegment(
							Integer.parseInt(this.getParentId(nodeId)), Integer.parseInt(this.getFrom(nodeId)),
							MAX_NUMBER_OF_CLEVELS + 1), Integer.parseInt(this.getParentId(nodeId)),
					Integer.parseInt(this.getFrom(nodeId)) + MAX_NUMBER_OF_CLEVELS, null, null, false, false);
		}
		return tempString;
	}

	private void addTitle(StringBuilder buffer, String title, Locale locale) {
		addTitle(null, buffer, title, locale);
	}

	private void addAiId(StringBuilder buffer, Integer aiId) {
		buffer.append("\"aiId\":\"" + aiId + "\"");
	}

	private void addTitleFromKey(StringBuilder buffer, String titleKey, Locale locale) {
		addTitle(null, buffer, this.getMessageSource().getMessage(titleKey, null, locale), locale);
	}

	private static void addKey(StringBuilder buffer, AlType alType, Number id, TreeType treeType) {
		buffer.append("\"key\":\"" + AlType.getKey(alType, id) + "\"");
		buffer.append(COMMA);
		buffer.append("\"type\":\"" + treeType + "\"");

	}

	private static void addKeyAndType(StringBuilder buffer, String key, String type) {
		buffer.append("\"key\":\"" + key + "\"");
		buffer.append(COMMA);
		buffer.append("\"type\":\"" + type + "\"");

	}

	private static void addKey(StringBuilder buffer, Object key, String nodeType) {

		if (nodeType.equals("country")) {
			buffer.append("\"key\":" + "\"country_" + key.toString() + "\"");
		} else if (nodeType.equals("archival_institution_group")) {
			buffer.append("\"key\":" + "\"aigroup_" + key.toString() + "\"");
		} else if (nodeType.equals("archival_institution_content")) {
			buffer.append("\"key\":" + "\"aicontent_" + key.toString() + "\"");
		} else if (nodeType.equals("archival_institution_no_content")) {
			buffer.append("\"key\":" + "\"ainocontent_" + key.toString() + "\"");
		} else if (nodeType.equals("hg_folder")) {
			buffer.append("\"key\":" + "\"hgfolder_" + key.toString() + "\"");
		} else if (nodeType.equals("holdings_guide")) {
			buffer.append("\"key\":" + "\"hg_" + key.toString() + "\"");
		} else if (nodeType.equals("fa_folder")) {
			buffer.append("\"key\":" + "\"fafolder_" + key.toString() + "\"");
		} else if (nodeType.equals("finding_aid")) {
			if (key == null) {
				buffer.append("\"key\":" + "\"fa_" + "notUploaded" + "\"");
			} else {
				buffer.append("\"key\":" + "\"fa_" + key.toString() + "\"");

			}
		} else if (nodeType.equals("c_level_group")) {
			buffer.append("\"key\":" + "\"hggroup_" + key.toString() + "\"");
		}

	}

	private static void addKeyMore(StringBuilder buffer, Integer parentId, Integer from, String nodeType) {
		if (nodeType.equals("hg_group_more")) {
			buffer.append("\"key\":" + "\"hggroupmore_" + parentId.toString() + "_" + from.toString() + "\"");
		} else if (nodeType.equals("hg_more")) {
			buffer.append("\"key\":" + "\"hgmore_" + parentId.toString() + "_" + from.toString() + "\"");
		} else if (nodeType.equals("fa_folder_more")) {
			buffer.append("\"key\":" + "\"fafoldermore_" + parentId.toString() + "_" + from.toString() + "\"");
		}

	}

	private void addPreviewId(StringBuilder buffer, Integer id, XmlType xmlType) {
		buffer.append("\"previewId\": \"" + xmlType.getSolrPrefix() + id + "\"");
	}

	private void addPreviewCId(StringBuilder buffer, Long id) {
		buffer.append("\"previewId\": \"C" + id + "\"");
	}

	// This method returns the Key for a specific node
	private String getKeyNode(String nodeId) {

		return nodeId.substring(nodeId.lastIndexOf('_') + 1);
	}

	// This method extracts the c-level parentId
	// This method is only used when a more node is clicked within the navigated
	// search
	private String getParentId(String nodeId) {

		// The whole key for a More after... node within a Holdings Guide
		// is hgfoldermore_parentId_fromwherestartthequery
		String nodeWithoutType = nodeId.substring(nodeId.indexOf('_') + 1);
		return nodeWithoutType.substring(0, nodeWithoutType.indexOf('_'));
	}

	// This method extracts from where the query is going to start offering the
	// results
	// This method is only used when a more node is clicked within the navigated
	// search
	private String getFrom(String nodeId) {

		// The whole key for a More after... node within a Holdings Guide
		// is hgfoldermore_parentId_fromwherestartthequery
		return nodeId.substring(nodeId.lastIndexOf('_') + 1);
	}
}
