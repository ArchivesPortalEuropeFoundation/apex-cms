package eu.archivesportaleurope.portal.search.eag;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.search.common.SearchUtil;
import eu.archivesportaleurope.portal.search.common.SearchResult;
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
		if (solrDocument.getFieldValue(SolrFields.EAG_NAME) != null){
			titleWithoutEscaping = solrDocument.getFirstValue(SolrFields.EAG_NAME).toString();
			String highlightedTitle =  SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAG_NAME, titleWithoutEscaping);
			this.title = DisplayUtils.encodeHtmlWithHighlighting(highlightedTitle);
			this.titleWithoutHighlighting = DisplayUtils.encodeHtml(titleWithoutEscaping);
		}		
//		this.description =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_DESCRIPTION, null));
//		this.occupations =  SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_OCCUPATION, null);
//		this.occupations = DisplayUtils.encodeHtmlWithHighlighting(occupations);
//		this.mandates =  SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_MANDATE, null);
//		this.mandates = DisplayUtils.encodeHtmlWithHighlighting(mandates);
//		this.functions =  SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_FACET_FUNCTION, null);
//		this.mandates = DisplayUtils.encodeHtmlWithHighlighting(mandates);
//		this.places =  SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_PLACES, null);
//		this.places = DisplayUtils.encodeHtmlWithHighlighting(places);
//		if (solrDocument.getFieldValue(SolrFields.EAC_CPF_FACET_ENTITY_TYPE) != null){
//			this.entityType = solrDocument.getFieldValue(SolrFields.EAC_CPF_FACET_ENTITY_TYPE).toString();
//		}
//		this.entityId =  SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_ENTITY_ID, null);
		this.repositoryCode = ApeUtil.encodeRepositoryCode((String) solrDocument.getFieldValue(SolrFields.EAG_REPOSITORY_CODE));
//		this.identifier = ApeUtil.encodeSpecialCharacters((String) solrDocument.getFieldValue(SolrFields.EAC_CPF_RECORD_ID));
//		this.other =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.OTHER, null));
//		this.ai = solrDocument.getFieldValue(SolrFields.AI).toString();
//		this.aiId = getIdFromString(this.ai);
//		this.ai = getDescriptionFromString(this.ai);

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
	public String getRepositoryCode() {
		return repositoryCode;
	}
	public String getIdentifier() {
		return identifier;
	}



}
