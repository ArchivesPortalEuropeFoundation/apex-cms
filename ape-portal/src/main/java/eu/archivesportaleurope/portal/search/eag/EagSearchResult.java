package eu.archivesportaleurope.portal.search.eag;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.search.common.SearchResult;
import eu.archivesportaleurope.portal.search.common.SearchUtil;
import eu.archivesportaleurope.util.ApeUtil;

/**
 * @author bverhoef
 *
 */

public class EagSearchResult extends SearchResult{
	private final static Logger LOGGER = Logger.getLogger(EagSearchResult.class);
	private static final char COLON = ':';

	private String id;
	private String title;
	private String titleWithoutHighlighting;
	private String otherNames;
	private String repositories;
	private String description;
	private String other;
	private String places;

	private String repositoryCode;
	private String identifier;

	private SolrDocument solrDocument;

	public EagSearchResult (SolrDocument solrDocument, Map<String, Map<String, List<String>>> highlightingMap){
		this.solrDocument = solrDocument;
		id = solrDocument.getFieldValue( SolrFields.ID).toString();
		String titleWithoutEscaping = null;
		boolean hitInName = false;
		if (solrDocument.getFieldValue(SolrFields.EAG_NAME) != null){
			titleWithoutEscaping = solrDocument.getFirstValue(SolrFields.EAG_NAME).toString();
			String highlightedTitle =  SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAG_NAME, titleWithoutEscaping);
			hitInName = highlightedTitle.contains(DisplayUtils.EM_START);
			this.title = DisplayUtils.encodeHtmlWithHighlighting(highlightedTitle);
			this.titleWithoutHighlighting = DisplayUtils.encodeHtml(titleWithoutEscaping);
		}		
		this.description =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAG_DESCRIPTION, null));
		if (!hitInName){
			this.otherNames =  getMultipleValued(highlightingMap,SolrFields.EAG_OTHER_NAMES);
			this.repositories = getMultipleValued(highlightingMap, SolrFields.EAG_REPOSITORIES);
		}
		this.repositoryCode = ApeUtil.encodeRepositoryCode((String) solrDocument.getFieldValue(SolrFields.REPOSITORY_CODE));

		this.other =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAG_OTHER, null));

	}
	private String getMultipleValued(Map<String, Map<String, List<String>>> highlightingMap, String solrField){
		List<String> temp= SearchUtil.getHighlightedStrings(highlightingMap, id, solrField);
		List<String> results = new ArrayList<String>();
		for (String tempItem: temp){
			if (tempItem.contains(DisplayUtils.EM_START))
				results.add(DisplayUtils.encodeHtmlWithHighlighting(tempItem));
		}
		return getMultipleValues(results,"<br/>");
		
	}
	protected String getMultipleValues(Collection<String> values, String separator){
		String result = "";
		Iterator<String> valuesIterator = values.iterator();
		while (valuesIterator.hasNext()){
			Object value = valuesIterator.next();
			if (valuesIterator.hasNext()){
				result += value + separator;
			}else {
				result += value;
			}
		}
		return result;
	}
	protected String getMultipleValues(Collection<Object> values){
		String result = "";
		Iterator<Object> valuesIterator = values.iterator();
		while (valuesIterator.hasNext()){
			Object value = valuesIterator.next();
			if (valuesIterator.hasNext()){
				result += value + " | ";
			}else {
				result += value;
			}
		}
		return result;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}





	private String getDescriptionFromString(String string){
		int index = string.indexOf(COLON);
		return string.substring(0,index);
	}
	private String getIdFromString(String string){
		int index = string.lastIndexOf(COLON);
		return string.substring(index+1);
	}



	public String getTitleWithoutHighlighting() {
		return titleWithoutHighlighting;
	}

	public static Logger getLogger() {
		return LOGGER;
	}

	public static char getColon() {
		return COLON;
	}


	public String getDescription() {
		return description;
	}

	public String getOther() {
		return other;
	}

	public SolrDocument getSolrDocument() {
		return solrDocument;
	}

	public String getPlaces() {
		return places;
	}


	public Object getCountry(){
		String country = solrDocument.getFieldValue(SolrFields.COUNTRY).toString();
		return getDescriptionFromString(country);
	}
	public String getRepositoryTypes(){
		Collection<Object> types= solrDocument.getFieldValues(SolrFields.EAG_REPOSITORY_TYPE);
		if (types == null){
			return null;
		}else {
			return getMultipleValues(types);
		}
	}
	
	public String getAddress(){
		Collection<Object> address= solrDocument.getFieldValues(SolrFields.EAG_ADDRESS);
		if (address == null){
			return null;
		}else {
			return getMultipleValues(address);
		}
	}
	public String getRepositoryCode() {
		return repositoryCode;
	}
	public String getIdentifier() {
		return identifier;
	}
	public String getOtherNames() {
		return otherNames;
	}
	public String getRepositories() {
		return repositories;
	}
	public String getContext(){
		StringBuilder result = new StringBuilder();
		int numberOfWhitespaces = 1;
		Collection<Object> aiGroups= solrDocument.getFieldValues(SolrFields.EAG_AI_GROUPS);
		if (aiGroups != null){
			Iterator<Object> valuesIterator = aiGroups.iterator();
			while (valuesIterator.hasNext()){
				//result.append("<span class=\"contextHierarchyItem\">");
//				for (int j = 0; j < numberOfWhitespaces;j++){
//					result.append("&nbsp;&nbsp;&nbsp;");
//				}
				result.append(" - ");
				result.append(valuesIterator.next());
				//result.append("</span>");
				//result.append("<br/>");
				numberOfWhitespaces++;
			}
		}
		return result.toString();
	}


}
