package eu.archivesportaleurope.portal.search.saved;

public class AiContentParams {
	private String repoCode;
	private String xmlTypeName;
	private String pageNumber = "1";

	public String getRepoCode() {
		return repoCode;
	}
	public void setRepoCode(String repoCode) {
		this.repoCode = repoCode;
	}
	public String getXmlTypeName() {
		return xmlTypeName;
	}
	public void setXmlTypeName(String xmlTypeName) {
		this.xmlTypeName = xmlTypeName;
	}
	public String getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

}
