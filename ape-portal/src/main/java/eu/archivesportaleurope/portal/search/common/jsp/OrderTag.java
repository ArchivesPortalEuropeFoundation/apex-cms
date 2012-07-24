package eu.archivesportaleurope.portal.search.common.jsp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.common.jsp.AbstractPortletTag;

public class OrderTag extends AbstractPortletTag implements DynamicAttributes {
	private final static String DEFAULT_VALUE = "relevancy";
	private String currentValue;
	private String value;
	private String key;
	private Map<String, Object> tagAttributes = new HashMap<String, Object>();

	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		tagAttributes.put(localName, value);
	}

	public void doTagInternal() throws JspException, IOException {
		boolean noOrdering =  StringUtils.isBlank(currentValue);
		if (noOrdering && DEFAULT_VALUE.equalsIgnoreCase(value)) {
			getJspContext().getOut().print("<span id=\"selectedOrder\">" + getValueFromResourceBundle(key)+ "</span>");
		} else if (noOrdering) {
			getJspContext().getOut().print(getLink());
		} else {
			if (value.equals(currentValue)){
				getJspContext().getOut().print("<span id=\"selectedOrder\">" + getValueFromResourceBundle(key) + "</span>");
			}else {
				getJspContext().getOut().print(getLink());
			}
			
		}

	}

	private StringBuilder getLink() {
		StringBuilder link = new StringBuilder();
		link.append("<a href=\"");
		link.append("javascript:updateSorting('" + value + "');");
		link.append("\" ");
		for (String attrName : tagAttributes.keySet()) {
			link.append(attrName);
			link.append("='");
			link.append(tagAttributes.get(attrName));
			link.append("' ");
		}
		link.append(">");
		link.append(getValueFromResourceBundle(key));
		link.append("</a>");
		return link;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}


	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}

}
