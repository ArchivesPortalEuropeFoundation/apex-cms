package eu.archivesportaleurope.portal.bookmark;


public class Bookmark implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 574790214370347998L;

	private String id;
	private String overviewPageNumber;
	private Date modifiedDate;
	private String bookmarkName;
	private String description;
	private String persistentLink;
	private String typedocument;
	private String liferay_user_id;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * @return the overviewPageNumber
	 */
	public String getOverviewPageNumber() {
		return overviewPageNumber;
	}

	/**
	 * @param overviewPageNumber the overviewPageNumber to set
	 */
	public void setOverviewPageNumber(String overviewPageNumber) {
		this.overviewPageNumber = overviewPageNumber;
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Date getModifiedDate() {
		return modifiedDate;
	}
	
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
	/**
	 * @return the bookmarkName
	 */
	public String getBookmarkName() {
		return bookmarkName;
	}
	
	/**
	 * @param bookmarkName the bookmarkName to set
	 */
	public void setBookmarkName(String bookmarkName) {
		this.bookmarkName = bookmarkName;
	}
	
	/**
	 * @return the persistentLink
	 */
	public String getPersistentLink() {
		return persistentLink;
	}
	
	/**
	 * @param persistentLink the persistentLink to set
	 */
	public void setPersistentLink(String persistentLink) {
		this.persistentLink = persistentLink;
	}
	
	/**
	 * @return the typedocument
	 */
	public String getTypedocument() {
		return typedocument;
	}
	
	/**
	 * @param typedocument the typedocument to set
	 */
	public void setTypedocument(String typedocument) {
		this.typedocument = typedocument;
	}

	/**
	 * @return the liferay_user_id
	 */
	public String getLiferay_user_id() {
		return liferay_user_id;
	}

	/**
	 * @param liferay_user_id the liferay_user_id to set
	 */
	public void setLiferay_user_id(String liferay_user_id) {
		this.liferay_user_id = liferay_user_id;
	}

}
