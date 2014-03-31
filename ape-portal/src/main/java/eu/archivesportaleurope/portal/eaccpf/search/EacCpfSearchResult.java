package eu.archivesportaleurope.portal.eaccpf.search;

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
	private String ai;
	private String aiId;
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
			String occupationWithoutEscaping = getMultipleValues(solrDocument.getFieldValues(SolrFields.EAC_CPF_OCCUPATION));
			this.occupation =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_OCCUPATION, occupationWithoutEscaping);
			this.occupation = DisplayUtils.encodeHtmlWithHighlighting(occupation);
		}	
		if (solrDocument.getFieldValue(SolrFields.EAC_CPF_PLACES) != null){
			String placesWithoutEscaping = getMultipleValues(solrDocument.getFieldValues(SolrFields.EAC_CPF_PLACES));
			this.places =  AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_PLACES, placesWithoutEscaping);
			this.places = DisplayUtils.encodeHtmlWithHighlighting(places);
		}	
//		this.places =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAC_CPF_PLACES, null));	
//		this.scopecontent =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.SCOPECONTENT, null));	
//		this.other =  DisplayUtils.encodeHtmlWithHighlighting(AdvancedSearchUtil.getHighlightedString(highlightingMap, id, EadSolrFields.OTHER, null));
//		this.fond = solrDocument.getFieldValue(EadSolrFields.TITLE_OF_FOND).toString();
//		this.fondId = getIdFromString(this.fond);
//		this.fond = getDescriptionFromString(this.fond);
		this.ai = solrDocument.getFieldValue(SolrFields.AI).toString();
		this.aiId = getIdFromString(this.ai);
		this.ai = getDescriptionFromString(this.ai);
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

	public String getOccupation() {
		return occupation;
	}
	public Object getCountry(){
		String country = solrDocument.getFieldValue(SolrFields.COUNTRY).toString();
		return getDescriptionFromString(country);
	}
	public Object getAi(){
		return ai;
	}


}
