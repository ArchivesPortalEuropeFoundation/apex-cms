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

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.archivesportaleurope.portal.common.jsp.AbstractPortletTag;
import eu.archivesportaleurope.portal.search.advanced.list.DateGap;

public class DateRemoveFacetTag extends AbstractPortletTag implements DynamicAttributes {
	private final static Logger LOGGER = Logger.getLogger(DateRemoveFacetTag.class);
	private final static SimpleDateFormat SOLR_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	private String currentValue;
	private String name;
//	private String value;
	private String gap;

	private Map<String, Object> tagAttributes = new HashMap<String, Object>();

	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		tagAttributes.put(localName, value);
	}

	public void doTagInternal() throws JspException, IOException {
		if (StringUtils.isNotBlank(currentValue)){
			String[] splittedStartDate = currentValue.split("_");
			String startDateString = splittedStartDate[0];
			String gapString = splittedStartDate[1];
			DateGap dateGap = DateGap.getGapById(gapString);
			if (dateGap == null) {
				this.getJspContext().getOut().print("ERROR");
			} else {
				DateGap dateGapPrevious = dateGap.previous();
				if (dateGapPrevious == null){
					
				}else {
					StringBuilder link = new StringBuilder();
					link.append("<a href=\"");
					link.append("javascript:removeRefinement('" + name + "');");
					link.append("\" ");
					for (String attrName : tagAttributes.keySet()) {
						link.append(attrName);
						link.append("='");
						link.append(tagAttributes.get(attrName));
						link.append("' ");
					}
					String dateSpan = getDateSpan(dateGapPrevious, startDateString);
					link.append(" title=\"");
					link.append(dateSpan);
					link.append("\" ");
					link.append(">");
					link.append(dateSpan);
	
					this.getJspContext().getOut().print(link);
					JspFragment body = getJspBody();
					if (body != null) {
						body.invoke(this.getJspContext().getOut());
					}
					this.getJspContext().getOut().print("</a>");
			
				}
			}
		}
			


	}
	private String getDateSpan(DateGap dateGapPrevious, String startDateString){
		String result = "";
		try {
			Date beginDate = SOLR_DATE_FORMAT.parse(startDateString);

			SimpleDateFormat dateFormat = new SimpleDateFormat(dateGapPrevious.getDateFormat());
			result += dateFormat.format(beginDate);
			if (!(Calendar.DAY_OF_MONTH == dateGapPrevious.getType() && dateGapPrevious.getAmount() == 1)){
				if (dateGapPrevious.getAmount() > 1){
					result += "-";
					Calendar endDateCalendar = Calendar.getInstance();
					endDateCalendar.setTime(beginDate);
					endDateCalendar.add(dateGapPrevious.getType(), (dateGapPrevious.getAmount()-1));
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



	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

	public String getGap() {
		return gap;
	}

	public void setGap(String gap) {
		this.gap = gap;
	}

}
