package eu.archivesportaleurope.portal.search.common;

import java.util.List;

import org.apache.solr.client.solrj.response.FacetField.Count;

import eu.apenet.commons.ResourceBundleSource;
import eu.apenet.commons.utils.DisplayUtils;

public class FacetValue {
	private static final char COLON = ':';
	private static final int MAX_NUMBER_OF_CHARACTERS = 25;
	private final String id;
	private String description;
	private final Long numberOfResults;
	private final boolean selected;
	public FacetValue(Count count, FacetType facetType, List<String> selectedItems,ResourceBundleSource resourceBundleSource ){
		String value = count.getName();
		if (facetType.isHasId()) {
			int index = value.indexOf(COLON);
			int lastIndex =value.lastIndexOf(COLON);
			id = value.substring(lastIndex + 1);
			description = value.substring(0, index);
		} else {
			id = value;
			description = value;
		}
		if (facetType.isValueIsKey()) {
			if (facetType.getPrefix() == null) {
				
				description = resourceBundleSource.getString(description.toLowerCase());
			} else {
				description = resourceBundleSource.getString(facetType.getPrefix() + description.toLowerCase());
			}

		}
		if (selectedItems != null){
			selected = selectedItems.contains(id);
		}else {
			selected = false;
		}
		numberOfResults = count.getCount();
	}
	public Long getNumberOfResults() {
		return numberOfResults;
	}
	public boolean isSelected() {
		return selected;
	}
	public String getHtmlShortDescription() {
		return DisplayUtils.encodeHtml(description, MAX_NUMBER_OF_CHARACTERS);
	}

	public String getId() {
		return id;
	}

	public String getHtmlLongDescription() {
		return DisplayUtils.encodeHtml(description);
	}

	public String getJavascriptLongDescription() {
		return DisplayUtils.escapeJavascript(getHtmlLongDescription());
	}
}
