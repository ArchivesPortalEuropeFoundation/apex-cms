package eu.archivesportaleurope.portal.tagcloud;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.apenet.commons.utils.DisplayUtils;

public class TagCloudItem {
	private static final Pattern WORD_PATTERN = Pattern.compile("([\\p{L}\\p{Digit}\\s]+)");

	private long count;
	private String key;
	private String name;
	private int tagNumber = 1;
	private NumberFormat numberFormat;

	public TagCloudItem() {

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

	public TagCloudItem(NumberFormat numberFormat, TagCloudItem item, String translatedName) {
		super();
		this.count = item.getCount();
		this.key = item.getKey();
		this.name = translatedName;
		this.tagNumber = item.getTagNumber();
		this.numberFormat = numberFormat;
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
	
	public String getShortName() {
		return clean(name);
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

	public String getCssClass() {
		return "tag" + tagNumber;
	}

	public void setTagNumber(int tagNumber) {
		this.tagNumber = tagNumber;
	}

	public int getTagNumber() {
		return tagNumber;
	}

	public String getDisplayCount() {
		if (count > 0) {
			if (numberFormat == null) {
				return count + "";
			} else {
				return numberFormat.format(count);
			}
		} else {
			return "";
		}
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
	private static String clean(String string){
		String result = "";
		Matcher matcher = WORD_PATTERN.matcher(string);
		if (matcher.find()) {
			result = DisplayUtils.substring(matcher.group().trim(), 20);
		}
		return result;

	}
}
