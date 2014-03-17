package eu.archivesportaleurope.portal.common;

import com.liferay.portal.kernel.util.PropsUtil;

public class PropertiesUtil {

	public static String get(String key){
		return PropsUtil.get(key);
	}
}
