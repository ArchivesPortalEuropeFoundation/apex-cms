package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.apenet.commons.solr.SolrValues;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.util.ApeUtil;

public class EadPersistentUrlTag extends SimpleTagSupport {
	private static final String NPID = "npid";
	private static final String SEARCH = "search";
	private static final String UNITID = "unitid";
	private static final String EADID = "eadid";
	private static final String TYPE = "type";
	private static final String AICODE = "aicode";
	private final static Logger LOGGER = Logger.getLogger(EadPersistentUrlTag.class);
	private String repoCode;
	private String eadid;
	private String xmlTypeName;
	private String unitid;
	private String searchFieldsSelectionId;
	private String searchTerms;
	private String searchId;
	private String pageNumber;

	private String var;
	private String noHttps;

	@Override
	public void doTag() throws JspException, IOException {
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		boolean noHttpsBoolean = "true".equalsIgnoreCase(noHttps);
		String url = FriendlyUrlUtil.getUrl(portletRequest, FriendlyUrlUtil.EAD_DISPLAY_PERSISTENT, noHttpsBoolean) ;
		url+= FriendlyUrlUtil.SEPARATOR + AICODE + FriendlyUrlUtil.SEPARATOR + ApeUtil.encodeRepositoryCode(repoCode)
				+ FriendlyUrlUtil.SEPARATOR + TYPE + FriendlyUrlUtil.SEPARATOR+ xmlTypeName + FriendlyUrlUtil.SEPARATOR + EADID + FriendlyUrlUtil.SEPARATOR
				+ ApeUtil.encodeSpecialCharacters(eadid);
		if (StringUtils.isBlank(unitid)) {
			if (StringUtils.isNotBlank(searchId)) {
				String solrPrefix = searchId.substring(0, 1);
				if (SolrValues.C_LEVEL_PREFIX.equals(solrPrefix)) {
					url+= FriendlyUrlUtil.SEPARATOR + NPID +FriendlyUrlUtil.SEPARATOR + searchId;
				}
			}
		}else {
			url+= FriendlyUrlUtil.SEPARATOR + UNITID + FriendlyUrlUtil.SEPARATOR+ ApeUtil.encodeSpecialCharacters(unitid);
		}


		if (StringUtils.isNotBlank(searchTerms)) {
			url += FriendlyUrlUtil.SEPARATOR + SEARCH + FriendlyUrlUtil.SEPARATOR + searchFieldsSelectionId + FriendlyUrlUtil.SEPARATOR
					+ ApeUtil.encodeSpecialCharacters(searchTerms);
		}
		getJspContext().setAttribute(var, url);
		super.doTag();
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setNoHttps(String noHttps) {
		this.noHttps = noHttps;
	}

	public void setRepoCode(String repoCode) {
		this.repoCode = repoCode;
	}

	public void setEadid(String eadid) {
		this.eadid = eadid;
	}

	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}


	public void setXmlTypeName(String xmlTypeName) {
		this.xmlTypeName = xmlTypeName;
	}

	public void setSearchFieldsSelectionId(String searchFieldsSelectionId) {
		this.searchFieldsSelectionId = searchFieldsSelectionId;
	}



	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}


}
