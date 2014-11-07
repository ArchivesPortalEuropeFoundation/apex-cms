package eu.archivesportaleurope.portal.ead;

public class EadParams {
	private String databaseId;
	private String eadid;
	private String repoCode;
	private String xmlTypeName;
	private Integer pageNumber;
	private String unitid;
	private String element;
	private String term;


	public String getDatabaseId() {
		return databaseId;
	}

	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}

	public String getEadid() {
		return eadid;
	}

	public void setEadid(String eadid) {
		this.eadid = eadid;
	}

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

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public String getUnitid() {
		return unitid;
	}

	public String getElement() {
		return element;
	}

	public String getTerm() {
		return term;
	}

	public void setUnitid(String unitid) {
		this.unitid = unitid;
	}

	public void setElement(String element) {
		this.element = element;
	}

	public void setTerm(String term) {
		this.term = term;
	}

}
