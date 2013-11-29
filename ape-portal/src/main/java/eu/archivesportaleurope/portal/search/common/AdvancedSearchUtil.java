package eu.archivesportaleurope.portal.search.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import eu.apenet.commons.solr.SolrFields;
import eu.archivesportaleurope.portal.common.al.AlType;

public final class AdvancedSearchUtil {

	private static final String YYYY = "yyyy";
	private static final String YYYY_MM = "yyyy-MM";
	private static final String YYYY_MM_DD = "yyyy-MM-dd";

	public static void setParameter(Map<String, List<String>> parameters, String name, String toBeAdded) {
		if (StringUtils.isNotBlank(toBeAdded)) {
			List<String> list = new ArrayList<String>();
			list.add(toBeAdded);
			parameters.put(name, list);
		}
	}

	public static void setParameter(Map<String, List<String>> parameters, String name, List<String> toBeAdded) {
		if (toBeAdded != null && toBeAdded.size() > 0) {
			parameters.put(name, toBeAdded);
		}
	}

	public static void addRefinement(SolrQueryParameters solrQueryParameters, String name, String toBeAdded) {
		if (StringUtils.isNotBlank(toBeAdded)) {
			solrQueryParameters.getOrParameters().remove(name);
			List<String> list = new ArrayList<String>();
			list.add(toBeAdded);
			solrQueryParameters.getAndParameters().put(name, list);
		}
	}

	public static void addRefinement(SolrQueryParameters solrQueryParameters, FacetType facet, String toBeAdded) {
		if (StringUtils.isNotBlank(toBeAdded)) {
			solrQueryParameters.getOrParameters().remove(facet.getRefinementFieldWithLabel());
			List<String> list = new ArrayList<String>();
			list.add(toBeAdded);
			solrQueryParameters.getAndParameters().put(facet.getRefinementFieldWithLabel(), list);
		}
	}

	public static void addRefinement(SolrQueryParameters solrQueryParameters, FacetType facet, List<String> toBeAdded) {
		if (toBeAdded != null && toBeAdded.size() > 0) {
			// solrQueryParameters.getOrParameters().remove(facet.getRefinementField());
			solrQueryParameters.getAndParameters().put(facet.getRefinementFieldWithLabel(), toBeAdded);
		}
	}

	public static boolean isValidDate(String date) {
		return parseDate(date, true) != null;
	}

	public static void setFromDate(Map<String, List<String>> parameters, String fromDate, boolean exact) {
		if (StringUtils.isNotBlank(fromDate)) {
			String date = parseDate(fromDate, true);
			if (exact) {
				setParameter(parameters, SolrFields.START_DATE, "[" + date + "T00:00:00Z TO *]");
			} else {
				setParameter(parameters, SolrFields.END_DATE, "[" + date + "T00:00:00Z TO *]");
			}
		}
	}

	public static void setToDate(Map<String, List<String>> parameters, String toDate, boolean exact) {
		if (StringUtils.isNotBlank(toDate)) {
			String date = parseDate(toDate, false);
			if (exact) {
				setParameter(parameters, SolrFields.END_DATE, "[* TO " + date + "T23:59:59Z]");
			} else {
				setParameter(parameters, SolrFields.START_DATE, "[* TO " + date + "T23:59:59Z]");
			}
		}
	}

