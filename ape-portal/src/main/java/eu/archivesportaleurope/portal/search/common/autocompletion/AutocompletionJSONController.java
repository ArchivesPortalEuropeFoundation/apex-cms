package eu.archivesportaleurope.portal.search.common.autocompletion;

import eu.apenet.commons.solr.AbstractSearcher;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.archivesportaleurope.portal.common.SpringResourceBundleSource;
import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;
import java.io.IOException;

/**
 * Generates an json for autocompletion
 * 
 * @author bastiaan
 * 
 */
@Controller(value = "autocompletionJSONController")
@RequestMapping(value = "VIEW")
public class AutocompletionJSONController extends AbstractJSONWriter {

	@ResourceMapping(value = "autocompletion")
	public void writeJSON(@ModelAttribute(value = "autocompletionForm") AutocompletionForm autocompletionForm,
			ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();

		try {
			NumberFormat numberFormat = NumberFormat.getInstance(resourceRequest.getLocale());
			StringBuilder builder = new StringBuilder();
			builder.append(START_ARRAY);
			if (StringUtils.isNotBlank(autocompletionForm.getTerm())) {
				List<AutocompletionResult> results = new ArrayList<AutocompletionResult>();
				AbstractSearcher abstractSearcher = null;
				if (AutocompletionForm.EAD.equals(autocompletionForm.getSourceType())) {
					abstractSearcher = getEadSearcher();
					add(results,abstractSearcher, autocompletionForm, null);
					write(builder, results, false, null,numberFormat);
				} else if (AutocompletionForm.EAC_CPF.equals(autocompletionForm.getSourceType())) {
					abstractSearcher = getEacCpfSearcher();
					add(results,abstractSearcher, autocompletionForm, null);
					write(builder, results, false, null,numberFormat);
				}  else if (AutocompletionForm.EAG.equals(autocompletionForm.getSourceType())) {
					abstractSearcher = getEagSearcher();
					add(results,abstractSearcher, autocompletionForm, null);
					write(builder, results, false, null,numberFormat);
				}else {
					SpringResourceBundleSource source = new SpringResourceBundleSource(this.getMessageSource(),
							resourceRequest.getLocale());
					add(results, getEadSearcher(), autocompletionForm, "archives");
					add(results, getEacCpfSearcher(), autocompletionForm, "names");
					add(results, getEagSearcher(), autocompletionForm, "institutions");
					write(builder, results, true, source,numberFormat);
				}

			}
			builder.append(END_ARRAY);
			writeToResponseAndClose(builder, resourceResponse);

		} catch (Exception e) {
			log.error(e.getMessage());
		}
		log.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}

	private static void write(StringBuilder builder, List<AutocompletionResult> results, boolean advanced, SpringResourceBundleSource source, NumberFormat numberFormat) {
		boolean isAdded = false;
		if (advanced) {
			Collections.sort(results);
			for (int i = 0; i < 10 && i < results.size(); i++) {
				AutocompletionResult result = results.get(i);
				if (isAdded) {
					builder.append(COMMA);
				} else {
					isAdded = true;
				}
				builder.append(START_ITEM);
				builder.append("\"value\":");
				builder.append("\"" + result.getTerm() + "\"");
				builder.append(COMMA);
				builder.append("\"label\":");
				builder.append("\"" + result.getTerm() + " (" + numberFormat.format(result.getFrequency()) + " " + source.getString("search.autocompletion.type." + result.getType()) + ")\"");
				builder.append(END_ITEM);
			}
		} else {
			for (AutocompletionResult result : results) {
				if (isAdded) {
					builder.append(COMMA);
				} else {
					isAdded = true;
				}
				builder.append("\"");
				builder.append(result.getTerm());
				builder.append("\"");
			}
		}
	}

	private static void add(List<AutocompletionResult> results, AbstractSearcher abstractSearcher,
			AutocompletionForm autocompletionForm, String sourceType) throws SolrServerException, IOException {
		TermsResponse termsResponse = abstractSearcher.getTerms(autocompletionForm.getTerm().trim());
		for (Map.Entry<String, List<Term>> entry : termsResponse.getTermMap().entrySet()) {
			for (Term termItem : entry.getValue()) {
				results.add(new AutocompletionResult(termItem, sourceType));
			}
		}
	}

	private static class AutocompletionResult implements Comparable<AutocompletionResult> {
		String type;
		String term;
		long frequency;

		protected AutocompletionResult(Term term, String type) {
			this.type = type;
			this.term = term.getTerm();
			this.frequency = term.getFrequency();
		}

		public String getType() {
			return type;
		}

		public String getTerm() {
			return term;
		}

		public long getFrequency() {
			return frequency;
		}

		@Override
		public int compareTo(AutocompletionResult o) {
			if (frequency > o.getFrequency()) {
				return -1;
			} else if (frequency < o.getFrequency()) {
				return 1;
			}
			return 0;
		}

	}

}