package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;

import javax.portlet.PortletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.common.urls.EacCpfPersistentUrl;

public class EacCpfPersistentUrlTag extends SimpleTagSupport {
	private String repoCode;
	private String id;
	private String relation;
	private String searchFieldsSelectionId;
	private String searchTerms;

	private String var;
	private String noHttps;

	@Override
	public void doTag() throws JspException, IOException {
		PortletRequest portletRequest = (PortletRequest) ((PageContext) getJspContext()).getRequest().getAttribute(
				"javax.portlet.request");
		boolean noHttpsBoolean = "true".equalsIgnoreCase(noHttps);
		EacCpfPersistentUrl eacCpfPerstistentUrl = new EacCpfPersistentUrl(repoCode, id);
		eacCpfPerstistentUrl.setRelation(relation);
		eacCpfPerstistentUrl.setSearchFieldsSelectionId(searchFieldsSelectionId);
		eacCpfPerstistentUrl.setSearchTerms(searchTerms);

		String url = FriendlyUrlUtil.getEacCpfPersistentUrl(portletRequest, eacCpfPerstistentUrl, noHttpsBoolean);
		getJspContext().setAttribute(var, url);
		super.doTag();
	}

	public void setRepoCode(String repoCode) {
		this.repoCode = repoCode;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setRelation(String relation) {
		this.relation = relation;
	}

	public void setSearchFieldsSelectionId(String searchFieldsSelectionId) {
		this.searchFieldsSelectionId = searchFieldsSelectionId;
	}

	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public void setNoHttps(String noHttps) {
		this.noHttps = noHttps;
	}




}
