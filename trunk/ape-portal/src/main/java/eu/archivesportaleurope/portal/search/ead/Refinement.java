package eu.archivesportaleurope.portal.search.ead;

public class Refinement {

	private String fieldValue;
	private String fieldName;
	private String longDescription;
	private boolean removed;
	
	public Refinement(String fieldName, Integer fieldValue, String longDescription) {
		this (fieldName, fieldValue+"", longDescription);
	}
	
	public Refinement(String fieldName, Integer fieldValue, String longDescription, boolean removed) {
		this (fieldName, fieldValue+"", longDescription,removed);
	}
	public Refinement(String fieldName, String fieldValue, String longDescription) {
		this (fieldName, fieldValue+"", longDescription, false);
	}
	public Refinement(String fieldName, String fieldValue, String longDescription, boolean removed) {
		super();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.longDescription = longDescription.replaceAll("'", "&#039");
		this.removed = removed;
	}
	public String getFieldValue() {
		return fieldValue;
	}
	public String getFieldName() {
		return fieldName;
	}
	public String getLongDescription() {
		return longDescription;
	}
	public String getCssClass(){
		if (removed){
			return "removed";
		}else {
			return "";
		}
	}

	public boolean isRemoved() {
		return removed;
	}

	public boolean isDate() {
		return false;
	}


}
