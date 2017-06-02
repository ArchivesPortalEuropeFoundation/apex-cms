package eu.archivesportaleurope.portal.display.eac.jsp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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
import eu.apenet.commons.utils.APEnetUtilities;

public class EacTag extends SimpleTagSupport {
	private Logger logger = Logger.getLogger(this.getClass());

	public static final String EACCPFDETAILS_XSLT = "eaccpfdetails";
	public static final String EACCPFDETAILS_PREVIEW_XSLT = "eaccpfdetailspreview";
	private String xml;
	private String searchTerms;
	private String searchFieldsSelectionId;
	private String secondDisplayUrl;
	private String aiId;
	private String type;
	private String databaseId;
	private String eacUrl;
	private String repositoryCode;
	private String eaccpfIdentifier;
	private String translationLanguage;
	// Variables for the relations.
	private String aiCodeUrl;
	private String eacUrlBase;
	private String eadUrl;
	private String langNavigator;
	private String dashboardPreview;

	private final static Logger LOG = Logger.getLogger(EacTag.class);
	private static final List<SolrField> DEFAULT_HIGHLIGHT_FIELDS = SolrField.getDefaults();

	private final static Map<String, String> xsltUrls = new HashMap<String,String>();
	static {
		xsltUrls.put(EACCPFDETAILS_XSLT, "xsl/eaccpf/eaccpfdetails.xsl");
		xsltUrls.put(EACCPFDETAILS_PREVIEW_XSLT,"xsl/eaccpf/eaccpfdetails-preview.xsl");
	}
	
	public final void doTag() throws JspException, IOException {		
		try {
			File file= new File(APEnetUtilities.getApePortalAndDashboardConfig().getRepoDirPath() + this.getEacUrl());
			if (file.exists()){
				FileReader eacFile = new FileReader(file);
				Source xmlSource = new StreamSource(new StringReader(this.readFile(eacFile)));
				List<SolrField> highlightFields = SolrField.getSolrFieldsByIdString(searchFieldsSelectionId);
				if (highlightFields.size() == 0) {
					highlightFields = DEFAULT_HIGHLIGHT_FIELDS;
				}

				Integer aiIdInt = null;
				if (StringUtils.isNotBlank(aiId)) {
					aiIdInt = Integer.parseInt(aiId);
				}
				String xslLocation = xsltUrls.get(getType());
				if (xslLocation == null){
					LOG.warn("EAC-CPF xsl type does not exist: " + getType());
				}else {
					EacXslt.convertEacToHtml(xslLocation, this.getJspContext().getOut(), xmlSource, searchTerms,
							highlightFields, getResourceBundleSource(), secondDisplayUrl, aiIdInt, "true".equalsIgnoreCase(getDashboardPreview()),
							APEnetUtilities.getApePortalConfig().getSolrStopwordsUrl(), this.getTranslationLanguage(), this.getAiCodeUrl(),
							this.getEacUrlBase(), this.getEadUrl(), this.getLangNavigator());
				}
			}else {
				try {
					LOG.error("No file: " + file.getCanonicalPath());
				} catch (IOException e) {
				}
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private String readFile(FileReader file) throws IOException {
		BufferedReader bufferedReader = new BufferedReader(file);
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String lineSeparator = System.getProperty("line.separator");

		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(lineSeparator);
		}

		return stringBuilder.toString();
	}
	protected ResourceBundleSource getResourceBundleSource() {
		LocalizationContext locCtxt = BundleSupport.getLocalizationContext((PageContext) this.getJspContext());
		if (locCtxt != null) {
			return new DefaultResourceBundleSource(locCtxt.getResourceBundle());
		} else {
			logger.error("Unable to find the localizationContext");
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
	
	public String getLangNavigator(){
		return this.langNavigator;
	}
	
	public void setLangNavigator(String langNavigator) {
		this.langNavigator = langNavigator;
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

	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public String getEacUrl() {
		return eacUrl;
	}

	public void setEacUrl(String eacUrl) {
		this.eacUrl = eacUrl;
	}

	/**
	 * @return the repositoryCode
	 */
	public String getRepositoryCode() {
		return this.repositoryCode;
	}

	/**
	 * @param repositoryCode the repositoryCode to set
	 */
	public void setRepositoryCode(String repositoryCode) {
		this.repositoryCode = repositoryCode;
	}

	/**
	 * @return the eaccpfIdentifier
	 */
	public String getEaccpfIdentifier() {
		return this.eaccpfIdentifier;
	}

	/**
	 * @param eaccpfIdentifier the eaccpfIdentifier to set
	 */
	public void setEaccpfIdentifier(String eaccpfIdentifier) {
		this.eaccpfIdentifier = eaccpfIdentifier;
	}

	/**
	 * @return the translationLanguage
	 */
	public String getTranslationLanguage() {
		return this.translationLanguage;
	}

	/**
	 * @param translationLanguage the translationLanguage to set
	 */
	public void setTranslationLanguage(String translationLanguage) {
		this.translationLanguage = translationLanguage;
	}

	/**
	 * @return the aiCodeUrl
	 */
	public String getAiCodeUrl() {
		return this.aiCodeUrl;
	}

	/**
	 * @param aiCodeUrl the aiCodeUrl to set
	 */
	public void setAiCodeUrl(String aiCodeUrl) {
		this.aiCodeUrl = aiCodeUrl;
	}

	/**
	 * @return the eacUrlBase
	 */
	public String getEacUrlBase() {
		return this.eacUrlBase;
	}

	/**
	 * @param eacUrlBase the eacUrlBase to set
	 */
	public void setEacUrlBase(String eacUrlBase) {
		this.eacUrlBase = eacUrlBase;
	}

	/**
	 * @return the eadUrl
	 */
	public String getEadUrl() {
		return this.eadUrl;
	}

	/**
	 * @param eadUrl the eadUrl to set
	 */
	public void setEadUrl(String eadUrl) {
		this.eadUrl = eadUrl;
	}

	public String getDashboardPreview() {
		return dashboardPreview;
	}

	public void setDashboardPreview(String dashboardPreview) {
		this.dashboardPreview = dashboardPreview;
	}
	

}
