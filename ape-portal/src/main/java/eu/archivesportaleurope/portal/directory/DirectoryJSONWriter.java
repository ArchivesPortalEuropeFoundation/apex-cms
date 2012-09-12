package eu.archivesportaleurope.portal.directory;



import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.apenet.commons.infraestructure.ArchivalInstitutionUnit;
import eu.apenet.commons.infraestructure.CountryUnit;
import eu.apenet.commons.infraestructure.NavigationTree;
import eu.apenet.commons.xslt.tags.AbstractEadTag;
import eu.apenet.persistence.vo.EadContent;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;
import eu.archivesportaleurope.portal.ead.EadTreeParams;

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


	@ResourceMapping(value="directoryTree")
	public void writeCountriesJSON(ResourceRequest resourceRequest,  ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(), resourceRequest.getLocale());
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
		for (int i=0; i < countryList.size(); i++) {
			//It is necessary to build a JSON response to display all the countries in Directory Tree
			countryUnit = countryList.get(i);
			builder.append(START_ITEM);
			addTitle(builder, countryUnit.getLocalizedName(), navigationTree.getResourceBundleSource().getLocale());
			builder.append(COMMA);
			builder.append(FOLDER_LAZY);
			builder.append(COMMA);
			addKey(builder, countryUnit.getCountry().getId(), "country");
			addGoogleMapsAddress(builder,countryUnit.getCountry().getCname());
			addCountryCode(builder,countryUnit.getCountry().getIsoname());
			builder.append(END_ITEM);
			if (i!=countryList.size()-1){
				builder.append(COMMA);
			}
		}
		
		builder.append(END_ARRAY);
		countryUnit = null;
		return builder;

	}
	private StringBuilder generateDirectoryJSON(NavigationTree navigationTree, List<CountryUnit> countryList) {
		StringBuilder builder= new StringBuilder();
		builder.append(START_ARRAY);
		builder.append(START_ITEM);
		addTitle(builder, navigationTree.getResourceBundleSource().getString("directory.text.directory"), navigationTree.getResourceBundleSource().getLocale());
		addGoogleMapsAddress(builder,"Europe");
		builder.append(COMMA);
		addExpand(builder);
		builder.append(COMMA);
		builder.append(FOLDER_WITH_CHILDREN);
		builder.append(generateCountriesTreeJSON( navigationTree,  countryList));
		builder.append(END_ITEM);
		builder.append(END_ARRAY);
		return builder;
	}
	
	@ResourceMapping(value="directoryTreeAi")
	public void writeAiJSON(@RequestParam String nodeId,  @RequestParam String countryCode,ResourceRequest resourceRequest,  ResourceResponse resourceResponse) {
		
		try {
			SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(), resourceRequest.getLocale());
			NavigationTree navigationTree = new NavigationTree(source);
			List<ArchivalInstitutionUnit> archivalInstitutionList = navigationTree.getArchivalInstitutionsByParentAiId(nodeId);
			
			// This filter has been added to display only those final archival institutions or groups which have eag files uploaded to the System
			// Remove it if the user wants to display again all the institutions even if they doesn't eag files uploaded
			archivalInstitutionList = navigationTree.filterArchivalInstitutionsWithEAG(archivalInstitutionList);

			Collections.sort(archivalInstitutionList);
			writeToResponseAndClose(generateArchivalInstitutionsTreeJSON(navigationTree, archivalInstitutionList, countryCode), resourceResponse);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
				
	}	
	private StringBuilder generateArchivalInstitutionsTreeJSON(NavigationTree navigationTree, List<ArchivalInstitutionUnit> archivalInstitutionList, String countryCode) {
		Locale locale = navigationTree.getResourceBundleSource().getLocale();
		StringBuilder buffer = new StringBuilder();
		ArchivalInstitutionUnit archivalInstitutionUnit = null;
		
		buffer.append(START_ARRAY);
		for (int i=0; i < archivalInstitutionList.size(); i++) {
			//It is necessary to build a JSON response to display all the archival institutions in Directory Tree
			archivalInstitutionUnit = archivalInstitutionList.get(i);
			if (archivalInstitutionUnit.getIsgroup() && archivalInstitutionUnit.isHasArchivalInstitutions()){
				//The Archival Institution is a group and it has archival institutions within it
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				buffer.append(FOLDER_LAZY);
				//buffer.append(COMMA);
				//buffer.append(NO_LINK);
				buffer.append(COMMA);
				addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_group");
				addCountryCode(buffer,countryCode);
				buffer.append(END_ITEM);
			}
			else if (archivalInstitutionUnit.getIsgroup() && archivalInstitutionUnit.isHasArchivalInstitutions()) {
				//The Archival Institution is a group but it doesn't have any archival institutions within it
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				buffer.append(FOLDER_NOT_LAZY);
				buffer.append(COMMA);
				buffer.append(NO_LINK);
				buffer.append(COMMA);
				addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_group");
				addCountryCode(buffer,countryCode);
				buffer.append(END_ITEM);
			}
			else if (!archivalInstitutionUnit.getIsgroup()){
				//The Archival Institution is a leaf
				buffer.append(START_ITEM);
				addTitle(buffer, archivalInstitutionUnit.getAiname(), locale);
				buffer.append(COMMA);
				if (archivalInstitutionUnit.getPathEAG() != null && !archivalInstitutionUnit.getPathEAG().equals("")) {
					//The archival institution has EAG
					addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_eag");
				}
				else {
					addKey(buffer, archivalInstitutionUnit.getAiId(), "archival_institution_no_eag");
					buffer.append(COMMA);
					buffer.append(NO_LINK);					
				}
				addCountryCode(buffer,countryCode);		
				buffer.append(END_ITEM);				
			}
			if (i!=archivalInstitutionList.size()-1){
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
		}
		else if (nodeType.equals("archival_institution_group")) {
			buffer.append("\"key\":" + "\"aigroup_" + key + "\"");			
		}
		else if (nodeType.equals("archival_institution_eag")) {
			buffer.append("\"aiId\":");
			buffer.append(" \"" + key);
			buffer.append("\" ");
			buffer.append(COMMA);
			buffer.append("\"key\":" + "\"aieag_" + key + "\"");

		}
		else if (nodeType.equals("archival_institution_no_eag")) {
			buffer.append("\"key\":" + "\"ainoeag_" + key + "\"");			
		}		
	}

}
