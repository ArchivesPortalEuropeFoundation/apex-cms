package eu.archivesportaleurope.portal.common;

import java.util.Locale;

import org.springframework.context.MessageSource;

import eu.apenet.commons.ResourceBundleSource;

public class SpringResourceBundleSource implements ResourceBundleSource {

	private MessageSource messageSource;
	private Locale locale;
	public SpringResourceBundleSource(MessageSource messageSource, Locale locale){
		this.messageSource = messageSource;
		this.locale = locale;
	}
	
	@Override
	public String getString(String key) {
		return getString(key, UNKNOWN + key + UNKNOWN);
	}
	

	@Override
	public String getString(String key, String defaultValue) {
		return messageSource.getMessage(key, null, defaultValue, locale);
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

}
