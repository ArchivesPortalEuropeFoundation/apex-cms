package eu.archivesportaleurope.portal.search.common;

import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;

import eu.apenet.commons.ResourceBundleSource;
import eu.apenet.commons.utils.DisplayUtils;

public class FacetValue {
	private final static Logger LOGGER = Logger.getLogger(FacetValue.class);
	private final static SimpleDateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private static final char COLON = ':';
	private static final int MAX_NUMBER_OF_CHARACTERS = 25;
	private String id;
	private String description;
	private String numberOfResults;
	private boolean selected;

	public FacetValue(FacetField facetField, Count count, FacetType facetType, List<String> selectedItems,
			ResourceBundleSource resourceBundleSource) {
		if (facetType.isDate()) {
			initDateFacetValue(facetField, count, facetType, selectedItems, resourceBundleSource);
		} else {
			initNormalFacetValue(facetField, count, facetType, selectedItems, resourceBundleSource);
		}
	}

	private void initNormalFacetValue(FacetField facetField, Count count, FacetType facetType,
			List<String> selectedItems, ResourceBundleSource resourceBundleSource) {
		String value = count.getName();
		if (facetType.isHasId()) {
			int index = value.indexOf(COLON);
			int lastIndex = value.lastIndexOf(COLON);
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
				if (FacetType.COUNTRY.equals(facetType)){
					description = DisplayUtils.getLocalizedCountryName(resourceBundleSource, description);
				}else {
					description = resourceBundleSource.getString(facetType.getPrefix() + description.toLowerCase());
				}
			}

		}
		if (selectedItems != null) {
			selected = selectedItems.contains(id);
		} else {
			selected = false;
		}
		numberOfResults = NumberFormat.getInstance(resourceBundleSource.getLocale()).format(count.getCount());
	}

	public void initDateFacetValue(FacetField facetField, Count count, FacetType facetType, List<String> selectedItems,
			ResourceBundleSource resourceBundleSource) {
		String value = count.getName();
		int indexOfTimeSeparator = value.indexOf('T');
		String dateString = value.substring(0, indexOfTimeSeparator);
		String gapString = facetField.getGap().replace("+", "");
		DateGap dateGap = DateGap.getGapByName(gapString);
		String dateSpan ="";
		
		try {
			dateSpan = getDateSpan(dateGap, dateString);
		} catch (Exception e) {
			LOGGER.error("Unable to parse: " + value + " " + gapString,e);
		}
		DateGap nextDateGap = dateGap.next();
		if (nextDateGap != null) {
			id = dateString + "_" + nextDateGap.getId();
			description = dateSpan;
		}
		if (selectedItems != null) {
			selected = selectedItems.contains(id);
		} else {
			selected = false;
		}
		numberOfResults = NumberFormat.getInstance(resourceBundleSource.getLocale()).format(count.getCount());
	}

	public static String getDateSpan(DateGap dateGap, String dateString) throws ParseException {
		String result = "";
		Date beginDate = SOLR_DATE_FORMAT.parse(dateString);

		SimpleDateFormat dateFormat = new SimpleDateFormat(dateGap.getDateFormat());
		result += dateFormat.format(beginDate);
		if (!(Calendar.DAY_OF_MONTH == dateGap.getType() && dateGap.getAmount() == 1)) {
			if (dateGap.getAmount() > 1) {
				result += "-";
				Calendar endDateCalendar = Calendar.getInstance();
				endDateCalendar.setTime(beginDate);
				endDateCalendar.add(dateGap.getType(), (dateGap.getAmount() - 1));
				result += dateFormat.format(endDateCalendar.getTime());
			}
		}
		return result;
	}

	public String getNumberOfResults() {
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
