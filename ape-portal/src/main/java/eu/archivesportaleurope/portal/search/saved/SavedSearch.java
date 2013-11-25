package eu.archivesportaleurope.portal.search.saved;

import java.io.Serializable;
import java.util.Date;

public class SavedSearch implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1125380681178677726L;
	private String id;
	private String description;
	private String term;
	private Date modifiedDate;
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
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public Date getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Date modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	
}
