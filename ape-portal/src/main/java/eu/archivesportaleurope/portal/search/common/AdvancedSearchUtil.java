package eu.archivesportaleurope.portal.search.common;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;

import eu.apenet.commons.solr.SolrFields;

public final class AdvancedSearchUtil {

	public static String convertToLangmaterial(String language) {
		if (language != null && !language.isEmpty()) {
			// if (!this.getLanguage().equals("All languages"))
			int id = Integer.parseInt(language);
			if (id != 0) {

				switch (id) {
				case 1:
					language = "ger";
					break;
				case 2:
					language = "gre";
					break;
				case 3:
					language = "eng";
					break;
				case 4:
					language = "spa";
					break;
				case 5:
					language = "fre";
					break;
				case 6:
					language = "gle";
					break;
				case 7:
					language = "lav";
					break;
				case 8:
					language = "mlt";
					break;
				case 9:
					language = "dut";
					break;
				case 10:
					language = "pol";
					break;
				case 11:
					language = "por";
					break;
				case 12:
					language = "slv";
					break;
				case 13:
					language = "fin";
					break;
				case 14:
					language = "swe";
					break;
				}
			}
		}
		return null;
	}




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

	@Deprecated
	public static void addRefinement(Map<String, List<String>> orParameters, Map<String, List<String>> andParameters,
			String name, String toBeAdded) {
		if (StringUtils.isNotBlank(toBeAdded)) {
			orParameters.remove(name);
			List<String> list = new ArrayList<String>();
			list.add(toBeAdded);
			andParameters.put(name, list);
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


	public static boolean isValidDate(String date){
		return  obtainDate(date, true) != null;
	}

	public static void setFromDate(Map<String, List<String>> parameters, String fromDate, boolean exact) {
		if (StringUtils.isNotBlank(fromDate)) {
			String date = obtainDate(fromDate, true);
			if (exact) {
				setParameter(parameters, SolrFields.START_DATE, "[" + date + "T00:00:00Z TO *]");
			} else {
				setParameter(parameters, SolrFields.END_DATE, "[" + date + "T00:00:00Z TO *]");
			}
		}
	}
	public static void setToDate(Map<String, List<String>> parameters, String toDate, boolean exact) {
		if (StringUtils.isNotBlank(toDate)) {
			String date = obtainDate(toDate, false);
			if (exact) {
				setParameter(parameters, SolrFields.END_DATE, "[* TO " + date + "T23:59:59Z]");
			} else {
				setParameter(parameters, SolrFields.START_DATE, "[* TO " + date + "T23:59:59Z]");
			}
		}
	}

	private static String obtainDate(String onedate, boolean isStartDate) {
		String year = null;
		String month = null;
		String day = null;
		try {
			onedate = onedate.replace("/", "-");
			if (onedate.contains("-")) {
				String[] list = onedate.split("-");
				if (list.length >= 1) {
					year = list[0];
				}
				if (list.length >= 2) {
					month = list[1];
				}
				if (list.length >= 3) {
					day = list[2];
				}
			} else {
				if (onedate.length() >= 4) {
					year = onedate.substring(0, 4);
				}
				if (onedate.length() >= 6) {
					month = onedate.substring(4, 6);
				}
				if (onedate.length() >= 8) {
					day = onedate.substring(6, 8);
				}
			}
			return obtainDate(year, month, day, isStartDate);
		} catch (Exception ex) {

		}
		return null;
	}

	private static String obtainDate(String yearString, String monthString, String dayString, boolean isStartDate) {
		if (yearString != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			Integer year = new Integer(yearString);
			Integer month = null;
			Integer day = null;
			if (monthString != null) {
				month = new Integer(monthString);
			}
			if (dayString != null) {
				day = new Integer(dayString);
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
			calendar.setTimeInMillis(0);
			calendar.set(Calendar.YEAR, year);
			if (isStartDate) {
				if (month == null) {
					calendar.set(Calendar.MONTH, 0);
				} else {
					calendar.set(Calendar.MONTH, month - 1);
				}
				if (day == null) {
					calendar.set(Calendar.DAY_OF_MONTH, 1);
				} else {
					calendar.set(Calendar.DAY_OF_MONTH, day);
				}
				return dateFormat.format(calendar.getTime());// + "T00:00:01Z";
			} else {
				if (month == null) {
					calendar.set(Calendar.MONTH, 11);
				} else {
					calendar.set(Calendar.MONTH, month - 1);
				}
				if (day == null) {
					calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
				} else {
					calendar.set(Calendar.DAY_OF_MONTH, day);
				}
				return dateFormat.format(calendar.getTime());// + "T23:59:59Z";
			}
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
}
