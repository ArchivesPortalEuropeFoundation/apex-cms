package eu.archivesportaleurope.portal.search.common.jsp;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspFragment;

import org.apache.log4j.Logger;

import eu.archivesportaleurope.portal.common.jsp.AbstractPortletTag;
import eu.archivesportaleurope.portal.search.common.DateGap;

public class DateFacetTag extends AbstractPortletTag implements DynamicAttributes {
	private final static Logger LOGGER = Logger.getLogger(DateFacetTag.class);
	private final static SimpleDateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
	private String name;
	private String value;
	private String gap;

	private Map<String, Object> tagAttributes = new HashMap<String, Object>();

	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		tagAttributes.put(localName, value);
	}

	public void doTagInternal() throws JspException, IOException {
		int indexOfTimeSeparator = value.indexOf('T');
		String dateString = value.substring(0, indexOfTimeSeparator);
		String gapString = gap.replace("+", "");
		DateGap dateGap = DateGap.getGapByName(gapString);

		if (dateGap == null) {
			this.getJspContext().getOut().print("ERROR");
		} else {
			String dateSpan = getDateSpan(dateGap, dateString);
			DateGap nextDateGap = dateGap.next();
			StringBuilder link = new StringBuilder();
			if (nextDateGap != null) {
				link.append("<a href=\"");
				String description = getValueFromResourceBundle("advancedsearch.facet.title." + name) + " " + dateSpan;
				link.append("javascript:addDateRefinement('" + name + "','" + dateString + "_" + nextDateGap.getId()
						+ "','" + description + "','" + description + "');");

				link.append("\" ");
				for (String attrName : tagAttributes.keySet()) {
					link.append(attrName);
					link.append("='");
					link.append(tagAttributes.get(attrName));
					link.append("' ");
				}
				link.append(" title=\"");
				link.append(dateSpan);
				link.append("\" ");
				link.append(">");
				link.append(dateSpan);
			}

			this.getJspContext().getOut().print(link);
			JspFragment body = getJspBody();
			if (body != null) {
				body.invoke(this.getJspContext().getOut());
			}
			if (nextDateGap != null) {
				this.getJspContext().getOut().print("</a>");
			}
		}

	}

	private String getDateSpan(DateGap dateGap, String dateString) {
		String result = "";
		try {
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
		} catch (ParseException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return result;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getGap() {
		return gap;
	}

	public void setGap(String gap) {
		this.gap = gap;
	}

}
