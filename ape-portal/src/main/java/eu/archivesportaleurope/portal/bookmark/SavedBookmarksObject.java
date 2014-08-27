package eu.archivesportaleurope.portal.bookmark;

import java.io.Serializable;
import java.util.Date;

public class SavedBookmarksObject<bookmarkName> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1125380681178677726L;
	private String id;
	private Date modifiedDate;
	private String bookmarkName;
	private String description;
	private String persistentLink;
	private String typedocument;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
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
}
