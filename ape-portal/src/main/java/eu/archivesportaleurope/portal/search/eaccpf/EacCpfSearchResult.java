package eu.archivesportaleurope.portal.search.eaccpf;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
import eu.archivesportaleurope.portal.search.common.SearchResult;
import eu.archivesportaleurope.util.ApeUtil;

/**
 * @author bverhoef
 *
 */

public class EacCpfSearchResult extends SearchResult{
	private final static Logger LOGGER = Logger.getLogger(EacCpfSearchResult.class);
	private static final char COLON = ':';

	private String id;
	private String title;
	private String titleWithoutHighlighting;
	private String alterdate;
	private String alterdateWithoutHighlighting;
	private String description;
	private String other;
	private String places;
	private String occupations;
	private String functions;
	private String mandates;
	private String entityType;
	private String entityId;
	private String ai;
	private String aiId;
	private String repositoryCode;
	private String identifier;
	private Integer numberOfArchivalMaterialRelations;
	private Integer numberOfNameRelations;
	private Integer numberOfInstitutions;
	private SolrDocument solrDocument;

	public EacCpfSearchResult (SolrDocument solrDocument, Map<String, Map<String, List<String>>> highlightingMap){
		this.solrDocument = solrDocument;
		id = solrDocument.getFieldValue( SolrFields.ID).toString();
		String titleWithoutEscaping = null;
		if (solrDocument.getFieldValue(SolrFields.EAC_CPF_NAMES) != null){
			titleWithoutEscaping = solrDocument.getFirstValue(SolrFields.EAC_CPF_NAMES).toString();
			String highlightedTitle =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_NAMES, titleWithoutEscaping);
			this.title = DisplayUtils.encodeHtmlWithHighlighting(highlightedTitle);
			this.titleWithoutHighlighting = DisplayUtils.encodeHtml(titleWithoutEscaping);
		}		
		if (solrDocument.getFieldValue(SolrFields.EAC_CPF_DATE_DESCRIPTION) != null){
			String alterdateWithoutEscaping  = solrDocument.getFieldValue(SolrFields.EAC_CPF_DATE_DESCRIPTION).toString();
			String highlightedAlterdate =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_DATE_DESCRIPTION, alterdateWithoutEscaping);
			this.alterdate = DisplayUtils.encodeHtmlWithHighlighting(highlightedAlterdate);			
			this.alterdateWithoutHighlighting = DisplayUtils.encodeHtml(alterdateWithoutEscaping);
		}
		this.description =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_DESCRIPTION, null));
		this.occupations =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_OCCUPATION, null);
		this.occupations = DisplayUtils.encodeHtmlWithHighlighting(occupations);
		this.mandates =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_MANDATE, null);
		this.mandates = DisplayUtils.encodeHtmlWithHighlighting(mandates);
		this.functions =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_FACET_FUNCTION, null);
		this.mandates = DisplayUtils.encodeHtmlWithHighlighting(mandates);
		this.places =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_PLACES, null);
		this.places = DisplayUtils.encodeHtmlWithHighlighting(places);
		if (solrDocument.getFieldValue(SolrFields.EAC_CPF_FACET_ENTITY_TYPE) != null){
			this.entityType = solrDocument.getFieldValue(SolrFields.EAC_CPF_FACET_ENTITY_TYPE).toString();
		}
		this.entityId =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_ENTITY_ID, null);
		this.repositoryCode = ApeUtil.encodeRepositoryCode((String) solrDocument.getFieldValue(SolrFields.EAC_CPF_AGENCY_CODE));
		this.identifier = ApeUtil.encodeSpecialCharacters((String) solrDocument.getFieldValue(SolrFields.EAC_CPF_RECORD_ID));
		this.other =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.OTHER, null));
		this.ai = solrDocument.getFieldValue(SolrFields.AI).toString();
		this.aiId = getIdFromString(this.ai);
		this.ai = getDescriptionFromString(this.ai);
		this.numberOfArchivalMaterialRelations = (Integer) solrDocument.getFieldValue(SolrFields.EAC_CPF_NUMBER_OF_MATERIAL_RELATIONS);
		this.numberOfInstitutions = (Integer) solrDocument.getFieldValue(SolrFields.EAC_CPF_NUMBER_OF_INSTITUTIONS_RELATIONS);
		this.numberOfNameRelations = (Integer) solrDocument.getFieldValue(SolrFields.EAC_CPF_NUMBER_OF_NAME_RELATIONS);
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

	public String getAlterdate() {
		return alterdate;
	}

	public String getAlterdateWithoutHighlighting() {
		return alterdateWithoutHighlighting;
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


	public String getOccupations() {
		return occupations;
	}
	public String getFunctions() {
		return functions;
	}
	public String getMandates() {
		return mandates;
	}
	public String getEntityType() {
		return entityType;
	}
	public String getAiId() {
		return aiId;
	}
	public Object getCountry(){
		String country = solrDocument.getFieldValue(SolrFields.COUNTRY).toString();
		return getDescriptionFromString(country);
	}
	public Object getAi(){
		return ai;
	}
	public String getRepositoryCode() {
		return repositoryCode;
	}
	public String getIdentifier() {
		return identifier;
	}

	public String getEntityId() {
		return entityId;
	}
	public Integer getNumberOfArchivalMaterialRelations() {
		return numberOfArchivalMaterialRelations;
	}
	public Integer getNumberOfNameRelations() {
		return numberOfNameRelations;
	}
	public Integer getNumberOfInstitutions() {
		return numberOfInstitutions;
	}

}
