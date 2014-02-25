package eu.archivesportaleurope.portal.common;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.apenet.commons.solr.SolrFields;
import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.TreeType;
import eu.archivesportaleurope.portal.search.advanced.AdvancedSearch;
import eu.archivesportaleurope.portal.search.advanced.tree.TreeAdvancedSearch;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;

public final class AnalyzeLogger {
	private static final String OPTIONAL = "optional";
	private static final String HIERARCHY = "hierarchy";
	private static final String TRUE = "true";
    private static final Logger SIMPLE_SEARCH_ANALYZE_LOGGER = Logger.getLogger("SIMPLE_SEARCH_ANALYZE");
    private static final Logger ADVANCED_SEARCH_ANALYZE_LOGGER = Logger.getLogger("ADVANCED_SEARCH_ANALYZE");
    private static final Logger ADVANCED_SEARCH_LIST_LOGGER = Logger.getLogger("ADVANCED_SEARCH_LIST_ANALYZE");
    private static final Logger ADVANCED_SEARCH_CONTEXT_LOGGER = Logger.getLogger("ADVANCED_SEARCH_CONTEXT_ANALYZE");
    private static final Logger PREVIEW_SECOND_DISPLAY_ANALYZE_LOGGER = Logger.getLogger("PREVIEW_SECOND_DISPLAY_ANALYZE");
    private static final Logger AL_ANALYZE_LOGGER = Logger.getLogger("AL_ANALYZE");
    
