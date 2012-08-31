package eu.archivesportaleurope.portal.common.al;


public enum TreeType {

	GROUP("group"), LEAF("leaf"), MORE("more");
	private String type;
	private TreeType(String type){
		this.type = type;
	}
	@Override
	public String toString() {
		return type;
	}
	public static TreeType getType(String string){
        for(TreeType treeType : TreeType.values()){
            if(treeType.type.equals(string)){
            	return treeType;
            }
        }
        return null;
	}
}
