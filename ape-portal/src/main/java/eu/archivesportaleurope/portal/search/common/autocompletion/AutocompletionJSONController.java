package eu.archivesportaleurope.portal.search.common.autocompletion;

import java.util.List;

import java.util.Map;

import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;


import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;


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
	public void writeJSON(ResourceRequest resourceRequest, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		String term = resourceRequest.getParameter("term").trim();
		
		try {
			
			StringBuilder builder = new StringBuilder();
			builder.append(START_ARRAY);
			if (StringUtils.isNotBlank(term)) {
				TermsResponse termsResponse = getSearcher().getTerms(term);
				boolean isAdded = false;
				for (Map.Entry<String, List<Term>> entry : termsResponse.getTermMap().entrySet()) {
					for (Term termItem : entry.getValue()) {
						if (isAdded) {
							builder.append(COMMA);
						} else {
							isAdded = true;
						}
						builder.append("\"");
						builder.append(termItem.getTerm());
						builder.append("\"");
					}
				}
			}
			builder.append(END_ARRAY);
			writeToResponseAndClose(builder, resourceResponse);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		log.debug("Context search time: " + (System.currentTimeMillis() - startTime));
	}

}