package eu.archivesportaleurope.portal.common.urls;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.common.FriendlyUrlUtil;
import eu.archivesportaleurope.util.ApeUtil;

public class AbstractContentUrl  extends AbstractUrl{
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
		return super.toString() + PARAMETER_TYPE + xmlTypeName + PARAMETER_ID + ApeUtil.encodeSpecialCharacters(identifier);
	}
	public String getSearchSuffix(){
		if (StringUtils.isNotBlank(searchTerms)) {
			return PARAMETER_SEARCH + searchFieldsSelectionId + FriendlyUrlUtil.SEPARATOR+ ApeUtil.encodeSpecialCharacters(searchTerms);
		}else {
			return "";
		}
	}

}
