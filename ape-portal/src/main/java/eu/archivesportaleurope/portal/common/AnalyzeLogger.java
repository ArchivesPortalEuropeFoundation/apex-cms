package eu.archivesportaleurope.portal.common;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.apenet.commons.solr.SolrFields;
import eu.archivesportaleurope.portal.common.al.AlType;
import eu.archivesportaleurope.portal.common.al.TreeType;
import eu.archivesportaleurope.portal.search.common.SolrQueryParameters;
import eu.archivesportaleurope.portal.search.ead.EadSearch;
import eu.archivesportaleurope.portal.search.ead.tree.TreeEadSearch;

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
    public static void logAdvancedSearch(EadSearch eadSearch, SolrQueryParameters solrQueryParameters){
    	if (ADVANCED_SEARCH_ANALYZE_LOGGER.isDebugEnabled() && solrQueryParameters != null){
    		String logLine = "";
    		if (HIERARCHY.equals(eadSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(eadSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   
    		if (OPTIONAL.equals(eadSearch.getMethod())) {
    			logLine +="or;";
    		}else {
    			logLine +=";";
    		} 
    		if ("0".equals(eadSearch.getElement())){
    			logLine +=";";
    		}else {
    			logLine += eadSearch.getElement() +";";
    		}
    		logLine += eadSearch.getTypedocument() +";";
    		if (StringUtils.isBlank(eadSearch.getFromdate())) {
    			logLine +=";";
    		}else {
    			logLine +="fd;";
    		} 
    		if (StringUtils.isBlank(eadSearch.getTodate())) {
    			logLine +=";";
    		}else {
    			logLine +="td;";
    		} 
    		if (StringUtils.isBlank(eadSearch.getExactDateSearch())) {
    			logLine +=";";
    		}else {
    			logLine +="ed;";
    		} 
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.COUNTRY_ID)) + ";";
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.AI_ID)) + ";";
    		logLine +=countList(solrQueryParameters.getOrParameters().get( SolrFields.FOND_ID)) + ";";
    		logLine +="\""+ eadSearch.getTerm() + "\"";
    		ADVANCED_SEARCH_ANALYZE_LOGGER.debug(logLine);
    	}    	
    }
    public static void logUpdateAdvancedSearchContext(TreeEadSearch eadSearch, SolrQueryParameters solrQueryParameters){
    	if (ADVANCED_SEARCH_CONTEXT_LOGGER.isDebugEnabled() && solrQueryParameters != null){
    		String logLine = "";
    		if (HIERARCHY.equals(eadSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(eadSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   
    		if (OPTIONAL.equals(eadSearch.getMethod())) {
    			logLine +="or;";
    		}else {
    			logLine +=";";
    		} 
    		if ("0".equals(eadSearch.getElement())){
    			logLine +=";";
    		}else {
    			logLine += eadSearch.getElement() +";";
    		}
    		logLine += eadSearch.getTypedocument() +";";
    		if (StringUtils.isBlank(eadSearch.getFromdate())) {
    			logLine +=";";
    		}else {
    			logLine +="fd;";
    		} 
    		if (StringUtils.isBlank(eadSearch.getTodate())) {
    			logLine +=";";
    		}else {
    			logLine +="td;";
    		} 
    		if (StringUtils.isBlank(eadSearch.getExactDateSearch())) {
    			logLine +=";";
    		}else {
    			logLine +="xd;";
    		} 
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.COUNTRY_ID)) + ";";
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.AI_ID)) + ";";
    		logLine +=countList(solrQueryParameters.getOrParameters().get( SolrFields.FOND_ID)) + ";";
    		if (StringUtils.isBlank(eadSearch.getStart())) {
    			logLine +=";";
    		}else {
    			logLine +=eadSearch.getStart() + ";";
    		}
    		if (StringUtils.isBlank(eadSearch.getLevel())) {
    			logLine +=";";
    		}else {
    			logLine +=eadSearch.getLevel() + ";";
    		}
    		logLine += eadSearch.getSearchType() + ";";
    		logLine +="\""+ eadSearch.getTerm() + "\"";
    		ADVANCED_SEARCH_CONTEXT_LOGGER.debug(logLine);
    	}    	
    }
    public static void logUpdateAdvancedSearchList(EadSearch eadSearch, SolrQueryParameters solrQueryParameters){
    	if (ADVANCED_SEARCH_LIST_LOGGER.isDebugEnabled() && solrQueryParameters != null){
    		String logLine = "";
    		if (HIERARCHY.equals(eadSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(eadSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   
    		if (OPTIONAL.equals(eadSearch.getMethod())) {
    			logLine +="or;";
    		}else {
    			logLine +=";";
    		} 
    		if ("0".equals(eadSearch.getElement())){
    			logLine +=";";
    		}else {
    			logLine += eadSearch.getElement() +";";
    		}
    		logLine += eadSearch.getTypedocument() +";";
    		if (StringUtils.isBlank(eadSearch.getFromdate())) {
    			logLine +=";";
    		}else {
    			logLine +="fd;";
    		} 
    		if (StringUtils.isBlank(eadSearch.getTodate())) {
    			logLine +=";";
    		}else {
    			logLine +="td;";
    		} 
    		if (StringUtils.isBlank(eadSearch.getExactDateSearch())) {
    			logLine +=";";
    		}else {
    			logLine +="xd;";
    		} 
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.COUNTRY_ID)) + ";";
    		logLine += countList(solrQueryParameters.getOrParameters().get( SolrFields.AI_ID)) + ";";
    		logLine +=countList(solrQueryParameters.getOrParameters().get( SolrFields.FOND_ID)) + ";";
    		logLine += countList(eadSearch.getCountryList()) + ";";
    		logLine += countList(eadSearch.getAiList()) + ";";
    		logLine += countList( eadSearch.getFondList()) + ";";
    		logLine += countList(eadSearch.getTypeList()) + ";";
    		logLine += countList( eadSearch.getDateTypeList()) + ";";
    		logLine += countList(eadSearch.getRoledaoList()) + ";";
    		logLine += countList( eadSearch.getDaoList()) + ";";
    		if (StringUtils.isBlank(eadSearch.getEnddate())) {
    			logLine +=";";
    		}else {
    			logLine +="ed;";
    		}
    		if (StringUtils.isBlank(eadSearch.getStartdate())) {
    			logLine +=";";
    		}else {
    			logLine +="sd;";
    		} 
    		logLine += eadSearch.getResultsperpage() + ";";
    		logLine += eadSearch.getPageNumber() + ";";
    		logLine +="\""+ eadSearch.getTerm() + "\"";
    		ADVANCED_SEARCH_LIST_LOGGER.debug(logLine);
    	}    	
    }
    public static void logSimpleSearch(EadSearch eadSearch){
    	if (SIMPLE_SEARCH_ANALYZE_LOGGER.isDebugEnabled()){
    		String logLine = "";
    		if (HIERARCHY.equals(eadSearch.getView())) {
    			logLine +="c;";
    		}else {
    			logLine +=";";
    		}
    		if (TRUE.equals(eadSearch.getDao())) {
    			logLine +="d;";
    		}else {
    			logLine +=";";
    		}   

    		logLine +="\""+ eadSearch.getTerm() + "\"";
    		SIMPLE_SEARCH_ANALYZE_LOGGER.debug(logLine);
    	}
    }
    public static void logPreview(String id){
    	if (PREVIEW_SECOND_DISPLAY_ANALYZE_LOGGER.isDebugEnabled()){
    		String logLine = "p;" +id;
    		PREVIEW_SECOND_DISPLAY_ANALYZE_LOGGER.debug(logLine);
    	}   	
    }
    public static void logSecondDisplay(String id){
    	if (PREVIEW_SECOND_DISPLAY_ANALYZE_LOGGER.isDebugEnabled()){
    		String logLine = "s;" +id;
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
