package eu.archivesportaleurope.portal.search.common.jsp;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Correction;

public class AutosuggestionTag extends SimpleTagSupport {
	private Object solrResponse;
	private String styleClass;
	private String numberOfResultsStyleClass;
	private String misSpelledStyleClass;

	public void doTag() throws JspException, IOException {
		QueryResponse response = (QueryResponse) solrResponse;
		List<Collation> suggestions = response.getSpellCheckResponse().getCollatedResults();
		if (suggestions != null) {
			for (Collation collation : suggestions) {
				String suggestion = collation.getCollationQueryString();
				for (Correction correction : collation.getMisspellingsAndCorrections()) {
					if (!correction.getOriginal().equals(correction.getCorrection())) {
						suggestion = suggestion.replaceAll(correction.getCorrection(), "<span class=\""
								+ misSpelledStyleClass + "\">" + correction.getCorrection() + "</span>");
					}
				}
				this.getJspContext()
						.getOut()
						.print("<a href=\"\" class=\"" + styleClass + "\">" + suggestion + "</a>&nbsp;<span class=\""
								+ numberOfResultsStyleClass + "\">(" + collation.getNumberOfHits() + ")</span>&nbsp;");
			}
		}

	}

	public Object getSolrResponse() {
		return solrResponse;
	}

	public void setSolrResponse(Object solrResponse) {
		this.solrResponse = solrResponse;
	}

	public String getStyleClass() {
		return styleClass;
	}

	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}

	public String getNumberOfResultsStyleClass() {
		return numberOfResultsStyleClass;
	}

	public void setNumberOfResultsStyleClass(String numberOfResultsStyleClass) {
		this.numberOfResultsStyleClass = numberOfResultsStyleClass;
	}

	public String getMisSpelledStyleClass() {
		return misSpelledStyleClass;
	}

	public void setMisSpelledStyleClass(String misSpelledStyleClass) {
		this.misSpelledStyleClass = misSpelledStyleClass;
	}

}
