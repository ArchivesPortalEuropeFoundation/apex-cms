package eu.archivesportaleurope.portal.common.al;

import eu.apenet.commons.solr.SolrValues;



public enum AlType {

	COUNTRY("country"), ARCHIVAL_INSTITUTION(SolrValues.AI_PREFIX), HOLDINGS_GUIDE(SolrValues.HG_PREFIX),SOURCE_GUIDE(SolrValues.SG_PREFIX),FINDING_AID(SolrValues.FA_PREFIX), C_LEVEL(SolrValues.C_LEVEL_PREFIX);
	private static final String SEPARATOR = "_";
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
            if(string.startsWith(alType.type + SEPARATOR)){
            	return alType;
            }
        }
        return null;
	}
	public static Integer getId(String string){
        for(AlType alType : AlType.values()){
            if(string.startsWith(alType.type + SEPARATOR)){
            	String subString = string.substring(alType.type.length()+ SEPARATOR.length());
            	return Integer.parseInt(subString);
            }
        }
        return null;
	}
	public static Long getLongId(String string){
        for(AlType alType : AlType.values()){
            if(string.startsWith(alType.type + SEPARATOR)){
            	String subString = string.substring(alType.type.length()+ SEPARATOR.length());
            	return Long.parseLong(subString);
            }
        }
        return null;
	}
	public static String getKey(AlType alType, Number id){
        return alType + SEPARATOR + id;
	}
}
