package eu.archivesportaleurope.portal.directory;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.infraestructure.ArchivalInstitutionUnit;
import eu.apenet.commons.infraestructure.CountryUnit;
import eu.apenet.commons.infraestructure.NavigationTree;
import eu.apenet.persistence.factory.DAOFactory;
import eu.apenet.persistence.vo.ArchivalInstitution;
import eu.apenet.persistence.vo.Coordinates;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;

/**
 * JSON Writer for the directory tree
 * 
 * @author bastiaan
 * 
 */
@Controller(value = "directoryJSONWriter")
@RequestMapping(value = "VIEW")
public class DirectoryJSONWriter extends AbstractJSONWriter {
	private static final String FOLDER_LAZY = "\"isFolder\": true, \"isLazy\": true";
	private static final String FOLDER_NOT_LAZY = "\"isFolder\": true";
	private static final String NO_LINK = "\"noLink\": true";

	@ResourceMapping(value = "directoryTree")
	public void writeCountriesJSON(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					resourceRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);
			List<CountryUnit> countryList = navigationTree.getALCountriesWithArchivalInstitutionsWithEAG();

			Collections.sort(countryList);
			writeToResponseAndClose(generateDirectoryJSON(navigationTree, countryList), resourceResponse);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}

	private StringBuilder generateCountriesTreeJSON(NavigationTree navigationTree, List<CountryUnit> countryList) {
		CountryUnit countryUnit = null;
		StringBuilder builder = new StringBuilder();
		builder.append(START_ARRAY);
		for (int i = 0; i < countryList.size(); i++) {
			// It is necessary to build a JSON response to display all the
			// countries in Directory Tree
			countryUnit = countryList.get(i);
			builder.append(START_ITEM);
			addTitle(builder, countryUnit.getLocalizedName(), navigationTree.getResourceBundleSource().getLocale());
			builder.append(COMMA);
			builder.append(FOLDER_LAZY);
			builder.append(COMMA);
			addKey(builder, countryUnit.getCountry().getId(), "country");
			addGoogleMapsAddress(builder, countryUnit.getCountry().getCname());
			addCountryCode(builder, countryUnit.getCountry().getIsoname());
			builder.append(END_ITEM);
			if (i != countryList.size() - 1) {
				builder.append(COMMA);
			}
		}

		builder.append(END_ARRAY);
		countryUnit = null;
		return builder;

	}

	private StringBuilder generateDirectoryJSON(NavigationTree navigationTree, List<CountryUnit> countryList) {
		StringBuilder builder = new StringBuilder();
		builder.append(START_ARRAY);
		builder.append(START_ITEM);
		addTitle(builder, navigationTree.getResourceBundleSource().getString("directory.text.directory"),
				navigationTree.getResourceBundleSource().getLocale());
		addGoogleMapsAddress(builder, "Europe");
		builder.append(COMMA);
		addExpand(builder);
		builder.append(COMMA);
		builder.append(FOLDER_WITH_CHILDREN);
		builder.append(generateCountriesTreeJSON(navigationTree, countryList));
		builder.append(END_ITEM);
		builder.append(END_ARRAY);
		return builder;
	}

	@ResourceMapping(value = "directoryTreeAi")
	public void writeAiJSON(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		String nodeId = resourceRequest.getParameter("nodeId");
		String countryCode = resourceRequest.getParameter("countryCode");
		try {
			if (StringUtils.isBlank(nodeId) || StringUtils.isBlank(countryCode)) {
				StringBuilder builder = new StringBuilder();
				builder.append(START_ARRAY);
				builder.append(END_ARRAY);
				writeToResponseAndClose(builder, resourceResponse);
			} else {
				SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
						resourceRequest.getLocale());
				NavigationTree navigationTree = new NavigationTree(source);
				List<ArchivalInstitutionUnit> archivalInstitutionList = navigationTree
						.getArchivalInstitutionsByParentAiId(nodeId);

				// This filter has been added to display only those final
				// archival institutions or groups which have eag files uploaded
				// to the System
				// Remove it if the user wants to display again all the
				// institutions even if they doesn't eag files uploaded
				archivalInstitutionList = navigationTree.filterArchivalInstitutionsWithEAG(archivalInstitutionList);

				Collections.sort(archivalInstitutionList);
				writeToResponseAndClose(
						generateArchivalInstitutionsTreeJSON(navigationTree, archivalInstitutionList, countryCode),
						resourceResponse);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

	}

	private StringBuilder generateArchivalInstitutionsTreeJSON(NavigationTree navigationTree,
			List<ArchivalInstitutionUnit> archivalInstitutionList, String countryCode) {
		Locale locale = navigationTree.getResourceBundleSource().getLocale();
		StringBuilder buffer = new StringBuilder();
		ArchivalInstitutionUnit archivalInstitutionUnit = null;

		buffer.append(START_ARRAY);
		for (int i = 0; i < archivalInstitutionList.size(); i++) {
			// It is necessary to build a JSON response to display all the
			// archival institutions in Directory Tree
			archivalInstitutionUnit = archivalInstitutionList.get(i);
			if (archivalInstitutionUnit.getIsgroup() && archivalInstitutionUnit.isHasArchivalInstitutions()) {
				// The Archival Institution is a group and it has archival
				// institutions within it
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				buffer.append(FOLDER_LAZY);
				// buffer.append(COMMA);
				// buffer.append(NO_LINK);
				buffer.append(COMMA);
				addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_group");
				addCountryCode(buffer, countryCode);
				buffer.append(END_ITEM);
			} else if (archivalInstitutionUnit.getIsgroup() && !archivalInstitutionUnit.isHasArchivalInstitutions()) {
				// The Archival Institution is a group but it doesn't have any
				// archival institutions within it
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(NO_LINK);
				buffer.append(COMMA);
				addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_group");
				addCountryCode(buffer, countryCode);
				buffer.append(END_ITEM);
			} else if (!archivalInstitutionUnit.getIsgroup()) {
				// The Archival Institution is a leaf
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				if (archivalInstitutionUnit.getPathEAG() != null && !archivalInstitutionUnit.getPathEAG().equals("")) {
					// The archival institution has EAG
					addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_eag");
				} else {
					addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_no_eag");
					buffer.append(COMMA);
					buffer.append(NO_LINK);
				}
				addCountryCode(buffer, countryCode);
				buffer.append(END_ITEM);
			}
			if (i != archivalInstitutionList.size() - 1) {
				buffer.append(COMMA);
			}
		}

		buffer.append(END_ARRAY);
		archivalInstitutionUnit = null;
		return buffer;

	}

	private void addTitle(StringBuilder buffer, String title, Locale locale) {
		addTitle(null, buffer, title, locale);
	}

	private static void addGoogleMapsAddress(StringBuilder buffer, String address) {
		buffer.append(COMMA);
		buffer.append("\"googleMapsAddress\":\"" + address + "\"");
	}

	private static void addCountryCode(StringBuilder buffer, String countryCode) {
		buffer.append(COMMA);
		buffer.append("\"countryCode\":\"" + countryCode + "\"");
	}

	private static void addKey(StringBuilder buffer, Number key, String nodeType) {

		if (nodeType.equals("country")) {
			buffer.append("\"key\":" + "\"country_" + key + "\"");
		} else if (nodeType.equals("archival_institution_group")) {
			buffer.append("\"key\":" + "\"aigroup_" + key + "\"");
		} else if (nodeType.equals("archival_institution_eag")) {
			buffer.append("\"aiId\":");
			buffer.append(" \"" + key);
			buffer.append("\" ");
			buffer.append(COMMA);
			buffer.append("\"key\":" + "\"aieag_" + key + "\"");

		} else if (nodeType.equals("archival_institution_no_eag")) {
			buffer.append("\"key\":" + "\"ainoeag_" + key + "\"");
		}
	}
	
	@ResourceMapping(value = "directoryTreeGMaps")
	public void displayGmaps(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
					resourceRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);

			String countryCode = resourceRequest.getParameter("countryCode");
			String institutionID = resourceRequest.getParameter("institutionID");
			
			//parse bad params (if jquery parse a null to "null", it has to be reconverted
			countryCode = (countryCode!=null && countryCode.equalsIgnoreCase("null"))?null:countryCode;
			institutionID = (institutionID!=null && institutionID.equalsIgnoreCase("null"))?null:institutionID;
			
			List<Coordinates> reposList = null;
			if (institutionID != null && !institutionID.isEmpty()) {
				ArchivalInstitution archivalInstitution = DAOFactory.instance().getArchivalInstitutionDAO().findById(Integer.parseInt(institutionID));
				reposList = DAOFactory.instance().getCoordinatesDAO().findCoordinatesByArchivalInstitution(archivalInstitution);
			} else if (countryCode != null && !countryCode.isEmpty()) {
				reposList = DAOFactory.instance().getCoordinatesDAO().getCoordinatesByCountryCode(countryCode);
			} else {
				reposList = DAOFactory.instance().getCoordinatesDAO().getCoordinates();
			}
			//Collections.sort(reposList);
			writeToResponseAndClose(generateGmapsJSON(navigationTree, reposList), resourceResponse);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}
	
	private StringBuilder generateGmapsJSON(NavigationTree navigationTree, List<Coordinates> repoList) {
		StringBuilder builder = new StringBuilder();
		builder.append(START_ITEM);
		builder.append("\"count\":" + repoList.size());
		builder.append(COMMA);
		builder.append("\"repos\":");
		
		builder.append(START_ARRAY);
		if(repoList!=null){
			Iterator<Coordinates> itRepoList = repoList.iterator();
			while(itRepoList.hasNext()){
				Coordinates repo = itRepoList.next();
				builder.append(buildNode(repo));
				if(itRepoList.hasNext()){
					builder.append(COMMA);
				}
			}
		}
		builder.append(END_ARRAY);
		
		builder.append(END_ITEM);
		return builder;
	}
	
	private StringBuilder buildNode(Coordinates repo){
		StringBuilder builder = new StringBuilder();
		builder.append(START_ITEM);
		builder.append("\"latitude\":\""+repo.getLat()+"\"");
		builder.append(COMMA);
		builder.append("\"longitude\":\""+repo.getLon()+"\"");
		builder.append(COMMA);
		builder.append("\"name\":\""+repo.getNameInstitution()+"\"");
		builder.append(END_ITEM);
		return builder;
	}

}
