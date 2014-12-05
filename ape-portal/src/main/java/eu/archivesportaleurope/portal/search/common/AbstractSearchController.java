package eu.archivesportaleurope.portal.search.common;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.portlet.PortletRequest;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.search.eaccpf.EacCpfSearcher;
import eu.archivesportaleurope.portal.search.ead.EadSearcher;
import eu.archivesportaleurope.portal.search.eag.EagSearcher;

public class AbstractSearchController {
	protected static final Pattern NO_WHITESPACE_PATTERN = Pattern.compile("\\S+");
	private static final Pattern LEADING_WILDCARD_PATTERN = Pattern.compile("[\\?\\*\\~].*");
	private static final Pattern END_WILDCARD_PATTERN = Pattern.compile("[^\\?\\*\\~]{2}.*");
	private static final String WILDCARD = "*";
	private EadSearcher eadSearcher;
	private EacCpfSearcher eacCpfSearcher;
	private EagSearcher eagSearcher;
	
	public EadSearcher getEadSearcher() {
		return eadSearcher;
	}

	public void setEadSearcher(EadSearcher eadSearcher) {
		this.eadSearcher = eadSearcher;
	}

	public EacCpfSearcher getEacCpfSearcher() {
		return eacCpfSearcher;
	}

	public void setEacCpfSearcher(EacCpfSearcher eacCpfSearcher) {
		this.eacCpfSearcher = eacCpfSearcher;
	}

	
	public EagSearcher getEagSearcher() {
		return eagSearcher;
	}

	public void setEagSearcher(EagSearcher eagSearcher) {
		this.eagSearcher = eagSearcher;
	}

	public static SolrQueryParameters getSolrQueryParametersByForm(AbstractSearchForm abstractSearchForm, PortletRequest portletRequest){
		if (StringUtils.isNotBlank(abstractSearchForm.getTerm())){
			SolrQueryParameters solrQueryParameters = new SolrQueryParameters();
			if (AbstractSearchForm.SEARCH_ALL_STRING.equals(abstractSearchForm.getTerm())){
				if (portletRequest.getUserPrincipal() == null){
					return null;
				}
				solrQueryParameters.setTerm("");
			}else {
				solrQueryParameters.setTerm(abstractSearchForm.getTerm());
			}
			solrQueryParameters.setMatchAllWords(abstractSearchForm.matchAllWords());
			return solrQueryParameters;			
		}
		return null;

	}
	protected static void updatePagination( ListResults results) {
		Long totalNumberOfPages = results.getTotalNumberOfResults() / results.getPageSize();
		Long rest = results.getTotalNumberOfResults() % results.getPageSize();
		if (rest > 0) {
			totalNumberOfPages++;
		}
		results.setTotalNumberOfPages(totalNumberOfPages);
	}

	private static boolean validateTerm(String string){
		Matcher matcher = NO_WHITESPACE_PATTERN.matcher(string.trim());
		while (matcher.find()) {
			String word = matcher.group();
			if (!WILDCARD.equals(word)){
		        Matcher m = LEADING_WILDCARD_PATTERN.matcher(word);
		        if (m.matches()){
		        	return false;
		        }else {
		        	m = END_WILDCARD_PATTERN.matcher(word);
		        	if (!m.matches()){
		        		return false;
		        	}
		        }
			}
		}
		return true;
	}
	public String validate(AbstractSearchForm abstractSearchForm, PortletRequest portletRequest) {
		if (StringUtils.isNotBlank(abstractSearchForm.getTerm())){
			boolean valid = validateTerm(abstractSearchForm.getTerm());
			if (!valid){
				return "search.message.noleadingwildcards";
			}
			
		}
		if (StringUtils.isNotBlank(abstractSearchForm.getFromdate())
				&& !SearchUtil.isValidDate(abstractSearchForm.getFromdate())) {
			return "search.message.IncorrectDateTyped";

		}
		if (StringUtils.isNotBlank(abstractSearchForm.getTodate())
				&& !SearchUtil.isValidDate(abstractSearchForm.getTodate())) {
			return "search.message.IncorrectDateTyped";
		}
		return null;
	}
}
