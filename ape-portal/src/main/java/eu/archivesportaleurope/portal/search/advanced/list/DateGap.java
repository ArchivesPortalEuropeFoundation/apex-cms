package eu.archivesportaleurope.portal.search.advanced.list;

import java.util.Calendar;

public enum DateGap {

	TIME_SPAN_1("1",  "200YEARS", 200, Calendar.YEAR, 10, DateGap.ONLY_YEAR), 
	TIME_SPAN_2("2", "20YEARS", 20, Calendar.YEAR, 10, DateGap.ONLY_YEAR), 
	TIME_SPAN_3("3", "2YEARS", 2, Calendar.YEAR, 10, DateGap.ONLY_YEAR), 
	TIME_SPAN_4("4", "3MONTHS", 3, Calendar.MONTH, 8,DateGap.YEAR_MONTH), 
	TIME_SPAN_5("5", "1MONTHS", 1, Calendar.MONTH, 3, DateGap.YEAR_MONTH),
	TIME_SPAN_6("6", "1DAYS", 1, Calendar.DAY_OF_MONTH, 31, DateGap.FULL_DATE);


	private final static String ONLY_YEAR = "yyyy";
	private final static String YEAR_MONTH = "yyyy/MM";	
	private final static String FULL_DATE = "yyyy/MM/dd";		
	private final String name;
	private final int amount;
	private final int type;
	private final String dateFormat;
	private final int solrTimespan;
	private final String id;

	DateGap(String id, String name, int amount, int type, int solrTimespan, String dateFormat) {
		this.id = id;
		this.name = name;
		this.amount = amount;
		this.type = type;
		this.dateFormat = dateFormat;
		this.solrTimespan = solrTimespan*amount;
	}

	public String getName() {
		return name;
	}

	public int getAmount() {
		return amount;
	}

	public int getType() {
		return type;
	}

	public String getDateFormat() {
		return dateFormat;
	}
	

	public int getSolrTimespan() {
		return solrTimespan;
	}

	public DateGap next() {
		if (this.equals(TIME_SPAN_1)) {
			return TIME_SPAN_2;
		} else if (this.equals(TIME_SPAN_2)) {
			return TIME_SPAN_3;
		} else if (this.equals(TIME_SPAN_3)) {
			return TIME_SPAN_4;
		}else if (this.equals(TIME_SPAN_4)) {
			return TIME_SPAN_5;
		}else if (this.equals(TIME_SPAN_5)) {
			return TIME_SPAN_6;
		}
		return null;
	}

	public DateGap previous() {
		if (this.equals(TIME_SPAN_1)) {
			return null;
		} else if (this.equals(TIME_SPAN_2)) {
			return TIME_SPAN_1;
		} else if (this.equals(TIME_SPAN_3)) {
			return TIME_SPAN_2;
		}else if (this.equals(TIME_SPAN_4)) {
			return TIME_SPAN_3;
		}else if (this.equals(TIME_SPAN_5)) {
			return TIME_SPAN_4;
		}else if (this.equals(TIME_SPAN_6)) {
			return TIME_SPAN_5;
		}
		return null;
	}

	public static DateGap first() {
		return TIME_SPAN_1;
	}

	public static DateGap last() {
		return TIME_SPAN_5;
	}

	public static DateGap getGapById(String id) {
		if (TIME_SPAN_1.id.equals(id)) {
			return TIME_SPAN_1;
		} else if (TIME_SPAN_2.id.equals(id)) {
			return TIME_SPAN_2;
		} else if (TIME_SPAN_3.id.equals(id)) {
			return TIME_SPAN_3;
		}else if (TIME_SPAN_4.id.equals(id)) {
			return TIME_SPAN_4;
		}else if (TIME_SPAN_5.id.equals(id)) {
			return TIME_SPAN_5;
		}else if (TIME_SPAN_6.id.equals(id)) {
			return TIME_SPAN_6;
		}
		return null;
	}
	public static DateGap getGapByName(String name) {
		if (TIME_SPAN_1.getName().equals(name)) {
			return TIME_SPAN_1;
		} else if (TIME_SPAN_2.getName().equals(name)) {
			return TIME_SPAN_2;
		} else if (TIME_SPAN_3.getName().equals(name)) {
			return TIME_SPAN_3;
		}else if (TIME_SPAN_4.getName().equals(name)) {
			return TIME_SPAN_4;
		}else if (TIME_SPAN_5.getName().equals(name)) {
			return TIME_SPAN_5;
		}else if (TIME_SPAN_6.getName().equals(name)) {
			return TIME_SPAN_6;
		}
		return null;
	}
	public String getId() {
		return id;
	}
	
}
