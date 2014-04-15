package eu.archivesportaleurope.portal.eaccpf.display;

public class EacCpfParams {

	private String type;
	private String id;
	private String databaseId;
//	private Long eacId;
//	private Integer pageNumber;
	private Integer xmlTypeId;
	private String recordid;
	private Integer aiId;
	private String repositoryCode;
	private String xmlTypeName;
	private String eaccpfIdentifier;
	private String searchFieldsSelection;
	private String searchTerms;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
/*	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	*/
/*	public Long getEacId() {
		return eacId;
	}
	public void setEacId(Long eacId) {
		this.eacId = eacId;
	}*/
	public Integer getXmlTypeId() {
		return xmlTypeId;
	}
	public void setXmlTypeId(Integer xmlTypeId) {
		this.xmlTypeId = xmlTypeId;
	}
	public Integer getAiId() {
		return aiId;
	}
	public void setAiId(Integer aiId) {
		this.aiId = aiId;
	}
	public String getRepositoryCode() {
		return this.repositoryCode;
	}
	public void setRepositoryCode(String repositoryCode) {
		this.repositoryCode = repositoryCode;
	}
	public String getXmlTypeName() {
		return xmlTypeName;
	}
	public void setXmlTypeName(String xmlTypeName) {
		this.xmlTypeName = xmlTypeName;
	}
	public String getRecordid() {
		return recordid;
	}
	public void setRecordid(String recordid) {
		this.recordid = recordid;
	}
	public String getDatabaseId() {
		return databaseId;
	}
	public void setDatabaseId(String databaseId) {
		this.databaseId = databaseId;
	}
	/**
	 * @return the eaccpfIdentifier
	 */
	public String getEaccpfIdentifier() {
		return this.eaccpfIdentifier;
	}
	/**
	 * @param eaccpfIdentifier the eaccpfIdentifier to set
	 */
	public void setEaccpfIdentifier(String eaccpfIdentifier) {
		this.eaccpfIdentifier = eaccpfIdentifier;
	}
	/**
	 * @return the searchFieldsSelection
	 */
	public String getSearchFieldsSelection() {
		return this.searchFieldsSelection;
	}
	/**
	 * @param searchFieldsSelection the searchFieldsSelection to set
	 */
	public void setSearchFieldsSelection(String searchFieldsSelection) {
		this.searchFieldsSelection = searchFieldsSelection;
	}
	/**
	 * @return the searchTerms
	 */
	public String getSearchTerms() {
		return this.searchTerms;
	}
	/**
	 * @param searchTerms the searchTerms to set
	 */
	public void setSearchTerms(String searchTerms) {
		this.searchTerms = searchTerms;
	}
}
