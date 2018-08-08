package eu.archivesportaleurope.portal.search.ead.list;

import eu.apenet.commons.solr.Ead3SolrFields;
import eu.apenet.commons.solr.SearchUtil;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.solr.SolrValues;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.search.common.DatabaseCacher;
import eu.archivesportaleurope.portal.search.common.SearchResult;

/**
 * @author bverhoef
 *
 */

public class EadSearchResult extends SearchResult{
	private static final char COLON = ':';
//	private static final int MAX_NUMBER_OF_CHARACTERS_TITLE = 80;
//	private static final int MAX_NUMBER_OF_CHARACTERS_UNITID = 80;	
//	private static final int MAX_NUMBER_OF_CHARACTERS_DATE = 20;	
//	private static final int MAX_NUMBER_OF_CHARACTERS_OTHER = 100;
//	private static final int MAX_NUMBER_OF_CHARACTERS_FOND = 150;	
	private String id;
	private String title;
	private String titleWithoutHighlighting;
	private String scopecontent;
	private String other;
	private String fond;
	private String fondId;
	private String ai;
	private String aiId;
	private String unitid;
	private String unitidForLink;
	private String otherUnitid;
	private String alterdate;
	private String alterdateWithoutHighlighting;
	private String level;
	private String repositoryCode; 
	private SolrDocument solrDocument;


	public EadSearchResult (SolrDocument solrDocument, Map<String, Map<String, List<String>>> highlightingMap, DatabaseCacher databaseCacher){
		this.solrDocument = solrDocument;
		id = solrDocument.getFieldValue( Ead3SolrFields.ID).toString();
		String titleWithoutEscaping = null;
		if (solrDocument.getFieldValue(Ead3SolrFields.ALTERNATE_UNIT_DATE) != null){
			String alterdateWithoutEscaping  = solrDocument.getFieldValue(Ead3SolrFields.ALTERNATE_UNIT_DATE).toString();
			String highlightedAlterdate =  SearchUtil.getHighlightedString(highlightingMap, id, Ead3SolrFields.ALTERNATE_UNIT_DATE, alterdateWithoutEscaping);
			this.alterdate = DisplayUtils.encodeHtmlWithHighlighting(highlightedAlterdate);			
			this.alterdateWithoutHighlighting = DisplayUtils.encodeHtml(alterdateWithoutEscaping);
		}
		if (solrDocument.getFieldValue(Ead3SolrFields.TITLE_PROPER) != null){
			titleWithoutEscaping = solrDocument.getFieldValue(Ead3SolrFields.TITLE_PROPER).toString();
			String highlightedTitle =  SearchUtil.getHighlightedString(highlightingMap, id, Ead3SolrFields.TITLE_PROPER, titleWithoutEscaping);
			this.title = DisplayUtils.encodeHtmlWithHighlighting(highlightedTitle);
			this.titleWithoutHighlighting = DisplayUtils.encodeHtml(titleWithoutEscaping);
		}		


		
		this.scopecontent =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, Ead3SolrFields.SCOPE_CONTENT, null));	
		this.other =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, Ead3SolrFields.OTHER, null));
		this.fond = solrDocument.getFieldValue(Ead3SolrFields.TITLE_PROPER).toString();
		this.fondId = getIdFromString(this.fond);
		this.fond = getDescriptionFromString(this.fond);
		this.ai = solrDocument.getFieldValue(Ead3SolrFields.AI).toString();
		this.aiId = getIdFromString(this.ai);
		this.ai = getDescriptionFromString(this.ai);
		if (solrDocument.getFieldValue(Ead3SolrFields.LEVEL_NAME) != null)
			this.level = solrDocument.getFieldValue(Ead3SolrFields.LEVEL_NAME).toString();
		
		if (SolrValues.LEVEL_CLEVEL.equals(this.level)){
			if (solrDocument.getFieldValue(Ead3SolrFields.UNIT_ID) != null){
				this.unitid  = solrDocument.getFieldValue(Ead3SolrFields.UNIT_ID).toString();
			}
			Object duplicateUnitid =solrDocument.getFieldValue(Ead3SolrFields.DUPLICATE_UNIT_ID);
			if (duplicateUnitid != null && "true".equalsIgnoreCase(duplicateUnitid.toString())){
				unitidForLink = null;
			}else {
				unitidForLink = this.unitid;
			}
			this.unitid =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, Ead3SolrFields.UNIT_ID, unitid));
			this.otherUnitid =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, Ead3SolrFields.OTHER_UNIT_ID, null));
			
			if (otherUnitid != null){
				this.otherUnitid = "(" + this.otherUnitid + ")";
			}
		}
		if (solrDocument.getFieldValue(Ead3SolrFields.REPOSITORY_CODE) == null){
			this.repositoryCode = databaseCacher.getRepositoryCode(Integer.parseInt(aiId));
		}else {
			this.repositoryCode = solrDocument.getFieldValue(Ead3SolrFields.REPOSITORY_CODE).toString();
		}
		
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getScopecontent() {
		return scopecontent;
	}

	public String getOther() {
		return other;
	}


	public String getUnitid() {
		return unitid;
	}
	public String getUnitidForLink(){
		return unitidForLink;	
	}

	public String getAlterdate() {
		return alterdate;
	}

	public String getOtherUnitid() {
		return otherUnitid;
	}

	public Object getCountry(){
		String country = solrDocument.getFieldValue(Ead3SolrFields.COUNTRY).toString();
		return getDescriptionFromString(country);
	}
	public Object getAi(){
		return ai;
	}
	public String getType(){
		return solrDocument.getFieldValue(Ead3SolrFields.RECORD_TYPE).toString();
	}
	public String getEadid(){
		return solrDocument.getFieldValue(Ead3SolrFields.ROOT_DOC_ID).toString();
	}
	public Object getDao(){
		return solrDocument.getFieldValue(Ead3SolrFields.DAO);
	}
	public Object getRoledao(){
		return solrDocument.getFieldValue(Ead3SolrFields.DAO_TYPE);
	}
//	public Object getUnitIdOfFond(){
//		// Check if the content has char '<' or '>' in order to avoid
//		// js ingestion (see issue 1248).
//		Object object = solrDocument.getFieldValue(Ead3SolrFields.UNITID_OF_FOND);
//		if (object != null && object instanceof String) {
//			return DisplayUtils.encodeHtml(object.toString());
//		} else {
//			return null;
//		}
//	}
	
	public Object getRepositoryCode(){
		return repositoryCode;
	}
	private String getDescriptionFromString(String string){
		int index = string.indexOf(COLON);
		return string.substring(0,index);
	}
	private String getIdFromString(String string){
		int index = string.lastIndexOf(COLON);
		return string.substring(index+1);
	}

	public String getFond() {
		return fond;
	}
	public String getEscapedFond() {
		return DisplayUtils.escapeJavascript(fond);
	}

	public SolrDocument getSolrDocument() {
		return solrDocument;
	}

	public String getFondId() {
		return fondId;
	}

	public String getAiId() {
		return aiId;
	}

	public String getTitleWithoutHighlighting() {
		return titleWithoutHighlighting;
	}

	public String getAlterdateWithoutHighlighting() {
		return alterdateWithoutHighlighting;
	}

	public String getLevel() {
		return level;
	}

}