	protected static String parseDate(String onedate, boolean isStartDate) {
		String dateString = onedate.replace("/", "-");
		int numberOfMatches = StringUtils.countMatches(dateString, "-");
		boolean isFullDate = numberOfMatches == 2;
		boolean isYearMonthDate = numberOfMatches == 1;
		boolean isOnlyYearDate = numberOfMatches == 0;
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
		boolean lenient = false;
		calendar.setLenient(lenient);
		Date date = null;
		SimpleDateFormat fullDateFormat = new SimpleDateFormat(YYYY_MM_DD);
		fullDateFormat.setCalendar(calendar);
		fullDateFormat.setLenient(lenient);


		/*
		 * full date parsing
		 */
		if (isFullDate) {
			try {

				date = fullDateFormat.parse(dateString);
				calendar.setTime(date);
			} catch (ParseException e) {

			}
		}
		/*
		 * year month date parsing
		 */
		else if (isYearMonthDate) {
			try {
				SimpleDateFormat yearMonthDateFormat = new SimpleDateFormat(YYYY_MM);
				yearMonthDateFormat.setCalendar(calendar);
				yearMonthDateFormat.setLenient(lenient);
				date = yearMonthDateFormat.parse(dateString);
				calendar.setTime(date);
				if (isStartDate) {
					calendar.set(Calendar.DAY_OF_MONTH, 1);
				} else {
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				}
			} catch (ParseException e1) {

			}
		}
		else if (isOnlyYearDate){
			/*
			 * year date parsing
			 */
			try {
				SimpleDateFormat yearDateFormat = new SimpleDateFormat(YYYY);
				yearDateFormat.setCalendar(calendar);
				yearDateFormat.setLenient(lenient);
				date = yearDateFormat.parse(dateString);
				calendar.setTime(date);
				if (isStartDate) {
					calendar.set(Calendar.MONTH, 0);
					calendar.set(Calendar.DAY_OF_MONTH, 1);
				} else {
					calendar.set(Calendar.MONTH, 11);
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				}
			} catch (ParseException e2) {
			}
		}
		if (date != null) {
			return fullDateFormat.format(calendar.getTime());
		}
		return null;
	}



	public static String getHighlightedString(Map<String, Map<String, List<String>>> highlightingMap, String id,
			String fieldName, String defaultValue) {
		try {
			return highlightingMap.get(id).get(fieldName).get(0);
		} catch (NullPointerException e) {
			return defaultValue;
		}

	}

	public static void addSelectedNodesToQuery(List<String> selectedNodes, SolrQueryParameters solrQueryParameters) {
		if (selectedNodes != null) {
			// Adding the ids of the Finding Aid selected for searching
			List<String> faHgIdsSelected = new ArrayList<String>();
			List<String> archivalInstitutionsIdsSelected = new ArrayList<String>();
			List<String> countriesSelected = new ArrayList<String>();
			for (String item : selectedNodes) {
				AlType alType = AlType.getAlType(item);
				Long id = AlType.getId(item);
				if (AlType.COUNTRY.equals(alType)) {
					countriesSelected.add(id.toString());
				} else if (AlType.ARCHIVAL_INSTITUTION.equals(alType)) {
					archivalInstitutionsIdsSelected.add(id.toString());
				} else if (AlType.FINDING_AID.equals(alType)) {
					faHgIdsSelected.add(alType.toString() + id);
				} else if (AlType.SOURCE_GUIDE.equals(alType)) {
					faHgIdsSelected.add(alType.toString() + id);
				} else if (AlType.HOLDINGS_GUIDE.equals(alType)) {
					faHgIdsSelected.add(alType.toString() + id);
				}
			}

			if (countriesSelected.size() > 0) {
				AdvancedSearchUtil.setParameter(solrQueryParameters.getOrParameters(), SolrFields.COUNTRY_ID,
						countriesSelected);
			}
			if (archivalInstitutionsIdsSelected.size() > 0) {
				AdvancedSearchUtil.setParameter(solrQueryParameters.getOrParameters(), SolrFields.AI_ID,
						archivalInstitutionsIdsSelected);
			}

			AdvancedSearchUtil.setParameter(solrQueryParameters.getOrParameters(), SolrFields.FOND_ID, faHgIdsSelected);
		}
	}
	
	public static void addPublishedFromDate(String publishFromDate, SolrQueryParameters solrQueryParameters) {
		if (StringUtils.isNotBlank(publishFromDate)) {
			SimpleDateFormat fullDateFormat = new SimpleDateFormat(YYYY_MM_DD);
			Date date;
			try {
				date = fullDateFormat.parse(publishFromDate);
				String solrDate = fullDateFormat.format(date);
				setParameter(solrQueryParameters.getAndParameters(), SolrFields.TIMESTAMP, "[" + solrDate + "T00:00:00Z TO *]");
			} catch (ParseException e) {
			}
			
			
		}
	}
}
