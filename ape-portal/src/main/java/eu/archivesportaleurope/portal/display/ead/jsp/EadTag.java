package eu.archivesportaleurope.portal.display.ead.jsp;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.jstl.fmt.LocalizationContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.taglibs.standard.tag.common.fmt.BundleSupport;

import eu.apenet.commons.DefaultResourceBundleSource;
import eu.apenet.commons.ResourceBundleSource;
import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.types.XmlType;
import eu.apenet.commons.utils.APEnetUtilities;

public class EadTag extends SimpleTagSupport {

    private final static Logger LOG = Logger.getLogger(EadTag.class);

    public static final String CDETAILS_CHILD_XSLT = "cdetails-child";
    public static final String FRONTPAGE_XSLT = "frontpage";
    public static final String CDETAILS_XSLT = "cdetails";
    private String xml;
    private String searchTerms;
    private String searchFieldsSelectionId;
    private String secondDisplayUrl;
    private String aiId;
    private String type;
    private String preview;
    private String dashboardPreview;
    private String xmlTypeName;
    private String eacUrl;

    private static final List<SolrField> DEFAULT_HIGHLIGHT_FIELDS = SolrField.getDefaults();

    private final static Map<String, String> xsltUrls = new HashMap<String, String>();

    static {
        xsltUrls.put(CDETAILS_XSLT, "xsl/ead/cdetails.xsl");
        xsltUrls.put(CDETAILS_CHILD_XSLT, "xsl/ead/cdetails-child.xsl");
        xsltUrls.put(FRONTPAGE_XSLT, "xsl/ead/frontpage.xsl");

    }

    public final void doTag() throws JspException, IOException {
        Source xmlSource = new StreamSource(new StringReader(xml));
        List<SolrField> highlightFields = SolrField.getSolrFieldsByIdString(searchFieldsSelectionId);
        if (highlightFields.size() == 0) {
            highlightFields = DEFAULT_HIGHLIGHT_FIELDS;
        }
        try {
            Integer aiIdInt = null;
            if (StringUtils.isNotBlank(aiId)) {
                aiIdInt = Integer.parseInt(aiId);
            }
            String xslLocation = xsltUrls.get(getType());
            if (xslLocation == null) {
                LOG.warn("EAD xsl type does not exist: " + getType());
            } else {
                String typeOfDisplay = "normal";
                if ("true".equalsIgnoreCase(getPreview())) {
                    typeOfDisplay = "preview";
                } else if (CDETAILS_CHILD_XSLT.equalsIgnoreCase(getType())) {
                    typeOfDisplay = "child";
                }
                XmlType xmlType = XmlType.getTypeByResourceName(xmlTypeName);
                EadXslt.convertEadToHtml(xslLocation, this.getJspContext().getOut(), xmlSource, searchTerms,
                        highlightFields, getResourceBundleSource(), secondDisplayUrl, aiIdInt,
                        "true".equalsIgnoreCase(getDashboardPreview()), APEnetUtilities.getApePortalConfig().getSolrStopwordsUrl(),
                        typeOfDisplay, xmlType, this.getEacUrl());
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    protected ResourceBundleSource getResourceBundleSource() {
        LocalizationContext locCtxt = BundleSupport.getLocalizationContext((PageContext) this.getJspContext());
        if (locCtxt != null) {
            return new DefaultResourceBundleSource(locCtxt.getResourceBundle());
        } else {
            LOG.error("Unable to find the localizationContext");
            return new DefaultResourceBundleSource(null);
        }
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getSearchTerms() {
        return searchTerms;
    }

    public void setSearchTerms(String searchTerms) {
        this.searchTerms = searchTerms;
    }

    public String getSearchFieldsSelectionId() {
        return searchFieldsSelectionId;
    }

    public void setSearchFieldsSelectionId(String searchFieldsSelectionId) {
        this.searchFieldsSelectionId = searchFieldsSelectionId;
    }

    public String getSecondDisplayUrl() {
        return secondDisplayUrl;
    }

    public void setSecondDisplayUrl(String secondDisplayUrl) {
        this.secondDisplayUrl = secondDisplayUrl;
    }

    public String getAiId() {
        return aiId;
    }

    public void setAiId(String aiId) {
        this.aiId = aiId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getDashboardPreview() {
        return dashboardPreview;
    }

    public void setDashboardPreview(String dashboardPreview) {
        this.dashboardPreview = dashboardPreview;
    }

    public String getXmlTypeName() {
        return xmlTypeName;
    }

    public void setXmlTypeName(String xmlTypeName) {
        this.xmlTypeName = xmlTypeName;
    }

    public String getEacUrl() {
        return eacUrl;
    }

    public void setEacUrl(String eacUrl) {
        this.eacUrl = eacUrl;
    }
}
