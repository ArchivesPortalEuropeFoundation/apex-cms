package eu.archivesportaleurope.portal.common.al;

import org.apache.commons.lang.StringUtils;

import eu.apenet.commons.solr.SolrValues;

public enum AlType {

	COUNTRY("country"), ARCHIVAL_INSTITUTION(SolrValues.AI_PREFIX), HOLDINGS_GUIDE(SolrValues.HG_PREFIX), SOURCE_GUIDE(
			SolrValues.SG_PREFIX), FINDING_AID(SolrValues.FA_PREFIX), C_LEVEL(SolrValues.C_LEVEL_PREFIX);
	private static final String COLON = ":";
	private static final String SEPARATOR = "_";
	private static final String DEPTH_SEPARATOR = "|";
	private static final String START_SEPARATOR = "-";
	public static final String LIST_SEPARATOR = ",";
	private String type;

	private AlType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	public static AlType getAlType(String string) {
		for (AlType alType : AlType.values()) {
			if (string.startsWith(alType.type + SEPARATOR)) {
				return alType;
			}
		}
		return null;
	}

	public static Long getId(String string) {
		int firstIndex = string.indexOf(SEPARATOR) + 1;
		int lastIndex = string.indexOf(COLON);
		String subString = string.substring(firstIndex, lastIndex);
		return Long.parseLong(subString);
	}
	public static String getKeyWithDepth(AlType alType, Number id, TreeType treeType, int depth) {
		return alType + SEPARATOR + id + COLON + treeType + DEPTH_SEPARATOR + depth;
	}

	public static String getKey(AlType alType, Number id, TreeType treeType) {
		return alType + SEPARATOR + id + COLON + treeType + DEPTH_SEPARATOR;
	}

	public static String getKey(String key, Integer start) {
		Long id = AlType.getId(key);
		AlType type = AlType.getAlType(key);
		TreeType treeType = AlType.getTreeType(key);
		return getKey(type, id, treeType, start);
	}

	public static String getKey(AlType alType, Number id, TreeType treeType, Integer start) {
		return alType + SEPARATOR + id + COLON + treeType + DEPTH_SEPARATOR + START_SEPARATOR + start;
	}

	public static TreeType getTreeType(String string) {
		int firstIndex = string.indexOf(COLON) + 1;
		int lastIndex = string.indexOf(DEPTH_SEPARATOR);
		String subString = null;
		if (lastIndex > 0) {
			subString = string.substring(firstIndex, lastIndex);
		} else {
			subString = string.substring(firstIndex);
		}
		return TreeType.getType(subString);
	}

	public static Integer getStart(String string) {
		int firstIndex = string.indexOf(START_SEPARATOR) + 1;
		if (firstIndex > 0) {
			String subString = string.substring(firstIndex);
			return Integer.parseInt(subString);
		} else {
			return 0;
		}
	}
	public static Integer getDepth(String string){
		int firstIndex = string.indexOf(DEPTH_SEPARATOR) + 1;
		int lastIndex = string.indexOf(START_SEPARATOR);
		String subString = null;
		if (lastIndex > 0) {
			subString = string.substring(firstIndex, lastIndex);
		} else {
			subString = string.substring(firstIndex);
		}
		if (StringUtils.isBlank(subString)){
			return -1;
		}else {
			return Integer.parseInt(subString);
		}
	}
}
