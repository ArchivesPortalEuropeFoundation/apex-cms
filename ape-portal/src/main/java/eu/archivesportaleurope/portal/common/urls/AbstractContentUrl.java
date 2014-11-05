package eu.archivesportaleurope.portal.common.urls;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import eu.apenet.commons.solr.HighlightType;
import eu.apenet.commons.solr.SolrField;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.util.ApeUtil;

public class AbstractContentUrl extends AbstractUrl {

	public static final String PARAMETER_TYPE = "/type/";
	public static final String PARAMETER_ID = "/id/";

	public static final String PARAMETER_SEARCH = "/search/";

	private String searchTerms;
	private String searchFieldsSelectionId;

	private String xmlTypeName;
	private String identifier;

	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}

	public void setSearchFieldsSelectionId(String searchFieldsSelectionId) {
		this.searchFieldsSelectionId = searchFieldsSelectionId;
	}

	public AbstractContentUrl(String repoCode, String xmlTypeName, String identifier) {
		super(repoCode);
		this.xmlTypeName = xmlTypeName;
		this.identifier = identifier;
	}

	@Override
	public String toString() {
		return super.toString() + PARAMETER_TYPE + xmlTypeName + PARAMETER_ID
				+ ApeUtil.encodeSpecialCharacters(identifier);
	}

	public String getSearchSuffix() {
		if (StringUtils.isNotBlank(searchTerms) && StringUtils.isNotBlank(searchFieldsSelectionId)) {
			List<SolrField> solrFields = SolrField.getSolrFieldsByIdString(searchFieldsSelectionId);
			HighlightType highlightType = HighlightType.DEFAULT;
			if (solrFields.size() > 0) {
				highlightType = solrFields.get(0).getType();
			}
			String newSearchWords = ApeUtil.encodeSpecialCharacters(searchTerms);
			if (StringUtils.isNotBlank(newSearchWords)) {
				return PARAMETER_SEARCH + searchFieldsSelectionId + FriendlyUrlUtil.SEPARATOR + newSearchWords;
			}

		}
		return "";
	}

}
