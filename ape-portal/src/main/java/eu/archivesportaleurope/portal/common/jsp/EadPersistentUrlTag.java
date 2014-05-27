package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;

import eu.apenet.persistence.vo.CLevel;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.urls.EadPersistentUrl;

public class EadPersistentUrlTag extends SimpleTagSupport {
	private String repoCode;
	private String eadid;
	private String xmlTypeName;
	private String unitid;
	private String searchFieldsSelectionId;
	private String searchTerms;
	private String searchId;
	private String pageNumber;
	private Object clevel;
	private String var;
	private String noHttps;

	@Override
	public void doTag() throws JspException, IOException {
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		boolean noHttpsBoolean = "true".equalsIgnoreCase(noHttps);
		EadPersistentUrl eadPersistentUrl = new EadPersistentUrl(repoCode, xmlTypeName, eadid);
		if (clevel != null){
			eadPersistentUrl.setClevel((CLevel) clevel);
		}else {
			eadPersistentUrl.setUnitid(unitid);
			if (StringUtils.isNumeric(searchId)){
				eadPersistentUrl.setSearchIdAsLong(Long.parseLong(searchId));
			}else {
				eadPersistentUrl.setSearchId(searchId);
			}
		}
		eadPersistentUrl.setSearchFieldsSelectionId(searchFieldsSelectionId);
		eadPersistentUrl.setSearchTerms(searchTerms);

		eadPersistentUrl.setPageNumber(pageNumber);
		String url = FriendlyUrlUtil.getEadPersistentUrl(portletRequest, eadPersistentUrl, noHttpsBoolean);
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

	public void setClevel(Object clevel) {
		this.clevel = clevel;
	}


}
