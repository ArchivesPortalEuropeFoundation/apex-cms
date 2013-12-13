package eu.archivesportaleurope.portal.common;

public class NotExistInDatabaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1826552223242473402L;
	private String id;
	public NotExistInDatabaseException(){
	}
	public NotExistInDatabaseException(String id){
		this.id = id;
	}
	public String getId() {
		return id;
	}

	
}
