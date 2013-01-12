package eu.archivesportaleurope.portal.language;

import java.io.Serializable;

public class Translation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3894852789998847921L;

	private String languageId;
	private String friendlyUrl;
	private String name;

    public Translation() {}

	public Translation(String languageId, String friendlyUrl, String name) {
		super();
		this.languageId = languageId;
		this.friendlyUrl = friendlyUrl;
		this.name = name;
	}
	public String getLanguageId() {
		return languageId;
	}
	public void setLanguageId(String languageId) {
		this.languageId = languageId;
	}
	public String getFriendlyUrl() {
		return friendlyUrl;
	}
	public void setFriendlyUrl(String friendlyUrl) {
		this.friendlyUrl = friendlyUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
