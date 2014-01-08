package eu.archivesportaleurope.portal.search.advanced;

public class Refinement {

	private String fieldValue;
	private String fieldName;
	private String longDescription;
	
	public Refinement(String fieldName, Integer fieldValue, String longDescription) {
		this (fieldName, fieldValue+"", longDescription);
	}
	
	public Refinement(String fieldName, String fieldValue, String longDescription) {
		super();
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
		this.longDescription = longDescription.replaceAll("'", "&#039");;
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
	
}
