package eu.archivesportaleurope.portal.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.liferay.portal.kernel.util.PropsUtil;

import eu.apenet.commons.utils.APEnetUtilities;

public class PropertiesUtil {
	private static final Logger LOGGER = Logger.getLogger(PropertiesUtil.class);

	private static Properties defaultProperties = new Properties();
	static {
		try {
			InputStream inputStream = PropertiesUtil.class.getClassLoader()
		        .getResourceAsStream("/default.properties");
	    	defaultProperties.load(inputStream);
		}catch (IOException ioe){
			LOGGER.error(APEnetUtilities.generateThrowableLog(ioe));
		}
	}
	
	public static String get(String key){
		String value = PropsUtil.get(key);
		if (value == null){
			value =  defaultProperties.getProperty(key);
		}
		return value;
	}
	public static Integer getInt(String key){
		String value = PropsUtil.get(key);
		if (value == null){
			value =  defaultProperties.getProperty(key);
		}
		return Integer.parseInt(value);
	}
	public static Long getLong(String key){
		String value = PropsUtil.get(key);
		if (value == null){
			value =  defaultProperties.getProperty(key);
		}
		return Long.parseLong(value);
	}
}
