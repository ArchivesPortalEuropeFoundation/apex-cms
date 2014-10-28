package eu.archivesportaleurope.portal.tagcloud;

public class TagCloudItem {

	private long count;
	private String key;
	private String name;
	private int tagNumber = 1;
	
	public TagCloudItem(){
		
	}
	
	public TagCloudItem(long count, String key) {
		super();
		this.count = count;
		this.key = key;
	}
	
	public TagCloudItem(TagCloudItem item, String translatedName) {
		super();
		this.count = item.getCount();
		this.key = item.getKey();
		this.name = translatedName;
		this.tagNumber = item.getTagNumber();
	}
	public long getCount() {
		return count;
	}
	public String getKey() {
		return key;
	}
	public String getName() {
		return name;
	}
	public boolean isEnabled() {
		return count > 0;
	}
	public void setCount(long count) {
		this.count = count;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCssClass(){
		return "tag" + tagNumber;
	}

	public void setTagNumber(int tagNumber) {
		this.tagNumber = tagNumber;
	}
	
	public int getTagNumber() {
		return tagNumber;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TagCloudItem other = (TagCloudItem) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}



	
}