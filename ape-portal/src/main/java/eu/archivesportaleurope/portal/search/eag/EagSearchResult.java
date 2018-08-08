package eu.archivesportaleurope.portal.search.eag;

import eu.apenet.commons.solr.Ead3SolrFields;
import eu.apenet.commons.solr.SearchUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrDocument;

import eu.apenet.commons.solr.SolrFields;
import eu.apenet.commons.solr.facet.FacetType;
import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.search.common.SearchResult;
import eu.archivesportaleurope.util.ApeUtil;

/**
 * @author bverhoef
 *
 */

public class EagSearchResult extends SearchResult{
	private static final String MDASH_SEPARATOR = "&nbsp;&mdash;&nbsp;";
	private static final String VIRTUAL_BAR_SEPARATOR = " | ";
	private static final String EMPTY = "";
	private static final String END_DIV = "</div>";
	private static final String START_DIV = "<div>";
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
	private String context;
	private String address;
	
	private SolrDocument solrDocument;
	private SpringResourceBundleSource resourceBundleSource;

	public EagSearchResult (SolrDocument solrDocument, Map<String, Map<String, List<String>>> highlightingMap, SpringResourceBundleSource resourceBundleSource){
		this.solrDocument = solrDocument;
		this.resourceBundleSource = resourceBundleSource;
		id = solrDocument.getFieldValue( Ead3SolrFields.ID).toString();
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
			this.otherNames =  getMultipleValued(highlightingMap,SolrFields.EAG_OTHER_NAMES,START_DIV,END_DIV, true, false);
			this.repositories = getMultipleValued(highlightingMap, SolrFields.EAG_REPOSITORIES,START_DIV,END_DIV, true, false);
		}
		this.repositoryCode = ApeUtil.encodeRepositoryCode((String) solrDocument.getFieldValue(Ead3SolrFields.REPOSITORY_CODE));

		this.other =  DisplayUtils.encodeHtmlWithHighlighting(SearchUtil.getHighlightedString(highlightingMap, id, SolrFields.EAG_OTHER, null));
		this.context = getMultipleValued(highlightingMap, SolrFields.EAG_AI_GROUPS,MDASH_SEPARATOR, EMPTY, true, true);
		this.address =  getMultipleValued(highlightingMap,SolrFields.EAG_ADDRESS, EMPTY, VIRTUAL_BAR_SEPARATOR, false, true);

	}
	private String getMultipleValued(Map<String, Map<String, List<String>>> highlightingMap, String solrField, String startSeparator, String endSeparator, boolean showAlwaysSeparators, boolean showAlways){
		List<String> temp= SearchUtil.getHighlightedStrings(highlightingMap, id, solrField);
		List<String> results = new ArrayList<String>();
		for (String tempItem: temp){
			if (showAlways || tempItem.contains(DisplayUtils.EM_START))
				results.add(DisplayUtils.encodeHtmlWithHighlighting(tempItem));
		}
		return getMultipleValues(results,startSeparator, endSeparator, showAlwaysSeparators, false, null);
		
	}
	protected String getMultipleValues(Collection<?> values, String startSeparator, String endSeparator, boolean showAlwaysSeparators, boolean valueIsKey, String prefix){
		String result = EMPTY;
		Iterator<?> valuesIterator = values.iterator();
		while (valuesIterator.hasNext()){
			Object value = valuesIterator.next();
			if (valueIsKey){
				if (prefix == null){
					value = resourceBundleSource.getString((String) value);
				}else {
					value = resourceBundleSource.getString(prefix +  value);
				}
			}
			if (valuesIterator.hasNext()){
				result += startSeparator + value + endSeparator;
			}else {
				if (showAlwaysSeparators){
					result += startSeparator + value + endSeparator;
				}else {
					result += value;
				}
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
		String country = solrDocument.getFieldValue(Ead3SolrFields.COUNTRY).toString();
		return getDescriptionFromString(country);
	}
	public String getRepositoryTypes(){
		Collection<Object> types= solrDocument.getFieldValues(SolrFields.EAG_REPOSITORY_TYPE);
		if (types == null){
			return null;
		}else {
			return getMultipleValues(types, EMPTY, VIRTUAL_BAR_SEPARATOR, false, true, FacetType.EAG_REPOSITORY_TYPE.getPrefix());
		}
	}
	
	public String getAddress(){
		return address;
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
	public String getContext() {
		return context;
	}


}
