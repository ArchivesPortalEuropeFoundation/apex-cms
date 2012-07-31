package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;

/**
 * @author bverhoef
 *
 */

public class SearchResult {
	private static final char COLON = ':';
	private static final int MAX_NUMBER_OF_CHARACTERS_TITLE = 80;
	private static final int MAX_NUMBER_OF_CHARACTERS_UNITID = 80;	
	private static final int MAX_NUMBER_OF_CHARACTERS_DATE = 20;	
	private static final int MAX_NUMBER_OF_CHARACTERS_OTHER = 100;
	private static final int MAX_NUMBER_OF_CHARACTERS_FOND = 150;	
	private String id;
	private String title;
	private String titleShortDescription;
	private String scopecontent;
	private String other;
	private String fond;
	private String fondId;
	private String ai;
	private String aiId;
	private String fondShortDescription;
	private String unitid;
	private String otherUnitid;
	private String alterdate;
	private String alterdateShortDescription;
	private SolrDocument solrDocument;

	public SearchResult (SolrDocument solrDocument, Map<String, Map<String, List<String>>> highlightingMap){
		this.solrDocument = solrDocument;
		id = solrDocument.getFieldValue( SolrFields.ID).toString();
		int numberOfAlterdateCharacters = 0;
		int numberOfTitleCharacters = 0;
		String alterdateWithoutEscaping = null;
		String titleWithoutEscaping = null;
		if (solrDocument.getFieldValue(SolrFields.ALTERDATE) != null){
			alterdateWithoutEscaping  = solrDocument.getFieldValue(SolrFields.ALTERDATE).toString();
			numberOfAlterdateCharacters = alterdateWithoutEscaping.length();
		}
		if (solrDocument.getFieldValue(SolrFields.TITLE) != null){
			titleWithoutEscaping = solrDocument.getFieldValue(SolrFields.TITLE).toString();
			numberOfTitleCharacters = titleWithoutEscaping.length();
		}		
		if (numberOfAlterdateCharacters > 0){
			String highlightedAlterdate =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.ALTERDATE, alterdateWithoutEscaping);
			int maxNumberOfCharacters = MAX_NUMBER_OF_CHARACTERS_DATE;
			if (numberOfTitleCharacters > 0 && numberOfTitleCharacters < MAX_NUMBER_OF_CHARACTERS_TITLE){
				maxNumberOfCharacters += (MAX_NUMBER_OF_CHARACTERS_TITLE - numberOfTitleCharacters);
			}			
			this.alterdateShortDescription = DisplayUtils.encodeHtmlWithHighlighting(highlightedAlterdate, maxNumberOfCharacters);
			this.alterdate = DisplayUtils.encodeHtml(alterdateWithoutEscaping);			
		}			

		if (numberOfTitleCharacters > 0){
			String highlightedTitle =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.TITLE, titleWithoutEscaping);
			int maxNumberOfCharacters = MAX_NUMBER_OF_CHARACTERS_TITLE;
			if (numberOfAlterdateCharacters > 0){
				if (numberOfAlterdateCharacters < MAX_NUMBER_OF_CHARACTERS_DATE){
					maxNumberOfCharacters -= numberOfAlterdateCharacters;
				}else {
					maxNumberOfCharacters -= MAX_NUMBER_OF_CHARACTERS_DATE;
				}
			}
			this.titleShortDescription = DisplayUtils.encodeHtmlWithHighlighting(highlightedTitle, maxNumberOfCharacters);
			this.title = DisplayUtils.encodeHtml(titleWithoutEscaping);
		}
	

		
		this.scopecontent =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.SCOPECONTENT, null),MAX_NUMBER_OF_CHARACTERS_OTHER );	
		this.other =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.OTHER, null), MAX_NUMBER_OF_CHARACTERS_OTHER);
		this.fond = solrDocument.getFieldValue(SolrFields.TITLE_OF_FOND).toString();
		this.fondId = getIdFromString(this.fond);
		this.fond = getDescriptionFromString(this.fond);
		this.ai = solrDocument.getFieldValue(SolrFields.AI).toString();
		this.aiId = getIdFromString(this.ai);
		this.ai = getDescriptionFromString(this.ai);
		if (solrDocument.getFieldValue(SolrFields.UNITID) != null){
			this.unitid  = solrDocument.getFieldValue(SolrFields.UNITID).toString();
		}
		this.unitid =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.UNITID, unitid),MAX_NUMBER_OF_CHARACTERS_UNITID);
		this.otherUnitid =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.OTHERUNITID, null),MAX_NUMBER_OF_CHARACTERS_UNITID);
		if (otherUnitid != null){
			this.otherUnitid = "(" + this.otherUnitid + ")";
		}
		this.fondShortDescription =  DisplayUtils.substring(this.fond, MAX_NUMBER_OF_CHARACTERS_FOND);
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

	public String getAlterdate() {
		return alterdate;
	}

	public String getAlterdateShortDescription() {
		return alterdateShortDescription;
	}

	public String getOtherUnitid() {
		return otherUnitid;
	}

	public Object getCountry(){
		String country = solrDocument.getFieldValue(SolrFields.COUNTRY).toString();
		return getDescriptionFromString(country);
	}
	public Object getAi(){
		return ai;
	}
	public String getType(){
		return solrDocument.getFieldValue(SolrFields.TYPE).toString();
	}
	public Object getDao(){
		return solrDocument.getFieldValue(SolrFields.DAO);
	}
	public Object getRoledao(){
		return solrDocument.getFieldValue(SolrFields.ROLEDAO);
	}
	public Object getUnitIdOfFond(){
		return solrDocument.getFieldValue(SolrFields.UNITID_OF_FOND);
	}
	private String getDescriptionFromString(String string){
		int index = string.indexOf(COLON);
		return string.substring(0,index);
	}
	private String getIdFromString(String string){
		int index = string.lastIndexOf(COLON);
		return string.substring(index+1);
	}

	public String getTitleShortDescription() {
		return titleShortDescription;
	}

	public String getFond() {
		return fond;
	}

	public String getFondShortDescription() {
		return fondShortDescription;
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

}
