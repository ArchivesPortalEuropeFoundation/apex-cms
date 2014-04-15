package eu.archivesportaleurope.portal.common.jsp;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.apenet.commons.solr.HighlightType;
import eu.apenet.commons.solr.HighlightUtil;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.archivesportaleurope.portal.eaccpf.search.DisplayPreviewContoller;

public class GenerateSearchWordsTag extends SimpleTagSupport {
	private final static Logger LOGGER = Logger.getLogger(GenerateSearchWordsTag.class);
	private String element;
	private String term;
	private String var;

	@Override
	public void doTag() throws JspException, IOException {
		String result = "";
		LOGGER.info(term + " ------ " + element);
		if (StringUtils.isNotBlank(term) && StringUtils.isNotBlank(element)) {
			List<String> words = HighlightUtil.getInstance(APEnetUtilities.getApePortalConfig().getSolrStopwordsUrl())
					.convertSearchTermToWords(term, HighlightType.getHighlightType(element));
			for (int i = 0; i < words.size(); i++){
				LOGGER.info("word: " + words.get(i));
				if (i > 0){
					result+="_" + words.get(i);
				}else {
					result+=words.get(i);
				}
			}

		}

		getJspContext().setAttribute(var, URLEncoder.encode(result, "utf-8"));
		super.doTag();
	}


	public void setTerm(String term) {
		this.term = term;
	}


	public void setVar(String var) {
		this.var = var;
	}

	public void setElement(String element) {
		this.element = element;
	}

}
