package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.eads.EadSolrFields;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;

/**
 * @author bverhoef
 *
 */

public class SearchResult {
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
	private String otherUnitid;
	private String alterdate;
	private String alterdateWithoutHighlighting;
	private String level;
	private SolrDocument solrDocument;

	public SearchResult (SolrDocument solrDocument, Map<String, Map<String, List<String>>> highlightingMap){
		this.solrDocument = solrDocument;
		id = solrDocument.getFieldValue( EadSolrFields.ID).toString();
		String titleWithoutEscaping = null;
		if (solrDocument.getFieldValue(EadSolrFields.ALTERDATE) != null){
			String alterdateWithoutEscaping  = solrDocument.getFieldValue(EadSolrFields.ALTERDATE).toString();
			String highlightedAlterdate =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.ALTERDATE, alterdateWithoutEscaping);
			this.alterdate = DisplayUtils.encodeHtmlWithHighlighting(highlightedAlterdate);			
			this.alterdateWithoutHighlighting = DisplayUtils.encodeHtml(alterdateWithoutEscaping);
		}
		if (solrDocument.getFieldValue(EadSolrFields.TITLE) != null){
			titleWithoutEscaping = solrDocument.getFieldValue(EadSolrFields.TITLE).toString();
			String highlightedTitle =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.TITLE, titleWithoutEscaping);
			this.title = DisplayUtils.encodeHtmlWithHighlighting(highlightedTitle);
			this.titleWithoutHighlighting = DisplayUtils.encodeHtml(titleWithoutEscaping);
		}		


		
		this.scopecontent =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.SCOPECONTENT, null));	
		this.other =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.OTHER, null));
		this.fond = solrDocument.getFieldValue(EadSolrFields.TITLE_OF_FOND).toString();
		this.fondId = getIdFromString(this.fond);
		this.fond = getDescriptionFromString(this.fond);
		this.ai = solrDocument.getFieldValue(EadSolrFields.AI).toString();
		this.aiId = getIdFromString(this.ai);
		this.ai = getDescriptionFromString(this.ai);
		if (solrDocument.getFieldValue(EadSolrFields.UNITID) != null){
			this.unitid  = solrDocument.getFieldValue(EadSolrFields.UNITID).toString();
		}
		this.unitid =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.UNITID, unitid));
		this.otherUnitid =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.OTHERUNITID, null));
		if (otherUnitid != null){
			this.otherUnitid = "(" + this.otherUnitid + ")";
		}
		if (solrDocument.getFieldValue(EadSolrFields.LEVEL) != null)
			this.level = solrDocument.getFieldValue(EadSolrFields.LEVEL).toString();
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

	public String getOtherUnitid() {
		return otherUnitid;
	}

	public Object getCountry(){
		String country = solrDocument.getFieldValue(EadSolrFields.COUNTRY).toString();
		return getDescriptionFromString(country);
	}
	public Object getAi(){
		return ai;
	}
	public String getType(){
		return solrDocument.getFieldValue(EadSolrFields.TYPE).toString();
	}
	public Object getDao(){
		return solrDocument.getFieldValue(EadSolrFields.DAO);
	}
	public Object getRoledao(){
		return solrDocument.getFieldValue(EadSolrFields.ROLEDAO);
	}
	public Object getUnitIdOfFond(){
		return solrDocument.getFieldValue(EadSolrFields.UNITID_OF_FOND);
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
