package eu.archivesportaleurope.portal.search.common.jsp;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.DynamicAttributes;
import javax.servlet.jsp.tagext.JspFragment;

import eu.apenet.commons.utils.DisplayUtils;
import eu.archivesportaleurope.portal.common.jsp.AbstractPortletTag;

public class FacetTag extends AbstractPortletTag implements DynamicAttributes {
	private static final String TRUE = "true";
	private static final char COLON = ':';
	private static final int MAX_NUMBER_OF_CHARACTERS = 25;
	private String name;
	private String value;
	private String remove;
	private String keyPrefix;
	private String valueIsKey;
	private String hasId;
	private String currentValue;

	private String id;
	private String description;
	private Map<String, Object> tagAttributes = new HashMap<String, Object>();

	protected String getShortDescription() {
		return DisplayUtils.encodeHtml(description, MAX_NUMBER_OF_CHARACTERS);
	}

	protected String getUrlParameter() {
		return id;
	}

	protected String getLongDescription() {
		return description;
	}

	public void setDynamicAttribute(String uri, String localName, Object value) throws JspException {
		tagAttributes.put(localName, value);
	}

	public void doTagInternal() throws JspException, IOException {

		if (TRUE.equalsIgnoreCase(hasId)) {
			int index = getValue().indexOf(COLON);
			int lastIndex = getValue().lastIndexOf(COLON);
			id = getValue().substring(lastIndex + 1);
			description = getValue().substring(0, index);
		} else {
			id = value;
			description = value;
		}
		if (TRUE.equalsIgnoreCase(valueIsKey)) {
			if (keyPrefix == null) {
				description = getValueFromResourceBundle(description.toLowerCase());
			} else {
				description = getValueFromResourceBundle(keyPrefix + description.toLowerCase());
			}

		}
		boolean isRemoveFacet = TRUE.equalsIgnoreCase(remove);
		boolean display = true;
		if (isRemoveFacet && currentValue != null &&  !currentValue.equals(id)){
			display = false;
		}
		if (display){
			StringBuilder link = new StringBuilder();
			link.append("<a href=\"");
			if (isRemoveFacet) {
				link.append("javascript:removeRefinement('" + name + "');");
			} else {
				link.append("javascript:addRefinement('" + name + "','" + getUrlParameter() + "');");
			}
	
			
			link.append("\" ");
			for (String attrName : tagAttributes.keySet()) {
				link.append(attrName);
				link.append("='");
				link.append(tagAttributes.get(attrName));
				link.append("' ");
			}
			link.append("title='");
			link.append(getLongDescription());
			link.append('\'');
			link.append(">");
			link.append(getShortDescription());
			this.getJspContext().getOut().print(link);
			JspFragment body = getJspBody();
			if (body != null) {
				body.invoke(this.getJspContext().getOut());
			}
			this.getJspContext().getOut().print("</a>");
		}
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

	public String getRemove() {
		return remove;
	}

	public void setRemove(String remove) {
		this.remove = remove;
	}

	public String getKeyPrefix() {
		return keyPrefix;
	}

	public void setKeyPrefix(String keyPrefix) {
		this.keyPrefix = keyPrefix;
	}

	public String getValueIsKey() {
		return valueIsKey;
	}

	public void setValueIsKey(String valueIsKey) {
		this.valueIsKey = valueIsKey;
	}

	public String getHasId() {
		return hasId;
	}

	public void setHasId(String hasId) {
		this.hasId = hasId;
	}

	public String getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(String currentValue) {
		this.currentValue = currentValue;
	}




}
