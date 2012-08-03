package eu.archivesportaleurope.portal.search.common.jsp;

import java.io.IOException;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.solr.client.solrj.response.SpellCheckResponse;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Collation;
import org.apache.solr.client.solrj.response.SpellCheckResponse.Correction;

public class AutosuggestionTag extends SimpleTagSupport {
	private Object spellCheckResponse;
	private String styleClass;
	private String numberOfResultsStyleClass;
	private String misSpelledStyleClass;

	public void doTag() throws JspException, IOException {
		SpellCheckResponse response =  (SpellCheckResponse) this.spellCheckResponse;
		List<Collation> suggestions = response.getCollatedResults();
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


	public Object getSpellCheckResponse() {
		return spellCheckResponse;
	}


	public void setSpellCheckResponse(Object spellCheckResponse) {
		this.spellCheckResponse = spellCheckResponse;
	}

}
