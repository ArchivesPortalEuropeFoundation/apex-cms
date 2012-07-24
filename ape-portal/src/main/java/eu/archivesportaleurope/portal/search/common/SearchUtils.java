package eu.archivesportaleurope.portal.search.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import eu.apenet.commons.infraestructure.ArchivalInstitutionUnit;
import eu.apenet.commons.infraestructure.FindingAidUnit;
import eu.apenet.commons.infraestructure.HoldingsGuideUnit;
import eu.apenet.persistence.vo.ArchivalInstitution;
 
public class SearchUtils {

	// This method builds the lists findingAidsSelectedForSearchId, holdingsGuideSelectedForSearchId,
	// countriesSelectedForSearchId and archivalInstitutionsSelectedForSearchId from navigationTreeNodesSelected 
	// (nodes selected in the Navigation Tree for filtering the search)
	public static void fillSelectedForSearchIdLists(List<String> navigationTreeNodesSelected, List<String> countriesSelectedForSearchId, List<String> archivalInstitutionsSelectedForSearchId, List<String> holdingsGuideSelectedForSearchId, List<String> findingAidsSelectedForSearchId) {
		
		String nodeId = null;
		String keyNode = null;
		List<HoldingsGuideUnit> holdingsGuideIndexed = new ArrayList<HoldingsGuideUnit>();
		List<FindingAidUnit> findingAidsIndexed = new ArrayList<FindingAidUnit>();
		List<ArchivalInstitution> finalArchivalInstitutionList = new ArrayList<ArchivalInstitution>();
		
		for (int i = 0; i < navigationTreeNodesSelected.size(); i ++) {
			nodeId = navigationTreeNodesSelected.get(i).trim();
			keyNode = nodeId.substring(nodeId.lastIndexOf('_') + 1);

			if (nodeId.startsWith("country_")){
				// The node is a country 
				countriesSelectedForSearchId.add(keyNode);
			}
			else if (nodeId.startsWith("aigroup_")){
				// The node is group of archival institutions
				// It is necessary to get all the final archival institutions which belongs to this group
				ArchivalInstitutionUnit.retrieveFinalArchivalInstitutions(finalArchivalInstitutionList, Integer.valueOf(keyNode), true);
				
				for (int j = 0; j < finalArchivalInstitutionList.size(); j ++) {
					archivalInstitutionsSelectedForSearchId.add(String.valueOf(finalArchivalInstitutionList.get(j).getAiId()));
				}
			}			
			else if (nodeId.startsWith("aicontent_")){
				// The node is an archival institution
				archivalInstitutionsSelectedForSearchId.add(keyNode);
			}
			else if (nodeId.startsWith("fa_")){
				// The node is a finding aid
				findingAidsSelectedForSearchId.add(keyNode);
			}
			else if (nodeId.startsWith("hg_")){
				// The node is a holdings guide
				holdingsGuideSelectedForSearchId.add(keyNode);
			}
			else if (nodeId.startsWith("hgfolder_")){
				// The node is the folder which includes all the holdings guide for an archival institution
				// First, it is necessary to retrieve all the holdings guide indexed for an archival institution
				holdingsGuideIndexed = ArchivalInstitutionUnit.getHoldingsGuide(Integer.parseInt(keyNode));
				
				for (int j = 0; j < holdingsGuideIndexed.size(); j ++) {
					holdingsGuideSelectedForSearchId.add(holdingsGuideIndexed.get(j).getHgId().toString());	
				}
				
			}
			else if (nodeId.startsWith("fafolder_")){
				// The node is the folder which includes all the finding aids which don't belong to any holdings guide for an archival institution
				// First, it is necessary to retrieve all the finding aids indexed which don't belong to any holdings guide for an archival institution
				findingAidsIndexed = ArchivalInstitutionUnit.getFindingAidsNotLinked(Integer.parseInt(keyNode));
				
				for (int k = 0; k < findingAidsIndexed.size(); k ++) {
					findingAidsSelectedForSearchId.add(findingAidsIndexed.get(k).getFaId().toString());	
				}
			}

		}
		
		nodeId = null;
		keyNode = null;
		holdingsGuideIndexed = null;
		findingAidsIndexed = null;
		finalArchivalInstitutionList = null;
		
	}
	public static String getLanguage(HttpServletRequest request) {
		String navTreeLang = "";
		HttpSession session = request.getSession();
		Locale locale = request.getLocale();
		if(session.getAttribute("WW_TRANS_I18N_LOCALE")==null){
			if (!locale.getLanguage().startsWith("de") && !locale.getLanguage().startsWith("en") && !locale.getLanguage().startsWith("es") && !locale.getLanguage().startsWith("el") && !locale.getLanguage().startsWith("fr") && !locale.getLanguage().startsWith("ga") && !locale.getLanguage().startsWith("lv") && !locale.getLanguage().startsWith("mt") && !locale.getLanguage().startsWith("nl") && !locale.getLanguage().startsWith("pl") && !locale.getLanguage().startsWith("pt") && !locale.getLanguage().startsWith("sl") && !locale.getLanguage().startsWith("fi") && !locale.getLanguage().startsWith("sv")) {
				navTreeLang = "en";  						
			}
			else {
				navTreeLang = locale.getLanguage().substring(0,2); 				
			}
		}
		else {
			if (!session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("de") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("es") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("el") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("fr") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("ga") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("lv") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("mt") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("nl") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("pl") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("pt") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("sl") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("fi") && !session.getAttribute("WW_TRANS_I18N_LOCALE").toString().startsWith("sv")) {
				navTreeLang = "en";					
			}
			else {
				navTreeLang =  session.getAttribute("WW_TRANS_I18N_LOCALE").toString().substring(0,2);							
			}
		} 
		return navTreeLang;
	}
	
}