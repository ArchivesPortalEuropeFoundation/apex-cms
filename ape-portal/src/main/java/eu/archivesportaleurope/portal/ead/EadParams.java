package eu.archivesportaleurope.portal.ead;

public class EadParams {
	private static final int ZERO = 0;

	private String id;
	private Integer xmlTypeId;
	private String eadid;
	private Integer aiId;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Integer getXmlTypeId() {
		return xmlTypeId;
	}
	public void setXmlTypeId(Integer xmlTypeId) {
		this.xmlTypeId = xmlTypeId;
	}
	public String getEadid() {
		return eadid;
	}
	public void setEadid(String eadid) {
		this.eadid = eadid;
	}
	public Integer getAiId() {
		return aiId;
	}
	public void setAiId(Integer aiId) {
		this.aiId = aiId;
	}

}
