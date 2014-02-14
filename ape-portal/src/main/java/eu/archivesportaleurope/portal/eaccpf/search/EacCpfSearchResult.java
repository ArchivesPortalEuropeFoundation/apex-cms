package eu.archivesportaleurope.portal.eaccpf.search;

import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.search.common.AdvancedSearchUtil;
import eu.archivesportaleurope.portal.search.common.SearchResult;

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
	private String occupation;
	private SolrDocument solrDocument;

	public EacCpfSearchResult (SolrDocument solrDocument, Map<String, Map<String, List<String>>> highlightingMap){
		this.solrDocument = solrDocument;
		id = solrDocument.getFieldValue( SolrFields.ID).toString();
		String titleWithoutEscaping = null;
//		if (solrDocument.getFieldValue(EadSolrFields.ALTERDATE) != null){
//			String alterdateWithoutEscaping  = solrDocument.getFieldValue(EadSolrFields.ALTERDATE).toString();
//			String highlightedAlterdate =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.ALTERDATE, alterdateWithoutEscaping);
//			this.alterdate = DisplayUtils.encodeHtmlWithHighlighting(highlightedAlterdate);			
//			this.alterdateWithoutHighlighting = DisplayUtils.encodeHtml(alterdateWithoutEscaping);
//		}
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
		if (solrDocument.getFieldValue(SolrFields.EAC_CPF_OCCUPATION) != null){
			String occupationWithoutEscaping = solrDocument.getFirstValue(SolrFields.EAC_CPF_OCCUPATION).toString();
			this.occupation =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_OCCUPATION, occupationWithoutEscaping);
			this.occupation = DisplayUtils.encodeHtmlWithHighlighting(occupation);
		}	
		if (solrDocument.getFieldValue(SolrFields.EAC_CPF_PLACES) != null){
			String placesWithoutEscaping = solrDocument.getFirstValue(SolrFields.EAC_CPF_PLACES).toString();
			this.places =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_PLACES, placesWithoutEscaping);
			this.places = DisplayUtils.encodeHtmlWithHighlighting(places);
		}	
//		this.places =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_PLACES, null));	
//		this.scopecontent =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.SCOPECONTENT, null));	
//		this.other =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.OTHER, null));
//		this.fond = solrDocument.getFieldValue(EadSolrFields.TITLE_OF_FOND).toString();
//		this.fondId = getIdFromString(this.fond);
//		this.fond = getDescriptionFromString(this.fond);
//		this.ai = solrDocument.getFieldValue(EadSolrFields.AI).toString();
//		this.aiId = getIdFromString(this.ai);
//		this.ai = getDescriptionFromString(this.ai);
//		if (solrDocument.getFieldValue(EadSolrFields.UNITID) != null){
//			this.unitid  = solrDocument.getFieldValue(EadSolrFields.UNITID).toString();
//		}
//		this.unitid =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.UNITID, unitid));
//		this.otherUnitid =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.OTHERUNITID, null));
//		if (otherUnitid != null){
//			this.otherUnitid = "(" + this.otherUnitid + ")";
//		}
//		if (solrDocument.getFieldValue(EadSolrFields.LEVEL) != null)
//			this.level = solrDocument.getFieldValue(EadSolrFields.LEVEL).toString();
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

	public String getOccupation() {
		return occupation;
	}
	public String getCountry(){
		Object country = solrDocument.getFieldValue(SolrFields.COUNTRY);
		if (country == null){
			return null;
		}else {
			return country.toString();
		}
	}
	public String getAi(){
		Object ai = solrDocument.getFieldValue(SolrFields.AI);
		if (ai == null){
			return null;
		}else {
			return ai.toString();
		}
	}


}
