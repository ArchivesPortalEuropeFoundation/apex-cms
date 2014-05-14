package eu.archivesportaleurope.portal.common.urls;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import eu.apenet.commons.solr.HighlightType;
import eu.apenet.commons.solr.HighlightUtil;
import eu.apenet.commons.solr.SolrField;
import eu.apenet.commons.utils.APEnetUtilities;
import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.portal.search.ead.EadSearchController;
import eu.archivesportaleurope.util.ApeUtil;

public class AbstractContentUrl  extends AbstractUrl{

	private final static Logger LOGGER = Logger.getLogger(AbstractContentUrl.class);
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
	
	public AbstractContentUrl(String repoCode, String xmlTypeName, String identifier){
		super(repoCode);
		this.xmlTypeName = xmlTypeName;
		this.identifier = identifier;
	}
	@Override
	public String toString() {
		LOGGER.info(identifier);
		LOGGER.info(ApeUtil.encodeSpecialCharacters(identifier));
		return super.toString() + PARAMETER_TYPE + xmlTypeName + PARAMETER_ID + ApeUtil.encodeSpecialCharacters(identifier);
	}
	public String getSearchSuffix(){
		LOGGER.info("start");
		if (StringUtils.isNotBlank(searchTerms) && StringUtils.isNotBlank(searchFieldsSelectionId)) {
			List<SolrField> solrFields = SolrField.getSolrFieldsByIdString(searchFieldsSelectionId);
			HighlightType highlightType = HighlightType.DEFAULT;
			LOGGER.info("start1 " + searchFieldsSelectionId);
			if (solrFields.size()> 0){
				highlightType = solrFields.get(0).getType();
			}
				LOGGER.info("start2");
				List<String> words = HighlightUtil.getInstance(APEnetUtilities.getApePortalConfig().getSolrStopwordsUrl())
						.convertSearchTermToWords(searchTerms, highlightType);
				LOGGER.info("start3 "+ words.size());
				String newSearchWords ="";
				for (int i = 0; i < words.size(); i++){
					if (i > 0){
						newSearchWords+="_" + words.get(i);
					}else {
						newSearchWords+=words.get(i);
					}
				}
				LOGGER.info(newSearchWords);
				if (StringUtils.isNotBlank(newSearchWords)) {
					return PARAMETER_SEARCH + searchFieldsSelectionId + FriendlyUrlUtil.SEPARATOR+ newSearchWords;
				}
			

		}
		return "";
	}

}
