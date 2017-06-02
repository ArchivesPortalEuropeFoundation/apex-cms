package eu.archivesportaleurope.portal.display.ead;

public class EadDetailsParams {

	private String type;
	private String id;
	private Long ecId;
	private Integer pageNumber;
	private boolean previewDetails;
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
	public Integer getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}
	public Long getEcId() {
		return ecId;
	}
	public void setEcId(Long ecId) {
		this.ecId = ecId;
	}
	public boolean isPreviewDetails() {
		return previewDetails;
	}
	public void setPreviewDetails(boolean previewDetails) {
		this.previewDetails = previewDetails;
	}

	
}
