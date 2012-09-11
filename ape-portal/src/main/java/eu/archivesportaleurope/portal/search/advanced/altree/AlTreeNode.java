package eu.archivesportaleurope.portal.search.advanced.altree;

import org.apache.commons.lang.StringUtils;

import eu.archivesportaleurope.portal.common.tree.TreeNode;

public class AlTreeNode extends TreeNode {
	private Integer aiId;
	private String previewId;
	private String type;
	private String alType;

	

	public Integer getAiId() {
		return aiId;
	}

	public void setAiId(Integer aiId) {
		this.aiId = aiId;
	}

	public String getPreviewId() {
		return previewId;
	}

	public void setPreviewId(String previewId) {
		this.previewId = previewId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAlType() {
		return alType;
	}

	public void setAlType(String alType) {
		this.alType = alType;
	}

	@Override
	protected String toStringInternal() {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		if (aiId != null) {
			builder.append("\"aiId\":");
			builder.append(" \"" + aiId + "\"");
			first = false;
		}
		if (StringUtils.isNotBlank(previewId)) {
			if (!first)
				builder.append(COMMA);
			first = false;
			builder.append("\"previewId\":");
			builder.append(" \"" + previewId + "\"");
		}
		return builder.toString();
	}

}
