package eu.archivesportaleurope.portal.search.common.autocompletion;

import java.util.List;
import java.util.Map;

import javax.portlet.ResourceResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.solr.client.solrj.response.TermsResponse;
import org.apache.solr.client.solrj.response.TermsResponse.Term;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.portlet.bind.annotation.ResourceMapping;

import eu.archivesportaleurope.portal.common.tree.AbstractJSONWriter;


/**
 * Generates an json for autocompletion
 * @author bastiaan
 *
 */
@Controller(value="autocompletionJSONController")
@RequestMapping(value = "VIEW")
public class AutocompletionJSONController extends AbstractJSONWriter {



	@ResourceMapping(value="autocompletion")
	public void writeJSON(@RequestParam String term, ResourceResponse resourceResponse) {
		long startTime = System.currentTimeMillis();
		try {
			TermsResponse termsResponse = getSearcher().getTerms(term);
			StringBuilder builder = new StringBuilder();
			builder.append(START_ARRAY);
			boolean isAdded = false;
			for (Map.Entry<String, List<Term>> entry: termsResponse.getTermMap().entrySet()){
				for (Term termItem: entry.getValue()){
					if (isAdded){
						builder.append(COMMA);
					}else {
						isAdded = true;
					}
					builder.append("\"");
					builder.append(termItem.getTerm());
					builder.append("\"");
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