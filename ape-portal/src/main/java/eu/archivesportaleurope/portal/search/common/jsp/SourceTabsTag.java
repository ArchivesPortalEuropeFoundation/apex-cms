package eu.archivesportaleurope.portal.search.common.jsp;

import java.io.IOException;
import java.text.NumberFormat;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

import eu.apenet.commons.DefaultResourceBundleSource;
import eu.apenet.commons.ResourceBundleSource;

public class SourceTabsTag extends SimpleTagSupport {
	private static final Logger LOGGER = Logger.getLogger(SourceTabsTag.class);
	private Object results;
	private String ajax;
	private String type;

	public void doTag() throws JspException, IOException {
		NumberFormat numberFormat = NumberFormat.getInstance(((PageContext) this.getJspContext()).getRequest().getLocale());
		JspWriter writer = this.getJspContext().getOut();
		if ("true".equalsIgnoreCase(ajax)){
			writer.append("<div class=\"hidden\" id=\"NEWsourceTabs\">");
		}else {
			writer.append("<div id=\"sourceTabs\" class=\"ui-tabs ui-widget ui-widget-content ui-corner-all\">");
		}
		writer.append("<ul id=\"tabscontainer\" class=\"ui-tabs-nav ui-helper-reset ui-helper-clearfix ui-widget-header ui-corner-all\">");
		addTab("ead","menu.archives-search", "ead-search" ,numberFormat);
		addTab("eacCpf","menu.name-search", "name-search" , numberFormat);
		addTab("eag","menu.institution-search", "institution-search", numberFormat );
		writer.append("</ul>");
		writer.append("<div class=\"icon_help\"></div>");
		writer.append("<div class=\"tab_header\">");
		writer.append("<div id=\"tabHeaderContent\"></div>");
		writer.append("</div>");
		writer.append("</div>");
	}
	private void addTab(String tabType, String tabName, String tabFunction,NumberFormat numberFormat) throws IOException{
		JspWriter writer = this.getJspContext().getOut();
		
		if (results == null){
			writer.append("<li class=\"ui-state-default ui-corner-top");
			if (tabType.equalsIgnoreCase(type)){
				writer.append(" ui-tabs-selected ui-state-active\"><a href=\"javascript:void(0);\"");
			}else {
				writer.append("\"><a href=\"javascript:changeSearch('"+ tabFunction + "')\"");
			}
			writer.append(">" +getResourceBundleSource().getString(tabName) + "</a></li>");
		}else {
			BeanWrapper searchBeanWrapper = PropertyAccessorFactory.forBeanPropertyAccess( this.results);
			writer.append("<li class=\"ui-state-default ui-corner-top");
			String otherCssClass = "disabled";
			Long numberOfResults = (Long) searchBeanWrapper.getPropertyValue(tabType + "NumberOfResults");
			if (numberOfResults > 0){
				otherCssClass ="";
			}
			if (tabType.equalsIgnoreCase(type)){
				writer.append(" ui-tabs-selected ui-state-active\"><a href=\"javascript:void(0);\">");
			}else {
				writer.append(" " + otherCssClass + "\"><a href=\"javascript:changeSearch('"+ tabFunction + "')\">");
			}
			writer.append(getResourceBundleSource().getString(tabName));
			if (numberOfResults > 0){
				
				writer.append("<span class=\"numberOfResults\">"+numberFormat.format(numberOfResults)+"</span>");
			}
			writer.append("</a></li>");			
		}
	}
	protected ResourceBundleSource getResourceBundleSource() {
		LocalizationContext locCtxt = BundleSupport.getLocalizationContext((PageContext) this.getJspContext());
		if (locCtxt != null) {
			return new DefaultResourceBundleSource(locCtxt.getResourceBundle());
		} else {
			LOGGER.error("Unable to find the localizationContext");
			return new DefaultResourceBundleSource(null);
		}
	}
	
	public Object getResults() {
		return results;
	}

	public void setResults(Object results) {
		this.results = results;
	}

	public String getAjax() {
		return ajax;
	}

	public void setAjax(String ajax) {
		this.ajax = ajax;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


}
