package eu.archivesportaleurope.portal.common.urls;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.util.ApeUtil;

public class AbstractUrl {
	public static final String PARAMETER_AICODE = "/aicode/";
	public static final String PARAMETER_PAGE = "/page/";	
	private String repoCode;
	private String pageNumber;
	public AbstractUrl(String repoCode) {
		this.repoCode = repoCode;
	}

	protected String getRepoCode() {
		return ApeUtil.encodeRepositoryCode(repoCode);
	}
	

	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}
	public void setPageNumberAsInt(Integer pageNumber) {
		if (pageNumber != null){
			this.pageNumber = pageNumber.toString();
		}
	}
	

	@Override
	public String toString() {
		return PARAMETER_AICODE + getRepoCode();
	}
	public String getPageNumberSuffix(){
		if (StringUtils.isNotBlank(pageNumber)){
			return PARAMETER_PAGE+ pageNumber;
		}else {
			return "";
		}
	}

}