    public static void logAlTree(AlType parentType, TreeType treeType, Integer start){
    	if (AL_ANALYZE_LOGGER.isDebugEnabled()){
    		String logLine ="";
    		if(AlType.COUNTRY.equals(parentType)){
    			logLine +="co;";
    		}else if(AlType.ARCHIVAL_INSTITUTION.equals(parentType)){
    			logLine +="a;";
    		}else if(AlType.HOLDINGS_GUIDE.equals(parentType)){
    			logLine +="h;";
    		}else if(AlType.SOURCE_GUIDE.equals(parentType)){
    			logLine +="s;";
    		}else if(AlType.FINDING_AID.equals(parentType)){
    			logLine +="f;";
    		}else if(AlType.C_LEVEL.equals(parentType)){
    			logLine +="cl;";
    		}
    		logLine += start;
    		AL_ANALYZE_LOGGER.debug(logLine);
    	}
    }
    public static void logAdvancedSearch(String userId, AdvancedSearch advancedSearch, SolrQueryParameters solrQueryParameters){
    	if (ADVANCED_SEARCH_ANALYZE_LOGGER.isDebugEnabled()){
    		String logLine = "";
    		if (StringUtils.isBlank(userId)){
    			logLine +="none;";
    		}else {
    			logLine +=userId +";";
    		}
    		if (HIERARCHY.equals(advancedSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(advancedSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   
    		if (TRUE.equals(advancedSearch.getMethod())) {
    			logLine +="or;";
    		}else {
    			logLine +=";";
    		} 
    		if ("0".equals(advancedSearch.getElement())){
    			logLine +=";";
    		}else {
    			logLine += advancedSearch.getElement() +";";
    		}
    		logLine += advancedSearch.getTypedocument() +";";
    		if (StringUtils.isBlank(advancedSearch.getFromdate())) {
    			logLine +=";";
    		}else {
    			logLine +="fd;";
    		} 
    		if (StringUtils.isBlank(advancedSearch.getTodate())) {
    			logLine +=";";
    		}else {
    			logLine +="td;";
    		} 
    		if (StringUtils.isBlank(advancedSearch.getExactDateSearch())) {
    			logLine +=";";
    		}else {
    			logLine +="ed;";
    		} 
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.COUNTRY_ID)) + ";";
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.AI_ID)) + ";";
    		logLine +=countList(solrQueryParameters.getOrParameters().get( SolrFields.FOND_ID)) + ";";
    		logLine +="\""+ advancedSearch.getTerm() + "\"";
    		ADVANCED_SEARCH_ANALYZE_LOGGER.debug(logLine);
    	}    	
    }
    public static void logUpdateAdvancedSearchContext(String userId,TreeAdvancedSearch advancedSearch, SolrQueryParameters solrQueryParameters){
    	if (ADVANCED_SEARCH_CONTEXT_LOGGER.isDebugEnabled()){
    		String logLine = "";
    		if (StringUtils.isBlank(userId)){
    			logLine +="none;";
    		}else {
    			logLine +=userId +";";
    		}
    		if (HIERARCHY.equals(advancedSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(advancedSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   
    		if (TRUE.equals(advancedSearch.getMethod())) {
    			logLine +="or;";
    		}else {
    			logLine +=";";
    		} 
    		if ("0".equals(advancedSearch.getElement())){
    			logLine +=";";
    		}else {
    			logLine += advancedSearch.getElement() +";";
    		}
    		logLine += advancedSearch.getTypedocument() +";";
    		if (StringUtils.isBlank(advancedSearch.getFromdate())) {
    			logLine +=";";
    		}else {
    			logLine +="fd;";
    		} 
    		if (StringUtils.isBlank(advancedSearch.getTodate())) {
    			logLine +=";";
    		}else {
    			logLine +="td;";
    		} 
    		if (StringUtils.isBlank(advancedSearch.getExactDateSearch())) {
    			logLine +=";";
    		}else {
    			logLine +="xd;";
    		} 
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.COUNTRY_ID)) + ";";
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.AI_ID)) + ";";
    		logLine +=countList(solrQueryParameters.getOrParameters().get( SolrFields.FOND_ID)) + ";";
    		if (StringUtils.isBlank(advancedSearch.getStart())) {
    			logLine +=";";
    		}else {
    			logLine +=advancedSearch.getStart() + ";";
    		}
    		if (StringUtils.isBlank(advancedSearch.getLevel())) {
    			logLine +=";";
    		}else {
    			logLine +=advancedSearch.getLevel() + ";";
    		}
    		logLine += advancedSearch.getSearchType() + ";";
    		logLine +="\""+ advancedSearch.getTerm() + "\"";
    		ADVANCED_SEARCH_CONTEXT_LOGGER.debug(logLine);
    	}    	
    }
    public static void logUpdateAdvancedSearchList(String userId, AdvancedSearch advancedSearch, SolrQueryParameters solrQueryParameters){
    	if (ADVANCED_SEARCH_LIST_LOGGER.isDebugEnabled()){
    		String logLine = "";
    		if (StringUtils.isBlank(userId)){
    			logLine +="none;";
    		}else {
    			logLine +=userId +";";
    		}
    		if (HIERARCHY.equals(advancedSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(advancedSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   
    		if (OPTIONAL.equals(advancedSearch.getMethod())) {
    			logLine +="or;";
    		}else {
    			logLine +=";";
    		} 
    		if ("0".equals(advancedSearch.getElement())){
    			logLine +=";";
    		}else {
    			logLine += advancedSearch.getElement() +";";
    		}
    		logLine += advancedSearch.getTypedocument() +";";
    		if (StringUtils.isBlank(advancedSearch.getFromdate())) {
    			logLine +=";";
    		}else {
    			logLine +="fd;";
    		} 
    		if (StringUtils.isBlank(advancedSearch.getTodate())) {
    			logLine +=";";
    		}else {
    			logLine +="td;";
    		} 
    		if (StringUtils.isBlank(advancedSearch.getExactDateSearch())) {
    			logLine +=";";
    		}else {
    			logLine +="xd;";
    		} 
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.COUNTRY_ID)) + ";";
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.AI_ID)) + ";";
    		logLine +=countList(solrQueryParameters.getOrParameters().get( SolrFields.FOND_ID)) + ";";
    		logLine += countList(advancedSearch.getCountryList()) + ";";
    		logLine += countList(advancedSearch.getAiList()) + ";";
    		logLine += countList( advancedSearch.getFondList()) + ";";
    		logLine += countList(advancedSearch.getTypeList()) + ";";
    		logLine += countList( advancedSearch.getDateTypeList()) + ";";
    		logLine += countList(advancedSearch.getRoledaoList()) + ";";
    		logLine += countList( advancedSearch.getDaoList()) + ";";
    		if (StringUtils.isBlank(advancedSearch.getEnddate())) {
    			logLine +=";";
    		}else {
    			logLine +="ed;";
    		}
    		if (StringUtils.isBlank(advancedSearch.getStartdate())) {
    			logLine +=";";
    		}else {
    			logLine +="sd;";
    		} 
    		logLine += advancedSearch.getResultsperpage() + ";";
    		logLine += advancedSearch.getPageNumber() + ";";
    		logLine +="\""+ advancedSearch.getTerm() + "\"";
    		ADVANCED_SEARCH_LIST_LOGGER.debug(logLine);
    	}    	
    }
    public static void logSimpleSearch(String userId, AdvancedSearch advancedSearch){
    	if (SIMPLE_SEARCH_ANALYZE_LOGGER.isDebugEnabled()){
    		String logLine = "";
    		if (StringUtils.isBlank(userId)){
    			logLine +="none;";
    		}else {
    			logLine +=userId +";";
    		}
    		if (HIERARCHY.equals(advancedSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(advancedSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   

    		logLine +="\""+ advancedSearch.getTerm() + "\"";
    		SIMPLE_SEARCH_ANALYZE_LOGGER.debug(logLine);
    	}
    }
    public static void logPreview(String id){
    	if (PREVIEW_SECOND_DISPLAY_ANALYZE_LOGGER.isDebugEnabled()){
    		String logLine = "p;" +id;
    		PREVIEW_SECOND_DISPLAY_ANALYZE_LOGGER.debug(logLine);
    	}   	
    }
	private static String countList(List<String> list){
		if (list == null){
			return "";
		}else {
			return list.size() +"";
		}
	}
}
