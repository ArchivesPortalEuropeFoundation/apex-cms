package eu.archivesportaleurope.portal.common.al;


public enum AlType {

	COUNTRY("country_"), ARCHIVAL_INSTITUTION("ai_");
	private String type;
	private AlType(String type){
		this.type = type;
	}
	@Override
	public String toString() {
		return type;
	}
	public static AlType getType(String string){
        for(AlType alType : AlType.values()){
            if(string.startsWith(alType.type)){
            	return alType;
            }
        }
        return null;
	}
	public static Integer getId(String string){
        for(AlType alType : AlType.values()){
            if(string.startsWith(alType.type)){
            	String subString = string.substring(alType.type.length());
            	return Integer.parseInt(subString);
            }
        }
        return null;
	}
}
